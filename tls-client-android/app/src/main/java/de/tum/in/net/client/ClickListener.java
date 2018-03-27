package de.tum.in.net.client;

import android.view.View;

/**
 * Created by johannes on 30.06.17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
