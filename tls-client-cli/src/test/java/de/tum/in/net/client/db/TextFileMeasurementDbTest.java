package de.tum.in.net.client.db;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class TextFileMeasurementDbTest {

  private MeasurementDb db;

  @Before
  public void emptyBeforeTest() throws Exception {
    db = TextFileMeasurementDb.getInstance();
    db.deleteAll();
    assertEquals(0, db.readAll().size());
  }


  @Test
  public void canAppendResult() throws Exception {
    final MeasurementDb db = TextFileMeasurementDb.getInstance();

    assertEquals(0, db.readAll().size());

    final NetworkId network = new NetworkId();
    final List<TlsClientServerResult> connections = new ArrayList<>();
    final TlsTestResult result = new TlsTestResult(network, connections);
    db.append(result);

    assertEquals(1, db.readAll().size());

    db.append(result);
    assertEquals(2, db.readAll().size());

    db.deleteAll();
    assertEquals(0, db.readAll().size());
  }

}
