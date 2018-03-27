package de.tum.in.net.client;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.OnlineTestSession;

/**
 * Created by johannes on 21.03.18.
 */

public class UploadResultService extends IntentService {

    private static final Logger log = LoggerFactory.getLogger(UploadResultService.class);
    public static final String INTENT_TEST_RESULT = "testResult";

    public UploadResultService() {
        super("UploadResultService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {

        final TlsTestResult result = (TlsTestResult) intent.getSerializableExtra(INTENT_TEST_RESULT);

        final String url = ConfigurationReader.getAnalysisHostUrl(this);

        final TestSession s = new OnlineTestSession(url);
        try {
            s.uploadResult(result);
            ResultStorage.saveFinal(this, result);
            log.debug("Result successfully uploaded to analysis server");
        } catch (final IOException e) {
            log.debug("Could not connect to analysis server, save result temporarily", e);
            ResultStorage.saveTemp(this, result);
        }

    }

}
