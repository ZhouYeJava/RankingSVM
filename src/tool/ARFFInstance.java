package tool;
/**
 * Title: Final form of instance
 * Description: All the score and the impress rate
 * 
 * @author Zhou Ye
 *
 */

public class ARFFInstance {

	private int impress; //times of impress
	private int search; //times of search
	private double play; //monthly play times
	private int score; //human score
	private double clickRate; //click/display
	private double popScore; //pop score
	private double nameVectorScore;
	private double songAndArtistNameVectorScore;
	private double nameKeywordVectorScore;
	private double nameKeywordConstantScore;
	private double albumNameKeywordConstantScore;
	private double albumNameVectorScore;
	private double albumAliasVectorScore;
	private double aliasVectorScore;
	private double artistNameVectorScore;
	private double totalClick;
	
	public int getImpress() {
		return impress;
	}
	
	public void setImpress(int impress) {
		this.impress = impress;
	}
	
	public int getSearch() {
		return search;
	}
	
	public void setSearch(int search) {
		this.search = search;
	}
	
	public double getPlay() {
		return play;
	}
	
	public void setPlay(double play) {
		this.play = play;
	}
	
	public double getPopScore() {
		return popScore;
	}
	
	public void setPopScore(double popScore) {
		this.popScore = popScore;
	}
	
	public double getNameVectorScore() {
		return nameVectorScore;
	}
	
	public void setNameVectorScore(double nameVectorScore) {
		this.nameVectorScore = nameVectorScore;
	}
	
	public double getSongAndArtistNameVectorScore() {
		return songAndArtistNameVectorScore;
	}
	
	public void setSongAndArtistNameVectorScore(double songAndArtistNameVectorScore) {
		this.songAndArtistNameVectorScore = songAndArtistNameVectorScore;
	}
	
	public double getNameKeywordVectorScore() {
		return nameKeywordVectorScore;
	}
	
	public void setNameKeywordVectorScore(double nameKeywordVectorScore) {
		this.nameKeywordVectorScore = nameKeywordVectorScore;
	}
	
	public double getNameKeywordConstantScore() {
		return nameKeywordConstantScore;
	}
	
	public void setNameKeywordConstantScore(double nameKeywordConstantScore) {
		this.nameKeywordConstantScore = nameKeywordConstantScore;
	}
	
	public double getAlbumNameKeywordConstantScore() {
		return albumNameKeywordConstantScore;
	}
	
	public void setAlbumNameKeywordConstantScore(
			double albumNameKeywordConstantScore) {
		this.albumNameKeywordConstantScore = albumNameKeywordConstantScore;
	}
	
	public double getAlbumNameVectorScore() {
		return albumNameVectorScore;
	}
	
	public void setAlbumNameVectorScore(double albumNameVectorScore) {
		this.albumNameVectorScore = albumNameVectorScore;
	}
	
	public double getAlbumAliasVectorScore() {
		return albumAliasVectorScore;
	}
	
	public void setAlbumAliasVectorScore(double albumAliasVectorScore) {
		this.albumAliasVectorScore = albumAliasVectorScore;
	}
	
	public double getAliasVectorScore() {
		return aliasVectorScore;
	}
	
	public void setAliasVectorScore(double aliasVectorScore) {
		this.aliasVectorScore = aliasVectorScore;
	}
	
	public double getArtistNameVectorScore() {
		return artistNameVectorScore;
	}
	
	public void setArtistNameVectorScore(double artistNameVectorScore) {
		this.artistNameVectorScore = artistNameVectorScore;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public double getClickRate() {
		return clickRate;
	}

	public void setClickRate(double clickRate) {
		this.clickRate = clickRate;
	}

	public double getTotalClick() {
		return totalClick;
	}

	public void setTotalClick(double totalClick) {
		this.totalClick = totalClick;
	}
	
}
