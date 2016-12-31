package types

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import models.SeriesRoundEvidence.seriesRoundIsModel._
import models.SiteBracket

@RunWith(classOf[JUnitRunner])
class BracketTest extends PlaySpec{

  "Bracket" should {

    "create a bracket of given size with the given targetScore and numberOFSetsToWin" in {
      SiteBracket.buildBracket(2,21,2).size mustBe 2
    }

    "Convert a bracket to Json" in {

      val result = Json.toJson(SiteBracket.buildBracket(1,21,2))
      println(result)
    }

  }

}
