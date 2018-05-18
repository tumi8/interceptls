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

        public TextView clientVersionView;
        public TextView clientCiphersView;
        public TextView clientCompressionsView;
        public TextView clientExtensionView;

        public TextView serverVersionView;
        public TextView serverCipherView;
        public TextView serverCompressionView;
        public TextView serverExtensionsView;

        public TextView certificateView;

        public MyViewHolder(final View view) {
            super(view);
            interception_icon = view.findViewById(R.id.interception_icon);
            target = view.findViewById(R.id.target);
            expandable_icon = view.findViewById(R.id.expandable_icon);
            details = view.findViewById(R.id.details);

            clientVersionView = view.findViewById(R.id.client_tls_version);
            clientCiphersView = view.findViewById(R.id.client_ciphers);
            clientCompressionsView = view.findViewById(R.id.client_compressions);
            clientExtensionView = view.findViewById(R.id.client_extensions);

            serverVersionView = view.findViewById(R.id.server_tls_version);
            serverCipherView = view.findViewById(R.id.server_cipher);
            serverCompressionView = view.findViewById(R.id.server_compression);
            serverExtensionsView = view.findViewById(R.id.server_extensions);

            certificateView = view.findViewById(R.id.tls_certificate);

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
                        holder.clientVersionView.setText(version.toString());

                        final Diff ciphers = diffClientHello.getCiphersDiff();
                        holder.clientCiphersView.setText(ciphers.toString());

                        final Diff compressions = diffClientHello.getCompressionDiff();
                        holder.clientCompressionsView.setText(compressions.toString());

                        for (final Map.Entry<String, Diff> diff : diffClientHello.getExtensionsDiff().entrySet()) {
                            holder.clientExtensionView.append(diff.getKey() + ": " + diff.getValue() + "\n");
                        }

                        final TlsMessageDiff diffServerHello = matchingAnalysis.getServerHelloDiff();

                        version = diffServerHello.getVersionDiff();
                        holder.serverVersionView.setText(version.toString());

                        final Diff cipher = diffServerHello.getCiphersDiff();
                        holder.serverCipherView.setText(cipher.toString());

                        final Diff compression = diffServerHello.getCompressionDiff();
                        holder.serverCompressionView.setText(compression.toString());

                        for (final Map.Entry<String, Diff> diff : diffServerHello.getExtensionsDiff().entrySet()) {
                            holder.serverExtensionsView.append(diff.getKey() + ": " + diff.getValue() + "\n");
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
