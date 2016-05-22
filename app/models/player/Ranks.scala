package models.player

object Ranks extends Enumeration{
  val Rec = new Rank("Rec",0)
  val Ng = new Rank("NG",1)
  val F = new Rank("F",2)
  val E6 = new Rank("E6",3)
  val E4 = new Rank("E4",4)
  val E2 = new Rank("E2",5)
  val E0 = new Rank("E0",6)
  val D6 = new Rank("D6",7)
  val D4 = new Rank("D4",8)
  val D2 = new Rank("D2",9)
  val D0 = new Rank("D0",10)
  val C6 = new Rank("C6",11)
  val C4 = new Rank("C4",12)
  val C2 = new Rank("C2",13)
  val C0 = new Rank("C0",14)
  val B6 = new Rank("B6",15)
  val B4 = new Rank("B4",16)
  val B2 = new Rank("B2",17)
  val B0 = new Rank("B0",18)
  val A = new Rank("A",19)

  def RankList = List(Rec,Ng,F,E6,E4,E2,E0,D6,D4,D2,D0,C6,C4,C2,C0,B6,B4,B2,B0,A)
  def getRank(rankString : String) = (for(rank <-RankList if rank.name.equals(rankString))yield rank).head
}
