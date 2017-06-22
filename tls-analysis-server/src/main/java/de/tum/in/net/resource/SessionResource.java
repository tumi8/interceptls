package de.tum.in.net.resource;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.session.SessionID;

/**
 * Session resource.
 */
@Path("session")
public class SessionResource {

  @Inject
  private DatabaseService db;

  /**
   * Return a new session ID.
   *
   * @return session id
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public String getIt() {
    return new Gson().toJson(new SessionID(db.newSessionID()));
  }
}
