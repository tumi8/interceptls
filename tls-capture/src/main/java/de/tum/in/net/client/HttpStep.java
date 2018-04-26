package de.tum.in.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.tum.in.net.model.Step;
import de.tum.in.net.model.TestContext;
import de.tum.in.net.model.TlsConstants;
import de.tum.in.net.model.TlsResult;

public class HttpStep implements Step {

  @Override
  public void process(HostAndPort target, TestContext ctx) throws IOException {

    String request = TlsConstants.REQUEST_LINE + "Host: " + target.getHost() + "\r\n\r\n";
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
