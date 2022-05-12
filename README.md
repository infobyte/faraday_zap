Its requiered have JDK installed. For ubuntu version you can use 
```
sudo apt install default-jdk
```

> Set development environment

- clone zap-extensions repo ```clone https://github.com/zaproxy/zap-extensions/```
- add faraday folder to ```zap-extensions/addOns```
- add ```"faraday"``` to ```settings.gradle.kts```

> Build
```shell script
./gradlew addOns:faraday:build
```

> Add to Zap

- Go to File dropmenu
- Go to Load Add-on File
- Go to then select the build (zap-extensions/addOns/faraday/build/zapAddOn/bin/faraday-release-2.zap)

> Use

- Go to Tools dropmenu
- Go to Faraday Configuration options
- Complete the fields with your credentials and click in login
- Select the workspace you want to use
- Click the save bottom
- Execute a scan
- Then go to Alerts
- Select the alert/s you want to load
- Do a right click and click in Send alert to faraday.
