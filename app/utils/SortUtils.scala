package utils

object SortUtils {
    val SORT_BY_ASC = 1
    val SORT_BY_DESC = -1

    def by(sortParameter: Option[String]) =
      sortParameter.map { param =>
        val (sortOrder, jsonField) = if (param.charAt(0) == '-') (SORT_BY_DESC, param.tail) else (SORT_BY_ASC, param)
        (jsonField, sortOrder)
      }.getOrElse(("id", SORT_BY_ASC))


}
