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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

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
    this(s.getInputStream(), s.getOutputStream());
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

