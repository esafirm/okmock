class OkMockPayload {
  RegExp path;
  String body;
  String method;
  int code;
  String message;
  Map<String, String> headers;

  OkMockPayload.fromJson(Map<String, dynamic> json) {
    String regexString = _createRegexFromGlob(json["path"]);
    print("regexString: $regexString");

    path = RegExp(regexString);
    body = json["body"].toString();
    method = json["method"];
    code = json["code"];
    message = json["message"];
    headers = json["headers"];
  }

  String _createRegexFromGlob(String glob) {
    String out = "^";
    glob.runes.forEach((element) {
      String char = String.fromCharCode(element);
      switch (char) {
        case "*":
          out += ".*";
          break;
        case "?":
          out += '.';
          break;
        case ".":
          out += "\\.";
          break;
        case "\\":
          out += "\\\\";
          break;
        default:
          out += char;
          break;
      }
    });
    return out;
  }
}
