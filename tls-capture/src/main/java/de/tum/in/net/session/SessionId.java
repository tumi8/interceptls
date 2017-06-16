package de.tum.in.net.session;

/**
 * TestSessionId is defined as [A-Za-Z0-9]+
 * 
 * @author johannes
 */
public class SessionId {

  private final String id;

  public SessionId(final String id) {
    this.id = id;
    if (!id.matches("[A-Za-z0-9]+")) {
      throw new IllegalArgumentException("id does not match the regex");
    }
  }

  public String getID() {
    return this.id;
  }

  @Override
  public String toString() {
    return id;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SessionId) {
      return this.id.equals(((SessionId) o).id);
    }
    return false;
  }

}
