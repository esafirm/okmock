package nolambda.gadb.okmock.adapter

data class OkMockPayload(
    val matcher: Matcher,
    val mock: Mock
)

data class Matcher(
    val path: Regex,
    val method: String
)

data class Mock(
    val code: Int,
    val body: String,
    val message: String,
    val headers: Map<String, String>
)
