package pt.inevo.lastfm

import dispatch._
import scala.collection.mutable.Map

object LastFM{
  val apiRoot = "http://ws.audioscrobbler.com/2.0"
  val http = new AppEngineHttp
  
  val methodPrefix = "?method="
  val keyParameter = "&api_key="+AccountInfo.apiKey
  
  
  def makeUrlRequest(requestTitle:String, urlParameters:Map[String, String] = Map[String, String]()): String = {
    var returnUrl = LastFM.apiRoot + methodPrefix
    returnUrl += requestTitle + "&"
    returnUrl += Http.q_str(urlParameters)
    returnUrl + keyParameter
  }
}