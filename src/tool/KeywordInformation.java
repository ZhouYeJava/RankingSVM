package tool;
/**
 * The information of query
 * @author Zhou Ye
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordInformation {

	private Integer impress = 0;
	private Integer search = 0;
	private Map<BigInteger, Integer> impressList = new HashMap<BigInteger, Integer>();
	private Map<BigInteger, Integer> searchList = new HashMap<BigInteger, Integer>();
	
	public void addImpress() {
		impress++;
	}
	
	public void addSearch() {
		search++;
	}
	
	public Integer getImpress() {
		return impress;
	}
	
	public Integer getSearch() {
		return search;
	}
	
	public void putImpressList(BigInteger id, Integer impress) {
		impressList.put(id, impress);
	}
	
	public void putSearchList(BigInteger id, Integer search) {
		searchList.put(id, search);
	}
	
	public boolean containsImpressList(BigInteger id) {
		return impressList.containsKey(id);
	}
	
	public boolean containsSearchList(BigInteger id) {
		return searchList.containsKey(id);
	}
	
	public void addImpressList(BigInteger id) {
		impressList.put(id,(impressList.get(id)+1));
	}
	
	public void addSearchList(BigInteger id) {
		searchList.put(id,(searchList.get(id)+1));
	}
	
	private String printList(Map<BigInteger, Integer> map) {
		List<Map.Entry<BigInteger, Integer>> list = new ArrayList<Map.Entry<BigInteger, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<BigInteger, Integer>>() { 
			@Override
		    public int compare(Map.Entry<BigInteger, Integer> o1, Map.Entry<BigInteger, Integer> o2) {      
		        return -(o1.getValue()).compareTo(o2.getValue());
		    }
		});
		String result = "[";
		for (Integer i = 0; i<list.size(); i++) {
			if (i==0) {
				result += printPair(list.get(i));
			}
			else {
				result += ", "+printPair(list.get(i));
			}
		}
		result += "]";
		return result;
	}
	
	private String printPair(Map.Entry<BigInteger, Integer> pair) {
		return "("+pair.getKey().toString()+", "+pair.getValue().toString()+")";
	}
	
	@Override
	public String toString() {
		return impress+"\t"+search+"\t"+printList(impressList)+"\t"+printList(searchList);
	}
	
}
