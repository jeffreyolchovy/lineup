# Lineup
A genetic algorithm for optimizing baseball and softball lineups.

![GUI step #1 screenshot](https://github.com/jeffreyolchovy/lineup/raw/master/screenshots/step1.png)
![GUI step #2 screenshot](https://github.com/jeffreyolchovy/lineup/raw/master/screenshots/step2.png)

## Usage
Front-end/GUI (node/npm) dependencies are downloaded via sbt.

From an interactive sbt session, issue:
```
> project gui
> run --port 8080
```

Visit http://localhost:8080 in a browser to enter your roster and generate lineups.

## Project structure

### ga
A library for the construction and execution of genetic algorithms.

### common
Shared resources (domain objects, [de]serializers, etc.) used by both the api and gui projects.

### api
A REST API for generating lineups given a roster and strategy.

### gui
A web application and server, with browser-based GUI, for generating lineups.
