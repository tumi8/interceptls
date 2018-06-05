package de.tum.in.net.client.db;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import de.tum.in.net.model.TlsTestResult;

public class TextFileMeasurementDb implements MeasurementDb {

  private static final String DB_FILENAME = "interceptls.db";
  private static final TextFileMeasurementDb SINGLETON = new TextFileMeasurementDb();

  private TextFileMeasurementDb() {

  }

  public static TextFileMeasurementDb getInstance() {
    return SINGLETON;
  }

  @Override
  public void append(final TlsTestResult result) throws IOException {
    final String json = new Gson().toJson(result) + System.lineSeparator();
    Files.write(Paths.get(DB_FILENAME), json.getBytes(Charset.defaultCharset()),
        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
  }

  @Override
  public List<TlsTestResult> readAll() throws IOException {
    final Path p = Paths.get(DB_FILENAME);

    final List<TlsTestResult> results = new ArrayList<>();
    if (Files.exists(p)) {
      final List<String> lines = Files.readAllLines(p);

      final Gson gson = new Gson();
      for (final String line : lines) {
        final TlsTestResult result = gson.fromJson(line, TlsTestResult.class);
        results.add(result);
      }

    }

    return results;
  }

  @Override
  public void deleteAll() throws IOException {
    Files.deleteIfExists(Paths.get(DB_FILENAME));
  }
}
