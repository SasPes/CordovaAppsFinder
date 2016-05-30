/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import static com.saspes.rest.CordovaAppsFinder.DEBUG;
import java.io.IOException;
import java.io.RandomAccessFile;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 *
 * @author SasPes
 */
public class UnApk7z {

    // list unzip apk
    public static boolean unzip(String path) {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(path, "r");
            inArchive = SevenZip.openInArchive(null, // autodetect archive type
                    new RandomAccessFileInStream(randomAccessFile));

            // Getting simple interface of the archive inArchive
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();

            if (DEBUG) {
                System.out.println("   Size   | Compr.Sz. | Filename");
                System.out.println("----------+-----------+---------");
            }

            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                if (DEBUG) {
                    System.out.println(String.format("%9s | %9s | %s", // 
                            item.getSize(),
                            item.getPackedSize(),
                            item.getPath()));
                }

                if (item.getPath().endsWith("cordova.js") || item.getPath().endsWith("phonegap.js")) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }

        return false;
    }

}
