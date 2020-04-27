package gymkapp.main.api

import android.util.Log
import okhttp3.OkHttpClient
import java.io.BufferedInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


lateinit var inputStreamCA: InputStream
const val tag = "RemoteAPI Certificate"

fun createSecureClient(): OkHttpClient = OkHttpClient.Builder().apply {
  val (sslSF, x509tm) = testingHttps()
  sslSocketFactory(sslSF, x509tm)
}.build()

fun testingHttps(): Pair<SSLSocketFactory, X509TrustManager> {
  //Load CAs from InputStream
  val cf = CertificateFactory.getInstance("X.509")
  val caInput = BufferedInputStream(inputStreamCA)
  val ca = caInput.use {
    cf.generateCertificate(it) as X509Certificate
  }

  //Create Keystore containing trusted CAs
  Log.d(tag, "ca=${ca.subjectDN}")
  val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
    load(null, null)
    setCertificateEntry("ca", ca)
  }
  //Create TrustManager
  val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
    init(keyStore)
  }
  //SSLContext que usa el TrustManager
  val context = SSLContext.getInstance("TLS").apply {
    init(null, tmf.trustManagers, null)
  }
  //Devolver los dos objetos que interesan
  return Pair(context.socketFactory, tmf.trustManagers[0] as X509TrustManager)
}