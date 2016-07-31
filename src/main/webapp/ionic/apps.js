var appsCount = 0;
var appsAndroidCount = 0;

var loadJSON = function (url, callback) {
    var xobj = new XMLHttpRequest();
    xobj.overrideMimeType("application/json");
    xobj.open('GET', url, true);
    xobj.onreadystatechange = function () {
        if (xobj.readyState === 4 && xobj.status === 200) {
            callback(xobj.responseText);
        }
    };
    xobj.send(null);
};

var getAndroidId = function (android) {
    var id = android.substring(android.indexOf("id=") + 3);
    if (id.includes("&")) {
        id = id.substring(0, id.indexOf("&"));
    }
    return id;
};

var init = function () {
    loadJSON('ionic/apps.json', function (apps) {
        var appsJson = JSON.parse(apps);

        var appsDiv = document.getElementById("apps");

        for (var i = 0; i < appsJson.length; i++) {
            for (var j = 0; j < appsJson[i].length; j++) {
                //console.log(appsJson[i][j]);
                appsCount++;

                if (appsJson[i][j].android_url.includes("play.google.com")) {
                    appsAndroidCount++;
                    var android = appsJson[i][j].android_url;

                    var appDiv =
                            "<a href='" + android + "' target='_blank'>" +
                            appsJson[i][j].app_name +
                            "</a> | " +
                            getAndroidId(android);
                    appsDiv.innerHTML = appsDiv.innerHTML + appsCount + ". " + appDiv + "<br/>";
                }
            }
        }

        var divCount = document.getElementById("appsCount");
        divCount.innerHTML = divCount.innerHTML + appsCount;

        var divAndroidCount = document.getElementById("appsAndroidCount");
        divAndroidCount.innerHTML = divAndroidCount.innerHTML + appsAndroidCount;
    });
};

var generateAndroidJson = function () {
    loadJSON('ionic/apps.json', function (apps) {
        var appsJson = JSON.parse(apps);
        var appJson = "[";

        for (var i = 0; i < appsJson.length; i++) {
            for (var j = 0; j < appsJson[i].length; j++) {
                //console.log(appsJson[i][j]);
                if (appsJson[i][j].android_url.includes("play.google.com")) {
                    appJson = appJson +
                            "{\"name\": \"" + appsJson[i][j].app_name.replace(/\"/g, "") + "\", " +
                            "\"id\": \"" + getAndroidId(appsJson[i][j].android_url) + "\"},";
                }
            }
        }
        appJson = appJson.substring(0, appJson.length - 1) + "]";

        var url = 'data:text/json;charset=utf8,' + encodeURIComponent(appJson);
        window.open(url, '_blank');
        window.focus();
    });
};

var generateUsedPluginsJson = function () {
    loadJSON('ionic/ionicapps.json', function (apps) {
        var appsJson = JSON.parse(apps);
        var pluginsAssociativeJson = [];

        for (var i = 0; i < appsJson.length; i++) {
            // count plugins
            // appsJson[0].plugins[0].name
            var plugins = appsJson[i].plugins;
            if (typeof plugins !== 'undefined') {
                for (var j = 0; j < plugins.length; j++) {
                    pluginsAssociativeJson[plugins[j].name] =
                            typeof pluginsAssociativeJson[plugins[j].name] === 'undefined' ?
                            1 :
                            pluginsAssociativeJson[plugins[j].name] + 1;
                }
            } else {
                console.log("no plugins");
            }
        }

        var pluginsJson = "";
        for (var i in pluginsAssociativeJson) {
            // console.log(i + " = " + pluginsJson[i]);
            pluginsJson = pluginsJson +
                    "{\"name\": \"" + i + "\", " +
                    "\"count\": \"" + pluginsAssociativeJson[i] + "\"}, ";
        }
        pluginsJson = "[" + pluginsJson.substring(0, pluginsJson.length - 2) + "]";

        pluginsJson = JSON.parse(pluginsJson).sort(predicatBy("count"));

        var url = 'data:text/json;charset=utf8,' + encodeURIComponent(JSON.stringify(pluginsJson));
        window.open(url, '_blank');
        window.focus();
    });
};

var predicatBy = function (prop) {
    return function (a, b) {
        if (parseInt(a[prop]) > parseInt(b[prop])) {
            return -1;
        } else if (parseInt(a[prop]) < parseInt(b[prop])) {
            return 1;
        }
        return 0;
    };
};