package persistence

import javax.inject.Singleton

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile

import scala.concurrent.Future


final case class ClienteRecord(id: Int, name: String)

@Singleton
class ClientesRepositorio @Inject()(
                                     protected val dbConfigProvider: DatabaseConfigProvider,
                                   )
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  val clientes = TableQuery[Clientes]


  def insert(cliente: ClienteRecord): Future[Int] = {
    val ins = clientes += cliente // insert into clients(id, name) values(cliente.id, cliente.name)
    val resultado: Future[Int] = dbConfig.db.run(
        ins
    )
    resultado
  }

  def selectById(id: Int): Future[Option[ClienteRecord]] = {
    val query = clientes.filter(x => x.id === id)
    val resultado: Future[Option[ClienteRecord]] = dbConfig.db.run(
      query.result.headOption
    )
    resultado
  }


}

class Clientes(tag: Tag) extends Table[ClienteRecord](tag, "CLIENTS") {
  def id = column[Int]("ID", O.PrimaryKey) // This is the primary key column
  def name = column[String]("NAME") // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name) <> (ClienteRecord.tupled, ClienteRecord.unapply)
}


