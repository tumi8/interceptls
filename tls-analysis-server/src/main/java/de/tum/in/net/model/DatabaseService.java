package de.tum.in.net.model;

import java.io.Closeable;
import java.sql.SQLException;

import de.tum.in.net.session.SessionID;

public interface DatabaseService extends Closeable {

  SessionID addTestResult(TlsTestResult result) throws SQLException;

  TlsTestResult getResult(SessionID id);
}
