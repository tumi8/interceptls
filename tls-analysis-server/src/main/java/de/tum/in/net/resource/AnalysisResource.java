package de.tum.in.net.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.diff.JsonDiff;
import com.google.gson.Gson;

import de.tum.in.net.TestResult;
import de.tum.in.net.model.AnalysisResult;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.HandshakeParser;
import de.tum.in.net.model.TestID;

/**
 * Session resource.
 */
@Path("analysis/{testID}")
public class AnalysisResource {

  private static final Logger log = LogManager.getLogger();

  @Inject
  private DatabaseService db;

  @Inject
  private HandshakeParser parser;

  /**
   * Return a new session ID.
   *
   * @return session id
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getAnalysis(@PathParam("testID") String testID) {
    if (!TestID.isTestID(testID)) {
      throw new WebApplicationException(Status.BAD_REQUEST);
    }

    TestResult result = db.getResult(TestID.parse(testID));

    AnalysisResult analysisResult;
    if (result.hasClientResult()) {
      if (result.hasServerResult()) {
        // all data available
        try {
          analysisResult = createDiff(result);
        } catch (IOException e) {
          log.error("Could not parse handshake", e);
          analysisResult = AnalysisResult.error("Error parsing handshake");
        }
      } else {
        // server data missing
        analysisResult = AnalysisResult.noServerResult();
      }
    } else {
      if (result.hasServerResult()) {
        // client data missing
        analysisResult = AnalysisResult.noClientResult();
      } else {
        // no client and no server data available
        analysisResult = AnalysisResult.noClientNoServerResult();
      }
    }

    return new Gson().toJson(analysisResult);
  }

  private AnalysisResult createDiff(TestResult result) throws IOException {
    String rec_client = parser.parse(result.getClientResult().getReceivedBytes());
    String sent_client = parser.parse(result.getClientResult().getSentBytes());

    String rec_server = parser.parse(result.getServerResult().getReceivedBytes());
    String sent_server = parser.parse(result.getServerResult().getSentBytes());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode diff_rec = JsonDiff.asJson(mapper.readTree(rec_client), mapper.readTree(sent_server));
    JsonNode diff_sent = JsonDiff.asJson(mapper.readTree(sent_client), mapper.readTree(rec_server));


    ObjectNode root = mapper.createObjectNode();
    root.put("diff_sent", diff_sent);
    root.put("diff_rec", diff_rec);

    if (diff_sent.size() == 0 && diff_rec.size() == 0) {
      return AnalysisResult.noInterception();
    } else {
      return AnalysisResult.intercepted(diff_sent.toString(), diff_rec.toString());
    }

  }
}
