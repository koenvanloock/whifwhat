package models

import java.util.UUID

import models.matches.{SiteGame, SiteMatch}
import models.player.{Player, PlayerScores, SeriesPlayer}
import models.types.{Bracket, BracketLeaf, BracketNode, SiteMatchNode}

object SiteBracket {

  def isWon(siteMatch: SiteMatch): Boolean = {
    (siteMatch.wonSetsA > siteMatch.wonSetsB) && siteMatch.wonSetsA == siteMatch.numberOfSetsToWin ||
      siteMatch.wonSetsA < siteMatch.wonSetsB && siteMatch.wonSetsB == siteMatch.numberOfSetsToWin
  }

  def isComplete(bracket: Bracket[SiteMatch]): Boolean = bracket.fold(isWon)(_ && _)


  def getMatchWinner(siteMatch: SiteMatch): Option[Player] = {
    if(isWon(siteMatch)){
      if (siteMatch.wonSetsA > siteMatch.wonSetsB) siteMatch.playerA else siteMatch.playerB
    }else{
      None
    }
  }

  def updateMatchWithChildrenWinner(bracket: Bracket[SiteMatch]):Bracket[SiteMatch] = bracket match{
    case BracketNode(value, left, right) => BracketNode(value.copy(playerA = getMatchWinner(left.getValue), playerB = getMatchWinner(right.getValue)),updateMatchWithChildrenWinner(left), updateMatchWithChildrenWinner(right))
    case BracketLeaf(value) => BracketLeaf(value)
  }


  def buildBracket(depth: Int, targetScore: Int, numberOfSetsToWin: Int): Bracket[SiteMatch] = if (depth > 1){
    BracketNode(SiteMatch(UUID.randomUUID().toString,None, None, "",0,true, targetScore, numberOfSetsToWin,0,0,Nil),
      buildBracket(depth - 1, targetScore, numberOfSetsToWin),
      buildBracket(depth - 1, targetScore, numberOfSetsToWin))
  } else{
    BracketLeaf(SiteMatch(UUID.randomUUID().toString,None, None, "",0,true, targetScore, numberOfSetsToWin,0,0,Nil))
  }


  def createNodesOfCouples(leafCoupleList: List[Bracket[SiteMatch]], numberOfSetsToWin: Int, targetScore: Int): Option[BracketNode[SiteMatch]] = {
    if(leafCoupleList.length == 2){
      Some(BracketNode(emptyMatch(numberOfSetsToWin, targetScore), leafCoupleList.head, leafCoupleList.drop(1).head))
    }else {
      None
    }
  }

  def createBracketOfNodes(nodeList: List[Bracket[SiteMatch]], numberOfSetsToWin: Int, targetScore: Int): Bracket[SiteMatch] = {
    if(nodeList.length == 1){
      nodeList.head
    } else {
      createBracketOfNodes(nodeList.grouped(2).toList.flatMap(x => createNodesOfCouples(x, numberOfSetsToWin, targetScore)), numberOfSetsToWin, targetScore)
    }
  }

  def completeBracket(matches: List[BracketLeaf[SiteMatch]], numberOfSetsToWin: Int, targetScore: Int): Bracket[SiteMatch] = {
    createBracketOfNodes(matches.grouped(2).toList.flatMap( leafCoupleList => createNodesOfCouples(leafCoupleList, numberOfSetsToWin, targetScore) ), numberOfSetsToWin, targetScore)
  }

  def createBracket(leafMatches: List[SiteMatch]) = leafMatches.length match{
    case 0 => BracketLeaf(emptyMatch(2,21))  // default to recover
    case 1 => BracketLeaf(leafMatches.head)
    case _ => completeBracket(leafMatches.map(x => BracketLeaf(x)), leafMatches.head.numberOfSetsToWin,leafMatches.head.targetScore)
  }

  def emptyMatch(numberOfSetsToWin: Int, targetScore: Int) = {
    SiteMatch(UUID.randomUUID().toString,None, None, "",0,true, targetScore, numberOfSetsToWin,0,0,createSets(numberOfSetsToWin))
  }

  def createSets(numberOfSetsToWin: Int): List[SiteGame] = (1 to 2* numberOfSetsToWin -1).map(gameNr => SiteGame(0,0,gameNr)).toList

  def convertNodeToSiteMatchNode: (BracketNode[SiteMatch]) => SiteMatchNode = node => SiteMatchNode(node.value, node.left, node.right)

  def prettyPrint(bracket: Bracket[SiteMatch]): Unit = bracket match {
    case BracketNode(siteMatch, left, right) =>
      prettyPrint(left)
      println()
      print("\t")
      println(siteMatch)
      prettyPrint(right)
    case BracketLeaf(siteMatch) => print(siteMatch)

  }

}
