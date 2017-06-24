package de.tum.in.net;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.tum.in.net.filter.LoggingFilter;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.services.MapDBDatabaseService;

/**
 * Main class.
 *
 */
public class Main {


  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * 
   * @param class1
   * 
   * @return Grizzly HTTP server.
   * @throws IOException
   */
  public static HttpServer startServer() throws IOException {
    AnalysisServerConfig conf = AnalysisServerConfig.loadDefault();
    return startServer(conf, new MapDBDatabaseService(true));
  }

  public static HttpServer startServer(DatabaseService dbService) throws IOException {
    AnalysisServerConfig conf = AnalysisServerConfig.loadDefault();
    return startServer(conf, dbService);
  }

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * 
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer(AnalysisServerConfig conf, DatabaseService dbService) {
    // create a resource config that scans for JAX-RS resources and providers
    // in de.tum.in.net package
    final ResourceConfig rc = new ResourceConfig().register(new MyApplicationBinder(dbService))
        .packages(true, "de.tum.in.net.resource").register(LoggingFilter.class);


    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    URI uri = UriBuilder.fromUri("http://localhost").port(conf.getPort()).build();
    return GrizzlyHttpServerFactory.createHttpServer(uri, rc);
  }

  /**
   * Main method.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {


    final HttpServer server = startServer();
    System.out.println("Jersey app started");
    System.in.read();
    server.stop();
  }
}

