package de.tum.in.net;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TapTest {

    @Test
    public void tapSavesBytes() throws IOException {
        String inputString = "hello world";
        String outputString = "output";
        byte[] input = inputString.getBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(input);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Tap tap = new Tap(in, out);

        tap.getIn().read(new byte[inputString.length()]);
        tap.getOut().write(outputString.getBytes());

        assertEquals(inputString, new String(tap.getInputBytes()));
        assertEquals(outputString, new String(tap.getOutputytes()));

    }
}
