package optimize;
/**
 * Generate the data set which can be used not only to generate ARFF file
 * @author Zhou Ye
 */

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import exception.HTMLGetterException;
import tool.InstanceFromFile;
import tool.InstanceFromWeb;
import exception.JSONGetterException;
import tool.KeySong;
import tool.PairInFile;

public class CreateDataSet {

	private int expand; //number of expansion of sub-query
	private boolean lenCord; //ask Zhuohao Li
	private boolean queryNorm; //the norm which is used before
	private boolean useQstructure; //ask Zhuohao Li
	private static final int SCORENUM = 12; //number of all scores
	private static Logger logger = Logger.getLogger(CreateDataSet.class.getName());
	
	/**
	 * Constructor
	 * @param expand Number of expansion of sub-query
	 * @param lenCord Ask Zhuohao Li
	 * @param queryNorm The norm which is used before
	 * @param useQstructure Ask Zhuohao Li
	 */
	public CreateDataSet(int expand, boolean lenCord, boolean queryNorm, boolean useQstructure) {
		this.expand = expand;
		this.lenCord = lenCord;
		this.queryNorm = queryNorm;
		this.useQstructure = useQstructure;
	}
	
	/**
	 * Main method to get all the information from file and web and write into an ARFF file
	 * @param path The location of the file
	 * @param clickpath The location of the file containing all click information
	 * @param percent How many data I need
	 */
	public void create(String path, String clickPath) {
		logger.info("Start Creating Data Set!");
		/*Read click*/
		HashMap<BigInteger, Integer> allClick = readClick(clickPath); //read all the click number
		/*Create the array to store the maximum value*/
		Double[] m = new Double[SCORENUM]; //store the maximum score
		for (int i=0; i<m.length; i++) {
			m[i] = 0.0;
		}
		/*Write initial file*/
		try {
			String fileName = "./data/AllData.data";
			File f = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(""); //write an empty file
			output.close();
		} catch (IOException ee) {
			logger.error("Error Writing AllData!");
		}
		try {
			String fileName = "./data/AllWord.word";
			File f = new File(fileName);
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(""); //write an empty file
			output.close();
		} catch (IOException ee) {
			logger.error("Error Writing AllWord!");
		}
		/*Read Files*/
		BufferedReader reader = null;
		String line;
		try {
			reader = new BufferedReader(new FileReader(new File(path)));
			String delimiter = "\t|||\t"; //use this to separate data in a line
			int counter = 0;
			while ((line = reader.readLine())!=null) {
				/*Construct the keyword for web search*/
				InstanceFromFile file = readFile(line);
				String q = file.getWord(); //search keyword
				String query; //the query for web
				int countBlank = countBlanks(q);
				if (countBlank>0) {
					query = q.replace(" ", "%20"); //change the format to satisfy the url encoding
				}
				else {
					query = q;
				}
				/*Web search*/
				ArrayList<InstanceFromWeb> web = new ArrayList<InstanceFromWeb>();
				try {
					web = readJSON("http://app-30.photo.163.org:7582/sd/service/query?index=SongValid&q="+query+"&stype=1&openExp=true&lenCord="+lenCord+"&queryNorm="+queryNorm+"&useQStructure="+useQstructure+"&length="+expand);
				} catch (JSONGetterException e) {
					logger.warn("The Word "+q+" Cannot Be Found In JSON!");
					continue; //search the next word!				
				}
				/*Build the instance and write into file*/
				String result = "";
				int count = 0;
				for (InstanceFromWeb j : web) {
					KeySong ks = new KeySong(q, j.getID()); //keyword and song id
					int c = file.getClick(ks);
					int d = file.getDisplay(ks);
					if (c==-1 || d==-1) {
						continue; //cannot get from file
					}
					else if (d==0) {
						continue; //invalid display number
					}
					else {
						int[] play = new int[2];
						try {
							play = readHTML("http://app-30.photo.163.org:8080/userprofile/searchSong.jsp?songid="+ks.getID().toString()+"&timerange=season&Submit=提交");
						} catch (HTMLGetterException e) {
							logger.warn("The ID "+ks.getID().toString()+" Cannot Be Found In HTML!");
							continue; //cannot get from data
						}
						if (!allClick.containsKey(ks.getID())) {
							logger.warn(ks.getID().toString()+" Does Not Have Click Number!");
							continue; //if the song does not have click number, then we do not need it
						}
						else {
							int totalClick = allClick.get(ks.getID());
							/*Organize output!*/
							double cli = (double) c; //click number
							double dis = (double) d; //display number
							double clickRate = cli/dis; //click rate
							Double[] scoreArray = {j.getScore("PopScore"), j.getScore("Name:VectorScore"), 
									j.getScore("SongAndArtistName:VectorScore"), j.getScore("NameKeyword:VectorScore"),
									j.getScore("NameKeyword:ConstantScore"), j.getScore("AlbumNameKeyword:ConstantScore"), 
									j.getScore("AlbumName:VectorScore"), j.getScore("AlbumAlias:VectorScore"), 
									j.getScore("Alias:VectorScore"), j.getScore("ArtistName:VectorScore"),
									(double) play[0], (double) totalClick};
							String scoreAll = scoreArray[0].toString();
							for (int i=1; i<scoreArray.length; i++) {
								scoreAll += delimiter+scoreArray[i].toString();
							}
							String score = q+delimiter+j.getID()+delimiter+j.getName()+delimiter+j.getArtist()+delimiter+j.getAlbum()+delimiter+
							               scoreAll+delimiter+file.getImpress()+delimiter+file.getSearch()+delimiter+clickRate+delimiter+manualRandom()+"\n";
							result += score;
							count++; //record the number of effective results for certain keyword
							for (int i=0; i<m.length; i++) {
								if (scoreArray[i]>m[i]) {
									m[i] = scoreArray[i];
								}
							}
						}
					}
				}
				/*Write data after the previous files*/
				if (count==0) {
					continue; //no need to record the query with zero results
				}
				else {
					try {
						String fileNameAdd = "./data/AllData.data";
						BufferedWriter outputAdd = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNameAdd, true), "UTF-8"));
						outputAdd.write(result);
						outputAdd.close();
					} catch (IOException e) {
						logger.error("Error Writing AllData!");
					}
					try {
						String fileNameAdd = "./data/AllWord.word";
						BufferedWriter outputAdd = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNameAdd, true), "UTF-8"));
						outputAdd.write(q+delimiter+count+"\n");
						outputAdd.close();
					} catch (IOException e) {
						logger.error("Error Writing AllWord!");
					}
				}
				counter++;
				if (counter%1000==0) {
					System.out.println("Create Data Set At "+counter+"...");
				}
			}
			/*Write max scores*/
			try {
				String fileName = "./data/AllMaxScore.score";
				File f = new File(fileName);
				BufferedWriter output = new BufferedWriter(new FileWriter(f));
				String scoreMax = m[0].toString();
				for (int i=1; i<m.length; i++) {
					scoreMax += delimiter+m[i].toString();
				}
				output.write(scoreMax+"\n");
				output.close();
			} catch (IOException e) {
				logger.error("Error Writing AllMaxScore!");
			}
			logger.info("Finish Creating Data Set!");
		} catch (IOException e) {
			logger.error("Problems When Reading!");
		}  finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error("Cannot Close Data File!");
			}
		}
	}

	/**
	 * Get information from file
	 * @param line Each line of the file
	 * @return The class containing all the information for the tuple (keyword,song)
	 */
	private InstanceFromFile readFile(String line) {
		String[] tempLine = line.split("\t");
		String keyword = tempLine[0]; //search keyword
		InstanceFromFile insf = null;
		int impress = Integer.parseInt(tempLine[1]); //impress number
		int search = Integer.parseInt(tempLine[2]); //search number
		insf  = new InstanceFromFile(keyword, impress, search);
		String impressList = tempLine[3]; //display song list
		String searchList = tempLine[4]; //search song list
		ArrayList<PairInFile> sepImpressList = splitListIntoPair(impressList); //separate the search list
		ArrayList<PairInFile> sepSearchList = splitListIntoPair(searchList); //separate the search list
		if (!sepImpressList.isEmpty()) {
			for (PairInFile i : sepImpressList) {
				BigInteger id = i.getId(); //song id
				int displayNum = i.getNum(); //display number
				KeySong ks = new KeySong(keyword, id);
				insf.putDisplay(ks, displayNum);				
			}
		}
		if (!sepSearchList.isEmpty()) {
			for (PairInFile i : sepSearchList) {
				BigInteger id = i.getId(); //song id
				int clickNum = i.getNum(); //click number
				KeySong ks = new KeySong(keyword, id);
				insf.putClick(ks, clickNum);				
			}
		}
		return insf;
	}
	
	/**
	 * Get information from web in JSON format
	 * @param strUrl Web address
	 * @return The class containing all the information for the tuple keyword-song 
	 * @throws JSONGetterException This will happen if you cannot connect to or get data from web in JSON
	 */
	private ArrayList<InstanceFromWeb> readJSON(String strUrl) throws JSONGetterException {
		String s = null;
	    try {
	    	URL url = new URL(strUrl);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
	        s = reader.readLine();
	        reader.close();
	       } catch (Exception e) {
	    	   throw new JSONGetterException();
	       }
    	ArrayList<InstanceFromWeb> insArray = new ArrayList<InstanceFromWeb>();
    	JSONObject dataJson = JSONObject.fromObject(s);
    	int code = dataJson.getInt("code"); //200 for normal and others for abnormal
    	if (code!=200) {
    		throw new JSONGetterException();
    	}
    	else {
        	JSONObject result = dataJson.getJSONObject("result");
        	int totalHit = result.getInt("totalHit");
        	if (totalHit==0) {
        		logger.warn("Zero Search Result!");
        	}
        	else{
        		JSONArray doc = result.getJSONArray("docs");
            	for (Object i : doc) {
            		InstanceFromWeb ins = new InstanceFromWeb();
            		JSONObject docMember = JSONObject.fromObject(i);
            		Integer id = docMember.getJSONObject("fields").getInt("ID"); //get the id 
            		String name = docMember.getJSONObject("fields").getString("Name"); //get the name
            		String artist = docMember.getJSONObject("fields").getString("ArtistName"); //get the artist
            		String album = docMember.getJSONObject("fields").getString("AlbumNameKeyword"); //get the album
            		ins.setID(new BigInteger(id.toString()));
            		ins.setName(name);
            		ins.setArtist(artist);
            		ins.setAlbum(album);
            		JSONObject explain = docMember.getJSONObject("explain");
            		double popScore = explain.getDouble("popScore"); //get the pop score
            		ins.setScore("PopScore", popScore);
            		JSONArray subQuerys = explain.getJSONArray("subQuerys");
            		for (Object j : subQuerys) {
            			JSONObject subMember = JSONObject.fromObject(j);
            			String field = subMember.getString("field"); //get the field name
            			String type = subMember.getString("type"); //get the type name
            			String ft = field+":"+type;
            			double subScore = subMember.getDouble("score"); //get the relative score
            			ins.setScore(ft, subScore);
            		}
            		insArray.add(ins);
            	}
        	}
    	}
    	return insArray;
	}
	
	/**
	 * Split a String into an ArrayList
	 * @param list A specific kind of String
	 * @return An ArrayList containing all the information
	 */
	private ArrayList<PairInFile> splitListIntoPair(String list) {
		if (list.length()==2) {
			return new ArrayList<PairInFile>(); //empty list
		}
		else {
			String subList = list.substring(2, (list.length()-2)); //eliminate []
			String[] splList = subList.split("\\)\\, \\("); //separate as pair
			ArrayList<PairInFile> afterSplit = new ArrayList<PairInFile>(); //separate as array
			for (String pair : splList) {
				String[] splPair = pair.split("\\, ");
				PairInFile p = new PairInFile();
				p.setId(new BigInteger(splPair[0]));
				p.setNum(Integer.parseInt(splPair[1]));
				afterSplit.add(p);
			}
			return afterSplit;
		}
	}
	
	/**
	 * Count how many blacks in a String
	 * @param s Any String
	 * @return Number of blanks
	 */
	private int countBlanks(String s){
    	int i = 0;
    	int count = 0;
    	 while(i<s.length()) {
    		 if(s.charAt(i)==' '){
    			 count++;
    		 }
    		 i++;
    	 }
    	 return count;
    }
	
	/**
	 * Get the information from web in HTML format
	 * @param path The address of the web
	 * @return Play times and user numbers (seasonal)
	 * @throws HTMLGetterException This will happen if you cannot connect to or get data from web in HTML
	 */
	private int[] readHTML(String path) throws HTMLGetterException {
		int[] result = new int[2];
		String s = null;
		try {
	    	URL url = new URL(path);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
	        String dataLine = null;
	        while ((dataLine = reader.readLine())!=null) {
	            s += dataLine+"\n";
	        }
	        reader.close();
	       } catch (Exception e) {
	    	   logger.warn("Cannot connect to the web in HTML!");
	    	   throw new HTMLGetterException();
	       }
	    Matcher m = Pattern.compile("<h3>时间段：.	播放次数：(\\d+)	播放用户数：(\\d+)</h3>").matcher(s);
	    if (m.find()==false) {
	    	throw new HTMLGetterException();
	    }
	    else {
	    	result[0] = Integer.parseInt(m.group(1));
		    result[1] = Integer.parseInt(m.group(2));
	    }
	    return result;
	}
	
	/**
	 * Randomly generate D, E or F to occupy the space of manual label
	 * @return D, E, F which can tell me where I have marked already
	 */
	private String manualRandom() {
		String[] array = {"D","E","F"};
		Random r = new Random();
		return array[Math.abs(r.nextInt())%3];
	}
	
	/**
	 * Read a file containing the click number for each song and store them in a HashMap
	 * @param path the location of the file
	 * @return the HashMap containing all the click number
	 */
	private HashMap<BigInteger, Integer> readClick(String path) {
		HashMap<BigInteger, Integer> data = new HashMap<BigInteger, Integer>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String dataLine = null;
			while ((dataLine = reader.readLine())!=null) {
				String[] s = dataLine.split("\t");
				BigInteger id = new BigInteger(s[1]); //song id
				int itemType = Integer.parseInt(s[2]); //1 for song type
				int click = Integer.parseInt(s[3]); //click number
				if (itemType!=1) {
					continue;
				}
				else {
					data.put(id, click);
				}
			}
			reader.close();
		} catch (IOException e) {
			logger.error("Problems When Reading Click Number!");
		}
		logger.info("Finish Loading Click Number!");
		return data;
	}
	
}
