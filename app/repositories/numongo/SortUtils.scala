package repositories.numongo

object SortUtils {

  val SORT_BY_ASC = 1
  val SORT_BY_DESC = -1

  def by(sortParameter: String): (Int, String) =
    if (sortParameter.charAt(0) == '-') (SORT_BY_DESC, sortParameter.tail)
    else (SORT_BY_ASC, sortParameter)


}
