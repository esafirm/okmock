package nolambda.gadb.okmock.parser

interface Parser {
    fun parse(data: String): List<OkMockPayload>
}