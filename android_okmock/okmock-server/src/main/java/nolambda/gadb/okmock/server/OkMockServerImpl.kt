package nolambda.gadb.okmock.server

import android.util.Log
import java.net.Socket
import kotlin.concurrent.thread

class OkMockServerImpl(
    private val port: Int = Const.DEFAULT_PORT
) : OkMockServer {

    private var connectionThread: ConnectionThread? = null
    private val clients: MutableMap<Socket, Client> = mutableMapOf()

    private val listeners = mutableListOf<Pair<String, (String) -> Unit>>()

    override fun start() {
        if (connectionThread != null) return

        val connectionThread = ConnectionThread(
            port = port,
            onClientConnected = ::onClientConnected,
            onError = ::onError
        )

        this.connectionThread = connectionThread
        connectionThread.start()
    }

    override fun stop() {
        connectionThread?.interrupt()
        connectionThread = null
        closeClients(clients.values)
        clients.clear()
    }

    override fun listen(channel: String, onRead: (String) -> Unit) {
        listeners.add(channel to onRead)
    }

    override fun send(data: String) {
        clients.forEach {
            it.value.writer.write(data)
        }
    }

    private fun onClientConnected(socket: Socket) {
        Log.d("OkMock", "client connected")

        val reader = ReaderThread(
            socket = socket,
            onRead = ::onRead,
            onDisconnected = { onClientDisconnected(socket) }
        )

        val writer = WriterThread(
            socket = socket,
            onDisconnected = { onClientDisconnected(socket) }
        )

        clients[socket] = Client(socket, reader, writer)

        reader.start()
        writer.start()
    }

    private fun onRead(string: String) {
        Log.d("OkMock", "onRead: $string")
        try {
            val (channel, payload) = string.parse()

            listeners.forEach {
                if (it.first == channel) {
                    it.second.invoke(payload)
                }
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun onClientDisconnected(socket: Socket) {
        clients[socket]?.also {
            closeClients(listOf(it))
        }
        clients.remove(socket)
    }

    private fun closeClients(clients: Iterable<Client>) {
        clients.forEach {
            it.reader.interrupt()
            it.writer.interrupt()
        }

        thread {
            clients.forEach {
                it.socket.closeSafe()
            }
        }
    }

    private fun onError(error: Exception) {
        Log.e("OkMock", "error: $error")
        error.printStackTrace()
    }

    private fun String.parse(): Pair<String, String> {
        val separatorIndex = indexOf(SEPARATOR)
        val channel = substring(0, separatorIndex)
        val payload = substring(separatorIndex + SEPARATOR_LENGTH)
        return channel to payload
    }

    private class Client(
        val socket: Socket,
        val reader: ReaderThread,
        val writer: WriterThread
    )

    companion object {
        private const val SEPARATOR = "|"
        private const val SEPARATOR_LENGTH = 1
    }
}