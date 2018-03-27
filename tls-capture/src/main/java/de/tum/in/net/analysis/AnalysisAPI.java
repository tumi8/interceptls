package de.tum.in.net.analysis;

import java.util.List;

import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.SessionID;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by johannes on 13.04.17.
 */

public interface AnalysisAPI {

  @POST("/result")
  Call<SessionID> uploadResult(@Body TlsTestResult result);

  @GET("/analysis/{session_id}")
  Call<List<AnalysisResult>> getAnalysis(@Path(value = "session_id", encoded = true) SessionID id);

}
