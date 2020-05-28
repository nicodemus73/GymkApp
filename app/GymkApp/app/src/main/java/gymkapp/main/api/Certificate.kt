package gymkapp.main.api

import okhttp3.OkHttpClient
import okio.Buffer
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private const val tag = "RemoteAPI Certificate"
val secureClientBuilder by lazy { createSecureClientBuilder() }

private fun createSecureClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder().apply {
  val (sslSF, x509tm) = generateKeyStore()
  sslSocketFactory(sslSF, x509tm)
}

private fun generateKeyStore(): Pair<SSLSocketFactory, X509TrustManager> {
  //Load CAs from InputStream
  val cf = CertificateFactory.getInstance("X.509")
  val ca = generateInputStreamCertificate().use {
    cf.generateCertificate(it) as X509Certificate
  }

  //Create Keystore containing trusted CAs
//  Log.d(tag, "ca=${ca.subjectDN}")
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

private fun generateInputStreamCertificate() = Buffer().writeUtf8(
  """
  -----BEGIN CERTIFICATE-----
  MIIHVzCCBT+gAwIBAgIUMExSPRGhPh864kaAq8DpKHnuU3wwDQYJKoZIhvcNAQEL
  BQAwgcAxDTALBgNVBAUTBDM0NTYxEjAQBgNVBAcMCUJhcmNlbG9uYTESMBAGA1UE
  CAwJQ2F0YWx1bnlhMQswCQYDVQQGEwJFUzEoMCYGA1UECgwfR3lta2FwcCBDZXJ0
  aWZpY2F0aW9uIEF1dGhvcml0eTEbMBkGA1UECwwSR3lta2FwcCBJbm5vdmF0aW9u
  MRswGQYJKoZIhvcNAQkBFgxneW1rQGFwcC5vcmcxFjAUBgNVBAMMDWd5bWthcHBj
  YS5vcmcwHhcNMjAwNDI3MTk1NzQxWhcNMjEwNDI3MTk1NzQxWjCBwDENMAsGA1UE
  BRMEMzQ1NjESMBAGA1UEBwwJQmFyY2Vsb25hMRIwEAYDVQQIDAlDYXRhbHVueWEx
  CzAJBgNVBAYTAkVTMSgwJgYDVQQKDB9HeW1rYXBwIENlcnRpZmljYXRpb24gQXV0
  aG9yaXR5MRswGQYDVQQLDBJHeW1rYXBwIElubm92YXRpb24xGzAZBgkqhkiG9w0B
  CQEWDGd5bWtAYXBwLm9yZzEWMBQGA1UEAwwNZ3lta2FwcGNhLm9yZzCCAiIwDQYJ
  KoZIhvcNAQEBBQADggIPADCCAgoCggIBAK5QBwTcShvrLfvFzkP57B/5HRUs+d+2
  rW/XQondH98Z/GicFAsY5QC0i0m00ZVyg0CNxZz/wTq4kgaaGqUNQWtBKWIAt64x
  fRSGaZyQf4KS3ZUwazGZqgM9+BbbqSezpKUMDhtqQE1F/Wr9HMgBo3ogZl/9vv05
  xBWwaZ4A3Fu2vatgJOI+PMjwrwwcSfPGAU0VM29ymYky6HxdXvokSLk3zHhLpuFF
  OqhzkU4MryfW+xl0nNBlqbqckuhRnhlItF1zyWP4cfyZQ8shpfzGxXfiGa/Yp9cb
  +RvYB3nZh6TuEknI4VNWtqHkN7BZv7Q2VYPG2O/82UWvXOHE1pbdGTsJmp5+vauj
  OCck+7YUeEvC4L8q8W/ZsXS1G0CD3B+VWO0BP0OsjqISYa8D0XlRU6iFVolL916/
  73Gtax7/AWHMZ9n6YxO/9Kp9F1SsKJ+ebaFf+ij/R0AWrMmhi3ppZwmP7KGYl/An
  6vsZ6vtIBAOa7MpxOc2rVoFuqaw6MPaP/CI8sGEt0JIWrre5ZgmyeqJW33U8qqJP
  Ol0mmkBB5jG5gEPo+85yyvtjezVDdb811UbEDoT7p1B4xaZFxZ9YG2tG1weDgdhX
  L1z/8wVDNrMkzyQojRTsIm5Hlz4aDZe+JA6Tbq0FThHESlsDDwYq8bJDpG0p7VpX
  tzEpByf8phqDAgMBAAGjggFFMIIBQTAdBgNVHQ4EFgQUGpYwufs+dtDZyJ6C5k2v
  8UYCwWkwggEABgNVHSMEgfgwgfWAFBqWMLn7PnbQ2cieguZNr/FGAsFpoYHGpIHD
  MIHAMQ0wCwYDVQQFEwQzNDU2MRIwEAYDVQQHDAlCYXJjZWxvbmExEjAQBgNVBAgM
  CUNhdGFsdW55YTELMAkGA1UEBhMCRVMxKDAmBgNVBAoMH0d5bWthcHAgQ2VydGlm
  aWNhdGlvbiBBdXRob3JpdHkxGzAZBgNVBAsMEkd5bWthcHAgSW5ub3ZhdGlvbjEb
  MBkGCSqGSIb3DQEJARYMZ3lta0BhcHAub3JnMRYwFAYDVQQDDA1neW1rYXBwY2Eu
  b3JnghQwTFI9EaE+HzriRoCrwOkoee5TfDAPBgNVHRMBAf8EBTADAQH/MAsGA1Ud
  DwQEAwIBBjANBgkqhkiG9w0BAQsFAAOCAgEAi07NfdXgEGimQ8sfMSGX+RP4xR3C
  BmQTb0nNAN438TiPm8mTMFwNwJMdaKya64d8MI/ElqiPGHGOJCMvTMbPYWnMwq7G
  XyFRPKT1Kprt0+7yEzCZD2FcjTDuGL3SmQWUFPqq/UIWxwV0TVB8xZe4z1vFGn2D
  V7/JGtzXdv04HeKYdceCwLvbDYdZTbH+2Je60Pa2OSKiQWX7OWnEjEVN+Fg/79cI
  AUC6gizLEso3j15nIS4XPFNg03jISbeeIl9NQ2ybxb1xWn2faR0rkUzVDlDRuZZ4
  F2FdSG5vthSpA9VvH7Iu1FWXyFQpjRc9dZn50sWDzhum1lKaiKWyswjViY4JKz/T
  wl4HoZzq81TvCPf0fh4DJkKygaMRBWve0674hHVEYss+VoqUH383oIVVYSxlvgMd
  rpM2SddqyETAeaRqw9Mlj8b/qFMfclqWPYt+jpJlAEoutF9z8IfZjJ+dWJpG832K
  ZOcblWCzuh8D3hn/CBWzcv1rmTcqv/dIMhC3HaFWKyQqEopee9XTwsw7IySglyLi
  TEP7aqH5WJMJvSz9KOo7kdpv1gs9jFMSOYHLtSL2Te9vMgc4QH5wnmjD6MZT0tRM
  dxTa/QmX8Qelyompa0KUJNSd9KZDdxPLO9pvbAmzNmHRBx708Z66GuMBKvAnghNL
  NSY05i/fQnLpGz0=
  -----END CERTIFICATE-----
""".trimIndent()
).inputStream()