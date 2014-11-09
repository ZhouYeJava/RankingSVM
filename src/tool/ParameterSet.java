package tool;
/**
 * The set of parameter names
 * @author Zhou Ye
 *
 */

public enum ParameterSet {

	IMPRESS, SEARCH, CATEGORY, EXPAND, LENCORD, QUERYNORM, USEQSTRUCTURE, THRESHOLD, FACTOR;
	
	@Override
	public String toString() {
		String content = name();
		String lower = content.substring(1).toLowerCase();
		return content.charAt(0)+lower;
	}
	
}
