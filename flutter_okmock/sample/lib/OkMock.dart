import 'package:dio/dio.dart';
import 'package:sample/OkMockAdapter.dart';
import 'package:sample/OkMockPayload.dart';
import 'package:sample/OkMockServer.dart';
import 'package:sample/PartialRequestInfo.dart';

class OkMock extends Interceptor {
  static const String CHANNEL_MOCK = "mock";
  static const String CHANNEL_CLEAR = "clear";

  Dio dio;
  OkMockServer server;
  OkMockAdapter adapter;

  List<OkMockPayload> mockResponses = new List();

  OkMock({Dio dio, OkMockServer server, OkMockAdapter adapter}) {
    _init(dio, server, adapter);
  }

  OkMock.createDefault(Dio dio) {
    _init(
        dio,
        OkMockServerImpl(),
        OkMockAdapter(
            deserializer: PayloadDeserializer(),
            serializer: DefaultSerializer()));
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

      return dio.resolve(payload.body);
    }
    return options;
  }

  Future<OkMockPayload> getMockResponse(RequestOptions options) async {
    if (mockResponses.isEmpty) return null;

    String method = options.method;
    String url = options.uri.toString();

    print("=> method $method url: $url");

    PartialRequestInfo info = PartialRequestInfo(method: method, url: url);
    return mockResponses.singleWhere(
        (element) => element.method == info.method && element.path == info.url);
  }
}
