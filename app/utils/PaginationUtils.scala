package utils

import models.Model
import play.api.Play

object PaginationUtils {
  val SORT_BY_ASC = 1
  val SORT_BY_DESC = -1

  val DEFAULT_PAGE_NR = 1
  val DEFAULT_PAGE_SIZE = 25

  def computeSkip(pageNumber: Int, top: Int) =
    if (pageNumber > 1) (pageNumber - 1) * top else 0

  private[utils] def getPageSize(optionalPageSizeString: Option[String]): Int = {
    optionalPageSizeString.map { pageSizeString =>
      pageSizeString.toInt
    }.getOrElse {
      DEFAULT_PAGE_SIZE
    }
  }

  private[utils] def getPageNumber(optionalPageNumberString: Option[String]) = {
    optionalPageNumberString.map { pageNumberString =>
      pageNumberString.toInt
    }.getOrElse {
      DEFAULT_PAGE_NR
    }
  }

  def paginate[M](optionalPageNumberString: Option[String], optionalPageSizeString: Option[String]) = (list: List[M]) => {
    val pageSize = getPageSize(optionalPageSizeString)
    val pageNumber = getPageNumber(optionalPageNumberString)
    val from = (pageNumber - 1) * pageSize
    val total = list.size
    val to = total min (from + pageSize)

    list.slice(from, to)
  }

  def filterIds[M: Model](optionalIds: Option[List[String]]): (List[M]) => List[M] = {
    val model = implicitly[Model[M]]
    import model._
    (ms: List[M]) =>
      optionalIds.map { ids =>
        ms.filter { m =>
          val dummyId = "!@#$%^&*()_+"
          ids.contains(getId(m)
            .getOrElse(dummyId))
        }
      }.getOrElse {
        ms
      }
  }

}