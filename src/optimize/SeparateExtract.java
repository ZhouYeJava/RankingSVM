package optimize;
/**
 * Separate the extract file
 * @author Zhou Ye
 */

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.sf.json.JSONObject;

import start.ParamImporter;
import tool.KeywordInformation;

public class SeparateExtract {
	
	private int impressStandard; //minimum impress number
	private int searchStandard; //minimum search number
	private String category; //Song/Artist/Album/Suggest
	private Map<String, KeywordInformation> dictionary; //query -> impress+search+impress list+search list
	private static Logger logger = Logger.getLogger(ParamImporter.class.getName());
	
	/**
	 * Constructor
	 * @param impressStandard Minimum impress number
	 * @param searchStandard Minimum search number
	 * @param category Song/Artist/Album/Suggest
	 */
	public SeparateExtract(int impressStandard, int searchStandard, String category) {
		this.impressStandard = impressStandard;
		this.searchStandard = searchStandard;
		this.category = category;
		dictionary = new HashMap<String, KeywordInformation>();
		PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * Main program to execute all the steps for separation 
	 * @param path Location of extract file
	 */
	public void separate(String path) {
		logger.info("Start Separate Extract!");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(path)));
			String line = null;
			int counter = 0;
			while ((line = br.readLine())!=null) {
				String[] pair = line.split("\t");
				if (pair.length!=2) {
					continue;
				}
				else {
					String tag = pair[0];
					String content = pair[1];
					if (tag.equals("search")) {
						searchExtraction(content, category);
					}
					else if (tag.equals("searchimpress")) {
						impressExtraction(content, category);
					}
					else if (tag.equals("searchkeyword")) {
						continue;
					}
					else {
						logger.warn("Invalid Tag: "+tag+"!");
						continue;
					}
				}
				counter++;
				if (counter%100000==0) {
					logger.info("Extraction At "+counter+"...");
					System.out.println("Extraction At "+counter+"...");
				}
			}
			List<Map.Entry<String, KeywordInformation>> list = new ArrayList<Map.Entry<String, KeywordInformation>>(dictionary.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, KeywordInformation>>() { 
				@Override
			    public int compare(Map.Entry<String, KeywordInformation> o1, Map.Entry<String, KeywordInformation> o2) {      
			        return -(o1.getValue().getImpress()).compareTo(o2.getValue().getImpress());
			    }
			});
			writeFile(list);
			logger.info("Finish Separate Extract!");
		} catch (IOException e) {
			logger.error("Cannot Read Extract File!");
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				logger.error("Cannot Close Extract File!");
			}
		}
	}
	
	/**
	 * extract the search-impress item
	 * @param content The item
	 * @param category Song/Artist/Album/Suggest
	 */
	private void impressExtraction(String content, String category) {
		JSONObject obj = JSONObject.fromObject(content);
		if (!obj.containsKey("type") || !obj.containsKey("item") || !obj.containsKey("keyword")) {
			logger.warn("Miss type, item or keyword!");
		}
		else {
			String type = obj.getString("type");
			String item = obj.getString("item");
			String keyword = obj.getString("keyword");
			if (!type.equals(category)) {
				return; //not match the category
			}
			else {
				if (!dictionary.containsKey(keyword)) {
					dictionary.put(keyword, new KeywordInformation());
				}
				else {
					KeywordInformation ki = dictionary.get(keyword);
					ki.addImpress();
					List<BigInteger> idList = convertStringToList(item);
					for (BigInteger id : idList) {
						if (category.equals("suggest")) {
							char i = id.toString().charAt(0);
							String subtractor = i+"000000000";
							BigInteger mid = id.subtract(new BigInteger(subtractor));
							if (!ki.containsImpressList(mid)) {
								ki.putImpressList(mid, 1);
							}
							else {
								ki.addImpressList(mid);
							}
						}
						else {
							if (!ki.containsImpressList(id)) {
								ki.putImpressList(id, 1);
							}
							else {
								ki.addImpressList(id);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * extract the search item
	 * @param content The item
	 * @param category Song/Artist/Album/Suggest
	 */
	private void searchExtraction(String content, String category) {
		JSONObject obj = JSONObject.fromObject(content);
		if (!obj.containsKey("type") || !obj.containsKey("id") || !obj.containsKey("keyword")) {
			logger.warn("Miss type, id or keyword!");
		}
		else {
			String type = obj.getString("type");
			BigInteger id = new BigInteger(obj.getString("id"));
			String keyword = obj.getString("keyword");
			if (!type.equals(category)) {
				return; //not match the category
			}
			else {
				if (!dictionary.containsKey(keyword)) {
					dictionary.put(keyword, new KeywordInformation());
				}
				else {
					KeywordInformation ki = dictionary.get(keyword);
					ki.addSearch();
					if (category.equals("suggest")) {
						char i = id.toString().charAt(0);
						String subtractor = i+"000000000";
						BigInteger mid = id.subtract(new BigInteger(subtractor));
						if (!ki.containsSearchList(mid)) {
							ki.putSearchList(mid, 1);
						}
						else {
							ki.addSearchList(mid);
						}
					}
					else {
						if (!ki.containsSearchList(id)) {
							ki.putSearchList(id, 1);
						}
						else {
							ki.addSearchList(id);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Convert a specific string into list
	 * @param str The string
	 * @return The list
	 */
	private List<BigInteger> convertStringToList(String str) {
		if (str.length()==2) {
			return new ArrayList<BigInteger>();
		}
		else {
			List<BigInteger> result = new ArrayList<BigInteger>();
			String substr = str.substring(1, str.length()-1);
			String[] temp = substr.split(",");
			for (String i : temp) {
				result.add(new BigInteger(i));
			}
			return result;
		}
	}
	
	/**
	 * Write the final file
	 * @param list The list containing all the information
	 */
	private void writeFile(List<Map.Entry<String, KeywordInformation>> list) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File("./data/AfterSeparation."+category)));
			for (Map.Entry<String, KeywordInformation> tuple : list) {
				if (tuple.getValue().getImpress()<=impressStandard || tuple.getValue().getSearch()<=searchStandard) {
					continue;
				}
				else {
					String line = tuple.getKey()+"\t"+tuple.getValue().toString()+"\n";
					bw.write(line);
				}
			}
		} catch (IOException e) {
			logger.error("Cannot Write AfterSeparation File!");
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				logger.error("Cannot Close AfterSeparation File!");
			}
		}
	}
	
}
