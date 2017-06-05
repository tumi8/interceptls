package de.tum.in.net;

import java.io.IOException;

import de.tum.in.net.model.TestSession;

public interface TestSessionProvider {

  public TestSession newTestSession() throws IOException;

}
