import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:intl/intl.dart';

import 'okmock_payload.dart';
import 'dart:developer' as developer;

abstract class Deserializer {
  List<OkMockPayload> deserialize(String data);
}

abstract class Serializer {
  String serialize(RequestOptions options, OkMockPayload payload);
}

class PayloadDeserializer extends Deserializer {
  static const String SEPARATOR = "_,_";

  static const int DEFAULT_CODE = 200;
  static const String DEFAULT_MESSAGE = "Mocked by OkMock";

  @override
  List<OkMockPayload> deserialize(String data) {
    developer.log("data: $data");

    List<String> mocks = data.split(SEPARATOR);
    developer.log("mock size: ${mocks.length}");

    List<OkMockPayload> payloads = mocks.map((mock) => createPayload(mock))
        .toList();
    return payloads;
  }

  OkMockPayload createPayload(String mock) {
    developer.log("Create payload from $mock");
    return OkMockPayload.fromJson(jsonDecode(mock));
  }
}

class DefaultSerializer extends Serializer {
  final DateFormat format = DateFormat('HH:mm:ss');

  @override
  String serialize(RequestOptions options, OkMockPayload payload) {
    String time = format.format(DateTime.now());
    return "$time - Intercept ${options.uri.path} by ${payload.matcher.path}";
  }
}

class OkMockAdapter {
  Deserializer deserializer;
  Serializer serializer;

  OkMockAdapter({Deserializer deserializer, Serializer serializer})
      : deserializer = deserializer,
        serializer = serializer;
}
