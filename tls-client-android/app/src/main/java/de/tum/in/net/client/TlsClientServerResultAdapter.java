package de.tum.in.net.client;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.tum.in.net.model.TlsClientServerResult;

/**
 * Created by johannes on 30.06.17.
 */

public class TlsClientServerResultAdapter extends RecyclerView.Adapter<TlsClientServerResultAdapter.MyViewHolder> {

    private int mExpandedPosition = -1;
    private final List<TlsClientServerResult> results;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView target;
        public ImageView interception_icon;
        public ImageView expandable_icon;
        public TextView details;

        public MyViewHolder(final View view) {
            super(view);
            interception_icon = view.findViewById(R.id.interception_icon);
            target = view.findViewById(R.id.target);
            expandable_icon = view.findViewById(R.id.expandable_icon);
            details = view.findViewById(R.id.details);

        }
    }

    public TlsClientServerResultAdapter(final List<TlsClientServerResult> results) {
        this.results = results;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.probed_host_card_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TlsClientServerResult result = results.get(position);

        holder.target.setText(result.getHostAndPort().toString());

        final boolean isExpanded = position == mExpandedPosition;
        if (result.isIntercepted()) {
            holder.interception_icon.setImageResource(R.drawable.ic_interception);
            //make it expandable
            holder.expandable_icon.setImageResource(isExpanded ? R.drawable.collapsable : R.drawable.expandable);
            holder.details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.itemView.setActivated(isExpanded);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mExpandedPosition = isExpanded ? -1 : position;
                    notifyItemChanged(position);
                }
            });
        } else {
            holder.interception_icon.setImageResource(R.drawable.ic_no_interception);
        }

    }


    @Override
    public int getItemCount() {
        return results.size();
    }
}
