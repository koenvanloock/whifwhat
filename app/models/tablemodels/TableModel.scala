package models.tablemodels

import slick.lifted.Rep

trait TableModel[TM] {
  def getRepId(tm: TM): Rep[String]
}
