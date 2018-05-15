package de.tum.in.net.client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        TextView t = findViewById(R.id.libraries_apache);
        t.setMovementMethod(LinkMovementMethod.getInstance());

        t = findViewById(R.id.libraries_mit);
        t.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

