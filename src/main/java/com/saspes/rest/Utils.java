/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static boolean checkCordova(Apk apk) throws IOException {
        System.out.println("[ Downloading ... ] " + apk.getAppId());
        File targetFile = new File("tempapk\\" + apk.getAppId() + ".apk");

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

        System.out.println("[ Saved ] " + targetFile.getAbsolutePath());

        return UnApk7z.unzip(targetFile.getPath());
    }

    public static Document getApkPage(String link) throws ParseException, IOException {
        HttpGet httpGetApp = new HttpGet(link);
        CloseableHttpResponse httpGetAppRes = auth.getHttpClient().execute(httpGetApp, auth.getHttpContext());
        System.out.println("[ " + httpGetAppRes.getStatusLine() + "] " + link);

        HttpEntity entityApp = httpGetAppRes.getEntity();
        String entityContentsApp = EntityUtils.toString(entityApp);
        Document docApp = Jsoup.parse(entityContentsApp);

        return docApp;
    }

}
