package com.olchovy.domain


object Statistic extends Enumeration
{
  val G       // games  
  ,   AB      // at-bats
  ,   H       // hits
  ,   `1B`    // singles
  ,   `2B`    // doubles
  ,   `3B`    // triples
  ,   HR      // home runs
  ,   RBI     // runs batted-in
  ,   R       // runs
  ,   BB      // walks
  ,   SO      // strike outs
  ,   SF      // sacrifice fly-outs
  ,   E       // errors
  ,   AVG     // batting average
  ,   SLG     // slugging average
  ,   OBA     // on-base average
  ,   EBA     // extra-base average
  ,   PIP     // put-in-play percentage
  ,   `HR/H`  // home runs per hit
  ,   `BB/AB` // walks per at-bat
  ,   `SO/AB` // strike outs per at-bat
  ,   `1B/H`  // singles per at-bat
  = Value
}

