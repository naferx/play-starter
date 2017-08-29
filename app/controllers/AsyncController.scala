package controllers

import javax.inject._

import akka.actor.ActorSystem
import persistence.{ClienteRecord, ClientesRepositorio}
import play.api.libs.json.{Json, OWrites, Writes}
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}


case class Cliente(nombre: String, id: Int)

@Singleton
class AsyncController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, repo: ClientesRepositorio)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  implicit val clienteWritable = Json.format[Cliente]

  /**
   * Creates an Action that returns a plain text message after a delay
   * of 1 second.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/message`.
   */
  def message = Action.async {
    getFutureMessage(1.second).map { msg => Ok(msg) }
  }


  def crearCliente = Action.async(parse.json) { request =>
    val placeResult = request.body.validate[Cliente]
    placeResult.fold(
      errors => {
        Future.successful(
          BadRequest(Json.obj("status" ->"KO", "message" -> "Json invalido"))
        )
      },
      client => {

        val record = ClienteRecord(client.id, client.nombre)
        val resultado: Future[Int] = repo.insert(record)
        resultado.map { registro =>
          Ok(Json.obj("status" ->"OK", "message" -> "cliente registrado con exito" ))
        }
      }
    )
  }

  def consultaClientes(id: Int) = Action.async {
    val resultado = repo.selectById(id)

    resultado.map { (registro: Option[ClienteRecord]) =>
      registro match {
        case Some(cliente) =>
          val json = Cliente( cliente.name, cliente.id)
          Ok(Json.toJson(json))
        case None => NotFound
      }

    }

  }

  private def getFutureMessage(delayTime: FiniteDuration): Future[String] = {
    val promise: Promise[String] = Promise[String]()
    actorSystem.scheduler.scheduleOnce(delayTime) {
      promise.success("Hi!")
    }(actorSystem.dispatcher) // run scheduled tasks using the actor system's dispatcher
    promise.future
  }

}
