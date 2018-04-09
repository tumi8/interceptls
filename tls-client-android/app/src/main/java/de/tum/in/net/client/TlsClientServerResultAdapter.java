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
        public View details;
        public TextView clientResultView;
        public TextView generalResult;
        public TextView versionView;
        public TextView cipherView;
        public TextView versionViewServer;
        public TextView cipherViewServer;

        public MyViewHolder(final View view) {
            super(view);
            interception_icon = view.findViewById(R.id.interception_icon);
            target = view.findViewById(R.id.target);
            expandable_icon = view.findViewById(R.id.expandable_icon);
            details = view.findViewById(R.id.details);
            generalResult = view.findViewById(R.id.result_general);
            clientResultView = view.findViewById(R.id.resultClientHello);
            versionView = view.findViewById(R.id.tls_version_client);
            cipherView = view.findViewById(R.id.tls_cipher_client);
            versionViewServer = view.findViewById(R.id.tls_version_server);
            cipherViewServer = view.findViewById(R.id.tls_cipher_server);

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
        holder.details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        if (result.isSuccess()) {
            if (result.isIntercepted()) {
                holder.interception_icon.setImageResource(R.drawable.ic_interception);
                //make it expandable
                holder.expandable_icon.setImageResource(isExpanded ? R.drawable.collapsable : R.drawable.expandable);
                holder.itemView.setActivated(isExpanded);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mExpandedPosition = isExpanded ? -1 : position;
                        notifyItemChanged(position);
                    }
                });


                //            final AnalysisResult analysisResult = AnalysisResult.noInterception("TODO");
                //
                //            holder.generalResult.setText(analysisResult.getTlsState() + "\n");
                //            holder.generalResult.setTextColor(Color.parseColor("#FF0000"));
                //            final TlsMessageDiff diffClientHello = analysisResult.getClientHelloDiff();
                //
                //            Diff version = diffClientHello.getVersionDiff();
                //            holder.versionView.setText(version.toString());
                //
                //            Diff cipher = diffClientHello.getCiphersDiff();
                //            holder.cipherView.setText(cipher.toString());
                //
                //            for (final Map.Entry<String, Diff> diff : diffClientHello.getExtensionsDiff().entrySet()) {
                //                holder.clientResultView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                //            }
                //
                //            final TlsMessageDiff diffServerHello = analysisResult.getServerHelloDiff();
                //
                //            version = diffServerHello.getVersionDiff();
                //            holder.versionViewServer.setText(version.toString());
                //
                //            cipher = diffServerHello.getCiphersDiff();
                //            holder.cipherViewServer.setText(cipher.toString());

                //final TextView serverResultView = (TextView) findViewById(R.id.resultServerHello);
                //for (final Map.Entry<String, Diff> diff : diffServerHello.getExtensionsDiff().entrySet()) {
                //    serverResultView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                //}

                //final TlsMessageDiff certDiff = analysisResult.getCertificateDiff();
                //final TextView certResultView = (TextView) findViewById(R.id.resultCertificate);
                //certResultView.setText(certDiff.getCertChainDiff().toString());


            } else {
                holder.interception_icon.setImageResource(R.drawable.ic_no_interception);
            }
        } else {
            holder.interception_icon.setImageResource(R.drawable.no_connection);
        }

    }


    @Override
    public int getItemCount() {
        return results.size();
    }
}
