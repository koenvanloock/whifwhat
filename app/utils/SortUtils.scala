package utils


import models.ResultLine
import models.player.SeriesPlayer

object SortUtils {

  import ResultLineSortingTypes._

  val SORT_BY_ASC = 1
  val SORT_BY_DESC = -1

  def by(sortParameter: Option[String]) =
    sortParameter.map { param =>
      val (sortOrder, jsonField) = if (param.charAt(0) == '-') (SORT_BY_DESC, param.tail) else (SORT_BY_ASC, param)
      (jsonField, sortOrder)
    }.getOrElse(("id", SORT_BY_ASC))


  def sortResultLines(resultLineSortingType: ResultLineSortingType, resultLines: List[ResultLine]) = {
    val ordering = resultLineSortingType match {
      case sortingType: ResultLineSortingType if resultLineSortingType == TIE_SORTING => Ordering.by {
        (resultLine: ResultLine) =>
          resultLine.tieScore.map { score =>
            (-score.wonMatches, -score.wonSets, score.lostSets, -score.wonPoints, score.lostPoints)
          }
      }
      case sortingType: ResultLineSortingType if resultLineSortingType == GENERAL =>
        Ordering.by { (resultLine: ResultLine) =>
          val score = resultLine.player.playerScores
          (-score.wonMatches, -score.wonSets, score.lostSets, -score.wonPoints, score.lostPoints)
        }
    }

    resultLines.sorted(ordering)
  }

  def sortSeriesPlayers(players: List[SeriesPlayer]) = {
    val ordering = Ordering.by { seriesPlayer: SeriesPlayer =>
      (-seriesPlayer.playerScores.wonMatches, -seriesPlayer.playerScores.wonSets, seriesPlayer.playerScores.lostSets, -seriesPlayer.playerScores.wonPoints, seriesPlayer.playerScores.lostPoints)
    }

    players.sorted(ordering)
  }


}

case class ResultLineSortingType(sortingType: String)

object ResultLineSortingTypes {
  val TIE_SORTING = ResultLineSortingType("tie-sorting")
  val GENERAL = ResultLineSortingType("general-sorting")
}