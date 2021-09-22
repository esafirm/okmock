import 'package:dio/dio.dart';

import 'okmock_adapter.dart';
import 'okmock_payload.dart';
import 'okmock_server.dart';
import 'partial_request.dart';

import 'dart:developer' as developer;

class OkMock extends Interceptor {
  static const String CHANNEL_MOCK = "mock";
  static const String CHANNEL_CLEAR = "clear";

  Dio dio;
  OkMockServer server;
  OkMockAdapter adapter;

  List<OkMockPayload> mockResponses = [];

  OkMock({Dio dio, OkMockServer server, OkMockAdapter adapter}) {
    _init(dio, server, adapter);
  }

  OkMock.createDefault(Dio dio) {
    _init(dio, OkMockServerImpl(), OkMockAdapter(deserializer: PayloadDeserializer(), serializer: DefaultSerializer()));
  }

  void _init(Dio dio, OkMockServerImpl server, OkMockAdapter adapter) {
    this.dio = dio;
    this.server = server;
    this.adapter = adapter;

    server.listen(CHANNEL_MOCK, (mock) {
      _registerMockResponse(adapter.deserializer.deserialize(mock));
    });
    server.listen(CHANNEL_CLEAR, (_) => _clearMockResponse());
    server.start();
  }

  void _registerMockResponse(List<OkMockPayload> payloads) {
    mockResponses.clear();
    mockResponses.addAll(payloads);
  }

  void _clearMockResponse() {
    print("Clearing mocks…");
    mockResponses.clear();
  }

  @override
  Future onRequest(RequestOptions options) async {
    OkMockPayload payload = await getMockResponse(options);
    if (payload != null) {
      server.send(adapter.serializer.serialize(options, payload));

      Mock mockPayload = payload.mock;
      Response response = await dio.resolve(Response(
          request: options,
          data: mockPayload.body,
          statusCode: mockPayload.code,
          statusMessage: mockPayload.message,
          headers: Headers()));

      mockPayload.headers.forEach((key, value) {
        response.headers.add(key, value.toString());
      });

      return response;
    }
    return options;
  }

  Future<OkMockPayload> getMockResponse(RequestOptions options) async {
    if (mockResponses.isEmpty) return null;

    String method = options.method;
    String url = options.uri.toString();

    developer.log("=> method $method url: $url", name: "OkMock");

    PartialRequestInfo info = PartialRequestInfo(method: method, url: url);
    OkMockPayload payload = mockResponses.firstWhere((element) {
      return element.matcher.method == info.method && element.matcher.path.hasMatch(info.url);
    }, orElse: () => null);

    return payload;
  }
}
