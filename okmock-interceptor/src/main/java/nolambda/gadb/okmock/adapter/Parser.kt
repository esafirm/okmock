package nolambda.gadb.okmock.adapter

interface Parser {
    fun parse(data: String): List<OkMockPayload>
}