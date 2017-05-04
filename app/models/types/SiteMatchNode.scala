package models.types

import models.matches.PingpongMatch

/**
  *
  * This class is used to help the writes resolve the recursive type
  *
  * */
case class SiteMatchNode(value: PingpongMatch, left: Bracket[PingpongMatch], right: Bracket[PingpongMatch])
