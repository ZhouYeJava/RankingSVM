package optimize;
/**
 * Do the logistic regression and calculate the final coefficients
 * @author Zhou Ye
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

public class Learning {
	
	private int num; //10 or 12
	private int y; //year
	private int m; //month
	private int d; //day
	private static Logger logger = Logger.getLogger(Learning.class.getName());
	
	/**
	 * Constructor
	 * @param num 10 or 12
	 */
	public Learning(int num) {
		this.num = num;
		Calendar cal = Calendar.getInstance();    
		y = cal.get(Calendar.YEAR);    
		m = cal.get(Calendar.MONTH)+1;    
		d = cal.get(Calendar.DATE); 
	}
	
	/**
	 * Calculate the coefficients
	 * @param online Online Weight file
	 */
	public void coefficient(String online) {
		logger.info("Start Learning With "+num+" coefficients!");
		if (num==10) {
			boolean result = coefLR10("./data/Coefficients"+num+"_"+y+"_"+m+"_"+d+".txt", "./data/AllMaxScore.score", online);
			if (result==true) {
				logger.info("Everything is good!");
			}
			else {
				logger.info("Bad happens!");
			}
		}
		else {
			boolean result = coefLR12("./data/Coefficients"+num+"_"+y+"_"+m+"_"+d+".txt", "./data/AllMaxScore.score", online);
			if (result==true) {
				logger.info("Everything is good!");
			}
			else {
				logger.info("Bad happens!");
			}
		}
		logger.info("Finish Learning With "+num+" coefficients!");
	}

	/**
	 * Generate the logistic regression model
	 * @param num Number of scores
	 */
	public void model() {
		String path = "./data/ClickRateCompare"+num+".arff";
        Instances data = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
            data = new Instances(reader);
            int n = data.numInstances();
            if (n==0) {
            	logger.error("No Instance! No Need For Learning!");
            	System.out.println("No Instance In ARFF File!");
            	System.exit(-1);
            }
            else {
                data.setClassIndex(data.numAttributes()-1);
            }
            reader.close();
		} catch (IOException e1) {
			logger.error("Cannot Read ARFF File!");
		}
		try {
			Logistic log = new Logistic();
			log.buildClassifier(data);
			double[][] c = log.coefficients();
			String coef = "";
			for (int i=0; i<c.length; i++) {
				for (int j=0; j<c[0].length; j++) {
					coef += c[i][j]+"\n";
				}
			}
			try { 
		        String fileName = "./data/Coefficients"+num+"_"+y+"_"+m+"_"+d+".txt";
		        File f = new File(fileName);
		        BufferedWriter output = new BufferedWriter(new FileWriter(f));
				output.write(coef);
				output.close();
			} catch (IOException e) {
				logger.error("Error Output!");
			}
			Evaluation eval = new Evaluation(data);
			eval.crossValidateModel(log, data, 10, new Random(1));
			String result = "LOGISTIC:\n"+log+"\n"+eval.toSummaryString()+"\n"+eval.toClassDetailsString()+"\n"+eval.toMatrixString()+"\n";
			try {
		        String fileName = "./output/LogisticRegression"+num+"_"+y+"_"+m+"_"+d+".txt";
		        File f = new File(fileName);
		        BufferedWriter output = new BufferedWriter(new FileWriter(f));
				output.write(result);
				output.close();
			} catch (IOException e) {
				logger.error("Error Output!");
			}
		} catch (Exception e) {
			logger.error("Classification Problem!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Obtain the coefficient of logistic regression
	 * @param coeff Coefficients file
	 * @param max MaxScore file
	 * @param online OnlineWeight file
	 * @return True for success and false for failure
	 */
	private boolean coefLR10(String coeff, String max, String online) {
		try {
			boolean flag = false;
			BufferedReader br1 = new BufferedReader(new FileReader(new File(coeff)));
			br1.readLine(); //the intercept is useless
			BufferedReader br2 = new BufferedReader(new FileReader(new File(max)));
			BufferedReader br3 = new BufferedReader(new FileReader(new File(online)));
			String line = null;
			ArrayList<Double> coefficient = new ArrayList<Double>();
			ArrayList<Double> score = new ArrayList<Double>();
			ArrayList<Double> weight = new ArrayList<Double>();
			while ((line=br1.readLine())!=null) {
				coefficient.add(Double.parseDouble(line));
			}
			if (coefficient.size()!=num) {
				logger.error("The number of coefficients has problems!");
				logger.error("You need to adjust coefficents manually!");
				System.out.println("Zero Coefficient Problem!");
				br1.close();br2.close();br3.close();
				return false;
			}
			else {
				String[] maxScore = br2.readLine().split("\t\\|\\|\\|\t");
				for (int i=0; i<num; i++) {
					score.add(Double.parseDouble(maxScore[i]));
				}
				String[] wt = br3.readLine().split(",");
				for (int j=0; j<num; j++) {
					weight.add(Double.parseDouble(wt[j]));
				}
				String[] name = {"PopScore", "Name:VectorScore", "SongAndArtistName:VectorScore", "NameKeyword:VectorScore", 
						"NameKeyword:ConstantScore", "AlbumNameKeyword:ConstantScore", "AlbumName:VectorScore", "AlbumAlias:VectorScore", 
						"Alias:VectorScore", "ArtistName:VectorScore"};
				if (name.length==num) {
					HashMap<String, Double> finalCoefficient = new HashMap<String, Double>();
					for (int k=0; k<name.length; k++) {
						finalCoefficient.put(name[k], coefficient.get(k)/score.get(k)*weight.get(k));
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./output/FinalCoefficient"+num+"_"+y+"_"+m+"_"+d+".txt")));
					for (String eachKey : finalCoefficient.keySet()) {
						bw.write(eachKey+":"+String.format("%.10f", finalCoefficient.get(eachKey))+"\n");
					}
					bw.close();
					flag = true;
				}
			}
			br1.close();
			br2.close();
			br3.close();
			return flag;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Obtain the coefficient of logistic regression
	 * @param coeff Coefficients file
	 * @param max MaxScore file
	 * @param online OnlineWeight file
	 * @return True for success and false for failure
	 */
	private boolean coefLR12(String coeff, String max, String online) {
		try {
			boolean flag = false;
			BufferedReader br1 = new BufferedReader(new FileReader(new File(coeff)));
			br1.readLine(); //the intercept is useless
			BufferedReader br2 = new BufferedReader(new FileReader(new File(max)));
			BufferedReader br3 = new BufferedReader(new FileReader(new File(online)));
			String line = null;
			ArrayList<Double> coefficient = new ArrayList<Double>();
			ArrayList<Double> score = new ArrayList<Double>();
			ArrayList<Double> weight = new ArrayList<Double>();
			while ((line=br1.readLine())!=null) {
				coefficient.add(Double.parseDouble(line));
			}
			if (coefficient.size()!=num) {
				logger.error("The number of coefficients has problems!");
				logger.error("You need to adjust coefficents manually!");
				System.out.println("Zero Coefficient Problem!");
				br1.close();br2.close();br3.close();
				return false;
			}
			else {
				String[] maxScore = br2.readLine().split("\t\\|\\|\\|\t");
				for (int i=0; i<num; i++) {
					score.add(Double.parseDouble(maxScore[i]));
				}
				String[] wt = br3.readLine().split(",");
				for (int j=0; j<num-2; j++) {
					weight.add(Double.parseDouble(wt[j]));
				}
				String[] name = {"PopScore", "Name:VectorScore", "SongAndArtistName:VectorScore", "NameKeyword:VectorScore", 
						"NameKeyword:ConstantScore", "AlbumNameKeyword:ConstantScore", "AlbumName:VectorScore", "AlbumAlias:VectorScore", 
						"Alias:VectorScore", "ArtistName:VectorScore", "SeasonalPlayTimes", "TotalClickNumber"};
				if (name.length==num) {
					HashMap<String, Double> finalCoefficient = new HashMap<String, Double>();
					for (int k=0; k<name.length; k++) {
						if (k<name.length-2) {
							finalCoefficient.put(name[k], coefficient.get(k)/score.get(k)*weight.get(k));
						}
						else {
							finalCoefficient.put(name[k], coefficient.get(k)/score.get(k));
						}
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File("./output/FinalCoefficient"+num+"_"+y+"_"+m+"_"+d+".txt")));
					for (String eachKey : finalCoefficient.keySet()) {
						bw.write(eachKey+":"+String.format("%.10f", finalCoefficient.get(eachKey))+"\n");
					}
					bw.close();
					flag = true;
				}
			}
			br1.close();
			br2.close();
			br3.close();
			return flag;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
