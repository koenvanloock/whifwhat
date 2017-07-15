package actors

import actors.TournamentEventActor.MatchCompleted
import akka.actor.Actor
import models.SeriesRound
import models.halls.{Hall, HallOverViewTournament}
import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, Rank}
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{JsObject, Json}
import utils.{JsonUtils, StreamUtils}


object ScoreStreamer{
  case class Start(channel: Concurrent.Channel[JsObject])


  case class ShowCompletedMatch(pingpongMatch: PingpongMatch)
  case class ShowRound(name: String, color: String, seriesRound: SeriesRound)

}

class ScoreStreamer extends Actor{
  import ScoreStreamer._
  import models.SeriesRoundEvidence._
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  implicit val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
  implicit val gameWrites = Json.writes[PingpongGame]
  implicit val listGameWrites = JsonUtils.listWrites[PingpongGame]
  implicit val matchWrites = Json.writes[PingpongMatch]

  private var channel: Option[Concurrent.Channel[JsObject]] = None

  def receive: Receive = {
    case Start(outChannel: Concurrent.Channel[JsObject]) => this.channel = Some(outChannel)
    case ShowCompletedMatch(completedMatch: PingpongMatch) =>
      this.channel.foreach( realChannel => StreamUtils.pushJsonObjectToStream("completedMatch", completedMatch, realChannel))

    case ShowRound(name, color, roundToShow) =>
      val roundJson = Json.obj("round" -> Json.toJson(roundToShow), "name"-> name, "color" -> color)
      this.channel.foreach( realChannel => StreamUtils.pushJsonObjectToStream("roundToShow", roundJson, realChannel))
  }
}
