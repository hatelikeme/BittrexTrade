package BittrexApiBinding
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
/**
  * Created by gk91 on 9/12/17.
  */
object HmacEncryptor {
  def encrypt(input: String, secret: String): String ={
    val sec = new SecretKeySpec(secret.getBytes(), "HmacSHA512")
    val mac = Mac.getInstance("HmacSHA512")
    mac.init(sec)
    mac.doFinal(input.getBytes()).map("%02X" format _).mkString
  }
}
