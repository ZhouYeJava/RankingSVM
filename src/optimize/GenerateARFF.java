package optimize;
/**
 * Generate the ARFF file
 * @author Zhou Ye
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tool.ARFFInstance;

public class GenerateARFF {
	
	private static final String HEADER1 = "@relation score_search"+"\n"+
                                          "@attribute \'PopScore\' real"+"\n"+
                                          "@attribute \'Name:VectorScore\' real"+"\n"+
                                          "@attribute \'SongAndArtistName:VectorScore\' real"+"\n"+
                                          "@attribute \'NameKeyword:VectorScore\' real"+"\n"+
                                          "@attribute \'NameKeyword:ConstantScore\' real"+"\n"+
                                          "@attribute \'AlbumNameKeyword:ConstantScore\' real"+"\n"+
                                          "@attribute \'AlbumName:VectorScore\' real"+"\n"+
                                          "@attribute \'AlbumAlias:VectorScore\' real"+"\n"+
                                          "@attribute \'Alias:VectorScore\' real"+"\n"+
                                          "@attribute \'ArtistName:VectorScore\' real"+"\n"+
                                          "@attribute \'SeasonalPlayTimes\' real"+"\n"+
                                          "@attribute \'TotalClickNumber\' real"+"\n"+
                                          "@attribute \'Class\' {good, bad}"+"\n"+
                                          "@data"+"\n"; //header for 12 scores
	private static final String HEADER2 = "@relation score_search"+"\n"+
                                          "@attribute \'PopScore\' real"+"\n"+
                                          "@attribute \'Name:VectorScore\' real"+"\n"+
                                          "@attribute \'SongAndArtistName:VectorScore\' real"+"\n"+
                                          "@attribute \'NameKeyword:VectorScore\' real"+"\n"+
                                          "@attribute \'NameKeyword:ConstantScore\' real"+"\n"+
                                          "@attribute \'AlbumNameKeyword:ConstantScore\' real"+"\n"+
                                          "@attribute \'AlbumName:VectorScore\' real"+"\n"+
                                          "@attribute \'AlbumAlias:VectorScore\' real"+"\n"+
                                          "@attribute \'Alias:VectorScore\' real"+"\n"+
                                          "@attribute \'ArtistName:VectorScore\' real"+"\n"+
                                          "@attribute \'Class\' {good, bad}"+"\n"+
                                          "@data"+"\n"; //header for 10 scores
	private int factor; //enlarging factor: 1 means no enlarging; 0 means no use of hypothesis test
	private int threshold; //maximum number for each search word I will do the comparison (superior 10)
	private static final int SCORENUM = 12; //number of all scores
	private int indicator; //1 for 12 scores and 2 for 10 scores
	private static Logger logger = Logger.getLogger(GenerateARFF.class.getName());
	
	/**
	 * 
	 * @param indicator
	 * @param factor
	 * @param threshold
	 */
	public GenerateARFF(int indicator, int factor, int threshold) {
		this.indicator = indicator;
		this.factor = factor;
		this.threshold = threshold;
	}
	
	/**
	 * Main file to generate ARFF
	 * @param allWord All the keywords
	 * @param allData All the scores and click rate
	 * @param allScoreMax All the maximum scores for normalization
	 */
	public void generateARFF(String allWord, String allData, String allScoreMax) {
		logger.info("Start Generating ARFF");
		/*Read files*/
		File word = new File(allWord); //all the keywords and number of songs for each word
		File data = new File(allData); //all the data for each tuple (keyword ,song)
		File maxScore = new File(allScoreMax); //all the maximum for each score used to normalize
		BufferedReader reader = null;
		BufferedReader readerInside = null;
		BufferedReader readerScore = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(word), "UTF-8"));
			readerInside = new BufferedReader(new InputStreamReader(new FileInputStream(data), "UTF-8"));
			readerScore = new BufferedReader(new InputStreamReader(new FileInputStream(maxScore), "UTF-8"));
			String line; //each line for AllWord
			List<ARFFInstance> allInstance = new ArrayList<ARFFInstance>(); //every word is one group
			/*Write initial files*/
			try {
				if (indicator==1) {
					String fileName = "./data/ClickRateCompare12.arff";
					File f = new File(fileName);
					BufferedWriter output = new BufferedWriter(new FileWriter(f));
					output.write(HEADER1); //write a header file
					output.close();
				}
				else {
					String fileName = "./data/ClickRateCompare10.arff";
					File f = new File(fileName);
					BufferedWriter output = new BufferedWriter(new FileWriter(f));
					output.write(HEADER2); //write a header file
					output.close();
				}
			} catch (IOException ew) {
				logger.error("Problem Encountered When Creating The ARFF File!");
			}
			/*Get Max Score*/
			String[] scoreArray = readerScore.readLine().split("\t\\|\\|\\|\t");
			double[] scoreMax = new double[SCORENUM]; //get all the maximum scores
			for (int i=0; i<scoreMax.length; i++) {
				double sc = Double.parseDouble(scoreArray[i]);
				if (sc==0) {
					scoreMax[i] = 1; //if the maximum score is zero, which means all scores are zero; we cannot divide zero thus use 1 instead
				}
				else {
					scoreMax[i] = Double.parseDouble(scoreArray[i]);
				}
			}
			int counter = 0;
			while ((line = reader.readLine())!=null) {
				String[] w = line.split("\t\\|\\|\\|\t"); //word+count
				int num = Integer.parseInt(w[1]);
				/*Filter*/
				if (num<threshold) { //we do not consider the word with very few songs matched
					logger.warn("The Word "+w[0]+" Has Fewer-than-threshold Matched Songs!");
					for (int i=0; i<num; i++) {
						readerInside.readLine();
					}
					continue;
				}
				else {
					/*Set Class*/
					for (int k=0; k<num; k++) {
						String dataLine = readerInside.readLine(); //line for AllData
						ARFFInstance ins = createInstance(dataLine);
						allInstance.add(ins);
					}
					double v = factor*std(allInstance)/Math.sqrt(allInstance.size()/2)*ttest(allInstance.size()-1); //standard to separate data based on hypothesis test
					String outputGroup = "";
					for (int a=0; a<allInstance.size(); a++) {
						for (int b=a; b<allInstance.size(); b++) {
							ARFFInstance i = allInstance.get(a);
							ARFFInstance j = allInstance.get(b);
							String cls; //class label
							if (i.getClickRate()-j.getClickRate()>v) {
								cls = "good";
							}
							else if (i.getClickRate()-j.getClickRate()<-v) {
								cls = "bad";
							}
							else {
								continue;
							}
							/*Normalize the score*/
							if (indicator==1) {
								outputGroup += (i.getPopScore()-j.getPopScore())/scoreMax[0]+","+
							                   (i.getNameVectorScore()-j.getNameVectorScore())/scoreMax[1]+","+
									           (i.getSongAndArtistNameVectorScore()-j.getSongAndArtistNameVectorScore())/scoreMax[2]+","+
							                   (i.getNameKeywordVectorScore()-j.getNameKeywordVectorScore())/scoreMax[3]+","+
									           (i.getNameKeywordConstantScore()-j.getNameKeywordConstantScore())/scoreMax[4]+","+
							                   (i.getAlbumNameKeywordConstantScore()-j.getAlbumNameKeywordConstantScore())/scoreMax[5]+","+
									           (i.getAlbumNameVectorScore()-j.getAlbumNameVectorScore())/scoreMax[6]+","+
							                   (i.getAlbumAliasVectorScore()-j.getAlbumAliasVectorScore())/scoreMax[7]+","+
									           (i.getAliasVectorScore()-j.getAliasVectorScore())/scoreMax[8]+","+
							                   (i.getArtistNameVectorScore()-j.getArtistNameVectorScore())/scoreMax[9]+","+
									           (i.getPlay()-j.getPlay())/scoreMax[10]+","+
									           (i.getTotalClick()-j.getTotalClick())/scoreMax[11]+","+
									           cls+"\n";        
							}
							else {
								outputGroup += (i.getPopScore()-j.getPopScore())/scoreMax[0]+","+
							                   (i.getNameVectorScore()-j.getNameVectorScore())/scoreMax[1]+","+
									           (i.getSongAndArtistNameVectorScore()-j.getSongAndArtistNameVectorScore())/scoreMax[2]+","+
							                   (i.getNameKeywordVectorScore()-j.getNameKeywordVectorScore())/scoreMax[3]+","+
									           (i.getNameKeywordConstantScore()-j.getNameKeywordConstantScore())/scoreMax[4]+","+
							                   (i.getAlbumNameKeywordConstantScore()-j.getAlbumNameKeywordConstantScore())/scoreMax[5]+","+
									           (i.getAlbumNameVectorScore()-j.getAlbumNameVectorScore())/scoreMax[6]+","+
							                   (i.getAlbumAliasVectorScore()-j.getAlbumAliasVectorScore())/scoreMax[7]+","+
									           (i.getAliasVectorScore()-j.getAliasVectorScore())/scoreMax[8]+","+
							                   (i.getArtistNameVectorScore()-j.getArtistNameVectorScore())/scoreMax[9]+","+
									           cls+"\n";        
							}      
						}
					}
					/*Write ARFF files*/
					if (indicator==1) {
						try {
							String fileNameAdd = "./data/ClickRateCompare12.arff";
							BufferedWriter outputAdd = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNameAdd, true)));
							outputAdd.write(outputGroup);
							logger.info("File NDIR Has Been Updated For The Word "+w[0]);
							outputAdd.close();
						} catch (IOException e) {
							logger.error("Error Output!");
							e.printStackTrace();
						}
						allInstance.clear();
					}
					else {
						try {
							String fileNameAdd = "./data/ClickRateCompare10.arff";
							BufferedWriter outputAdd = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileNameAdd, true)));
							outputAdd.write(outputGroup);
							logger.info("File NDIR Has Been Updated For The Word "+w[0]);
							outputAdd.close();
						} catch (IOException e) {
							logger.error("Error Output!");
							e.printStackTrace();
						}
						allInstance.clear();
					}
				}
				counter++;
				if (counter%10000==0) {
					System.out.println("Generate ARFF With "+num+" Score At "+counter+"...");
				}
			}
			logger.info("Finish Generating ARFF!");
		} catch (IOException e) {
			logger.error("Problems When Reading!");
		} finally {
			try {
				readerScore.close();
				readerInside.close();
				reader.close();
			} catch (IOException e) {
				logger.error("Cannot Close The Reader!");
			}
		}
	}
	
	/**
	 * Create the instances in the ARFF file
	 * @param dataLine Each line containing all the score for the tuple (keyword, song)
	 * @return The instance containing all the information to generate ARFF file
	 */
	private ARFFInstance createInstance(String dataLine) {
		String[] d = dataLine.split("\t\\|\\|\\|\t");
		ARFFInstance ins = new ARFFInstance();
		ins.setPopScore(Double.parseDouble(d[5]));
		ins.setNameVectorScore(Double.parseDouble(d[6]));
		ins.setSongAndArtistNameVectorScore(Double.parseDouble(d[7]));
		ins.setNameKeywordVectorScore(Double.parseDouble(d[8]));
		ins.setNameKeywordConstantScore(Double.parseDouble(d[9]));
		ins.setAlbumNameKeywordConstantScore(Double.parseDouble(d[10]));
		ins.setAlbumNameVectorScore(Double.parseDouble(d[11]));
		ins.setAlbumAliasVectorScore(Double.parseDouble(d[12]));
		ins.setAliasVectorScore(Double.parseDouble(d[13]));
		ins.setArtistNameVectorScore(Double.parseDouble(d[14]));
		ins.setPlay(Double.parseDouble(d[15]));
		ins.setTotalClick(Double.parseDouble(d[16]));
		ins.setSearch(Integer.parseInt(d[17]));
		ins.setImpress(Integer.parseInt(d[18]));
		ins.setClickRate(Double.parseDouble(d[19]));
		return ins;
	}
	
	/**
	 * Get the 95% quantile for the t-test under different degree of freedom; the quantile is based on 2x
	 * @param x Degree of freedom (cannot exceed 20)
	 * @return The 95% quantile
	 */
	private double ttest(int x) {
		double[] array = {4.3026,2.7764,2.4469,2.3060,2.2282,2.1788,2.1448,2.1199,2.1009,2.0860,2.0739,2.0639,2.0555,2.0484,2.0423,2.0369,2.0322,2.0281,2.0244};
		return array[x-1];
	}
	
	/**
	 * Get the mean of click rate
	 * @param x An array of ARFFInstance
	 * @return Mean of click rate
	 */
	private double mean(List<ARFFInstance> x) {
		double sum = 0;
		for (ARFFInstance i : x) {
			sum += i.getClickRate();
		}
		return sum/x.size();
	}
	
	/**
	 * Get the standard deviation of click rate
	 * @param x An array of ARFFInstance
	 * @return Standard deviation of click rate
	 */
	private double std(List<ARFFInstance> x) {
		double m = mean(x);
		double sum = 0;
		for (ARFFInstance i : x) {
			sum += Math.pow(i.getClickRate()-m, 2);
		}
		int n = x.size();
		return Math.sqrt(sum/(n-1));
	}
	
}
