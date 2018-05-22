/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.util;

import org.junit.Test;

import de.tum.in.net.util.Tap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
