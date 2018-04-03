package de.tum.in.net.client;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 30.06.17.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.MyViewHolder> {

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

    public ResultsAdapter(final List<AndroidTlsResult> results) {
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
        if(result.getResult().anyInterception()){
            holder.interception_icon.setImageResource(R.drawable.ic_interception);
        }else{
            holder.interception_icon.setImageResource(R.drawable.ic_no_interception);
        }


        holder.timestamp.setText(result.getTimestamp());
        if(result.isUploaded()){
            holder.upload_icon.setImageResource(R.drawable.ic_upload_done);
        } else{
            holder.upload_icon.setImageResource(R.drawable.ic_upload);
        }

    }


    @Override
    public int getItemCount() {
        return results.size();
    }
}
