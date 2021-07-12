BIN_PATH=$(GOPATH)/bin/okmock

all: build 
build:
	go build -o okmock -v
buildbin: 
	go build -o $(BIN_PATH)
run: 
	# This is for the case where you want to run the binary
	# @go run main.go -d mocksamples
	@go run main.go create