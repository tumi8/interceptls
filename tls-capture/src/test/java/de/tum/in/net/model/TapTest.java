package de.tum.in.net.model;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.tum.in.net.model.Tap;

import static org.junit.Assert.assertEquals;

public class TapTest {

  @Test
  public void tapSavesBytes() throws IOException {
    final String inputString = "hello world";
    final String outputString = "output";
    final byte[] input = inputString.getBytes();
    final ByteArrayInputStream in = new ByteArrayInputStream(input);

    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    final Tap tap = new Tap(in, out);

    tap.getIn().read(new byte[inputString.length()]);
    tap.getOut().write(outputString.getBytes());

    assertEquals(inputString, new String(tap.getInputBytes()));
    assertEquals(outputString, new String(tap.getOutputytes()));

  }
}
