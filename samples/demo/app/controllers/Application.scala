package controllers

import play.api.mvc._
import scala.language.implicitConversions
import com.tomekgrobel.mnemonic.Memcached
import concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  val Key = "test"

  def index = Action {
    val message = "Your new application is ready."
    Memcached.set[String](Key, message, 1.minute)
    Ok(views.html.index(message))
  }

  def memcached = Action.async {
    val future = Memcached.get[String](Key)
    future.map {
      message => Ok(views.html.mnemonic(message.getOrElse("No message found in cache!")))
    }
  }

}