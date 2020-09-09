package nolambda.gadb.okmock.server

import java.io.Closeable
import java.net.ServerSocket
import java.net.Socket

fun Any.closeSafe() {
    try {
        when (this) {
            is Closeable -> close()
            is Socket -> close()
            is ServerSocket -> close()
        }
    } catch (ignored: Exception) {
    }
}