package tool;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Title: Temporary instance from web
 * Description: song id/name/artist/album and the class instance from web
 * 
 * @author Zhou Ye
 *
 */

public class InstanceFromWeb {

	private BigInteger id;
	private String name;
	private String artist;
	private String album;
	private HashMap<String, Double> score;
	
	public InstanceFromWeb() {
		score = new HashMap<String, Double>();
		score.put("PopScore", 0.0);
		score.put("Name:VectorScore", 0.0);
		score.put("SongAndArtistName:VectorScore", 0.0);
		score.put("NameKeyword:VectorScore", 0.0);
		score.put("NameKeyword:ConstantScore", 0.0);
		score.put("AlbumNameKeyword:ConstantScore", 0.0);
		score.put("AlbumName:VectorScore", 0.0);
		score.put("AlbumAlias:VectorScore", 0.0);
		score.put("Alias:VectorScore", 0.0);
		score.put("ArtistName:VectorScore", 0.0);
	}
	
	public void setID(BigInteger id) {
		this.id = id;
	}
	
	public BigInteger getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public Double getScore(String item) {
		return score.get(item);
	}

	public void setScore(String item, Double score) {
		this.score.put(item, score);
	}
	
}
