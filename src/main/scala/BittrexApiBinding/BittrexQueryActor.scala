package BittrexApiBinding

import akka.actor.Actor

class BittrexQueryActor extends Actor {

  override def receive: Receive = {
    case Query(method, options) => MakeQuery(method, options)
  }

  private val MarketSet = Set("getopenorders",
    "cancel",
    "sellmarket",
    "selllimit",
    "buymarket",
    "buylimit")

  private val AccountSet = Set("getbalances",
    "getbalance",
    "getdepositaddress",
    "withdraw",
    "getorderhistory",
    "getorder",
    "getdeposithistory",
    "getwithdrawalhistory")

  private def getMethodSet(method: String): String = {
    if (AccountSet.contains(method)){
      return "account"
    }
    if (MarketSet.contains(method)){
      return "market"
    }
    "public"
  }

  private def urlencode(options: Map[String, String]): String = {
    options.map({
      case (key, value) => "%s=%s&".format(key, value)
    }).reduce(_ + _)
  }

  private val baseUrl = "https://bittrex.com/api/v1.1/%s/%s?"

  private def constructRequestURL(methodSet: String, method: String, options: Map[String, String]): String = methodSet match {
    case m if m != "public" => {
        val requestUrl = baseUrl.format(m, method)
        val bstr = "%s&apikey=%snonce=%s&"
        val time = System.currentTimeMillis()*1000
        bstr.format(requestUrl, ApiKey.key, time.toString) + urlencode(options)
    }
    case _ => baseUrl.format(methodSet, method)
  }

  def dispatch(requestUrl: String, encrypted: String): Unit ={
    println(requestUrl)
    println(encrypted)
  }

  def MakeQuery(method: String, options: Map[String, String]): Unit ={
    val methodSet = getMethodSet(method)
    val requestUrl = constructRequestURL(methodSet, method, options)
    val encrypted = HmacEncryptor.encrypt(requestUrl, ApiKey.secret)
    dispatch(requestUrl, encrypted)
  }
}