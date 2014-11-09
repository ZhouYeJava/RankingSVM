package start;
/**
 * Import all the parameters
 * @author Zhou Ye
 */


import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import tool.ParameterSet;

import exception.ParameterImportException;

public class ParamImporter {
	
	private Map<String, Object> parameter = new HashMap<String, Object>(); //all the parameters
	private static Logger logger = Logger.getLogger(ParamImporter.class.getName()); 
	
	/**
	 * Constructor
	 */
	public ParamImporter() {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	/**
	 * Main program which will execute all the steps to import parameters
	 * @param para The location of parameter file
	 */
	public void readParam(String para) {
		logger.info("Start Checking Parameters!");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(para)));
			String line = null;
			while((line = br.readLine())!=null) {
				if (line.startsWith("#")) {
					continue;
				}
				else {
					String[] temp = line.split("=");
					logger.info(line);
					checkParam(temp);
				}
			}
			logger.info("Finish Checking Parameters!");
		} catch (IOException e) {
			logger.error("Cannot Read Parameter File!");
		} catch (ParameterImportException e) {
			logger.error("Problems When Importing Parameters!");
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				logger.error("Cannot Close Parameter File!");
			}
		}
	}
	
	/**
	 * Check the correctness of parameters
	 * @param paraLine Each line of parameters
	 * @throws ParameterImportException Illegal parameters 
	 */
	private void checkParam(String[] paraLine) throws ParameterImportException {
		if (paraLine.length!=2) {
			logger.error("The Input Format Has Problems!");
			throw new ParameterImportException();
		}
		else {
			ParameterSet indicator = ParameterSet.valueOf(paraLine[0].trim().toUpperCase());
			String value = paraLine[1].trim();
			switch(indicator) {
			case IMPRESS: //impress
				try {
					int impress = Integer.parseInt(value);
					parameter.put(paraLine[0].trim(), impress);
					break;
				} 
				catch (NumberFormatException e) {
					logger.error("Error When Reading Impress!");
					throw new ParameterImportException();
				} 
			case SEARCH: //search
				try {
					int search = Integer.parseInt(value);
					parameter.put(paraLine[0].trim(), search);
					break;
				} 
				catch (NumberFormatException e) {
					logger.error("Error When Reading Search!");
					throw new ParameterImportException();
				} 
			case CATEGORY: //category
				if (value.equals("song") || value.equals("artist") || value.equals("album") || value.equals("suggest")) {
					parameter.put(paraLine[0].trim(), value);
					break;
				}
				else {
					logger.error("Error When Reading Category!");
					throw new ParameterImportException();
				}
			case EXPAND: //expand
				try {
					int expand = Integer.parseInt(value);
					parameter.put(paraLine[0].trim(), expand);
					break;
				} 
				catch (NumberFormatException e) {
					logger.error("Error When Reading Expand!");
					throw new ParameterImportException();
				} 
			case LENCORD: //lenCord
				if (value.equals("true") || value.equals("false")) {
					parameter.put(paraLine[0].trim(), Boolean.parseBoolean(value));
					break;
				}
				else {
					logger.error("Error When Reading LenCord!");
					throw new ParameterImportException();
				}
			case QUERYNORM: //queryNorm
				if (value.equals("true") || value.equals("false")) {
					parameter.put(paraLine[0].trim(), Boolean.parseBoolean(value));
					break;
				}
				else {
					logger.error("Error When Reading QueryNorm!");
					throw new ParameterImportException();
				}
			case USEQSTRUCTURE: //useQstructure
				if (value.equals("true") || value.equals("false")) {
					parameter.put(paraLine[0].trim(), Boolean.parseBoolean(value));
					break;
				}
				else {
					logger.error("Error When Reading UseQStructure!");
					throw new ParameterImportException();
				}
			case THRESHOLD: //threshold
				try {
					int threshold = Integer.parseInt(value);
					parameter.put(paraLine[0].trim(), threshold);
					break;
				} 
				catch (NumberFormatException e) {
					logger.error("Error When Reading Threshold!");
					throw new ParameterImportException();
				} 
			case FACTOR: //factor
				try {
					int factor = Integer.parseInt(value);
					parameter.put(paraLine[0].trim(), factor);
					break;
				} 
				catch (NumberFormatException e) {
					logger.error("Error When Reading Factor!");
					throw new ParameterImportException();
				}
			default: //no match
				logger.error("The Parameter Is Not In Parameter Set!");
				throw new ParameterImportException();
			}
		}
	}
	
	/**
	 * Obtain the parameters
	 * @return all parameters
	 */
	public Map<String, Object> getParameter() {
		return parameter;
	}
	
}
