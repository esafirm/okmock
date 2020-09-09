package nolambda.gadb.okmock.parser

data class OkMockPayload(
    val path: Regex,
    val body: String,
    val method: String,
    val code: Int,
    val message: String,
    val headers: Map<String, String>
)