import 'package:dio/dio.dart';
import 'package:okmock/OkMock.dart';

Dio _dio;

dynamic getHttp() async {
  try {
    Response response = await getDio().get("http://www.google.com");
    return response.data;
  } catch (e) {
    print(e);
  }
}

Dio getDio() {
  if (_dio == null) {
    _dio = Dio();
    _dio.interceptors.add(OkMock.createDefault(_dio));
  }
  return _dio;
}
