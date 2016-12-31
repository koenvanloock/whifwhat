package models.types

import models.matches.SiteMatch

/**
  *
  * This class is used to help the writes resolve the recursive type
  *
  * */
case class SiteMatchNode(value: SiteMatch, left: Bracket[SiteMatch], right: Bracket[SiteMatch])
