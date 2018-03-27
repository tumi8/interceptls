package de.tum.in.net.resource;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.SessionID;

/**
 * ResultResource.
 */
@Path("result")
public class ResultResource {
  private static final Logger log = LogManager.getLogger();

  @Inject
  private DatabaseService db;

  /**
   * Upload the result of a complete TLS test.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public String uploadResult(String body) {
    TlsTestResult result = new Gson().fromJson(body, TlsTestResult.class);
    log.info("Received new test result");

    try {
      SessionID id = db.addTestResult(result);
      log.debug("Successfully uploaded.");
      return new Gson().toJson(id);
    } catch (Exception e) {
      log.error("Exception while uploading result.", e);
      throw new WebApplicationException(Status.BAD_REQUEST);
    }
  }
}
