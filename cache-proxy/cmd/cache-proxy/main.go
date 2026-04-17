package main

import (
	"flag"
)

func main() {

	PORT := flag.Int("port", 0, "Define PORT on which the caching proxy server will run")
	ORIGIN := flag.String("origin", "", "Define the URL of the server to which the requests will be forwarded")
	CLEAR_CACHE := flag.Bool("clear-cache", false, "Clear the cache")
	flag.Parse()

}