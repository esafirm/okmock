package httpmock

import (
	"encoding/json"
	"os"
	"strings"
)

type CreateMockParam struct {
	Name     string
	Path     string
	Method   string
	Response string
}

// Create mock file from mock context
func CreateMockFile(param CreateMockParam) {

	// Serialize mock data
	data := serializeMockData(param)

	// Write file
	file, err := os.Create(param.Name)
	if err != nil {
		panic(err)
	}
	defer file.Close()
	file.Write(data)
}

func serializeMockData(param CreateMockParam) []byte {
	context := MockContext{
		Matcher: MockMatcher{
			Path:   param.Path,
			Method: param.Method,
		},
		Mock: MockResponse{
			Code:   "200",
			Body:   param.Response,
			Header: nil,
		},
	}

	if strings.Contains(param.Name, ".json") {
		// Convert to JSON string
		data, err := json.Marshal(context)
		if err != nil {
			panic(err)
		}
		return data
	}

	data, err := objectToYaml(context)
	if err != nil {
		panic(err)
	}

	return data
}
