#!/bin/bash

DIR=$(cd $(dirname ${BASH_SOURCE[0]}) && pwd)
PROJECT_ROOT="$DIR/../.."
DATA_DIR="$DIR/../data"

pushd $PROJECT_ROOT &> /dev/null

./sbt "run --strategy $DATA_DIR/strategy.json --players $DATA_DIR/players.json"
./sbt "run --strategy $DATA_DIR/strategy.json --lineup $DATA_DIR/lineup.json"

popd &> /dev/null

exit 0
