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

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.analysis.ProbedHostAnalysis;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.HandshakeAnalyser;
import de.tum.in.net.model.TlsTestResult;

/**
 * ResultResource.
 */
@Path("result")
public class ResultResource {
  private static final Logger log = LogManager.getLogger();

  @Inject
  private DatabaseService db;

  @Inject
  private HandshakeAnalyser analyser;

  /**
   * Upload the result of a complete TLS test.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public String uploadResult(String body) {
    TlsTestResult result = new Gson().fromJson(body, TlsTestResult.class);
    log.info("Received new test result");

    try {
      db.addTestResult(result);
      log.debug("Successfully uploaded.");

      NetworkStats stats = db.getNetworkStats(result.getNetworkId());

      List<ProbedHostAnalysis> probedHostAnalysis = analyser.analyse(result);
      log.debug("Successfully analysed.");

      AnalysisResult analysisResult = new AnalysisResult(stats, probedHostAnalysis);
      return new Gson().toJson(analysisResult);

    } catch (Exception e) {
      log.error("Exception while uploading result.", e);
      throw new WebApplicationException(Status.BAD_REQUEST);
    }
  }
}
