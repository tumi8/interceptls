package de.tum.in.net.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import de.tum.in.net.TestResult;
import de.tum.in.net.model.AnalysisResult;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.Diff;
import de.tum.in.net.model.HandshakeParser;
import de.tum.in.net.model.TLSHandshake;
import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TlsMessageType;

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
        } catch (IllegalStateException e) {
          log.error("Unexpected error in handshake bytes", e);
          analysisResult = AnalysisResult.error("Unexpected error in handshake bytes");
        } catch (Exception e) {
          log.error("Unknwon error", e);
          analysisResult = AnalysisResult.error("Unexpected error");
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

    if (rec_client.equals(sent_server) && sent_client.equals(rec_server)) {
      return AnalysisResult.noInterception();
    }

    System.out.println(rec_server);
    System.out.println(sent_client);

    Type listType = new TypeToken<List<TLSHandshake>>() {}.getType();

    List<TLSHandshake> messages_rec = new Gson().fromJson(rec_server, listType);
    List<TLSHandshake> messages_sent = new Gson().fromJson(sent_client, listType);


    TLSHandshake clientHello_rec = messages_sent.stream()
        .filter(msg -> TlsMessageType.ClientHello.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find client hello."));

    List<Diff> clientHelloDiffs = clientHello_rec.createDiff(messages_rec);

    System.err.println(clientHelloDiffs);

    return AnalysisResult.intercepted(clientHelloDiffs);

  }
}
