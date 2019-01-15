package services

import models.{SpellDao, StudentForUpdate, StudentLibraryVisitDao}

class LibraryService(spellDao: SpellDao, libraryDao: StudentLibraryVisitDao) {
  def commitLibraryVisit(student: StudentForUpdate): Unit = {
    val spellId = spellDao.findRandomIdToLearn(student.id, student.level)
    libraryDao.createVisit(student.id, spellId)
  }

  def commitVisitEnd(student: StudentForUpdate): Unit =
    libraryDao.endVisit(student.id)
}
