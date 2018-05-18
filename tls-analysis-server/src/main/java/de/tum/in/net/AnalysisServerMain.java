/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net;

import java.io.IOException;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.tum.in.net.filter.LoggingFilter;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.services.PostgreSQLDatabaseService;

/**
 * Main class.
 *
 */
public class AnalysisServerMain {


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
    return startServer(conf,
        new PostgreSQLDatabaseService(conf.getDbUser(), conf.getDbPassword(), conf.getDbTarget()));
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
    // .register(CORSFilter.class);


    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    SSLContextConfigurator sslContext = new SSLContextConfigurator();
    sslContext.setKeyStoreFile(conf.getKeyStore());
    sslContext.setKeyStorePass(conf.getKeyStorePassword());
    sslContext.setKeyStoreType("PKCS12");
    SSLEngineConfigurator engineConf = new SSLEngineConfigurator(sslContext);
    engineConf.setClientMode(false);
    engineConf.setNeedClientAuth(false);

    return GrizzlyHttpServerFactory.createHttpServer(conf.getURI(), rc, true, engineConf);
  }

  /**
   * Main method.
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    startServer();
  }
}

