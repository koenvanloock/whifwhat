package utils

import models.{Rank, Ranks}


object RankConverter {

  def getRankOfInt(Rankstring: Int): Rank = Rankstring match {
    case 0 => Ranks.Rec
    case 1 => Ranks.Ng
    case 2 => Ranks.F
    case 3 => Ranks.E6
    case 4 => Ranks.E4
    case 5 => Ranks.E2
    case 6 => Ranks.E0
    case 7 => Ranks.D6
    case 8 => Ranks.D4
    case 9 => Ranks.D2
    case 10 => Ranks.D0
    case 11 => Ranks.C6
    case 12 => Ranks.C4
    case 13 => Ranks.C2
    case 14 => Ranks.C0
    case 15 => Ranks.B6
    case 16 => Ranks.B4
    case 17 => Ranks.B2
    case 18 => Ranks.B0
    case 19 => Ranks.A
    case _ => Ranks.Rec
  }
}