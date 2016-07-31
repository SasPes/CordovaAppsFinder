/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import java.util.ArrayList;

/**
 *
 * @author SasPes
 */
public class Apk {

    String name;
    String link;
    String appId;
    String download;
    String image;
    ArrayList<Plugin> plugins;

    public Apk() {
    }

    public Apk(String name, String link, String appId, String download, String image, ArrayList<Plugin> plugins) {
        this.name = name;
        this.link = link;
        this.appId = appId;
        this.download = download;
        this.image = image;
        this.plugins = plugins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(ArrayList<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public String toString() {
        return "\nApk{" + "name=" + name + ", link=" + link + ", download=" + download + ", image=" + image + '}';
    }
    
    
}
