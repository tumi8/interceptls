package de.tum.in.net.client.db;

import java.io.IOException;
import java.util.List;

import de.tum.in.net.model.TlsTestResult;

public interface MeasurementDb {

  void append(TlsTestResult result) throws IOException;

  List<TlsTestResult> readAll() throws IOException;

  void deleteAll() throws IOException;

}
