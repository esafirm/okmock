class Matcher {
  RegExp path;
  String method;

  Matcher.fromJson(Map<String, Object> json) {
    String regexString = _createRegexFromGlob(json["path"]);
    print("regexString: $regexString");

    path = RegExp(regexString);
    method = json["method"];
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

class Mock {
  String body;
  int code;
  String message;
  Map<String, String> headers;

  Mock.fromJson(Map<String, dynamic> json) {
    body = json["body"].toString();
    code = json["code"];
    message = json["message"];
    headers = json["headers"];
  }
}

class OkMockPayload {
  Matcher matcher;
  Mock mock;

  OkMockPayload.fromJson(Map<String, dynamic> json) {
    Map<String, dynamic> matcherJson = json["matcher"];
    Map<String, dynamic> mockJson = json["mock"];

    matcher = Matcher.fromJson(matcherJson);
    mock = Mock.fromJson(mockJson);
  }
}
