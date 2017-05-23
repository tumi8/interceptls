package de.tum.in.net.session;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by johannes on 13.04.17.
 */

public class APIClient {


  static Retrofit createClient(final String url) {

    final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    final OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    return new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
        .client(client).build();

  }
}
