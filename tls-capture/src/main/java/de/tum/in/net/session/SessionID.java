package de.tum.in.net.session;

/**
 * SessionId is defined as a long.
 * 
 * @author johannes
 */
public class SessionID {

  private final long id;

  public SessionID(long id) {
    this.id = id;
  }

  public long getID() {
    return this.id;
  }

  @Override
  public String toString() {
    return Long.toString(id);
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof SessionID) {
      return this.id == ((SessionID) o).id;
    }
    return false;
  }

}
