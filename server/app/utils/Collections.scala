package utils

object Collections {
  implicit class GroupedSeq[E](seq: Seq[E]) {
    def groupConsecutiveBy[GroupingE](grouper: E => GroupingE): Seq[(GroupingE, Seq[E])] =
      seq.foldRight(Nil : Seq[(GroupingE, Seq[E])]) { (item, acc) =>
        val groupBy = grouper(item)
        acc match {
          case (groupedBy, items) :: rest if groupedBy == groupBy =>
            (groupedBy, item +: items) +: rest
          case groups =>
            (groupBy, Seq(item)) +: groups
        }
      }
  }
}
