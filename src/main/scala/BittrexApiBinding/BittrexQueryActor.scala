package BittrexApiBinding

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, ResponseEntity, StatusCodes}
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits.global

class BittrexQueryActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Query(method, options) => MakeQuery(method, options)
    case HttpResponse(StatusCodes.OK, headers, entity, _) => processResponse(entity)
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

  private def processResponse(entity: ResponseEntity): Unit = {
    entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach{body=>
      log.info(body.utf8String)
    }
  }

  private def getMethodSet(method: String): String = method match {
    case mt: String if AccountSet.contains(mt) => "account"
    case mt: String if MarketSet.contains(mt) => "market"
    case _ => "public"
  }

  private def urlencode(options: Map[String, String]): String = options match {
    case opt if opt.nonEmpty => {
      opt.map({
        case (key, value) => "%s=%s&".format(key, value)
      }).reduce(_ + _)
    }
    case _ => ""
  }

  private val baseUrl = "https://bittrex.com/api/v1.1/%s/%s?"

  private def constructRequestURL(methodSet: String, method: String, options: Map[String, String]): String = methodSet match {
    case m if m != "public" =>
        val requestUrl = baseUrl.format(m, method)
        val bstr = "%s&apikey=%s&nonce=%s&"
        val time = System.currentTimeMillis().toInt
        bstr.format(requestUrl, ApiKey.key, time.toString) + urlencode(options)

    case _ => baseUrl.format(methodSet, method)
  }

  val http = Http(context.system)
  implicit val materializer: Materializer = ActorMaterializer()

  def dispatch(requestUrl: String, encrypted: String): Unit ={
    val header = RawHeader("apisign", encrypted)
    val req = HttpRequest(uri = requestUrl)
      .addHeader(header)
    log.info(requestUrl)
    log.info(encrypted)
    val responseFuture = http.singleRequest(req)
    responseFuture.pipeTo(self)
  }

  def MakeQuery(method: String, options: Map[String, String]): Unit ={
    val methodSet = getMethodSet(method)
    val requestUrl = constructRequestURL(methodSet, method, options)
    val encrypted = HmacEncryptor.encrypt(requestUrl, ApiKey.secret)
    dispatch(requestUrl, encrypted)
  }
}