package com.example.demotlsclient;

import org.spongycastle.util.io.TeeInputStream;
import org.spongycastle.util.io.TeeOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by johannes on 22.03.17.
 */

public class Tap {

    private final ByteArrayOutputStream tapIn = new ByteArrayOutputStream();
    private final ByteArrayOutputStream tapOut = new ByteArrayOutputStream();

    private final InputStream in;
    private final OutputStream out;

    public Tap(InputStream input, OutputStream output){
        this.in = new TeeInputStream(input, tapIn);
        this.out = new TeeOutputStream(output, tapOut);
    }

    public InputStream getIn(){
        return in;
    }

    public OutputStream getOut(){
        return out;
    }

    public byte[] getInputBytes(){
        return tapIn.toByteArray();
    }

    public byte[] getOutputytes(){
        return tapOut.toByteArray();
    }
}

