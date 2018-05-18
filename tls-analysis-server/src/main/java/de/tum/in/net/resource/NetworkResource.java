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
package de.tum.in.net.resource;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.NetworkId;

@Path("network")
public class NetworkResource {

  private static final Logger log = LogManager.getLogger();

  @Inject
  private DatabaseService db;

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public String getAnalysis(String body) {
    NetworkId networkId = new Gson().fromJson(body, NetworkId.class);

    try {
      NetworkStats stats = db.getNetworkStats(networkId);
      return new Gson().toJson(stats);
    } catch (SQLException e) {
      log.error("Could not determine network stats.", e);
      throw new InternalServerErrorException();
    }
  }

}
