package tool;
/**
 * The pair in the file
 * @author Zhou Ye
 */

import java.math.BigInteger;

public class PairInFile {

	private BigInteger id; //the identifier of the song
	private int num; //the click or display number
	
	public BigInteger getId() {
		return id;
	}
	
	public void setId(BigInteger id) {
		this.id = id;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	@Override
	public String toString() {
		return "("+id.toString()+":"+num+")";
	}
	
}
