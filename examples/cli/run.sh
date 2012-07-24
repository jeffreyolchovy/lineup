#!/bin/bash

PROJECT_ROOT='../..'

cd $PROJECT_ROOT

./sbt "run
  --strategy examples/data/strategy.json
  --players examples/data/players.json"

./sbt "run
  --strategy examples/data/strategy.json
  --lineup examples/data/lineup.json"

exit 0
