import 'package:dio/dio.dart';
import 'package:sample/OkMock.dart';

Dio _dio;

void getHttp() async {
  try {
    Response response = await getDio().get("http://www.google.com");
    print(response);
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