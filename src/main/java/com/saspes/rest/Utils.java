/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author SasPes
 */
public class Utils {

    public static Auth auth = new Auth();

    public static PrintStream out = null;
    private static final String WEBINF = "WEB-INF";

    public static boolean checkCordova(Apk apk) throws IOException {
        System.out.println("[ " + Utils.getDate() + " ] [ Downloading ... ] " + apk.getAppId());
        File targetFile = new File("tempapk/" + apk.getAppId() + ".apk");

        targetFile.getParentFile().mkdirs();

        CloseableHttpClient client = HttpClients.createDefault();
        try (CloseableHttpResponse response = client.execute(new HttpGet(apk.getDownload()))) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (FileOutputStream outstream = new FileOutputStream(targetFile)) {
                    entity.writeTo(outstream);
                }
            }
        }

        System.out.println("[ " + Utils.getDate() + " ] [ Saved ] " + targetFile.getAbsolutePath());

        ArrayList<Plugin> plugins = UnApk7z.unzip(targetFile.getPath());
        if (plugins != null) {
            apk.setPlugins(plugins);
            System.out.println("*********************************************************");
            System.out.println("* " + apk.getName() + " (" + apk.getAppId() + " ) is Cordova/PhoneGap app [ " + Utils.getDate() + " ] ");
            System.out.println("*********************************************************");
            return true;
        } else {
            return false;
        }
    }

    public static Document getApkPage(String link) throws ParseException, IOException {
        if (out == null) {
            out = new PrintStream(new FileOutputStream(getWebRootPath() + "output.log"));
            System.setOut(out);
            System.out.println(""
                    + "                                                                                                _                                     \n"
                    + " _____              _                   _____                   _____  _         _             | |   _____            _____           \n"
                    + "|     | ___  ___  _| | ___  _ _  ___   |  _  | ___  ___  ___   |   __||_| ___  _| | ___  ___   | |  |   __| ___  ___ |  _  | ___  ___ \n"
                    + "|   --|| . ||  _|| . || . || | || .'|  |     || . || . ||_ -|  |   __|| ||   || . || -_||  _|  | |  |__   || .'||_ -||   __|| -_||_ -|\n"
                    + "|_____||___||_|  |___||___| \\_/ |__,|  |__|__||  _||  _||___|  |__|   |_||_|_||___||___||_|    | |  |_____||__,||___||__|   |___||___|\n"
                    + "                                              |_|  |_|                                         |_|                                    \n"
                    + "");
        }

        HttpGet httpGetApp = new HttpGet(link);
        CloseableHttpResponse httpGetAppRes = auth.getHttpClient().execute(httpGetApp, auth.getHttpContext());
        System.out.println("[ " + Utils.getDate() + " ] [ " + httpGetAppRes.getStatusLine() + "] " + link);

        HttpEntity entityApp = httpGetAppRes.getEntity();
        String entityContentsApp = EntityUtils.toString(entityApp);
        Document docApp = Jsoup.parse(entityContentsApp);

        return docApp;
    }

    public static String getWebRootPath() {
        String filePath = "";
        URL url = Utils.class.getResource("Utils.class");
        String className = url.getFile();
        filePath = className.substring(0, className.indexOf(WEBINF));
        return filePath;
    }

    public static String getDate() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.dd.MM hh:mm:ss");
        return sdf.format(date);
    }
}
