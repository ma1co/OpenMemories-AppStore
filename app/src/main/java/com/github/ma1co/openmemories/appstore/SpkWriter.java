package com.github.ma1co.openmemories.appstore;

import android.util.Base64;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SpkWriter extends OutputStream {
    private final int blockSize = 0x100000;

    // from TouchLessShutter100.1.spk
    private final byte[] header = Base64.decode("MXNwawAAAAAAAQAAfik1FCXsgsYe8dc2r63CgJZqLa3VP/7j1V5givrUOVGFOhvp42JlsFweQ0WsSRnWye9OAh9Yv4XkhRw8sr0UQW0mFSYpZSMllmS+isJHf3vW0cFiryhsZaf1ehgA5InPJPxY+wQfKeoQP1/KPrmWuq+O6iz9ZDG7dl3O2BELNB220xPcQKgqbiE0Jy46o8dXC4DV0ddTPy788/8KAPkWkYR0FwBdqEH9KQasfgdn+vu7Hn6g4cdoKKzmb91ElQZgnMYEyKvxHZQUp8spbZOEGzoGLAXlo/NAVW3CXmwORnRmJC/0M8wgDvnynZu99iJyEu9AvEPLdLHfAr4xU6M0og==", Base64.DEFAULT);
    private final byte[] key = Base64.decode("wwGv0i3bpMCQ7KRiWOS/sQ==", Base64.DEFAULT);

    private final OutputStream outputStream;
    private final Cipher cipher;
    private final byte[] buffer = new byte[blockSize];
    private int bufferOffset = 0;
    private boolean headerWritten = false;

    public SpkWriter(OutputStream outputStream) throws GeneralSecurityException {
        this.outputStream = outputStream;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        length += offset;
        while (offset < length) {
            int n = Math.min(length - offset, buffer.length - bufferOffset);
            System.arraycopy(data, offset, buffer, bufferOffset, n);
            offset += n;
            bufferOffset += n;
            if (bufferOffset == buffer.length)
                writeBuffer();
        }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] {(byte) b});
    }

    @Override
    public void close() throws IOException {
        writeBuffer();
        outputStream.close();
    }

    private void writeHeader() throws IOException {
        outputStream.write(header);
        headerWritten = true;
    }

    private void writeBuffer() throws IOException {
        if (!headerWritten)
            writeHeader();
        if (bufferOffset > 0) {
            try {
                outputStream.write(cipher.doFinal(buffer, 0, bufferOffset));
                bufferOffset = 0;
            } catch (GeneralSecurityException e) {
                throw new IOException("AES exception");
            }
        }
    }
}
