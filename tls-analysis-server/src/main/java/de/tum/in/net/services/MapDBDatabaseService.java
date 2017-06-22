package de.tum.in.net.services;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;
import org.mapdb.HTreeMap.KeySet;
import org.mapdb.Serializer;

import de.tum.in.net.TestResult;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.ScenarioResult;

public class MapDBDatabaseService implements DatabaseService, Closeable {

  private final Random random = new SecureRandom();
  private DB db;

  private ConcurrentMap<String, ScenarioResult> clientResults;
  private ConcurrentMap<String, ScenarioResult> serverResults;
  private KeySet<String> sessions;

  public MapDBDatabaseService(boolean useFile) throws IOException {
    Maker maker = null;
    if (useFile) {
      maker = DBMaker.fileDB("file.db").fileMmapEnable();
    } else {
      maker = DBMaker.memoryDB();
    }

    this.db = maker.transactionEnable().make();

    this.sessions = db.hashSet("sessions", Serializer.STRING).createOrOpen();

    this.clientResults =
        db.hashMap("clientResults", Serializer.STRING, Serializer.JAVA).createOrOpen();
    this.serverResults =
        db.hashMap("serverResults", Serializer.STRING, Serializer.JAVA).createOrOpen();

  }

  @Override
  public String newSessionID() {
    String key;
    do {
      key = new BigInteger(130, random).toString(32);
    } while (sessions.contains(key));

    return key;
  }

  @Override
  public void addResult(TestID id, ScenarioResult result) {
    // if (!sessions.contains(id.getSessionID().toString())) {
    // throw new IllegalStateException("Session id does not exist.");
    // }

    switch (result.getNode()) {
      case SERVER:
        serverResults.put(id.toString(), result);
        break;
      case CLIENT:
        clientResults.put(id.toString(), result);
        break;
      default:
        throw new IllegalStateException("Unknown node type");
    }

  }

  @Override
  public TestResult getResult(TestID id) {
    ScenarioResult client = clientResults.get(id.toString());
    ScenarioResult server = serverResults.get(id.toString());
    return new TestResult(client, server);
  }

  @Override
  public void close() throws IOException {
    db.close();
  }

}
