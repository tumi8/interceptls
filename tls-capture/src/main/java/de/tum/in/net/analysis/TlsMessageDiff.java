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
package de.tum.in.net.analysis;

import java.util.Collections;
import java.util.Map;

public class TlsMessageDiff {

  private Diff version;
  private Diff ciphers;
  private Diff compression;
  private Map<String, Diff> extDiffs;
  private Diff certChain;


  /*
   * Client Hello / Server Hello Diff
   */
  public TlsMessageDiff(Diff version, Diff ciphers, Diff compression, Map<String, Diff> extDiffs) {
    this.version = version;
    this.ciphers = ciphers;
    this.compression = compression;
    this.extDiffs = extDiffs;
  }

  public TlsMessageDiff(Diff certChain) {
    this.certChain = certChain;
  }

  public Diff getVersionDiff() {
    return version;
  }

  public Diff getCiphersDiff() {
    return ciphers;
  }

  public Diff getCompressionDiff() {
    return compression;
  }

  public Diff getCertChainDiff() {
    return certChain;
  }

  public Map<String, Diff> getExtensionsDiff() {
    return Collections.unmodifiableMap(extDiffs);
  }

}
