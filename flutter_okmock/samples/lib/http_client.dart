import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:okmock/OkMock.dart';

import 'dart:developer' as developer;

class HttpClient {
  static HttpClient _instance;

  factory HttpClient() => _instance ??= new HttpClient._internal();

  Dio _dio;

  HttpClient._internal() {
    _dio = Dio();
    _dio.interceptors.add(OkMock.createDefault(_dio));
  }

  Future<Response> requestLogin(String username, String password) async {
    developer
        .log("request login with username: $username and password: $password");

    final auth = 'Basic ' + base64Encode(utf8.encode('$username:$password'));
    final url = "https://httpbin.org/basic-auth/okmock/12345";
    final res = await _dio.get(url,
        options: Options(headers: <String, String>{'authorization': auth}));

    developer.log("response: $res");

    return Future.value(res);
  }
}
