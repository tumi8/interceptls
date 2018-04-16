package de.tum.in.net.services;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import de.tum.in.net.analysis.ProbedHostAnalysis;
import de.tum.in.net.analysis.TlsMessage;
import de.tum.in.net.analysis.TlsMessageDiff;
import de.tum.in.net.analysis.TlsMessageType;
import de.tum.in.net.model.HandshakeAnalyser;
import de.tum.in.net.model.HandshakeParser;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class TlsHandshakeAnalyser implements HandshakeAnalyser {

  @Inject
  private HandshakeParser parser;

  @Override
  public List<ProbedHostAnalysis> analyse(TlsTestResult result) throws IOException {

    List<ProbedHostAnalysis> analysisResult = new ArrayList<>();
    for (TlsClientServerResult r : result.getClientServerResults()) {
      if (r.isSuccess()) {
        analysisResult.add(createDiff(r));
      }
    }

    return analysisResult;
  }


  private ProbedHostAnalysis createDiff(TlsClientServerResult result) throws IOException {
    String rec_client = parser.parse(result.getClientResult().getReceivedBytes());
    String sent_client = parser.parse(result.getClientResult().getSentBytes());

    String rec_server = parser.parse(result.getServerResult().getReceivedBytes());
    String sent_server = parser.parse(result.getServerResult().getSentBytes());

    if (rec_client.equals(sent_server) && sent_client.equals(rec_server)) {
      return ProbedHostAnalysis.noInterception(result.getHostAndPort().toString());
    }

    Type listType = new TypeToken<List<TlsMessage>>() {}.getType();

    // client hello diff
    List<TlsMessage> messages_rec = new Gson().fromJson(rec_server, listType);
    List<TlsMessage> messages_sent = new Gson().fromJson(sent_client, listType);

    TlsMessage clientHello_rec = messages_sent.stream()
        .filter(msg -> TlsMessageType.ClientHello.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find client hello."));

    TlsMessageDiff clientHello = clientHello_rec.createDiff(messages_rec);


    // server hello and certificate diff
    messages_rec = new Gson().fromJson(rec_client, listType);
    messages_sent = new Gson().fromJson(sent_server, listType);

    TlsMessage serverHello_rec = messages_rec.stream()
        .filter(msg -> TlsMessageType.ServerHello.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find server hello."));

    TlsMessageDiff serverHello = serverHello_rec.createDiff(messages_sent);

    TlsMessage certificate_rec = messages_rec.stream()
        .filter(msg -> TlsMessageType.Certificate.equals(msg.getType())).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find certificate."));

    TlsMessageDiff certificate = certificate_rec.createDiff(messages_sent);

    return ProbedHostAnalysis.intercepted(result.getHostAndPort().toString(), clientHello,
        serverHello, certificate);

  }

}
