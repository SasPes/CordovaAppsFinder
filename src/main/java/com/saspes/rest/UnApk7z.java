/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static com.saspes.rest.CordovaAppsFinder.DEBUG;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
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
    public static ArrayList<Plugin> unzip(String path) {
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

            boolean cordova = false;
            boolean cordovaPlugins = false;
            ArrayList<Plugin> plugins = new ArrayList<>();
            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                if (DEBUG) {
                    System.out.println(String.format("%9s | %9s | %s", // 
                            item.getSize(),
                            item.getPackedSize(),
                            item.getPath()));
                }

                if (item.getPath().endsWith("cordova.js") || item.getPath().endsWith("phonegap.js")) {
                    cordova = true;
                }

                if (item.getPath().endsWith("cordova_plugins.js")) {
                    cordovaPlugins = true;
                    String cordovaPluginsFile = ArchieveInputStreamHandler.readFile(new ArchieveInputStreamHandler(item).getInputStream(), 1000);

                    String metadata = "module.exports.metadata";
                    String jsonPluginsString = cordovaPluginsFile.substring(cordovaPluginsFile.indexOf(metadata));
                    jsonPluginsString = jsonPluginsString.substring(
                            jsonPluginsString.indexOf("{"),
                            jsonPluginsString.indexOf("}") + 1
                    );

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonPluginsString);

                    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                    for (Map.Entry<String, JsonElement> entry : entrySet) {
                        plugins.add(new Plugin(entry.getKey(), entry.getValue().getAsString()));
                    }

                }

                if (cordova && cordovaPlugins) {
                    return plugins;
                }
            }

            return null;
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

        return null;
    }

}
