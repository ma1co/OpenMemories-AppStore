package com.github.ma1co.openmemories.appstore;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SpkWriter extends OutputStream {
    private final int blockSize = 0x100000;

    // from TouchLessShutter100.1.spk
    private final byte[] header = Util.fromHex("3173706b00000000000100007e29351425ec82c61ef1d736afadc280966a2dadd53ffee3d55e608afad43951853a1be9e36265b05c1e4345ac4919d6c9ef4e021f58bf85e4851c3cb2bd14416d261526296523259664be8ac2477f7bd6d1c162af286c65a7f57a1800e489cf24fc58fb041f29ea103f5fca3eb996baaf8eea2cfd6431bb765dced8110b341db6d313dc40a82a6e2134272e3aa3c7570b80d5d1d7533f2efcf3ff0a00f91691847417005da841fd2906ac7e0767fafbbb1e7ea0e1c76828ace66fdd449506609cc604c8abf11d9414a7cb296d93841b3a062c05e5a3f340556dc25e6c0e467466242ff433cc200ef9f29d9bbdf6227212ef40bc43cb74b1df02be3153a334a2");
    private final byte[] key = Util.fromHex("c301afd22ddba4c090eca46258e4bfb1");

    private final OutputStream outputStream;
    private final Cipher cipher;
    private final byte[] buffer = new byte[blockSize];
    private int bufferOffset = 0;

    public SpkWriter(OutputStream outputStream) throws GeneralSecurityException, IOException {
        this.outputStream = outputStream;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
        writeHeader();
    }

    @Override
    public void write(final byte[] data, final int offset, final int length) throws IOException {
        int written = 0;
        while (written < length) {
            int n = Math.min(length - written, buffer.length - bufferOffset);
            System.arraycopy(data, offset + written, buffer, bufferOffset, n);
            written += n;
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
    }

    private void writeBuffer() throws IOException {
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
