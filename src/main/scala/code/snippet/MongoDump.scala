package code
package snippet

import model._

import net.liftweb._
import common._
import util._
import Helpers._

class MongoDump {
  val mains = MainDoc.findAll
  
  def render =
    <ul>
      {
        mains.flatMap { m =>
          <li>{"MainDoc.name: %s, RefDoc.name: %s".format(m.name.is, m.refDocId.obj.map(_.name.is).openOr("RefDoc not found"))}</li>
        }
      }
    </ul>
}
