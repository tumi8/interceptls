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
package de.tum.in.net.client;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by johannes on 30.06.17.
 */

public class AndroidTlsResultAdapter extends RecyclerView.Adapter<AndroidTlsResultAdapter.MyViewHolder> {

    private final List<AndroidTlsResult> results;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp;
        public ImageView interception_icon;
        public ImageView upload_icon;

        public MyViewHolder(final View view) {
            super(view);
            interception_icon = view.findViewById(R.id.interception_icon);
            timestamp = view.findViewById(R.id.timestamp);
            upload_icon = view.findViewById(R.id.upload_icon);

        }
    }

    public AndroidTlsResultAdapter(final List<AndroidTlsResult> results) {
        this.results = results;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tls_card_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AndroidTlsResult result = results.get(position);
        if (result.getTestResult().anyInterception()) {
            holder.interception_icon.setImageResource(R.drawable.ic_interception);
        } else {
            holder.interception_icon.setImageResource(R.drawable.ic_no_interception);
        }


        final String iso8601 = result.getTimestamp();
        holder.timestamp.setText(Util.formatTimestamp(iso8601));
        if (result.isUploaded()) {
            holder.upload_icon.setImageResource(R.drawable.ic_upload_done);
        } else {
            holder.upload_icon.setImageResource(R.drawable.ic_upload);
        }

    }


    @Override
    public int getItemCount() {
        return results.size();
    }
}
