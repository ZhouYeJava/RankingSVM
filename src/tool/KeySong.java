package tool;
/**
 * Title: Keyword-Song
 * Description: The tuple of keyword and song id
 * 
 * @author Zhou Ye
 *
 */

import java.math.BigInteger;

public class KeySong {

	private String keyword; //search keyword
	private BigInteger id; //identifier of the song
	
	public KeySong(String keyword, BigInteger id) {
		this.keyword = keyword;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "keyword = "+keyword+"\t"+
				"ID = "+id.toString()+"\t";
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public BigInteger getID() {
		return id;
	}
	
	@Override
	public boolean equals(Object comp) {
		if (comp instanceof KeySong) {
			KeySong c = (KeySong) comp;
			if (this.keyword.equals(c.keyword) && this.id.equals(c.id)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37*result+keyword.hashCode();
		result = 37*result+id.hashCode();
		return result;
	}
	
}
