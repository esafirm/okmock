BIN_PATH=$(GOPATH)/bin/okmock

all: build 
build:
	go build -o okmock -v
buildbin: 
	go build -o $(BIN_PATH)
run: 
	@go run main.go -d mocksamples