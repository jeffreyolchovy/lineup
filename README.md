Lineup
======

A genetic algorithm for optimizing baseball and softball lineups.

How it works
------------
Given a set of players, an initial, random set of lineups is generated.

Each lineup is scored by a given fitness function.

High-scoring lineups are retained while low-scoring lineups are discarded.

Lineups then undergo independent mutation or are recombinated with other lineups.

This process is repeated until some maximum number of generations has been reached.

The solution set of high-scoring lineups is the return value of the algorithm.

Installation
------------
See INSTALL

Usage
-----
For API and CLI usage examples, browse the /examples/api and /examples/cli directories, respectively.

