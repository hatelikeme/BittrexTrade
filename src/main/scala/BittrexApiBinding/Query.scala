package BittrexApiBinding

case class Query(method: String, options: Map[String, String])

object QueryGenerator {
  def GetBalance(currency: String): Query = {
    Query("getbalance", Map(
      "currency" -> currency
    ))
  }

  def BuyLimit(market: String, quantity: Float, rate: Float): Query = {
    Query("buylimit", Map(
      "market" -> market,
      "quantity" -> quantity.toString,
      "rate" -> rate.toString
    ))
  }

  def SellLimit(market: String, quantity: Float, rate: Float): Query = {
    Query("selllimit", Map(
      "market" -> market,
      "quantity" -> quantity.toString,
      "rate" -> rate.toString
    ))
  }

  def Cancel(uuid: String): Query  = {
    Query("cancel", Map(
      "uuid" -> uuid
    ))
  }

  def GetOpenOrders(market: String): Query = {
    Query("getopenorders",Map(
      "market" -> market
    ))
  }
}

