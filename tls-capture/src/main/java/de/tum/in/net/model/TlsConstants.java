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
package de.tum.in.net.model;

public final class TlsConstants {

  public static final String REQUEST_METHOD = "GET";
  public static final String REQUEST_URI = "/test";
  public static final String HTTP_VERSION = "HTTP/1.1";

  /**
   * The complete request line including CRLF according to RFC2616 Sec. 5.1.
   */
  public static final String REQUEST_LINE =
      REQUEST_METHOD + " " + REQUEST_URI + " " + HTTP_VERSION + "\r\n";

  public static final String TLS_ANALYSIS_URL = "https://interceptls.net.in.tum.de:8412";
  public static final String TLS_INFORMATION_SERVER_URL = "https://interceptls.net.in.tum.de";
  public static final String TLS_INFORMATION_SERVER_URL_WITH_PORT =
      "https://interceptls.net.in.tum.de:8413";
  public static final String TLS_CAPTURE_SERVER_HOST = "interceptls.net.in.tum.de";

  private TlsConstants() {
    //
  }

}
