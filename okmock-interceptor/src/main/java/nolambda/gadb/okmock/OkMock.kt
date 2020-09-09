package nolambda.gadb.okmock

import nolambda.gadb.okmock.adapter.DefaultSerializer
import nolambda.gadb.okmock.adapter.OkMockAdapter
import nolambda.gadb.okmock.adapter.OkMockPayload
import nolambda.gadb.okmock.adapter.PayloadParser
import nolambda.gadb.okmock.server.OkMockServer
import nolambda.gadb.okmock.server.OkMockServerImpl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class OkMock(
    okMockServer: OkMockServer = OkMockServerImpl(),
    adapter: OkMockAdapter = OkMockAdapter(
        parser = PayloadParser(),
        serializer = DefaultSerializer()
    )
) : Interceptor {

    private val sender = { req: Request, payload: OkMockPayload ->
        okMockServer.send(adapter.serializer.serialize(req, payload))
    }

    init {
        okMockServer.start()
        okMockServer.listen {
            registerMockResponse(adapter.parser.parse(it))
        }
    }

    private val mockResponses = mutableListOf<OkMockPayload>()

    private fun registerMockResponse(payloads: List<OkMockPayload>) {
        mockResponses.clear()
        mockResponses.addAll(payloads)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val (payload, mockResponse) = getMockResponse(request) ?: return chain.proceed(request)

        sender(request, payload)

        return mockResponse
    }

    private fun getMockOrNull(info: PartialRequestInfo): OkMockPayload? {
        val haveMethod = mockResponses.any { it.method == info.method }
        if (haveMethod.not()) return null

        val url = info.url
        return mockResponses.find { it.path.matches(url) }
    }

    private fun getMockResponse(request: Request): Pair<OkMockPayload, Response>? {
        val url: String = request.url.toString()
        val method: String = request.method
        val partialRequest = PartialRequestInfo(url, method)
        val payload = getMockOrNull(partialRequest) ?: return null
        val responseBody = payload.body.toResponseBody("application/text".toMediaTypeOrNull())

        val builder: Response.Builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(payload.code)
            .message(payload.message)
            .receivedResponseAtMillis(System.currentTimeMillis())
            .body(responseBody)

        payload.headers.forEach {
            builder.header(it.key, it.value)
        }
        return payload to builder.build()
    }
}