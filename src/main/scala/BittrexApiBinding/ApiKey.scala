package BittrexApiBinding

import com.typesafe.config.ConfigFactory

/**
  * Created by gk91 on 9/11/17.
  */
object ApiKey {
  private val file = io.Source.fromFile("apiKey.conf").getLines().mkString
  private val app = ConfigFactory.parseString(file).getConfig("app")
  val key = app.getString("apikey")
  val secret = app.getString("secret")
}
