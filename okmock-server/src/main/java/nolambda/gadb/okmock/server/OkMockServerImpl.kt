package nolambda.gadb.okmock.server

import android.util.Log
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

class OkMockServerImpl(
    private val port: Int = Const.DEFAULT_PORT
) : OkMockServer {

    private var connectionThread: ConnectionThread? = null
    private val clients: MutableMap<Socket, Client> = mutableMapOf()

    private val listeners = mutableSetOf<(String) -> Unit>()

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

    override fun listen(onRead: (String) -> Unit) {
        listeners.add(onRead)
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
        listeners.forEach {
            it.invoke(string)
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

    private fun onError(error: IOException) {
        Log.e("OkMock", "error: $error")
    }

    private class Client(
        val socket: Socket,
        val reader: ReaderThread,
        val writer: WriterThread
    )
}