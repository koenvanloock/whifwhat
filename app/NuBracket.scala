
import models.matches.{SiteGame, SiteMatch}
import models.player.{Player, Ranks}
import models.types._
import play.api.libs.json.Json
import models.SeriesRoundEvidence.seriesRoundIsModel._
import models.SiteBracket

object NuBracket {

  def main(args: Array[String]) {



    val l = LeafMatch(SiteMatch("123",Some(Player("1","Koen","Van Loock", Ranks.D0)), Some(Player("3","Luk","Geraets", Ranks.D6)),"1",3,true,21,2,2,0,List(SiteGame(21,15,1), SiteGame(21,13,2))))
    val r = LeafMatch(SiteMatch("124",Some(Player("4","Aram","Pauwels", Ranks.B4)), Some(Player("2","Hans","Van Bael", Ranks.E4)),"1",12,true,21,2,2,0,Nil))
    val bracket = NodeMatch[SiteMatch](SiteMatch("123",None, None,"2",0,true,21,2,2,0,List()),l, r)


    def isWon(siteMatch: SiteMatch): Boolean = {
      (siteMatch.wonSetsA > siteMatch.wonSetsB) && siteMatch.wonSetsA == siteMatch.numberOfSetsToWin ||
        siteMatch.wonSetsA > siteMatch.wonSetsB && siteMatch.wonSetsB == siteMatch.numberOfSetsToWin
    }


    //println(Bracket.map(bracket)(isWon))
    //println(SiteBracket.isComplete(bracket))


    //println(SiteBracket.updateMatchWithChildrenWinner(bracket))

    def createSets(siteMatch: SiteMatch): SiteMatch = {
      val createdGames  = (1 to 2*siteMatch.numberOfSetsToWin -1).map(gameNr => SiteGame(0,0,gameNr)).toList
      siteMatch.copy(games = createdGames)
    }


    val matchList = List(
      SiteMatch("123",Some(Player("1","Koen","Van Loock", Ranks.D0)), Some(Player("3","Luk","Geraets", Ranks.D6)),"1",3,true,21,2,2,0,List(SiteGame(21,15,1), SiteGame(21,13,2))),
      SiteMatch("124",Some(Player("1","Koen","Van Loock", Ranks.D0)), Some(Player("3","Luk","Geraets", Ranks.D6)),"1",3,true,21,2,2,0,List(SiteGame(21,15,1), SiteGame(21,13,2))),
      SiteMatch("125",Some(Player("4","Aram","Pauwels", Ranks.B4)), Some(Player("2","Hans","Van Bael", Ranks.E4)),"1",12,true,21,2,2,0,Nil),
      SiteMatch("126",Some(Player("4","Aram","Pauwels", Ranks.B4)), Some(Player("2","Hans","Van Bael", Ranks.E4)),"1",12,true,21,2,2,0,Nil)

    )

    //SiteBracket.prettyPrint( SiteBracket.createBracket(matchList))


    //println(Bracket.fold(SiteBracket.buildBracket(5,21,2))(_ => 1)(_ + _))
    //println(Bracket.map(SiteBracket.buildBracket(5,21,2))(createSets))

    println(SiteBracket.createBracket(matchList).getRoundList(2).mkString("\n"))
  }
}

