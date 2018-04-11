package de.tum.in.net.analysis;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TlsTestResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by johannes on 13.04.17.
 */

public interface AnalysisAPI {

  @POST("/result")
  Call<AnalysisResult> uploadResult(@Body TlsTestResult result);

  @POST("/network")
  Call<NetworkStats> getNetworkStats(@Body NetworkId network);

}
