package nolambda.gadb.okmock

import nolambda.gadb.okmock.server.OkMockServer
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

data class PartialRequestInfo(
    val url: String,
    val method: String
)

class OkMock(
    okMockServer: OkMockServer
) : Interceptor {

    init {
        okMockServer.listen {
            registerMockResponse(it)
        }
    }

    private val mockResponseMap = mutableMapOf<PartialRequestInfo, String>()

    private fun registerMockResponse(data: String) {

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return getMockResponse(request) ?: return chain.proceed(request)
    }

    private fun getMockResponse(request: Request): Response? {
        val url: String = request.url.toString()
        val method: String = request.method
        val partialRequest = PartialRequestInfo(url, method)
        if (!mockResponseMap.containsKey(partialRequest)) {
            return null
        }
        val mockResponse = mockResponseMap[partialRequest]
        val responseBody = mockResponse?.toResponseBody("application/text".toMediaTypeOrNull())

        val builder: Response.Builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OkMock")
            .receivedResponseAtMillis(System.currentTimeMillis())
            .body(responseBody)

//        if (mockResponse.headers != null && !mockResponse.headers.isEmpty()) {
//            for (header in mockResponse.headers) {
//                if (!TextUtils.isEmpty(header.name) && !TextUtils.isEmpty(header.value)) {
//                    builder.header(header.name, header.value)
//                }
//            }
//        }
        return builder.build()
    }
}