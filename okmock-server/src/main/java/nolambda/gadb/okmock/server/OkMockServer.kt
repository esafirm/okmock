package nolambda.gadb.okmock.server

interface OkMockServer {
    fun start()
    fun stop()

    fun listen(onRead: (String) -> Unit)
    fun send(data: String)
}