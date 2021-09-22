## OkMock

OkHttp interceptor for mocking Android request

## Prerequisite

- [okmock-cli](https://github.com/esafirm/okmock/tree/main/okmock-cli)

## Quick Start

### Android Side

1. Add `OkMock` as your dependency

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

```
dependencies {
  implementation 'com.github.esafirm:okmock:1.0.0'
}
```

2. Add `OkMock` as `OkHttp` application interceptor

```kotlin
OkHttpClient.Builder()
	.addInterceptor(OkMock())
	.build()
```

### Desktop Side

1. After install `okmock-cli`, create mock file

```json
{
  "path": "*google.com*",
  "method": "GET",
  "body": {
    "message": "hello mock"
  }
}
```

2. Run `okmcok -f <file>`

That's it! Now every time a request is matching the `path` and `method`, your request will return `body`

## License

MIT @ Esa Firman
