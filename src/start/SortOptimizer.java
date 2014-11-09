package start;
/**
 * The entrance of the program
 * @author Zhou Ye
 */

import java.util.Map;

import optimize.CreateDataSet;
import optimize.GenerateARFF;
import optimize.Learning;
import optimize.SeparateExtract;

import tool.Sys;

public class SortOptimizer {

	public static void main(String[] args) {
		SortOptimizer sortOpt = new SortOptimizer();
		/*clear everything and get start*/
		sortOpt.cleanDirectory(); 
		/*read parameters*/
		Map<String, Object> allPara = sortOpt.readParameter(args[0]); //args[0]
		System.out.println("Parameters has been successfully imported!");
		/*separate from extract file*/
		sortOpt.separate(Integer.parseInt(allPara.get("impress").toString()), Integer.parseInt(allPara.get("search").toString()), allPara.get("category").toString(), args[1]); //args[1]
		System.out.println("Raw File Has Been Extracted!");
		/*Create the temporary data set*/
		sortOpt.createDataSet(Integer.parseInt(allPara.get("expand").toString()), Boolean.parseBoolean(allPara.get("lenCord").toString()), Boolean.parseBoolean(allPara.get("queryNorm").toString()),
				              Boolean.parseBoolean(allPara.get("useQstructure").toString()), "./data/AfterSeparation."+allPara.get("category").toString(), args[2]); //args[2]
		System.out.println("Data Set Has Been Created!");
		/*Generate ARFF*/
		sortOpt.generateARFF(Integer.parseInt(allPara.get("factor").toString()), Integer.parseInt(allPara.get("threshold").toString()), 
				             "./data/AllWord.word", "./data/AllData.data", "./data/AllMaxScore.score");
		System.out.println("ARFF File Has Been Generated!");
		/*Learning By Logistic Regression*/
		sortOpt.learn(args[3]); //args[3]
		System.out.println("END!");
	}
	
	/**
	 * Clear the files in directories data/output/log
	 */
	private void cleanDirectory() {
		Sys.deleteDirectory("./data/");
		Sys.deleteDirectory("./output/");
		Sys.deleteDirectory("./log/");
	}
	
	/**
	 * Obtain the parameters
	 * @param path The location of the parameter file
	 * @return The map containing all the parameters and value
	 */
	private Map<String, Object> readParameter(String path) {
		ParamImporter para = new ParamImporter();
		para.readParam(path);
		return para.getParameter();
	}
	
	/**
	 * Separate the necessary part from the extract file
	 * @param impressStandard The smallest number of impress
	 * @param searchStandard The smallest number of search
	 * @param category Song/Artist/Album/suggest
	 * @param pathExtract The location of the extract file
	 */
	private void separate(int impressStandard, int searchStandard, String category, String pathExtract) {
		SeparateExtract se = new SeparateExtract(impressStandard, searchStandard, category);
		se.separate(pathExtract);
	}
	
	/**
	 * Generate the data set we use to generate ARFF file and this data set can be used to do something else
	 * @param expand Number of expansion of sub-query
	 * @param lenCord Not clear
	 * @param queryNorm It is true, the model is not linear; so generally we set it false
	 * @param useQstructure Not clear but set it true as always
	 * @param path The file containing query and click rate
	 * @param clickPath The file containing all the click rate
	 */
	private void createDataSet(int expand, boolean lenCord, boolean queryNorm, boolean useQstructure, String path, String clickPath) {
		CreateDataSet cds = new CreateDataSet(expand, lenCord, queryNorm, useQstructure);
		cds.create(path, clickPath);
	}
	
	/**
	 * Generate the ARFF file which can be analyzed by WEKA
	 * @param factor Enlarging factor
	 * @param threshold Minimum number of the result of a query
	 * @param allWord Word file
	 * @param allData Data file
	 * @param allScoreMax MaxScore file
	 */
	private void generateARFF(int factor, int threshold, String allWord, String allData, String allScoreMax) {
		GenerateARFF ga10 = new GenerateARFF(1, factor, threshold);
		ga10.generateARFF(allWord, allData, allScoreMax);
		GenerateARFF ga12 = new GenerateARFF(2, factor, threshold);
		ga12.generateARFF(allWord, allData, allScoreMax);
	}
	
	/**
	 * Generate the weights for online use by WEKA
	 * @param online Online weight file
	 */
	private void learn(String online) {
		Learning l10 = new Learning(10);
		l10.model();
		l10.coefficient(online);
		Learning l12 = new Learning(12);
		l12.model();
		l12.coefficient(online);
	}
	
}
