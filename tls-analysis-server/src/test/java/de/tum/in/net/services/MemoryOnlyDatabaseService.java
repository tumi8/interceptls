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
package de.tum.in.net.services;

import java.io.IOException;
import java.sql.SQLException;

import de.tum.in.net.analysis.GeneralStatistic;
import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.DatabaseService;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsTestResult;

public class MemoryOnlyDatabaseService implements DatabaseService {

  @Override
  public void addTestResult(TlsTestResult result) {
    // stub
  }

  @Override
  public void close() throws IOException {
    // stub
  }

  @Override
  public NetworkStats getNetworkStats(NetworkId networkId) throws SQLException {
    // stub
    return new NetworkStats();
  }

  @Override
  public GeneralStatistic getGeneralStatistic() throws SQLException {
    // stub
    return new GeneralStatistic(10, 2);
  }

}
