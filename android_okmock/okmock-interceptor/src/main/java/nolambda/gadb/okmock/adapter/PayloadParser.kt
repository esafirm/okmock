package nolambda.gadb.okmock.adapter

import org.json.JSONObject


class PayloadParser : Parser {

    companion object {
        private const val SEPARATOR = "_,_"

        private const val DEFAULT_CODE = 200
        private const val DEFAULT_MESSAGE = "Mocked by OkMock"
    }

    override fun parse(data: String): List<OkMockPayload> {
        val mocks = data.split(SEPARATOR)
        val jsonList = mocks.map { JSONObject(it) }

        return jsonList.map {
            val matcherJson = it.getJSONObject("matcher")
            val mockJson = it.getJSONObject("mock")

            val path = matcherJson.getString("path")

            val matcher = Matcher(
                path = Regex(createRegexFromGlob(path)),
                method = matcherJson.getString("method")
            )

            val mock = Mock(
                body = mockJson.getString("body"),
                headers = mockJson.getMap("header"),
                code = mockJson.getIntOrDefault("code", DEFAULT_CODE),
                message = mockJson.getStringOrDefault("message", DEFAULT_MESSAGE)
            )

            OkMockPayload(
                matcher = matcher,
                mock = mock
            )
        }
    }

    private fun JSONObject.getMap(key: String): Map<String, String> {
        if (has(key).not()) return emptyMap()

        val jsonObject = getJSONObject(key)
        val result = mutableMapOf<String, String>()
        jsonObject.keys().forEach {
            result[it] = jsonObject.getString(it)
        }

        return result
    }

    private fun JSONObject.getStringOrDefault(key: String, default: String): String {
        return if (has(key)) {
            getString(key)
        } else default
    }

    private fun JSONObject.getIntOrDefault(key: String, default: Int): Int {
        return if (has(key)) {
            getInt(key)
        } else default
    }

    private fun createRegexFromGlob(glob: String): String {
        var out = "^"
        for (element in glob) {
            when (element) {
                '*' -> out += ".*"
                '?' -> out += '.'
                '.' -> out += "\\."
                '\\' -> out += "\\\\"
                else -> out += element
            }
        }
        out += '$'
        return out
    }
}