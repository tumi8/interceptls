package de.tum.in.net;

import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.NameType;
import org.bouncycastle.crypto.tls.ServerName;
import org.bouncycastle.crypto.tls.ServerNameList;
import org.bouncycastle.crypto.tls.TlsCipherFactory;
import org.bouncycastle.crypto.tls.TlsExtensionsUtils;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by wohlfart on 11.08.16.
 */
public abstract class SniTlsClient extends DefaultTlsClient {

    String serverName;

    public SniTlsClient(final String serverName) {
        super();
        this.serverName = serverName;
    }

    public SniTlsClient(final String serverName, final TlsCipherFactory cipherFactory) {
        super(cipherFactory);
        this.serverName = serverName;
    }

    @Override
    public Hashtable getClientExtensions()
            throws IOException {
        Hashtable clientExtensions = super.getClientExtensions();

        // create ServerNameList
        final ServerName sn = new ServerName(NameType.host_name, serverName);
        final Vector<ServerName> vlist = new Vector<>();
        vlist.add(sn);
        final ServerNameList list = new ServerNameList(vlist);

        // add list to clientExtensions
        clientExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(clientExtensions);
        TlsExtensionsUtils.addServerNameExtension(clientExtensions, list);

        return clientExtensions;
    }
}