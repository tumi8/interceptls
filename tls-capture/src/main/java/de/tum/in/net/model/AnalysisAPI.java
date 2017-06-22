package de.tum.in.net.model;

import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.session.SessionID;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by johannes on 13.04.17.
 */

public interface AnalysisAPI {

  @POST("/session")
  Call<SessionID> newSessionID();

  @PUT("/result/{session_id}")
  Call<ResponseBody> uploadHandshake(@Path(value = "session_id", encoded = true) TestID testID,
      @Body ScenarioResult result);

  @GET("/analysis/{session_id}")
  Call<?> getAnalysis(@Path(value = "session_id", encoded = true) TestID testID);


}
