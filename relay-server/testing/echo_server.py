import socket

host = ''
port = 50000
backlog = 5
size = 1024
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host,port))
s.listen(backlog)
while 1:
    client, address = s.accept()
    for i in range(5):
        data = client.recv(size)
        if data:
            client.send(data.upper())
    client.close() 
