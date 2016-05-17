# Cordova Apps Finder
Cordova Apps Finder from [apkpure.com](https://apkpure.com/)

Spring, Jersey, HttpComponents, gson, jsoup, 7-Zip-JBinding   

### List all apps for page 1  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/listapps/1  

### Find app with package name com.facebook.lite  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/findapp/com.facebook.lite  

### Check all apps for page 1  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapps/1  

### Check all apps for page 1 to page 10  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapps/1/10  

### Check app (not a Corodva app)  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapp/com.facebook.lite  

### Check app (is a Cordova app)  
Method: GET  
URL: http://localhost:8080/CordovaAppsFinder/rest/api/checkapp/com.lgi.myupc.ch  