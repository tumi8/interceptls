from time import sleep
import socket
import threading
import SocketServer
import ipaddr
import logging
import asyncore

server_ip = "localhost"
server_port = 4444
logfile = "/tmp/TLSRelay_command_server.log"

relay_list_lock = threading.Lock()
relays = {}

class ThreadedTCPServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):
    daemon_threads = True
    allow_reuse_address = True

class CommandServerRequestHandler(SocketServer.BaseRequestHandler):
    BUFFER_SIZE = 4096
    SOM = "["
    EOM = "]\n"
    SEPARATOR = "|"
    
    def recv_command(self):
        received_data = ""
        for i in range(10):
            received_data += self.request.recv(self.BUFFER_SIZE)
            if received_data.endswith(self.EOM):
                break
        
        assert received_data.startswith(self.SOM)
        assert received_data.endswith(self.EOM)
        
        command = received_data[len(self.SOM):-len(self.EOM)].split(self.SEPARATOR)
        assert len(command) >= 1
        return command
    
    def recv_relay_to(self):
        cmd, l3_proto, l4_proto, dest_ip, dest_port = self.recv_command()
        
        assert cmd == "relay_to"
        l3_proto = int(l3_proto)
        assert l3_proto in [socket.AF_INET, socket.AF_INET6]
        l4_proto = int(l4_proto)
        assert l4_proto in [socket.SOCK_STREAM, socket.SOCK_DGRAM]
        dest_ip = str(ipaddr.IPAddress(dest_ip))
        dest_port = int(dest_port)
        assert dest_port in range(1, 2**16)
        
        return l3_proto, l4_proto, (dest_ip, dest_port)
    
    def send_command(self, *cmd_parts):
        cmd_parts = map(lambda item: str(item), cmd_parts)
        
        msg = self.SOM
        msg += self.SEPARATOR.join(cmd_parts)
        msg += self.EOM
        self.request.sendall(msg)

    def handle(self):
        # read client's request for a relay
        try:
            l3_proto, l4_proto, dest_addr = self.recv_relay_to()
        except (ValueError, AssertionError):
            return
        
        # setup relay
        (client_ip, client_port) = self.request.getpeername()
        
        relay = Relay(l3_proto, l4_proto, 443, client_ip, dest_addr)
        relay_list_lock.acquire()
        relays[(l3_proto, l4_proto, 443, client_ip)] = relay
        relay_list_lock.release()
        
        # confirm established relay
        self.send_command("relay_at", "127.0.0.1", dest_addr[1])
        
        # wait until relayed connection closed
        while not relay.closed:
            sleep(1)
        
        # send relayed data transcript
        self.send_command("relay_transcript", relay.transcript)


class AsyncPeer(asyncore.dispatcher_with_send):
    BUFFER_SIZE = 4096
    other_peer = None

    def __init__(self, relay, sock = None, connect = None):
        self.relay = relay
        if sock:
            asyncore.dispatcher_with_send.__init__(self, sock)
        else:
            asyncore.dispatcher_with_send.__init__(self)
            self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
        
        if connect:
            self.connect(connect)

    def handle_error(self, *n):
        self.relay.close()

    def handle_read(self):
        data = self.recv(self.BUFFER_SIZE)
        self.relay.forward(self, data)

    def handle_close(self):
        self.relay.close()

class RelayListener(asyncore.dispatcher_with_send):

    def __init__(self, l3_proto, l4_proto, listen_port):
        asyncore.dispatcher_with_send.__init__(self)
        self.l3_proto = l3_proto
        self.l4_proto = l4_proto
        self.listen_port = listen_port
        
        self.create_socket(self.l3_proto, self.l4_proto)
        self.set_reuse_addr()
        self.bind(("", self.listen_port))
        self.listen(5)

    def handle_accept(self):
        client_sock, (client_ip, client_port) = self.accept()
        
        index = (self.l3_proto, self.l4_proto, self.listen_port, client_ip)
        print index
        print relays
        if index in relays:
            print client_ip
            relays[index].client_accepted(client_sock)
        else:
            client_sock.close()

class Relay:

    def __init__(self, l3_proto, l4_proto, relay_port, client_ip, dest_addr):
        self.l3_proto = l3_proto
        self.l4_proto = l4_proto
        self.relay_port = relay_port
        self.client_ip = client_ip
        self.dest_addr = dest_addr
        self.transcript = ""
        self.lock = threading.Lock()
        self.dest_sock = None
        self.client_sock = None
        self.closed = False
        
    def client_accepted(self, client_sock):
        self.lock.acquire()
        self.client_sock = AsyncPeer(self, sock=client_sock)
        self.dest_sock = AsyncPeer(self, connect=self.dest_addr)
        self.lock.release()
    
    def forward(self, source, data):
        self.lock.acquire()
        if len(data) > 0:
            if source == self.client_sock:
                self.dest_sock.sendall(data)
                self.transcript += data
            elif source == self.dest_sock:
                self.client_sock.sendall(data)
                self.transcript += data
        self.lock.release()
    
    def close(self):
        self.lock.acquire()
        self.closed = True
        if self.dest_sock:
            self.dest_sock.close()
            self.dest_sock = None
        if self.client_sock:
            self.client_sock.close()
            self.client_sock = None
        self.lock.release()
        print self.transcript
        
        relay_list_lock.acquire()
        del relays[(self.l3_proto, self.l4_proto, self.relay_port,
                    self.client_ip)]
        relay_list_lock.release()


if __name__ == "__main__":
    
    #logging.basicConfig(filename=logfile, level=logging.DEBUG,
    #                    format="%(asctime)s [%(levelname)s] %(message)s")
    logging.basicConfig(level=logging.DEBUG,
                        format="%(asctime)s [%(levelname)s] %(message)s")

    # setup relay listeners
    l3_protos = [socket.AF_INET]
    l4_protos = [socket.SOCK_STREAM]
    ports = [80,443]
    listeners = {}
    for l3 in l3_protos:
        for l4 in l4_protos:
            for port in ports:
                listeners[(l3, l4, port)] = RelayListener(l3, l4, port)

    select_thread = threading.Thread(target=asyncore.loop)
    select_thread.daemon = True
    select_thread.start()

    # command server
    command_server = ThreadedTCPServer((server_ip, server_port), CommandServerRequestHandler)
    logging.info("serving at " + str(command_server.server_address))
    
    try:
        command_server.serve_forever()
    except KeyboardInterrupt:
        print "\nCtrl-c pressed ..."
        # do some cleanup here
