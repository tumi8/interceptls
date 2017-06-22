package de.tum.in.net;

import java.io.IOException;
import java.net.URI;

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
  // Base URI the Grizzly HTTP server will listen on
  public static final String BASE_URI = "http://localhost:3000/";

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * 
   * @param class1
   * 
   * @return Grizzly HTTP server.
   * @throws IOException
   */
  public static HttpServer startServer() throws IOException {
    return startServer(new MapDBDatabaseService(true));
  }

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * 
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer(DatabaseService dbService) {
    // create a resource config that scans for JAX-RS resources and providers
    // in de.tum.in.net package
    final ResourceConfig rc = new ResourceConfig().register(new MyApplicationBinder(dbService))
        .packages(true, "de.tum.in.net.resource").register(LoggingFilter.class);


    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }

  /**
   * Main method.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    final HttpServer server = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
        + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    System.in.read();
    server.stop();
  }
}

