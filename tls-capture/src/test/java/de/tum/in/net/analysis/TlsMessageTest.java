package de.tum.in.net.analysis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TlsMessageTest {

  @Test
  public void differentVersions() {
    String json =
        "{\"ciphers\":[49195],\"compressions\":[0],\"type\":\"ClientHello\",\"version\":768}";
    TlsMessage clientHello = new Gson().fromJson(json, TlsMessage.class);
    assertEquals("SSLv3", clientHello.getVersion());

    json = "{\"ciphers\":[49195],\"compressions\":[0],\"type\":\"ClientHello\",\"version\":769}";
    clientHello = new Gson().fromJson(json, TlsMessage.class);
    assertEquals("TLSv1.0", clientHello.getVersion());

    json = "{\"ciphers\":[49195],\"compressions\":[0],\"type\":\"ClientHello\",\"version\":770}";
    clientHello = new Gson().fromJson(json, TlsMessage.class);
    assertEquals("TLSv1.1", clientHello.getVersion());

    json = "{\"ciphers\":[49195],\"compressions\":[0],\"type\":\"ClientHello\",\"version\":771}";
    clientHello = new Gson().fromJson(json, TlsMessage.class);
    assertEquals("TLSv1.2", clientHello.getVersion());

  }


  @Test
  public void clientHelloParsing() {
    // output from tls-json-parser
    String clientMessages =
        "[{\"ciphers\":[49195,49187,49161,49199,49191,49171,162,64,50,158,103,51,156,60,47,255],\"compressions\":[0],\"ext\":{\"ecPointFormats\":[0,1,2],\"ellipticCurves\":[23,24],\"signatureAlgorithms\":[[2,1],[3,1],[4,1],[5,1],[6,1],[2,2],[3,2],[4,2],[5,2],[6,2],[2,3],[3,3],[4,3],[5,3],[6,3]]},\"type\":\"ClientHello\",\"version\":771},{\"type\":\"ClientKeyExchange\"},{\"type\":\"ChangeCipherSpec\"}]";


    Type listType = new TypeToken<List<TlsMessage>>() {}.getType();
    // client hello diff
    List<TlsMessage> messages_rec = new Gson().fromJson(clientMessages, listType);

    assertEquals(3, messages_rec.size());

    assertEquals(TlsMessageType.ClientHello, messages_rec.get(0).getType());
    assertEquals(TlsMessageType.ClientKeyExchange, messages_rec.get(1).getType());
    assertEquals(TlsMessageType.ChangeCipherSpec, messages_rec.get(2).getType());

    TlsMessage clientHello = messages_rec.get(0);
    assertEquals("TLSv1.2", clientHello.getVersion());
    assertArrayEquals(new int[] {49195, 49187, 49161, 49199, 49191, 49171, 162, 64, 50, 158, 103,
        51, 156, 60, 47, 255}, clientHello.getCiphers());
    assertArrayEquals(new int[] {0}, clientHello.getCompressions());
    assertArrayEquals(new int[] {0, 1, 2}, clientHello.getExtensions().getEcPointFormats());
    assertArrayEquals(new int[] {23, 24}, clientHello.getExtensions().getEllipticCurves());

    List<List<Integer>> sigAlgs = clientHello.getExtensions().getSignatureAlgorithms();
    assertEquals(15, sigAlgs.size());

    List<SNI> sni = clientHello.getExtensions().getSni();
    assertNull(sni);
  }

  @Test
  public void serverHelloParsing() {
    // output from tls-json-parser
    String serverMessages =
        "[{\"cipher\":49199,\"compression\":0,\"ext\":{\"denegotiationInfo\":[],\"ecPointFormats\":[0]},\"type\":\"ServerHello\",\"version\":771},{\"cert_chain\":[\"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\"],\"type\":\"Certificate\"},{\"type\":\"ServerKeyExchange\"},{\"type\":\"ServerDone\"},{\"type\":\"ChangeCipherSpec\"}]";


    Type listType = new TypeToken<List<TlsMessage>>() {}.getType();
    List<TlsMessage> messages_rec = new Gson().fromJson(serverMessages, listType);

    assertEquals(5, messages_rec.size());

    assertEquals(TlsMessageType.ServerHello, messages_rec.get(0).getType());
    assertEquals(TlsMessageType.Certificate, messages_rec.get(1).getType());
    assertEquals(TlsMessageType.ServerKeyExchange, messages_rec.get(2).getType());
    assertEquals(TlsMessageType.ServerDone, messages_rec.get(3).getType());
    assertEquals(TlsMessageType.ChangeCipherSpec, messages_rec.get(4).getType());


    TlsMessage serverHello = messages_rec.get(0);
    assertEquals("TLSv1.2", serverHello.getVersion());
    assertEquals(49199, serverHello.getCipher());
    assertEquals(0, serverHello.getCompression());

  }

  @Test
  public void clientHelloDiff() {
    // output from tls-json-parser
    String clientMessage =
        "[{\"ciphers\":[64,50,158],\"compressions\":[0],\"ext\":{\"ecPointFormats\":[0,1,2],\"ellipticCurves\":[23,24],\"signatureAlgorithms\":[[2,1],[3,1]]},\"type\":\"ClientHello\",\"version\":771}]";
    String clientMessage2 =
        "[{\"ciphers\":[64,158],\"compressions\":[0],\"ext\":{\"ecPointFormats\":[0,1,2],\"ellipticCurves\":[23,24,25],\"signatureAlgorithms\":[[2,1]]},\"type\":\"ClientHello\",\"version\":770}]";


    Type listType = new TypeToken<List<TlsMessage>>() {}.getType();
    // client hello diff
    List<TlsMessage> messages_rec = new Gson().fromJson(clientMessage, listType);
    List<TlsMessage> messages_rec2 = new Gson().fromJson(clientMessage2, listType);

    assertEquals(1, messages_rec.size());
    assertEquals(TlsMessageType.ClientHello, messages_rec.get(0).getType());

    assertEquals(1, messages_rec2.size());
    assertEquals(TlsMessageType.ClientHello, messages_rec2.get(0).getType());

    TlsMessage clientHello = messages_rec.get(0);

    TlsMessageDiff msgDiff = clientHello.createDiff(messages_rec2);

    Diff version = msgDiff.getVersionDiff();
    assertTrue(version.differs());
    assertEquals("TLSv1.2", version.getExpected());
    assertEquals("TLSv1.1", version.getActual());
    assertEquals("expected: TLSv1.2, but was: TLSv1.1", version.toString());

    Diff ciphers = msgDiff.getCiphersDiff();
    assertEquals("[64, 50, 158]", ciphers.getExpected());
    assertEquals("[64, 158]", ciphers.getActual());

    Diff compression = msgDiff.getCompressionDiff();
    assertFalse(compression.differs());
    assertEquals("NoDiff([0])", compression.toString());
    assertEquals(compression.getExpected(), compression.getActual());

  }

}
