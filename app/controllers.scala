package controllers

import pt.inevo.lastfm.User
import play._
import play.mvc._

object Application extends Controller {

	def index = Template

	def topArtists(user:String) = Json(new User(user).getTopArtists().toArray)

			//val topArtistTracks = ghostm.getArtistTracks(artistName = topArtists(0).name)
			//Json(topArtistTracks)
	
}
