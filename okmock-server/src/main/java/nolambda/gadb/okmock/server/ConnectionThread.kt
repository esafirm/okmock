package nolambda.gadb.okmock.server

import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

internal class ConnectionThread(
    private val port: Int,
    private val onClientConnected: (Socket) -> Unit,
    private val onError: (IOException) -> Unit
) : Thread() {

    @Volatile
    private var socketRef: ServerSocket? = null

    override fun run() {
        val serverSocket = try {
            ServerSocket().apply {
                reuseAddress = true
                bind(InetSocketAddress(port))
            }
        } catch (e: IOException) {
            onError(e)
            return
        }

        socketRef = serverSocket

        try {
            while (!isInterrupted) {
                val socket = try {
                    serverSocket.accept()
                } catch (e: IOException) {
                    onError(e)
                    return
                }

                onClientConnected(socket)
            }
        } catch (e: IOException) {
            onError(e)
        } catch (ignored: InterruptedException) {
            interrupt()
        } finally {
            serverSocket.closeSafe()
        }
    }

    override fun interrupt() {
        super.interrupt()

        thread { socketRef?.closeSafe() }
    }
}
