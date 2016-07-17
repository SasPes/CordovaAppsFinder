var appsCount = 0;

var loadJSON = function (callback) {
    var xobj = new XMLHttpRequest();
    xobj.overrideMimeType("application/json");
    xobj.open('GET', 'ionic/apps.json', true);
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
    loadJSON(function (apps) {
        var appsJson = JSON.parse(apps);

        var appsDiv = document.getElementById("apps");

        for (var i = 0; i < appsJson.length; i++) {
            for (var j = 0; j < appsJson[i].length; j++) {
                //console.log(appsJson[i][j]);
                appsCount++;

                if (appsJson[i][j].android_url.includes("play.google.com")) {
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
    });
};