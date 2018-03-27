package de.tum.in.net.model;

import java.io.IOException;

import de.tum.in.net.client.HostAndPort;

public interface Step {

  void process(HostAndPort target, TestContext ctx) throws IOException, TlsAbortException;

}
