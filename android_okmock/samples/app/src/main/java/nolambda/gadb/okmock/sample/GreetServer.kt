package nolambda.gadb.okmock.sample

import nolambda.gadb.okmock.server.Const
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class GreetServer {
    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null

    fun start(port: Int) {
        serverSocket = ServerSocket(port)
        clientSocket = serverSocket.accept()
        writer = PrintWriter(clientSocket.getOutputStream(), true)
//        reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

        val greeting = "A"
//        val greeting = reader?.readLine()
//        println("Receive greeting: $greeting")

        while (true) {
            if (greeting == "halo") {
                writer?.println("hello client")
            } else {
                writer?.println("unrecognised greeting")
            }
        }
    }

    fun stop() {
        reader?.close()
        writer?.close()
        clientSocket.close()
        serverSocket.close()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val server = GreetServer()
            server.start(Const.DEFAULT_PORT)
        }
    }
}


class GreetClient {
    private var clientSocket: Socket? = null
    private var out: PrintWriter? = null
    private var reader: BufferedReader? = null

    fun startConnection(ip: String?, port: Int) {
        clientSocket = Socket(ip, port)
        out = PrintWriter(clientSocket!!.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))
    }

    fun sendMessage(msg: String?): String {
        out!!.println(msg)
        return reader!!.readLine()
    }

    fun stopConnection() {
        reader!!.close()
        out!!.close()
        clientSocket!!.close()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val client = GreetClient()
            client.startConnection("localhost", Const.DEFAULT_PORT)
            println("Connected! Sending message …")
            val response = client.sendMessage("halo")
            println("Got response: $response")
        }
    }
}