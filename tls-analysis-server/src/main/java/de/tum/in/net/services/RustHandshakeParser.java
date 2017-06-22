package de.tum.in.net.services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.net.model.HandshakeParser;

public class RustHandshakeParser implements HandshakeParser {

  private static final Logger log = LogManager.getLogger();

  public String parse(String base64) throws IOException {

    ProcessBuilder builder = new ProcessBuilder("lib/tls-json-parser", base64);
    Process p = builder.start();

    try {
      p.waitFor(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      p.destroy();
      throw new IOException("Process did not finish", e);
    }

    String stdout = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
    String stderr = IOUtils.toString(p.getErrorStream(), Charset.defaultCharset());
    if (stderr != null && !stderr.isEmpty()) {
      log.error("Rust-parser stderr:" + stderr);
    }

    if (p.exitValue() != 0) {
      throw new IOException("Process did not terminate normally.");
    }

    return stdout;

  }

}
