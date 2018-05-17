package models

import java.util.UUID

import models.matches.{PingpongGame, PingpongMatch}
import models.player.Player
import models.types.{Bracket, BracketLeaf, BracketNode, SiteMatchNode}
import utils.HandicapCalculator

object SiteBracket {

  def isWon(siteMatch: PingpongMatch): Boolean = {
    (siteMatch.wonSetsA > siteMatch.wonSetsB) && siteMatch.wonSetsA == siteMatch.numberOfSetsToWin ||
      siteMatch.wonSetsA < siteMatch.wonSetsB && siteMatch.wonSetsB == siteMatch.numberOfSetsToWin
  }

  def isComplete(bracket: Bracket[PingpongMatch]): Boolean = bracket.fold(isWon)(_ && _)


  def getMatchWinner(siteMatch: PingpongMatch): Option[Player] = {
    if(isWon(siteMatch)){
      if (siteMatch.wonSetsA > siteMatch.wonSetsB) siteMatch.playerA else siteMatch.playerB
    }else{
      None
    }
  }

  def updateMatchWithChildrenWinner(bracket: Bracket[PingpongMatch]):Bracket[PingpongMatch] = bracket match{
    case BracketNode(value, left, right) => BracketNode(value.copy(
      playerA = getMatchWinner(left.getValue),
      playerB = getMatchWinner(right.getValue),
      handicap = HandicapCalculator.calculateHandicapWithOpts(getMatchWinner(left.getValue), getMatchWinner(right.getValue), value.targetScore)),
      updateMatchWithChildrenWinner(left), updateMatchWithChildrenWinner(right))
    case BracketLeaf(value) => BracketLeaf(value)
  }


  def buildBracket(depth: Int, targetScore: Int, numberOfSetsToWin: Int): Bracket[PingpongMatch] = if (depth > 1){
    BracketNode(PingpongMatch(UUID.randomUUID().toString,None, None, "",0,true, targetScore, numberOfSetsToWin,0,0,Nil),
      buildBracket(depth - 1, targetScore, numberOfSetsToWin),
      buildBracket(depth - 1, targetScore, numberOfSetsToWin))
  } else{
    BracketLeaf(PingpongMatch(UUID.randomUUID().toString,None, None, "",0,true, targetScore, numberOfSetsToWin,0,0,Nil))
  }


  def createNodesOfCouples(leafCoupleList: List[Bracket[PingpongMatch]], numberOfSetsToWin: Int, targetScore: Int): Option[BracketNode[PingpongMatch]] = {
    if(leafCoupleList.length == 2){
      Some(BracketNode(emptyMatch(numberOfSetsToWin, targetScore), leafCoupleList.head, leafCoupleList.drop(1).head))
    }else {
      None
    }
  }

  def createBracketOfNodes(nodeList: List[Bracket[PingpongMatch]], numberOfSetsToWin: Int, targetScore: Int): Bracket[PingpongMatch] = {
    if(nodeList.length == 1){
      nodeList.head
    } else {
      createBracketOfNodes(nodeList.grouped(2).toList.flatMap(x => createNodesOfCouples(x, numberOfSetsToWin, targetScore)), numberOfSetsToWin, targetScore)
    }
  }

  def completeBracket(matches: List[BracketLeaf[PingpongMatch]], numberOfSetsToWin: Int, targetScore: Int): Bracket[PingpongMatch] = {
    createBracketOfNodes(matches.grouped(2).toList.flatMap( leafCoupleList => createNodesOfCouples(leafCoupleList, numberOfSetsToWin, targetScore) ), numberOfSetsToWin, targetScore)
  }

  def createBracket(leafMatches: List[PingpongMatch]) = leafMatches.length match{
    case 0 => BracketLeaf(emptyMatch(2,21))  // default to recover
    case 1 => BracketLeaf(leafMatches.head)
    case _ => completeBracket(leafMatches.map(x => BracketLeaf(x)), leafMatches.head.numberOfSetsToWin,leafMatches.head.targetScore)
  }

  def emptyMatch(numberOfSetsToWin: Int, targetScore: Int) = {
    PingpongMatch(UUID.randomUUID().toString,None, None, "",0,true, targetScore, numberOfSetsToWin,0,0,createSets(numberOfSetsToWin))
  }

  def createSets(numberOfSetsToWin: Int): List[PingpongGame] = (1 to 2* numberOfSetsToWin -1).map(gameNr => PingpongGame(0,0,gameNr)).toList

  def convertNodeToSiteMatchNode: (BracketNode[PingpongMatch]) => SiteMatchNode = node => SiteMatchNode(node.value, node.left, node.right)

  def prettyPrint(bracket: Bracket[PingpongMatch]): Unit = bracket match {
    case BracketNode(siteMatch, left, right) =>
      prettyPrint(left)
      println()
      print("\t")
      println(siteMatch)
      prettyPrint(right)
    case BracketLeaf(siteMatch) => print(siteMatch)

  }

}
