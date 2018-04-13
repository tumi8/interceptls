package de.tum.in.net.client;

import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.analysis.Diff;
import de.tum.in.net.analysis.ProbedHostAnalysis;
import de.tum.in.net.analysis.TlsMessageDiff;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.util.CertificateUtil;

/**
 * Created by johannes on 30.06.17.
 */

public class TlsClientServerResultAdapter extends RecyclerView.Adapter<TlsClientServerResultAdapter.MyViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(TlsClientServerResultAdapter.class);
    private int mExpandedPosition = -1;
    private final AndroidTlsResult result;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView target;
        public ImageView interception_icon;
        public ImageView expandable_icon;
        public View details;
        public TextView clientHelloView;
        public TextView versionView;
        public TextView cipherView;
        public TextView versionViewServer;
        public TextView cipherViewServer;
        public TextView serverHelloView;
        public TextView certificateView;

        public MyViewHolder(final View view) {
            super(view);
            interception_icon = view.findViewById(R.id.interception_icon);
            target = view.findViewById(R.id.target);
            expandable_icon = view.findViewById(R.id.expandable_icon);
            details = view.findViewById(R.id.details);
            clientHelloView = view.findViewById(R.id.resultClientHello);
            versionView = view.findViewById(R.id.tls_version_client);
            cipherView = view.findViewById(R.id.tls_cipher_client);
            versionViewServer = view.findViewById(R.id.tls_version_server);
            cipherViewServer = view.findViewById(R.id.tls_cipher_server);
            serverHelloView = view.findViewById(R.id.resultServerHello);
            certificateView = view.findViewById(R.id.resultCertificate);

        }
    }

    public TlsClientServerResultAdapter(final AndroidTlsResult result) {
        this.result = Objects.requireNonNull(result);
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.probed_host_card_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TlsClientServerResult r = result.getTestResult().getClientServerResults().get(position);

        holder.target.setText(r.getHostAndPort().toString());

        final boolean isExpanded = position == mExpandedPosition;
        holder.details.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        if (r.isSuccess()) {
            if (r.isIntercepted()) {
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


                final AnalysisResult analysisResult = result.getAnalysisResult();
                if (analysisResult != null) {
                    ProbedHostAnalysis matchingAnalysis = null;
                    for (final ProbedHostAnalysis a : analysisResult.getProbedHosts()) {
                        if (r.getHostAndPort().toString().equals(a.getTarget())) {
                            matchingAnalysis = a;
                        }
                    }
                    if (matchingAnalysis != null) {
                        final TlsMessageDiff diffClientHello = matchingAnalysis.getClientHelloDiff();
                        Diff version = diffClientHello.getVersionDiff();
                        holder.versionView.setText(version.toString());

                        Diff cipher = diffClientHello.getCiphersDiff();
                        holder.cipherView.setText(cipher.toString());

                        for (final Map.Entry<String, Diff> diff : diffClientHello.getExtensionsDiff().entrySet()) {
                            holder.clientHelloView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                        }

                        final TlsMessageDiff diffServerHello = matchingAnalysis.getServerHelloDiff();

                        version = diffServerHello.getVersionDiff();
                        holder.versionViewServer.setText(version.toString());

                        cipher = diffServerHello.getCiphersDiff();
                        holder.cipherViewServer.setText(cipher.toString());


                        for (final Map.Entry<String, Diff> diff : diffServerHello.getExtensionsDiff().entrySet()) {
                            holder.serverHelloView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                        }

                        final TlsMessageDiff certDiff = matchingAnalysis.getCertificateDiff();
                        final Diff certChainDiff = certDiff.getCertChainDiff();
                        holder.certificateView.setText("");
                        try {
                            final String[] expectedCertsBase64 = certChainDiff.getExpected().replace("[", "").replace("]", "").split(",");
                            final List<X509Certificate> expectedCerts = new ArrayList<>();
                            for (final String cert : expectedCertsBase64) {
                                expectedCerts.add(CertificateUtil.readCert(new ByteArrayInputStream(Base64.decode(cert, Base64.DEFAULT))));
                            }

                            final String[] actualCertsBase64 = certChainDiff.getExpected().replace("[", "").replace("]", "").split(",");
                            final List<X509Certificate> actualCerts = new ArrayList<>();
                            for (final String cert : actualCertsBase64) {
                                actualCerts.add(CertificateUtil.readCert(new ByteArrayInputStream(Base64.decode(cert, Base64.DEFAULT))));
                            }
                            holder.certificateView.append("Expected: ");
                            for (final X509Certificate x509 : expectedCerts) {
                                holder.certificateView.append("\n" + x509.toString());
                            }

                            holder.certificateView.append("\n\nActual: ");
                            for (final X509Certificate x509 : actualCerts) {
                                holder.certificateView.append("\n" + x509.toString());
                            }

                        } catch (CertificateException | IOException e) {
                            log.error("Could not display certificate info", e);
                        }
                    }
                }


            } else {
                holder.interception_icon.setImageResource(R.drawable.ic_no_interception);
            }
        } else {
            holder.interception_icon.setImageResource(R.drawable.no_connection);
        }

    }


    @Override
    public int getItemCount() {
        return result.getTestResult().getClientServerResults().size();
    }
}
