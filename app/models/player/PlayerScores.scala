package models.player

case class PlayerScores(wonMatches: Int = 0, lostMatches: Int = 0, wonSets: Int = 0, lostSets: Int = 0, wonPoints: Int = 0, lostPoints: Int = 0, totalPoints: Int = 0) {
  def +(otherScore: PlayerScores): PlayerScores = PlayerScores(
    this.wonMatches + otherScore.wonMatches,
    this.lostMatches + otherScore.lostMatches,
    this.wonSets + otherScore.wonSets,
    this.lostSets + otherScore.lostSets,
    this.wonPoints + otherScore.wonPoints,
    this.lostPoints + otherScore.lostPoints,
    this.totalPoints + otherScore.totalPoints
  )

  def calculateScore: Double = {
    1.0 * (1000 * wonMatches -
      1000 * lostMatches +
      100 * wonSets -
      100 * lostSets) +
      10 * wonPoints / (wonPoints + lostPoints + 1)
  }

}