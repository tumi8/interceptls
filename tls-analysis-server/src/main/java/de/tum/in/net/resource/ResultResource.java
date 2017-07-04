package de.tum.in.net.resource;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.tum.in.net.TestResult;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * ResultResource.
 */
@Path("result/{testID}")
public class ResultResource {
  private static final Logger log = LogManager.getLogger();

  @Inject
  private DatabaseService db;

  /**
   * Upload the result of a captured handshake.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUploadedResult(@PathParam("testID") String testID) {
    TestID id = TestID.parse(testID);

    try {
      TestResult result = db.getResult(id);
      return Response.ok(new Gson().toJson(result)).build();
    } catch (NoSuchElementException e) {
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  /**
   * Upload the result of a captured handshake.
   */
  @PUT
  public void uploadResult(@PathParam("testID") String testID, String body) {
    ScenarioResult result = new Gson().fromJson(body, ScenarioResult.class);
    log.info("Received {} result: {}", result.getNode(), testID);

    if (!TestID.isTestID(testID)) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }

    TestID id = TestID.parse(testID);
    try {
      db.addResult(id, result);
      log.debug("Successfully uploaded.");
    } catch (Exception e) {
      log.error("Illegal", e);
      throw new WebApplicationException(Status.BAD_REQUEST);
    }


  }
}
