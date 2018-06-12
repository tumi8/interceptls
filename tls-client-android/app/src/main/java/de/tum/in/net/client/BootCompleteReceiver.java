package de.tum.in.net.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TlsJobService.init(context);
        DbCleanJobService.init(context);
    }
}
