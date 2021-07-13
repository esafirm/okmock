package nolambda.gadb.okmock.adapter

import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DefaultSerializer : Serializer {

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun serialize(request: Request, mockPayload: OkMockPayload): String {
        val time = dateFormat.format(Date())
        return "$time - Intercept ${request.url} by ${mockPayload.matcher.path}"
    }
}