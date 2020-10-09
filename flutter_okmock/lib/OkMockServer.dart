import 'dart:io';

abstract class OkMockServer {
  void start();
  void stop();

  void listen(String channel, Function(String) onRead);
  void send(String data);
}

class OkMockServerImpl extends OkMockServer {
  static const String SEPARATOR = "|";
  static const int SEPARATOR_LENGTH = 1;

  int port;
  Socket clientSocket;
  ServerSocket serverSocket;
  Map<String, Function> listeners = new Map();

  OkMockServerImpl([int port = 6379]) {
    this.port = port;
  }

  @override
  void listen(String channel, Function onRead) {
    listeners[channel] = onRead;
  }

  @override
  void stop() {
    if (serverSocket != null) {
      serverSocket.close();
    }
  }

  @override
  void start() {
    ServerSocket.bind(InternetAddress.anyIPv4, port)
        .then((ServerSocket server) {
      server.listen((client) {
        this.serverSocket = server;
        this.clientSocket = client;
        this.clientSocket.listen((List<int> event) {
          _onRead(new String.fromCharCodes(event).trim());
        }, onDone: () {
          client.destroy();
        });
      });
    });
  }

  void _onRead(String data) {
    print("OnRead: " + data);

    List channelAndPayload = _parseMessage(data);
    String channel = channelAndPayload[0];
    String payload = channelAndPayload[1];

    listeners[channel].call(payload);
  }

  @override
  void send(String data) {
    clientSocket.write(data + "\n");
  }

  List _parseMessage(String data) {
    int separatorIndex = data.indexOf(SEPARATOR);
    String channel = data.substring(0, separatorIndex);
    String payload = data.substring(separatorIndex + SEPARATOR_LENGTH);
    return [channel, payload];
  }
}
