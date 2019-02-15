## About
The project duplicate all methods of SPLOT API. Its collect all results by each API-method from all configed SPLOT-resources and return consolidated response.

The project have cache.

The next methods used cached result:
- /api/v1/get/countries
- /api/v1/get/cities
- /api/v1/get/trips
- /api/v1/get/route-info

## Tomcat settings

Its important to add to tomcat context allowCasualMultipartParsing property

```xml
<Context allowCasualMultipartParsing="true" path="/">
    <Resources cachingAllowed="true" cacheMaxSize="100000" />

    <!-- Default set of monitored resources. If one of these changes, the    -->
    <!-- web application will be reloaded.                                   -->
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <WatchedResource>WEB-INF/tomcat-web.xml</WatchedResource>
    <WatchedResource>${catalina.base}/conf/web.xml</WatchedResource>

    <!-- Uncomment this to disable session persistence across Tomcat restarts -->
    <!--
    <Manager pathname="" />
    -->
</Context>
```

## Logging
For config logging properties edit file **/matrix-service/src/main/resources/log4j2.xml** or in deploed project **/matrix-service/WEB-INF/classes/log4j2.xml**

## Resources configuration
For config resources properties edit file **/matrix-service/src/main/resources/resource.properties** or in deploed project **/matrix-service/WEB-INF/classes/resource.properties**

**url.0 ... url.N** - the addresses of servers with splot-api. These servers must have the same login/password.

**login** and **password** used only for ping **urls**. Users of matrix-service can have another login/password.
