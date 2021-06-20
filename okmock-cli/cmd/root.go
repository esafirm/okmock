package cmd

import (
	"fmt"
	"github.com/spf13/cobra"
	"os"

	adb "github.com/esafirm/okmock/adb"
	httpmock "github.com/esafirm/okmock/httpmock"
)

var dir string
var file string
var prefix string

var rootCmd = &cobra.Command{
	Use:   "okmock",
	Short: "Mock HTTP(s) response to OkMock interceptors",
	Long:  `OkMock is a tool to mock HTTP(s) responses for Okhttp interceptor in Android or Dio in Flutter`,
	Args:  cobra.MaximumNArgs(0),
	Run: func(cmd *cobra.Command, args []string) {
		forwardList := adb.ForwardList()

		// Handle blank
		if len(forwardList.Output) <= 1 {
			fmt.Printf("Forwarding port to %d", forwardList)
			adb.Forward(httpmock.DEFAULT_PORT)
		}

		if &dir == nil || len(dir) == 0 {
			currentDir, _ := os.Getwd()
			dir = currentDir
		}

		// Printing current config
		fmt.Printf("Preparing…\n")
		fmt.Printf("Dir: %s\n", dir)
		fmt.Printf("Prefix: %s\n\n", prefix)

		var mockContexts []httpmock.MockContext
		var err error

		if &file == nil || len(file) == 0 {
			mockContexts, err = httpmock.ReadMockFiles(dir, &prefix)
		} else {
			mockContexts, err = httpmock.ReadMockFile(file)
		}

		if err != nil {
			fmt.Printf("Error: %s\n", err)
			os.Exit(2)
		}

		httpmock.Connect(mockContexts)
	},
}

func init() {
	rootCmd.Flags().StringVarP(&dir, "directory", "d", "", "Set mock file to all json in passed directory")
	rootCmd.Flags().StringVarP(&file, "file", "f", "", "Set mock file to passed file")
	rootCmd.Flags().StringVarP(&prefix, "prefix", "p", "mock_", "Set prefix for mock file")
}

func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Fprintln(os.Stderr, err)
		os.Exit(1)
	}
}
