package cmd

import (
	"fmt"
	"os"

	pui "github.com/manifoldco/promptui"
	"github.com/spf13/cobra"

	httpmock "github.com/esafirm/okmock/httpmock"
)

type onresultfunc func(submit string, answer *httpmock.CreateMockParam)

type question struct {
	OnResult onresultfunc
	Prompt   pui.Prompt
}

// createCmd represents the start command
var createCmd = &cobra.Command{
	Use:   "create",
	Short: "Create mock file with a wizard",
	Run: func(cmd *cobra.Command, args []string) {

		questions := []question{
			{
				Prompt: pui.Prompt{
					Label:   "Name",
					Default: "mock_sample.yaml",
				},
				OnResult: func(submit string, answer *httpmock.CreateMockParam) {
					answer.Name = submit
				},
			},
			{
				Prompt: pui.Prompt{
					Label:   "Path you want to mock. Ex: sample.com/submit/*",
					Default: "sample.com/submit/*",
				},
				OnResult: func(submit string, answer *httpmock.CreateMockParam) {
					answer.Path = submit
				},
			},
			{
				Prompt: pui.Prompt{
					Label:   "Method you want to mock",
					Default: "GET",
				},
				OnResult: func(submit string, answer *httpmock.CreateMockParam) {
					answer.Method = submit
				},
			},
			{
				Prompt: pui.Prompt{
					Label:   "Mock response. Ex: {\"success\":true}",
					Default: "{}",
				},
				OnResult: func(submit string, answer *httpmock.CreateMockParam) {
					answer.Response = submit
				},
			},
		}

		answer, err := askQuestions(questions)
		if err != nil {
			fmt.Println(err)
			return
		}

		httpmock.CreateMockFile(answer)

		// get current working directory
		curr_wd, err := os.Getwd()
		if err != nil {
			fmt.Println(err)
			return
		}

		fmt.Printf("\n✅ Your mock file is created at %s/%s", curr_wd, answer.Name)
	},
}

func askQuestions(questions []question) (httpmock.CreateMockParam, error) {
	var answer = httpmock.CreateMockParam{}

	for i := range questions {
		item := questions[i]
		result, err := item.Prompt.Run()

		if err != nil {
			return httpmock.CreateMockParam{}, err
		}

		item.OnResult(result, &answer)
	}

	return answer, nil
}

func init() {
	rootCmd.AddCommand(createCmd)
}
