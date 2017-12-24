package models

import models.matches.PingpongMatch

case class SwissLeg(id: String, matches: List[PingpongMatch])
