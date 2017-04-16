package services

import javax.inject.Inject

import repositories.mongo.HallRepository

class HallService @Inject()(hallRepository: HallRepository){

  def retrieveAll = hallRepository.retrieveAll()

}
