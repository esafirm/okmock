package httpmock

import (
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"path/filepath"
	"strings"
)

type MockMatcher struct {
	Path   string `json:"path" yaml:"path"`
	Method string `json:"method" yaml:"method"`
}

type MockResponse struct {
	Code   string      `json:"code" yaml:"code"`
	Body   interface{} `json:"body" yaml:"body"`
	Header interface{} `json:"header" yaml:"header"`
}

type MockContext struct {
	Matcher MockMatcher  `json:"matcher" yaml:"matcher"`
	Mock    MockResponse `json:"mock" yaml:"mock"`
	Raw     string       `json:"-" yaml:"-"`
}

func ReadMockFile(path string) ([]MockContext, error) {
	files := []string{path}
	if len(files) == 0 {
		return nil, errors.New(fmt.Sprintf("No mock files in %s", path))
	}

	return read(files)
}

func ReadMockFiles(dir string, prefix *string) ([]MockContext, error) {
	files := listFiles(dir, *prefix)
	if len(files) == 0 {
		return nil, errors.New(fmt.Sprintf("No mock files in %s with prefix %s", dir, *prefix))
	}
	return read(files)
}

func read(files []string) ([]MockContext, error) {
	contents := getContents(files)
	contexts := getContexts(contents)
	return contexts, nil
}

func getContexts(jsons []string) []MockContext {
	var contexts []MockContext
	for _, jsonString := range jsons {
		var context MockContext
		json.Unmarshal([]byte(jsonString), &context)
		context.Raw = jsonString
		contexts = append(contexts, context)
	}
	return contexts
}

func getContents(files []string) []string {
	var contents []string
	for _, f := range files {
		ext := strings.ToLower(filepath.Ext(f))
		file, err := ioutil.ReadFile(f)
		if err != nil {
			continue
		}

		var content string
		if ext == ".yml" || ext == ".yaml" {
			content = SpaceMap(YamlToJsonString(file))
		} else {
			content = SpaceMap(string(file))
		}
		contents = append(contents, content)
	}
	return contents
}

func listFiles(dir string, prefix string) []string {
	var files []string
	fileInfos, err := ioutil.ReadDir(dir)
	CheckErr(err)

	for _, info := range fileInfos {
		if strings.HasPrefix(info.Name(), prefix) {
			files = append(files, info.Name())
		}
	}
	return files
}
