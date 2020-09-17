## OkMock

OkHttp interceptor for mocking Android request

## Prerequisite

- [gadb](https://github.com/esafirm/gadb)

## Quick Start

### Android Side

1. Add `OkMock` as your dependency
2. Add `OkMock` as `OkHttp` application interceptor

```kotlin
OkHttpClient.Builder()
	.addInterceptor(OkMock())
	.build()
```

### Desktop Side

1. After install `gadb`, create mock file with prefix `mock_`

```json
{
  "path": "*google.com*",
  "method": "GET",
  "body": {
    "message": "hello mock"
  }
}
```
E
2. Run `gadb mock`

That's it! Now every time a request is matching the `path` and `method`, your request will return `body`

## License

MIT @ Esa Firman
