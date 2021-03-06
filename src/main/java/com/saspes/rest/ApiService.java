package com.saspes.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import static com.saspes.rest.CordovaAppsFinder.DEBUG;
import static com.saspes.rest.CordovaAppsFinder.URL;
import static com.saspes.rest.CordovaAppsFinder.URL_APP;
import static com.saspes.rest.CordovaAppsFinder.URL_APPS;
import static com.saspes.rest.Utils.getWebRootPath;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/api")
public class ApiService {

    @Autowired
    ServletContext servletContext;

    @GET
    @Path("/listapps/{page}")
    public @ResponseBody
    @Produces("application/json")
    String listAppsFromPage(@PathParam("page") String page) throws IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(URL_APPS + page);

        CloseableHttpResponse apps = Utils.auth.getHttpClient().execute(httpGet, Utils.auth.getHttpContext());
        System.out.println("[ " + Utils.getDate() + " ] [ " + apps.getStatusLine() + " ] " + URL_APPS + page);

        HttpEntity entity = apps.getEntity();
        String entityContents = EntityUtils.toString(entity);

        Document doc = Jsoup.parse(entityContents);
        Elements pagedata = doc.select("#pagedata");
        Elements apkApps = pagedata.select("li");

        List<Apk> apkList = new ArrayList<>();
        // System.out.println("[ Apps ] " + apkApps.size());
        for (Element apkApp : apkApps) {
            // name
            Elements a = apkApp.select("a");
            String name = a.attr("title");

            // link
            String link = URL + a.attr("href");

            // appId
            String appId = link.substring(link.lastIndexOf("/") + 1).toLowerCase();

            // download
            Document docApp = Utils.getApkPage(link);
            Elements downloadApp = docApp.select("body div.main div.details div.down-warp div a");
            String download = downloadApp.attr("href");

            if (download.equals("#download")) {
                downloadApp = docApp.select("body div.main div.versions dl dd:nth-child(2) div a");
                download = downloadApp.attr("href");
            }

            // image
            Elements img = apkApp.select("img");
            String image = img.attr("data-original");

            docApp = Utils.getApkPage(URL + download);
            downloadApp = docApp.select("#download_link");
            download = downloadApp.attr("href");

            apkList.add(new Apk(name, link, appId, download, image, null));

            if (DEBUG) {
                break;
            }
        }

        return new Gson().toJson(apkList);
    }

    @GET
    @Path("/findapp/{packageName}")
    public @ResponseBody
    @Produces("application/json")
    String findApp(@PathParam("packageName") String packageName) throws IOException, URISyntaxException {
        // link
        String link = URL_APP + packageName;

        // appId
        String appId = packageName;

        // apk page
        Document docApp = Utils.getApkPage(link);

        // download
        Elements downloadApp = docApp.select("body div.main div.details div.down-warp div a");
        String download = URL + downloadApp.attr("href");

        if (download.equals("#download")) {
            downloadApp = docApp.select("body div.main div.versions dl dd:nth-child(2) div a");
            download = downloadApp.attr("href");
        }

        // name
        Elements nameApp = docApp.select("body div.main div.details div.p10 dl dd h1");
        String name = nameApp.text();

        // image 
        Elements imageApp = docApp.select("body div.main div.details div.p10 dl dt img");
        String image = imageApp.attr("src");

        docApp = Utils.getApkPage(download);
        downloadApp = docApp.select("#download_link");
        download = downloadApp.attr("href");

        Apk apk = new Apk(name, link, appId, download, image, null);
        return new Gson().toJson(apk);
    }

    @GET
    @Path("/checkapp/{packageName}")
    public @ResponseBody
    @Produces("application/json")
    String checkApp(@PathParam("packageName") String packageName) throws IOException, URISyntaxException {
        Apk apk = new Gson().fromJson(findApp(packageName), Apk.class);

        if (!Utils.checkCordova(apk)) {
            apk = new Apk();
        }

        return new Gson().toJson(apk);
    }

    @GET
    @Path("/checkapps/{page}")
    public @ResponseBody
    @Produces("application/json")
    String checkApps(@PathParam("page") Integer page) throws IOException, URISyntaxException {
        TypeToken<List<Apk>> token = new TypeToken<List<Apk>>() {
        };
        List<Apk> apks = new Gson().fromJson(listAppsFromPage(page.toString()), token.getType());

        // init 7Zip libs
        try {
            if (!SevenZip.isInitializedSuccessfully()) {
                SevenZip.initSevenZipFromPlatformJAR();
            }
            System.out.println("[" + Utils.getDate() + "] 7-Zip-JBinding library was initialized");
        } catch (SevenZipNativeInitializationException e) {
            e.printStackTrace();
            return "ERRRO: 7-Zip-JBinding library was initialized";
        }

        List<Apk> apkCordova = new ArrayList<Apk>();
        for (Apk apk : apks) {
            if (Utils.checkCordova(apk)) {
                apkCordova.add(apk);
            }

            if (DEBUG) {
                break;
            }
        }

        return new Gson().toJson(apkCordova);
    }

    @GET
    @Path("/checkapps/{from}/{to}")
    public @ResponseBody
    @Produces("application/json")
    String checkAppsFromToPage(@PathParam("from") Integer from, @PathParam("to") Integer to) throws IOException, URISyntaxException {
        List<Apk> apkCordova = new ArrayList<Apk>();
        for (int i = from; i <= to; i++) {
            TypeToken<List<Apk>> token = new TypeToken<List<Apk>>() {
            };
            List<Apk> apks = new Gson().fromJson(checkApps(i), token.getType());
            apkCordova.addAll(apks);
        }

        return new Gson().toJson(apkCordova);
    }

    @GET
    @Path("/checkionicapps")
    public @ResponseBody
    @Produces("application/json")
    String checkIonicApps() throws FileNotFoundException, IOException, URISyntaxException {
        String appsAll = "";
        String pathToJson = servletContext.getRealPath("/ionic/apps-android.json");

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new FileReader(pathToJson));
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (int i = 0 ; i < jsonArray.size() ; i++) {
            JsonObject app = jsonArray.get(i).getAsJsonObject();
            String appId = app.get("id").getAsString();
            String appJson = checkApp(appId);
            //System.out.println(appJson);
            appsAll = appsAll + appJson + ",\n";
        }

        // fix json
        appsAll = "[" + appsAll.substring(0, appsAll.length() - 2) + "]";

        // save all to json file
        File file = new File(getWebRootPath() + "ionicapps.json");
        file.getParentFile().mkdirs();

        try (FileOutputStream fop = new FileOutputStream(file)) {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = appsAll.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        return appsAll;
    }
}
