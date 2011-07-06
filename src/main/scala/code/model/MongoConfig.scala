package code
package model

import net.liftweb._
import common._
import json._
import mongodb._
import util.Props

import com.mongodb.{Mongo, ServerAddress}

object AdminDB extends MongoIdentifier {
	val jndiName = "admin"
}

object MongoConfig extends Loggable {
  implicit val formats = DefaultFormats
  
  case class CloudFoundryMongo(name: String, label: String, plan: String, credentials: CloudFoundryMongoCredentials)
  case class CloudFoundryMongoCredentials(hostname: String, port: String, username: String, password: String, name: String, db: String)

  def init() {
    Option(System.getenv("VCAP_SERVICES")) match {
      case Some(s) =>
        try {
          // cloud foundry environment
          parse(s) \\ "mongodb-1.8" match {
            case JArray(ary) => ary foreach { mngoJson =>
              val credentials = mngoJson.extract[CloudFoundryMongo].credentials

              logger.debug("MongoDB hostname: %s".format(credentials.hostname))
              logger.debug("MongoDB port: %s".format(credentials.port))
              logger.debug("MongoDB db: %s".format(credentials.db))
              logger.debug("MongoDB username: %s".format(credentials.username))
              logger.debug("MongoDB password: %s".format(credentials.password))

              MongoDB.defineDbAuth(
                DefaultMongoIdentifier,
                new Mongo(credentials.hostname, credentials.port.toInt),
                credentials.db,
                credentials.username,
                credentials.password
              )
              logger.info("MongoDB inited: %s".format(credentials.name))
            }
            case x => logger.warn("Json parse error: %s".format(x))
          }
        }
        catch {
          case e => logger.error("Error initing Mongo: %s".format(e.getMessage))
        }
      case _ =>
        // local dev environment
        val mainMongoHost = new Mongo(Props.get("mongo.host", "localhost"), Props.getInt("mongo.port", 27017))
		    MongoDB.defineDb(
		      DefaultMongoIdentifier,
		      mainMongoHost,
		      Props.get("mongo.main_name", "mongo_ref_example")
		    )
		    MongoDB.defineDb(
		      AdminDB,
		      mainMongoHost,
		      Props.get("mongo.admin_name", "admin")
		    )
		    logger.info("MongoDB inited")
    }
  }

  override def finalize() {
    MongoDB.close
  }
}
