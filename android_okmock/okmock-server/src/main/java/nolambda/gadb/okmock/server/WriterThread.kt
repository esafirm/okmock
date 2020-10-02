package nolambda.gadb.okmock.server

import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class WriterThread(
    private val socket: Socket,
    private val onDisconnected: () -> Unit = {},
    private val onError: (IOException) -> Unit = {}
) : Thread() {

    private val queue = LinkedBlockingQueue<String>()

    override fun run() {
        try {
            val writer = PrintWriter(socket.getOutputStream(), true)
            while (!isInterrupted) {
                if (queue.isEmpty().not()) {
                    writer.println(queue.take())
                }
            }
        } catch (e: IOException) {
            onError(e)
        } catch (e: InterruptedException) {
            interrupt()
        } finally {
            socket.closeSafe()
            onDisconnected()
        }
    }

    fun write(string: String) {
        queue += string
    }
}