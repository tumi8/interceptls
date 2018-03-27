package de.tum.in.net.services;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.SessionID;

public class MemoryOnlyDatabaseService implements DatabaseService {

  private ConcurrentMap<SessionID, TlsTestResult> results = new ConcurrentHashMap<>();

  @Override
  public SessionID addTestResult(TlsTestResult result) {
    SessionID key;
    do {
      key = new SessionID(new Random().nextInt());
    } while (results.containsKey(key));

    results.put(key, result);
    return key;

  }

  @Override
  public TlsTestResult getResult(SessionID id) {
    return results.get(id);
  }

  @Override
  public void close() throws IOException {
    // nothing to do here
  }

}
