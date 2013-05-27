#!/bin/bash

# NOTE: currently requires api server running in separate process

DIR=$(cd $(dirname ${BASH_SOURCE[0]}) && pwd)
PROJECT_ROOT="$DIR/../.."
DATA_DIR="$DIR/../data"

pushd $PROJECT_ROOT &> /dev/null

response=$(curl -sX POST \
 -H 'Content-type:application/json' \
 -d "{\"strategy\":$(cat $DATA_DIR/strategy.json), \"players\":$(cat $DATA_DIR/players.json)}" \
 'http://localhost:8080/lineups')

echo "$response"

response=$(curl -sX POST \
 -H 'Content-type:application/json' \
 -d "{\"strategy\":$(cat $DATA_DIR/strategy.json), \"players\":$(cat $DATA_DIR/lineup.json)}" \
 'http://localhost:8080/fitness')

echo "$response"

popd &> /dev/null

exit 0 
