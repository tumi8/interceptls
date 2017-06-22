package de.tum.in.net.model;

import java.io.IOException;

public interface HandshakeParser {

  String parse(String base64) throws IOException;

}
