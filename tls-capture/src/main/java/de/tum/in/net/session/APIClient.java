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
package de.tum.in.net.session;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by johannes on 13.04.17.
 */

public class APIClient {

  private APIClient() {
    // utility
  }

  static Retrofit createClient(final String url) {

    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

    final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
        .hostnameVerifier(AnalysisTlsContext.getHostnameVerifier()).sslSocketFactory(
            AnalysisTlsContext.createSocketFactory(), AnalysisTlsContext.getTrustManager())
        .build();

    return new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
        .client(client).build();

  }
}
