## OkMock

Dio interceptor for mocking HTTP request in Flutter

## Prerequisite

- [gadb](https://github.com/esafirm/gadb)

## Quick Start

### Flutter Side

1. Add `OkMock` as your dependency

```groovy
dependencies:
  okmock: "0.0.1"
```

2. Add `OkMock` as `Dio` interceptor

```dart
dio = Dio();
dio.interceptors.add(OkMock.createDefault(dio));
```

### Desktop Side

1. After install `gadb`, create mock file in json or yaml

```json
{
  "path": "*google.com*",
  "method": "GET",
  "body": {
    "message": "hello mock"
  }
}
```

2. Run `gadb mock -f <file>`

That's it! Now every time a request is matching the `path` and `method`, your request will return `body`

## Example

You can also check out the [flutter app example](https://github.com/esafirm/okmock/tree/master/flutter_sample)

## License

MIT @ Esa Firman
