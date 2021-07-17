import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:intl/intl.dart';

import 'OkMockPayload.dart';

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
    List<String> mocks = data.split(SEPARATOR);
    List<OkMockPayload> payloads =
        mocks.map((e) => OkMockPayload.fromJson(jsonDecode(e))).toList();
    return payloads;
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
