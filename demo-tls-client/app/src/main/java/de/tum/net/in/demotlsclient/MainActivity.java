package de.tum.net.in.demotlsclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.tum.in.net.demotlsclient.R;

/**
 * Created by wohlfart on 11.08.16.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void buttonClick(View v) {

        AsyncResult<MyTaskParams, String> asyncResult = new AsyncResult<MyTaskParams, String>() {
            @Override
            public void publishProgress(MyTaskParams p){
                if (p.tls_handshake != null) {
                    ((TextView) findViewById(R.id.tview_tls_handshake)).setText(p.tls_handshake);
                }
                if (p.tcp_payload != null) {
                    ((TextView) findViewById(R.id.tview_tcp_payload)).setText(p.tcp_payload);
                }
            }

            @Override
            public void publishResult(String result) {
                ((Button) findViewById(R.id.button)).setText("done");
            }
        };

        TLSTask myTask = new TLSTask(asyncResult);
        myTask.execute();


        System.out.println("clicked");
    }




}
