package httpmock

import (
	"strings"
)

const (
	CHANNEL_CLEAR     = "clear"
	CHANNEL_MOCK      = "mock"
	PAYLOAD_SEPARATOR = "|"
	SEPARATOR         = "_,_"
)

func createMockPayload(mockString []string) string {
	return CHANNEL_MOCK + PAYLOAD_SEPARATOR + strings.Join(mockString, SEPARATOR)
}

func createClearPayload() string {
	return CHANNEL_CLEAR + PAYLOAD_SEPARATOR
}
