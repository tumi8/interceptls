package de.tum.in.net.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestContext {

  private InputStream in;
  private OutputStream out;
  private Socket socket;
  private TlsResult tlsServerResult;
  private TlsResult tlsClientResult;

  public TestContext(Socket s) throws IOException {
    this.socket = s;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
  }

  public void setInputStream(InputStream in) {
    this.in = in;
  }

  public InputStream getInputStream() {
    return this.in;
  }

  public void setOutputStream(OutputStream out) {
    this.out = out;
  }

  public OutputStream getOutputStream() {
    return this.out;
  }

  public void setClientResult(TlsResult tlsClientResult) {
    this.tlsClientResult = tlsClientResult;
  }

  public void setServerResult(TlsResult tlsServerResult) {
    this.tlsServerResult = tlsServerResult;
  }

  public TlsResult getServerResult() {
    return this.tlsServerResult;
  }

  public TlsResult getClientResult() {
    return this.tlsClientResult;
  }

  public Socket getSocket() {
    return this.socket;
  }


}
