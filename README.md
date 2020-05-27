
> Set development environment

- clone zap-extensions repo ```clone https://github.com/zaproxy/zap-extensions/```
- add faraday folder to ```zap-extensions/addOns```
- add ```"faraday"``` to ```settings.gradle.kts```

> build
```shell script
./gradlew addOns:faraday:build
```