package code
package model

import net.liftweb._
import common._
import mongodb.record._
import mongodb.record.field._
import record.field._

class MainDoc private() extends MongoRecord[MainDoc] with ObjectIdPk[MainDoc] {
  def meta = MainDoc

  object name extends StringField(this, 12)
  object refDocId extends ObjectIdRefField(this, RefDoc) {
    override def options = RefDoc.findAll.map(rd => (Full(rd.id.is), rd.name.is))
  }
}
object MainDoc extends MainDoc with MongoMetaRecord[MainDoc]

class RefDoc private() extends MongoRecord[RefDoc] with ObjectIdPk[RefDoc] {
  def meta = RefDoc

  object name extends StringField(this, 12)
}
object RefDoc extends RefDoc with MongoMetaRecord[RefDoc] 
