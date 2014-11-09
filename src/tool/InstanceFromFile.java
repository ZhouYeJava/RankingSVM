package tool;
/**
 * Title: Instance from web
 * Description: the impress rate and the keyword
 * 
 * @author Zhou Ye
 *
 */

import java.util.HashMap;

public class InstanceFromFile {

	private String keyword;
	private int impress;
	private int search;
	private HashMap<KeySong, Integer> click;
	private HashMap<KeySong, Integer> display;
	
	public InstanceFromFile(String keyword, int impress, int search) {
		this.keyword = keyword;
		this.search = search;
		this.impress = impress;
		click = new HashMap<KeySong, Integer>();
		display = new HashMap<KeySong, Integer>();
	}
	
	public void putClick(KeySong ks, Integer clickNum) {
		click.put(ks, clickNum);
	}
	
	public void putDisplay(KeySong ks, Integer displayNum) {
		display.put(ks, displayNum);
	}
	
	public String getWord() {
		return keyword;
	}
	
	public int getSearch() {
		return search;
	}
	
	public int getImpress() {
		return impress;
	}
	
	public Integer getClick(KeySong ks) {
		if (click.containsKey(ks)) {
			return click.get(ks);
		}
		else {
			return -1; //if you cannot get the click number
		}
	}
	
	public Integer getDisplay(KeySong ks) {
		if (display.containsKey(ks)) {
			return display.get(ks);
		}
		else {
			return -1; //if you cannot get the display number
		}
	}
	
}
