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
package de.tum.in.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.tum.in.net.model.Step;
import de.tum.in.net.model.TestContext;
import de.tum.in.net.model.TlsConstants;
import de.tum.in.net.model.TlsResult;

public class FakeHostHttpStep implements Step {

  private final String host;

  public FakeHostHttpStep(String host) {
    this.host = Objects.requireNonNull(host);
  }

  @Override
  public void process(HostAndPort target, TestContext ctx) throws IOException {

    String request = TlsConstants.REQUEST_LINE + "Host: " + host + "\r\n\r\n";
    ctx.getOutputStream().write(request.getBytes());


    InputStream in = ctx.getInputStream();
    BufferedReader r = new BufferedReader(new InputStreamReader(in));

    // basic http parser, ignores all header field and only looks for the content which comes after
    // an empty line
    String line;
    do {
      line = r.readLine();
    } while (!line.isEmpty());
    String content = r.readLine();

    try {
      TlsResult serverResult = new Gson().fromJson(content, TlsResult.class);
      ctx.setServerResult(serverResult);
    } catch (JsonSyntaxException e) {
      throw new IOException("Unexpected response from server", e);
    }


  }

}
