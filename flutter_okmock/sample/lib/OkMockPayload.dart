class OkMockPayload {
  String path;
  String body;
  String method;
  int code;
  String message;
  Map<String, String> headers;

  OkMockPayload.fromJson(Map<String, dynamic> json) {
    path = json["path"];
    body = json["body"];
    method = json["method"];
    code = json["code"];
    message = json["message"];
    headers = json["headers"];
  }
}
