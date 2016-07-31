/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 *
 * @author saspes
 */
public class ArchieveInputStreamHandler {

    private ISimpleInArchiveItem item;
    private ByteArrayInputStream arrayInputStream;

    public ArchieveInputStreamHandler(ISimpleInArchiveItem item) {
        this.item = item;
    }

    public InputStream getInputStream() throws SevenZipException {

        item.extractSlow(new ISequentialOutStream() {
            @Override
            public int write(byte[] data) throws SevenZipException {
                arrayInputStream = new ByteArrayInputStream(data);
                return data.length; // Return amount of consumed data
            }
        });
        return arrayInputStream;
    }

    //got from http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    public static String readFile(final InputStream is, final int bufferSize) {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0) {
                        break;
                    }
                    out.append(buffer, 0, rsz);
                }
            } finally {
                in.close();
            }
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Error occurs: " + ex);
        } catch (IOException ex) {
            System.err.println("Error occurs: " + ex);
        }
        return out.toString();
    }
}
