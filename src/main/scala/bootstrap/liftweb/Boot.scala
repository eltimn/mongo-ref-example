package bootstrap.liftweb

import scala.collection.JavaConversions._

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._

import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {
    // init mongodb
    MongoConfig.init()

    // where to search snippet
    LiftRules.addToPackages("code")

    // set the default htmlProperties
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    // Build SiteMap
    val entries = List(
      Menu.i("Home") / "index", // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"), "Static Content")))
    // the User management menu items

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // The function to test if a user is logged in. Used by built-in snippet TestCond.
    //LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Print the System environment vars
    val envMap = System.getenv
    envMap.keySet.toList.foreach { key =>
      logger.debug("%s: %s".format(key, envMap.get(key)))
    }

    // print vcap services
    val vcapServices = Option(System.getenv("VCAP_SERVICES"))
    vcapServices foreach { s =>
      logger.debug("VCAP_SERVICES: %s".format(s))
    }
    
    // some test data
    if (RefDoc.findAll.length == 0) {
      val ref1 = RefDoc.createRecord
        .name("ref1")
        .save

      val md1 = MainDoc.createRecord
      .name("md1")
      .refDocId(ref1.id.is)
      .save
    }
  }
}
