package pt.inevo.lastfm
import scala.xml._
import siena._
import java.lang.{Long => JLong}

class Artist(var name:String = "", var mbid:String = "", var url:String = "") extends Model {
	
	 @Id
	 var id: JLong = _
  
	def setValues(artistNode:Node){
		name = (artistNode \ "name").text
		mbid = (artistNode \ "mbid").text
		url = (artistNode \ "url").text
	}

  override def toString:String = {
    name+" "+mbid+" "+url
  }
}