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

import java.util.Objects;

public class ProbedHostAnalysis {

  private final String target;
  private final TlsState tlsState;
  private TlsMessageDiff clientHello;
  private TlsMessageDiff serverHello;
  private TlsMessageDiff certificate;

  private ProbedHostAnalysis(final String target, final TlsState tlsState) {
    this.target = Objects.requireNonNull(target);
    this.tlsState = Objects.requireNonNull(tlsState);
  }

  private ProbedHostAnalysis(final String target, final TlsMessageDiff clientHello, final TlsMessageDiff serverHello,
      final TlsMessageDiff certificate) {
    this.target = Objects.requireNonNull(target);
    this.tlsState = TlsState.INTERCEPTION;
    this.clientHello = Objects.requireNonNull(clientHello);
    this.serverHello = Objects.requireNonNull(serverHello);
    this.certificate = Objects.requireNonNull(certificate);
  }

  public TlsMessageDiff getClientHelloDiff() {
    return clientHello;
  }

  public TlsMessageDiff getServerHelloDiff() {
    return serverHello;
  }

  public TlsMessageDiff getCertificateDiff() {
    return certificate;
  }

  public String getTarget() {
    return target;
  }

  public static ProbedHostAnalysis noInterception(final String target) {
    return new ProbedHostAnalysis(target, TlsState.NO_INTERCEPTION);
  }

  public static ProbedHostAnalysis intercepted(final String target, final TlsMessageDiff clientHello,
      final TlsMessageDiff serverHello, final TlsMessageDiff certificate) {
    return new ProbedHostAnalysis(target, clientHello, serverHello, certificate);
  }

  public boolean isIntercepted() {
    return TlsState.INTERCEPTION.equals(tlsState);
  }

  public TlsState getTlsState() {
    return tlsState;
  }


}
