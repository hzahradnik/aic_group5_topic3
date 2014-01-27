package models

import play.api.libs.ws.WS
import java.util.concurrent.{Future, TimeoutException}
import play.libs.F.Promise
import scala.concurrent.duration._
import scala.concurrent.Await

/**
 * Created with IntelliJ IDEA.
 * User: dominik
 * Date: 11/26/13
 * Time: 7:03 PM
 * To change this template use File | Settings | File Templates.
 */
object TestAnalyze {
  def analyze( keyword : String, timeout : Int, url : String ) : String = {
    return try {
      val request = WS.url(url).withQueryString( "taskword" -> keyword ).get
      val response = Await.result(request, timeout second)

      response.body
    } catch {
      case _ : Throwable => "{ error: true }"
    }
  }
}
