package de.tum.in.net.model;

import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by johannes on 22.03.17.
 */

public class Tap {

  private final ByteArrayOutputStream tapIn = new ByteArrayOutputStream();
  private final ByteArrayOutputStream tapOut = new ByteArrayOutputStream();

  private final InputStream in;
  private final OutputStream out;

  public Tap(final InputStream input, final OutputStream output) {
    this.in = new TeeInputStream(input, tapIn);
    this.out = new TeeOutputStream(output, tapOut);
  }

  public Tap(final Socket s) throws IOException {
    this.in = s.getInputStream();
    this.out = s.getOutputStream();
  }

  public InputStream getIn() {
    return in;
  }

  public OutputStream getOut() {
    return out;
  }

  public byte[] getInputBytes() {
    return tapIn.toByteArray();
  }

  public byte[] getOutputytes() {
    return tapOut.toByteArray();
  }
}

