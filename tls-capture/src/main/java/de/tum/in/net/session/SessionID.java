package de.tum.in.net.session;

/**
 * TestSessionId is defined as [A-Za-Z0-9]+
 * 
 * @author johannes
 */
public class SessionID {

  private final String sessionID;

  public SessionID(final String id) {
    this.sessionID = id;
    if (!id.matches("[A-Za-z0-9]+")) {
      throw new IllegalArgumentException("id does not match the regex");
    }
  }

  public String getID() {
    return this.sessionID;
  }

  @Override
  public String toString() {
    return sessionID;
  }

  @Override
  public int hashCode() {
    return sessionID.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SessionID) {
      return this.sessionID.equals(((SessionID) o).sessionID);
    }
    return false;
  }

}
