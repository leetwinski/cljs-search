#!/bin/bash

build_server() {
    echo ">>> BUILDING SERVER <<<"
    lein clean
    lein ring uberjar
    docker build -f ./Dockerfile_server -t search-api:latest .
}

build_client() {
    echo ">>> BUILDING CLIENT <<<"
    lein clean
    lein cljsbuild once
    docker build -f ./Dockerfile_client -t search-ui:latest .
}

build_elastic() {
    echo ">>> BUILDING ELASTIC <<<"
    cd elastic
    docker build --tag=elastic-custom . 
}

print_usage() {
    cat <<EOF >&2
build docker images:
-a|--all      build all
-c|--client   build client
-s|--server   build server
-e|--elastic  build elastic
EOF

}

if [[ 0 -eq $# ]]; then
    print_usage
    exit 1
fi

server=
client=
elastic=
all=

while [[ -n "$1" ]]; do
    case "$1" in
        -c|--client)  client=1
                      shift
                      ;;
        -s|--server)  server=1
                      shift
                      ;;
        -e|--elastic) elastic=1
                      shift
                      ;;
        -a|--all)     all=1
                      shift
                      ;;
        -h|--help)    print_usage
                      exit 1
                      ;;
        *)            echo "unknown param $1" >&2
                      exit 1
    esac
done

if [[ -n "$all" ]]; then
    echo ">>> BUILDING ALL <<<"
    build_server && build_client && build_elastic
    exit
fi

if [[ -n "$server" ]]; then
    build_server
fi

if [[ -n "$client" ]]; then
    build_client
fi

if [[ -n "$elastic" ]]; then
    build_elastic
fi
