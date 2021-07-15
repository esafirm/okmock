import 'package:okmock/OkMockPayload.dart' as OkMock;

void main() {
  // create map with key and value
  final matcherJson = new Map<String, Object>();
  matcherJson["path"] = "google.com";
  matcherJson["method"] = "GET";

  final matcher = OkMock.Matcher.fromJson(matcherJson);

  assert(matcher.path.hasMatch("google.com"));
  assert(matcher.method == matcherJson["method"]);
}
