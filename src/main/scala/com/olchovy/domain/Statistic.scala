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
  = Value

  val `HR/H`  = Value("HR/H")   // home runs per hit
  val `BB/AB` = Value("BB/AB")  // walks per at-bat
  val `SO/AB` = Value("SO/AB")  // strike outs per at-bat
  val `1B/H`  = Value("1B/H")   // singles per at-bat
}

