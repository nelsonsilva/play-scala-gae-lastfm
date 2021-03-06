package pt.inevo.lastfm
import dispatch._
import Http._
import scala.xml._
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

class User(var userId:String){
  var id = ""
  var name = ""
  var realname = ""
  var url = ""
  var image = ""
  var country = ""
  var age = 0
  var gender = ""
  var subscriber = 0	  
  var playcount = 0
  var playlists = 0
  var bootstrap = 0
  
  def init(): User = {
    val userNodes = getInfo()
    id = (userNodes \ "id").text
    name = (userNodes \ "name").text
    realname = (userNodes \ "realname").text
    url = (userNodes \ "url").text
    image = (userNodes \ "image").text
    age = (userNodes \ "age").text.toInt
    gender = (userNodes \ "gender").text
    subscriber = (userNodes \ "subscriber").text.toInt
    playcount = (userNodes \ "playcount").text.toInt
    playlists = (userNodes \ "playlists").text.toInt
    bootstrap = (userNodes \ "bootstrap").text.toInt
    this
  }
  
  def getInfo(): NodeSeq = {
	  User.getInfo(userId)
  }
  
  def getTopArtists(timePeriod:String = User.OVERALL): ArrayBuffer[Artist]={
	  var artistNodes = User.getTopArtists(userId, timePeriod)
    var artists = new ArrayBuffer[Artist]
    artistNodes foreach {(node) =>
      val newArtist = new Artist()
      newArtist.setValues(node)
      artists+=(newArtist)
    }
    //println(artists)
    return artists
  }

  def getArtistTracks(artistName:String, startTime:Int = -1, endTime:Int = -1, page:Int = -1)={
	  var trackNodes = User.getArtistTracks(userId, artistName, startTime, endTime, page)
    var tracks = new ArrayBuffer[Track]
    trackNodes map {(node) =>
      val artistString = (node \ "artist").text
      val trackNameString = (node \ "name").text
      new Track(artistName=artistString, track=trackNameString, user=this).init
    }

  }
  
  def getRecentTracks(limit:Int = -1, page:Int = -1): ArrayBuffer[Track] = {
    var trackNodes = User.getRecentTracks(userId, limit, page)
    var tracks = new ArrayBuffer[Track]
    trackNodes foreach {(node) =>
      val artistString = (node \ "artist").text
      val trackNameString = (node \ "name").text
      tracks+=(new Track(artistName=artistString, track=trackNameString, user=this).init)
    }
    //println(tracks)
    return tracks
  }
  
  def getNumberOfRecentTracksPages(): Int = {
    val paramMap = Map[String, String]()
    paramMap += ("user" -> userId)
    val getRecentTracksUrl:String = LastFM.makeUrlRequest("user.getRecentTracks", paramMap)
    var responseSeq = LastFM.http(getRecentTracksUrl <> { _ \\ "recenttracks" })
    var totalPagesInt = 0
    val tempNode = responseSeq.headOption
    tempNode match {
      case None => {println("No first element in getNumberOfRecentTracks")}
      case Some(x) => {
        val totalPagesOption = x.attribute("totalPages")
        var totalPagesInt = 0
        totalPagesOption match{
          case None => {println("No total pages attribute")}
          case Some(tp) => {
            totalPagesInt = Integer.parseInt(tp.toString)
          }
        }
      }
    }
    return totalPagesInt
  }
}

object User {
  val OVERALL = "overall"
  val SEVEN_DAYS = "7day"
  val THREE_MONTHS = "3month"
  val SIX_MONTHS = "6month"
  val TWELVE_MONTHS = "12month"
	
  def getInfo(userId:String): NodeSeq = {
    val paramMap = Map[String, String]()
    paramMap += ("user" -> userId)
    val getInfoUrl:String = LastFM.makeUrlRequest("user.getInfo", paramMap)
    LastFM.http(getInfoUrl <> { _ \\ "user" })
  }
  
  def getTopArtists(userId:String, timePeriod:String = OVERALL): NodeSeq={
	  val paramMap = Map[String, String]()
    paramMap += ("user" -> userId)
    val getInfoUrl:String = LastFM.makeUrlRequest("user.getTopArtists", paramMap)
    LastFM.http(getInfoUrl <> { _ \\ "artist" })
  }
  
  def getRecentTracks(userId:String, limit:Int = -1, page:Int = -1): NodeSeq = {
    val paramMap = Map[String, String]()
    paramMap += ("user" -> userId)
    if(limit >= 0){
      paramMap += ("limit"->limit.toString)
    }
    if(page >= 0){
      paramMap += ("page"->page.toString)
    }
    val getRecentTracksUrl:String = LastFM.makeUrlRequest("user.getRecentTracks", paramMap)
    LastFM.http(getRecentTracksUrl <> { _ \\ "track" })
  }

  def getArtistTracks(userId:String, artistName:String, startTime:Int = -1, endTime:Int = -1, page:Int = -1): NodeSeq = {
    val paramMap = Map[String, String]()
    paramMap += ("user" -> userId)
    paramMap += ("artist" -> artistName)
    if(startTime >= 0){
      paramMap += ("startTimestamp"->startTime.toString)
    }
    if(endTime >= 0){
      paramMap += ("endTimestamp"->endTime.toString)
    }
    if(page >= 0){
      paramMap += ("page"->page.toString)
    }
    val getRecentTracksUrl:String = LastFM.makeUrlRequest("user.getArtistTracks", paramMap)
    LastFM.http(getRecentTracksUrl <> { _ \\ "track" })
  }
}