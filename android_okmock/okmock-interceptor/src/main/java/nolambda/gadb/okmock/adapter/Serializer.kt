package nolambda.gadb.okmock.adapter

import okhttp3.Request

interface Serializer {
    fun serialize(request: Request, mockPayload: OkMockPayload): String
}