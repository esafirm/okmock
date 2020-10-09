package nolambda.gadb.okmock.server

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class ReaderThread(
    private val socket: Socket,
    private val onRead: (String) -> Unit,
    private val onDisconnected: () -> Unit = {},
    private val onError: (IOException) -> Unit = {}
) : Thread() {

    override fun run() {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (!isInterrupted) {
                val line = reader.readLine()
                if (line != null) {
                    onRead(line)
                }
            }
        } catch (e: IOException) {
            onError(e)
        } finally {
            socket.closeSafe()
            onDisconnected()
        }
    }
}