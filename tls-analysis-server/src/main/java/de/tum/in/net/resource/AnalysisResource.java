package de.tum.in.net.resource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.analysis.TLSHandshake;
import de.tum.in.net.analysis.TlsMessageDiff;
import de.tum.in.net.analysis.TlsMessageType;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.HandshakeParser;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.SessionID;

/**
 * Session resource.
 */
@Path("analysis/{sessionID}")
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
  public String getAnalysis(@PathParam("sessionID") String sessionID) {

    SessionID id = new SessionID(Long.parseLong(sessionID));
    TlsTestResult result = db.getResult(id);
    if (result == null) {
      throw new NotFoundException();
    }

    List<AnalysisResult> analysisResult = new ArrayList<>();


    try {
      for (TlsClientServerResult r : result.getClientServerResults()) {
        analysisResult.add(createDiff(r));
      }

    } catch (IOException e) {
      log.error("Could not parse handshake", e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (IllegalStateException e) {
      log.error("Unexpected error in handshake bytes", e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      log.error("Unknwon exception", e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }


    return new Gson().toJson(analysisResult);
  }

  private AnalysisResult createDiff(TlsClientServerResult result) throws IOException {
    String rec_client = parser.parse(result.getClientResult().getReceivedBytes());
    String sent_client = parser.parse(result.getClientResult().getSentBytes());

    String rec_server = parser.parse(result.getServerResult().getReceivedBytes());
    String sent_server = parser.parse(result.getServerResult().getSentBytes());

    if (rec_client.equals(sent_server) && sent_client.equals(rec_server)) {
      return AnalysisResult.noInterception(result.getHostAndPort().toString());
    }

    System.out.println(rec_server);
    System.out.println(sent_client);
    System.out.println(sent_server);
    System.out.println(rec_client);

    Type listType = new TypeToken<List<TLSHandshake>>() {}.getType();

    // client hello diff
    List<TLSHandshake> messages_rec = new Gson().fromJson(rec_server, listType);
    List<TLSHandshake> messages_sent = new Gson().fromJson(sent_client, listType);

    TLSHandshake clientHello_rec = messages_sent.stream()
        .filter(msg -> TlsMessageType.ClientHello.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find client hello."));

    TlsMessageDiff clientHello = clientHello_rec.createDiff(messages_rec);


    // server hello and certificate diff
    messages_rec = new Gson().fromJson(rec_client, listType);
    messages_sent = new Gson().fromJson(sent_server, listType);

    TLSHandshake serverHello_rec = messages_rec.stream()
        .filter(msg -> TlsMessageType.ServerHello.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find server hello."));

    TlsMessageDiff serverHello = serverHello_rec.createDiff(messages_sent);

    TLSHandshake certificate_rec = messages_rec.stream()
        .filter(msg -> TlsMessageType.Certificate.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find certificate."));

    TlsMessageDiff certificate = certificate_rec.createDiff(messages_sent);

    return AnalysisResult.intercepted(result.getHostAndPort().toString(), clientHello, serverHello,
        certificate);

  }
}
