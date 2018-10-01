package codeRelease;

import java.io.BufferedReader;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.StringUtils;

/**
 * This class implements functions that read from papers.csv and cityListJoin.csv, produces derivative data tables 
 * including cityListMerged.csv, and finally outputs the analysis data table SelectCities[Edges].csv. 
 * 
 * @author Xiaoran Yan( everyan@cs.unm.edu )
 * @version BP 0.1
 * @time Sep 22, 2010
 */

public class PrimaryCity {
	class City { //inner class city
		String rowID;
		ArrayList<String> mergedID; //list of matching strings of the city/metro in the dataset
		String standardName; //standard name of the city/metro area
		ArrayList<String> matchName; //list of matching strings of the city/metro in the dataset
		String country; //country name
		String metroArea; //metro Name
		String fullAddress; //example full address from the dataset
		double[] paperCount; //cumulative paper count for each city/metro
		ArrayList<Double> countList; //list of paperCount for metro merging
		ArrayList<ArrayList<String>> nameList; //list of matchNames for metro merging
		double latitude;
		double longitude;
		String airPort; //airportID map
		boolean updateTag; //tag for tracking metro merging
		//constructors for the inner class City
		City (String name, double[] coord, double count){ //basic constructor
			standardName = name;
			latitude = coord[0];
			longitude = coord[1];
			paperCount = new double[6];
			paperCount[0] = count;
		}

		City (int id, String address, String standardN, String countryName, double[] coord, String metro){ //full constructor
			rowID = String.valueOf(id);
			mergedID = new ArrayList<String>();
			mergedID.add(rowID);
			standardName = standardN;
			matchName = new ArrayList<String>();
			matchName.add(standardN);
			paperCount = new double[6];
			for (int i=0; i<paperCount.length; i++)
				paperCount[i] = 0;
			countList = new ArrayList<Double>();
			nameList = new ArrayList<ArrayList<String>>();
			country = countryName;
			metroArea = metro; //if there is a metro tag
			fullAddress = address;
			latitude = coord[0];
			longitude = coord[1];
			updateTag = false;
		}
		City (City copy){ //copy constructor
			rowID = copy.rowID;
			mergedID = copy.mergedID;
			standardName = copy.standardName;
			matchName = copy.matchName;
			paperCount[0] = copy.paperCount[0];
			countList = copy.countList;
			nameList = copy.nameList;
			country = copy.country;
			metroArea = copy.metroArea; //if there is a metro tag
			fullAddress = copy.fullAddress;
			latitude = copy.latitude;
			longitude = copy.longitude;
			airPort = copy.airPort;
			updateTag = false;
		}
	}
	
	ArrayList<City> cityList = new ArrayList<City>(); //list of City objects from the dataset
	ArrayList<City> mergeList = new ArrayList<City>(); //list of Metro objects merged from the city list

	/**
	 * This constructor builds the geo mapping from cityListState.csv
	 */	
	public PrimaryCity(String dir) {
		String line="";
		String word="";
		String word2 = "";
		String word3 = "";
		String address = "";
		double[] coordinates = new double[2];
		//Build the standard city properties from GIS lists
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "cityListJoin.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		int count = 0;
		int rowCount = 0;
		BufferedReader cityBuffer =  new BufferedReader(cityReader);
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				rowCount++;
				String metro = "NA"; //for metro merge
			    Matcher m = p.matcher(line);
			    m.find();m.find();//row ID
			    m.find();m.find();//Paper ID
			    m.find();m.find();//Pub year
			    m.find();//full address
			    address = m.group();
			    m.find();
			    m.find();//WoS city
			    word = m.group().replaceAll("\"", "");
			    m.find();
			    m.find();//WoS state
			    word2 = m.group().replaceAll("\"", "");
			    if (! word2.equals(",")) {
			    	word += word2;
			    	m.find();
			    }
			    m.find();//WoS Country
			    word2 = m.group().replaceAll("\"", "");			    
			    word3 = word+word2; m.find();//city+state+country concatenated
			    m.find();//Latitude
			    if (!m.group().equals(",")) {
				    coordinates[0] = Double.parseDouble(m.group());
				    m.find();
			    }
			    else //if no geocoding
			        coordinates[0] = 500;
			    m.find();//Longitude
			    if (!m.group().equals(",")) {
				    coordinates[1] = Double.parseDouble(m.group());
				    m.find();
			    }
			    else  //if no geocoding
			        coordinates[1] = 500;
			    m.find();//Metro name
			    if (!m.group().equals(",")) {
				    metro = m.group();
				    m.find();
			    }
			    m.find();//Standard name
			    boolean exist = false;
			    /*
			    for (int i=0; i<cityList.size(); i++) {
			    	if (cityList.get(i).fullAddress.equals(address)) {
			    		exist = true;
			    		cityList.get(i).matchName.add(m.group().replaceAll("\\s",""));
			    		cityList.get(i).matchName.add(word3.replaceAll("\\s",""));
			    		if (word2.equals("United States")) {
			    			cityList.get(i).matchName.add((word+"USA").replaceAll("\\s","")); //Alias for USA
		    			}
		    			if (word2.equals("China")) {
		    				cityList.get(i).matchName.add((word+"Peoples R China").replaceAll("\\s","")); //Alias for China
		    			}
			    	}
			    }*/
			    if (!exist) {
				    City newCity = new City(rowCount, address, m.group(), word2, coordinates, metro);
				    newCity.matchName.add(m.group().replaceAll("\\s",""));
				    newCity.matchName.add(word3.replaceAll("\\s",""));
				    if (word2.equals("USA")) {
				    	word2 = "United States"; //Alias for USA
	    			}
				    if (word2.equals("United States")) {
				    	newCity.matchName.add((word+"USA").replaceAll("\\s","")); //Alias for USA
	    			}
	    			if (word2.equals("Peoples R China")) {
	    				word2 = "China"; //Alias for China
	    			}
	    			if (word2.equals("China")) {
	    				newCity.matchName.add((word+"Peoples R China").replaceAll("\\s","")); //Alias for China
	    			}
				    cityList.add(newCity);
				    count++;
			    }
			}
			System.out.println(rowCount +" cities read.");
			System.out.println(count +" cities added.");
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * This constructor builds the geo mapping from cityListMerge2.csv
	 */	
	public PrimaryCity(String dir, boolean merge) {
		String line="";
		String word="";
		String word2 = "";
		String word3 = "";
		String address = "";
		double[] coordinates = new double[2];
		//Build the standard city properties from GIS lists
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "cityListMerged.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		int count = 0;
		int rowCount = 0;
		BufferedReader cityBuffer =  new BufferedReader(cityReader);
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				rowCount++;
			    Matcher m = p.matcher(line);
			    m.find();//full address
			    address = m.group();
			    m.find();
			    m.find();//Latitude
			    if (!m.group().equals(",")) {
				    coordinates[0] = Double.parseDouble(m.group());
				    m.find();
			    }
			    else //if no geocoding
			        coordinates[0] = 500;
			    m.find();//Longitude
			    if (!m.group().equals(",")) {
				    coordinates[1] = Double.parseDouble(m.group());
				    m.find();
			    }
			    else  //if no geocoding
			        coordinates[1] = 500;
			    m.find();//standard name
			    word = m.group().replace("\"", "");
			    m.find();
			    m.find();//country
			    word2 = m.group().replace("\"", "");
			    if (word2.equals("USA"))
			    	word2 = "United States"; //Alias for USA
			    if (word2.equals("Peoples R China")) 
			    	word2 = "China"; //Alias for China
			    if (word2.equals("England")||word2.equals("Scotland")||word2.equals("Wales")||word2.equals("North Ireland")) 
			    	word2 = "United Kingdom"; //Alias for United Kingdom
			    m.find();
			    m.find();m.find();//number of affiliations
			    m.find();m.find();//merged city IDs
			    m.find();//merged city List
			    word3 = m.group();
			    City newCity = new City(rowCount, address, word, word2, coordinates, word);
			    String matchStrings[] = word3.split("\\|"); //all cities for each author
			    for (int i=0; i<matchStrings.length; i++)
			    	newCity.matchName.add(matchStrings[i].replace("\"", ""));
			    cityList.add(newCity);
			    count++;
			}
			System.out.println(rowCount +" cities read.");
			System.out.println(count +" cities added.");
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		//read legacy node id for metros/cities
		try {
			cityReader = new FileReader(dir + "legacyNodeTable.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		cityBuffer =  new BufferedReader(cityReader);
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
			    Matcher m = p.matcher(line);
			    m.find();//id
			    word = m.group().replace("\"", "");
			    m.find();
			    m.find();//standard name
			    word2 = m.group().replace("\"", "");
			    m.find();
			    m.find();m.find();//latitude
			    m.find();m.find();//longitude
			    m.find();m.find();//country
			    m.find();//AirportID
			    
			    boolean matched = false;
			    int firstMatch = 0;
			    for (int i=0; i<cityList.size(); i++) {
			    	if (cityList.get(i).standardName.replaceAll("\\s","").equalsIgnoreCase(word2.replaceAll("\\s",""))) {
			    		if (matched == false) {
				    		matched = true;
				    		firstMatch = i;
				    		if (!cityList.get(i).rowID.contains("n")) {
				    			cityList.get(i).rowID = word; //read the legacy node id
				    			cityList.get(i).airPort = m.group().replace("\"", ""); // read the legacy airport id
				    		}
				    		else {
				    			System.out.println("mark multi-matched cityList.");
				    			cityList.get(i).mergedID.add(word); //merge indicator flags
				    		}
			    		}
			    		else {//remove duplicate 
			    			cityList.get(firstMatch).matchName.addAll(cityList.get(i).matchName);
			    			cityList.get(firstMatch).mergedID.addAll(cityList.get(i).mergedID);
			    			cityList.get(i).mergedID.clear(); 
			    			cityList.get(i).mergedID.add(word +"removed"); //remove indicator flags 
					    	System.out.println("mark duplicate.");
			    		}
			    	}
			    }
			    if (!matched)
			    	System.out.println("unmatched cityList.");
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**Function for calculating edit distance for string matching
	 * 
	 * @return
	 */
	public static double similarity(String s1, String s2) {
	    String longer = StringUtils.lowerCase(s1), shorter = StringUtils.lowerCase(s2);
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = StringUtils.lowerCase(s2); shorter = StringUtils.lowerCase(s1);
	    }
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    //If you have StringUtils, you can use it to calculate the edit distance:
	    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longerLength;

	  }
	
    /**
     * This function converts decimal degrees to radians	
     * @param deg double
     * @return double
     */
    private static double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
    }

    /**
     * This function converts radians to decimal degrees
     * @param rad double
     * @return double
     */
    private static double rad2deg(double rad) {
            return (rad * 180 / Math.PI);
    }
	
    /**
     * This function merges cities into metro areas with the 
     * @param rad double
     * @return double
     */
	public void cityMerge(String dir) { 
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "cityListMerged.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);
		print.println("full_address, Latitude, longitude, string, country, affliations, rowIDlist, matchList, mergeList, mergeCount");
		
		int cityMerged = 0;
		int coCount = 0;
		for (int i=0; i<cityList.size(); i++) {
			City c = cityList.get(i);
			if (c.metroArea.equalsIgnoreCase("NA")) {
				//print.println(c.fullAddress+","+c.latitude+","+c.longitude+","+c.standardName+","+c.country+","+c.affiliationCount
					//			+","+c.rowID+","+matchList+ ",noMerge"+ ",noMerge");
				c.countList.add(c.paperCount[0]);
				c.nameList.add(c.matchName);
				mergeList.add(c);
			}
			else {
				boolean exists = false;
				for (int j=0; j<mergeList.size(); j++) {
					if (c.metroArea.equalsIgnoreCase(mergeList.get(j).metroArea)) {
						exists = true;
						cityMerged++;
						mergeList.get(j).rowID += "|" + c.rowID;
						mergeList.get(j).matchName.addAll(c.matchName); //combining city name matches
						mergeList.get(j).nameList.add(c.matchName);
						mergeList.get(j).countList.add(c.paperCount[0]);
						if (c.paperCount[0]>mergeList.get(j).paperCount[0]) {
							mergeList.get(j).standardName = c.standardName;
							mergeList.get(j).country = c.country;
							mergeList.get(j).fullAddress = c.fullAddress;
							mergeList.get(j).latitude = c.latitude;
							mergeList.get(j).longitude = c.longitude;
							mergeList.get(j).paperCount[0] = c.paperCount[0];
						}
					}
				}
				if (!exists) {
					//c.matchName.clear(); for printing only
					//c.matchName.add(c.standardName);
					c.countList.add(c.paperCount[0]);
					c.nameList.add(c.matchName);
					mergeList.add(c);
				}
			}
		}
		/*
		//update to merge closeby cities <10km
		ArrayList<ArrayList<Integer>> mergeMetros = new ArrayList<ArrayList<Integer>>();
		for (int i=0; i<mergeList.size(); i++){
			City c1 = mergeList.get(i);
			for (int j=i+1; j<mergeList.size(); j++){
				City c2 = mergeList.get(j);
                double long1 = c1.longitude;
                double long2 = c2.longitude;
                double lati1 = c1.latitude;
                double lati2 = c2.latitude;

                double theta = long1 - long2;
                double dist = Math.sin(deg2rad(lati1)) * Math.sin(deg2rad(lati2)) 
                        + Math.cos(deg2rad(lati1)) * Math.cos(deg2rad(lati2)) * Math.cos(deg2rad(theta));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;
                if (dist < 10){ //if within 10 km
					for (int k=0; k<mergeMetros.size(); k++){
						if ()
					}
                }
			}
		}
		*/
		for (int i=0; i<mergeList.size(); i++) {
			City c = mergeList.get(i);
			print.print(c.fullAddress+","+c.latitude+","+c.longitude+","+c.standardName+","+c.country+",");
			int count = 0;
			String mergeCount = "";
			String mergeCities = "";
			for (int j=0; j<c.nameList.size(); j++) {
				count += c.countList.get(j);
				mergeCount += c.countList.get(j) + "|";
				for (int k=0; k<c.nameList.get(j).size(); k++)
					mergeCities += c.nameList.get(j).get(k) + ";";
				mergeCities += "|";
			}
			if (mergeCount.equals("")) {
				mergeCount = "NULL";
				mergeCities = "NULL";
			}
			String matchList = c.matchName.get(0); //for delimiter positioning
			for (int j=1; j<c.matchName.size(); j++) //for city name matching
				matchList += "|\"" + c.matchName.get(j)+"\""; 
			print.println(count+","+c.rowID+","+matchList+","+mergeCities+"\","+mergeCount+"\"");
		}
		System.out.println(cityMerged + " cities merged");
		System.out.println(coCount + "papers with coauthors");
	}

    /**
     * This function generates the node table while calculating the MASS column from "locations.csv"
     * @param dir String
     * @return void
     */
	public void generateNodeTable (String dir){
		String line="";
		String word="";
		String word2 = "";
		double[] coordinates = new double[2];
		ArrayList<City> massList = new ArrayList<City>(); //list of City objects from the dataset

		int pruneCount = 0;
		//Build the standard city properties from GIS lists
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "locations.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "massMatch0.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);

		int cityUnmatch = 0;
		int cityMerged = 0;
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		BufferedReader cityBuffer =  new BufferedReader(cityReader);
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				Matcher m = p.matcher(line);
			    m.find();m.find();//node ID
			    m.find();m.find();//city label
			    m.find();//city Latitude
			    if (!m.group().equals(",")) {
				    coordinates[0] = Double.parseDouble(m.group());
				    m.find();
			    }
			    else //if no geocoding
			        coordinates[0] = 500;
			    m.find();//city longitude
			    if (!m.group().equals(",")) {
				    coordinates[1] = Double.parseDouble(m.group());
				    m.find();
			    }
			    else //if no geocoding
			        coordinates[1] = 500;
			    m.find();//country(empty in locations.csv)
			    m.find();//MASS (paper count)
			    word = m.group().replace("\"", "");
			    m.find();
			    m.find();//nameList
			    word2 = m.group().replace("\"", "");
				City newCity = new City(word2, coordinates, Double.parseDouble(word)); // create the dummy city
				massList.add(newCity);
			}

			print.println("Id,Label,Lat,Lng,Country,AirportID,Mass,MergeList");
			for (int i=0; i<cityList.size(); i++) {
				City c = cityList.get(i);
				c.paperCount[0] = 0;
				if (c.rowID.contains("n")) {
					print.print(c.rowID+","+c.standardName+","+c.latitude+","+c.longitude+","+c.country+","+c.airPort+",");
					for (int j=0; j<massList.size(); j++){
	                        double long1 = c.longitude;
	                        double long2 = massList.get(j).longitude;
	                        double lati1 = c.latitude;
	                        double lati2 = massList.get(j).latitude;
	
	                        double theta = long1 - long2;
	                        double dist = Math.sin(deg2rad(lati1)) * Math.sin(deg2rad(lati2)) 
	                                + Math.cos(deg2rad(lati1)) * Math.cos(deg2rad(lati2)) * Math.cos(deg2rad(theta));
	                        dist = Math.acos(dist);
	                        dist = rad2deg(dist);
	                        dist = dist * 1.1515 * 60; //times 1.609344 for km
	                        if (dist > 20000) //check if the distance is over half quator length
	                            dist = 20000;
	                        if (dist < 10){ //if within 10 miles, merge into the metro
	                        	c.paperCount[0] += massList.get(j).paperCount[0];
	                        	c.matchName.add("MASS_\"" +massList.get(j).standardName);
	                        	//cityMerged ++;
	                        }
	                }
					if(c.paperCount[0] == 0) {
						cityUnmatch++;
						c.paperCount[0]++; //avoid 0 for mathematical degeneracy
					}
					String matchList = c.matchName.get(0); //for delimiter positioning
					for (int j=1; j<c.matchName.size(); j++) //for city name matching
						matchList += "|\"" + c.matchName.get(j); 
					print.println(c.paperCount[0]+","+matchList);
				}
				else
					cityMerged ++;
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("City counted.");
		System.out.println(cityUnmatch + " cities unmatched");
		System.out.println(cityMerged + " cities merged");
	}
	

    /**
     * This function adds to the edge table with the GeoDist,Mass,CollabPaper,CoaffiliatePaper columns based on data tables "paper.csv".
     * @param rad double
     * @return double
     */
	
	public void generateEdgeTable (String dir){
	    double[] lng = new double[4];
	    double[] lat = new double[4];
	    String[] name = new String[4];
	    String[] idn = new String[4];
	    
	    name[0] = "Ann Arbor MI United States";
		idn[0] = "n0";
		lat[0] = 42.2821006775;
		lng[0] = -83.7484664917;
		name[1] = "Tempe AZ United States";
		idn[1] = "n2";
		lat[1] = 33.4255104065;
		lng[1] = -111.937423706;
		name[2] = "Bloomington IN United States";
		idn[2] = "n4";
		lat[2] = 39.766910553;
		lng[2] = -86.5342407227;
		name[3] = "Indianapolis IN United States";
		idn[3] = "n8";
		lat[3] = 39.766910553;
		lng[3] = -86.1499633789;
	    
		String line="";
		String word="";
		String word2 = "";
		int pruneCount = 0;
		//Build the standard city properties from GIS lists
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "papers.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
				
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "SelectCities[Edges].csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);
		print.println("Source,Target,Id,duplicate,UMich,ASU,IUB,IUPUI,Dist2Air,MASS,Seats0Stop,Seats1Stop,Seats2Stop,Seats3Stop,Lines0Stop,Lines1Stop,Lines2Stop,Lines3Stop," +
				"GeoDist,CollabPaper");
		
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		int paperUnmatch = 0;
		int cityUnmatch = 0;
		int nodeCount=0;
		int coCount = 0;
		BufferedReader cityBuffer =  new BufferedReader(cityReader);
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				Matcher m = p.matcher(line);
			    m.find();//Paper ID
			    word2 = m.group().replace("\"", "");
			    m.find();
			    m.find();//publication year
			    if (Integer.parseInt(m.group())>2007) {
				    m.find();
				    m.find();m.find();//title
				    m.find();m.find();//journal
				    m.find();
				    if (m.group().replace("\"", "").equals("Article")) {//document type
				    	m.find();
				    	m.find();//citations
					    if (!m.group().equals(","))
						    m.find();				
					    m.find();//List of authors
					    int authorCount = m.group().replace("\"", "").split("\\|").length;
					    m.find();
					    m.find();//cityMatcher
					    word = m.group();
					    m.find();
					    m.find();m.find();//author mapped
					    m.find();//cityMatcher
					    word2 = m.group(); //full address list
			    		String[] temp = word.replace("\"", "").split("\\|"); //List of cities for all author
			    		
				    	if (authorCount>0 /*&& temp.length==authorCount && line.toLowerCase().contains(school)*/) { //only include papers with multiple authors
				    		coCount++;
					    	String[] authorLocals = new String[30];
	
					    	if (temp.length<=authorLocals.length) {//prune the long author list to 30
					    		authorLocals = temp;
					    	}
					    	else {
					    		pruneCount++;
					    		int half = authorLocals.length/2;
					    		for (int i=0; i<half; i++) {//include first 15
					    			authorLocals[i] = temp[i];
					    		}
					    		for (int i=0; i<half; i++) {//include last 15
					    			authorLocals[i+half] = temp[temp.length-i-1];	
					    		}
					    	}
					    	String lastLocals = "";
					    	int error = 0; //checking if the affiliations are identical
						    for (int h=0; h<temp.length; h++) {
						    	if (lastLocals.equals(temp[h]))
						    		error++;
					    		lastLocals = temp[h];
					    	}
						    if (lastLocals.replace("\"", "").split("\\;").length < 3)
						    	error = -1; //only counts when at least three affiliations duplicates 
						    if(error < temp.length-1) {
						    	HashMap<Integer, Boolean>[] collabFlag = new HashMap[4];
						    	HashMap<Integer, Boolean>[] coaffiFlag = new HashMap[4];
						    	for (int k=0; k<4; k++)
						    		collabFlag[k] = new HashMap<Integer, Boolean>();
						    	for (int k=0; k<4; k++)
						    		coaffiFlag[k] = new HashMap<Integer, Boolean>();
						    	int[] paperEgo = new int[4];
						        for (int h=0; h<authorLocals.length; h++) {
							    	String cities[] = authorLocals[h].split(";"); //all cities for each author
						    		int cutoff = cities.length;
							    	if (cutoff > 3) //set cutoff limit of co-affiliations to 3
							    		cutoff = 3;
						    		boolean[] ego = new boolean[4]; //for track ego authors 
						    		int[][] matchId = new int[cutoff][4];
							    	for (int j=0; j<cutoff; j++) {
							    		boolean matched = false;
									    for (int i=0; i<cityList.size(); i++) {
									    	if (cityList.get(i).matchName.contains(cities[j].replaceAll("\\s",""))) {
									    		for (int k=0; k<4; k++) {
										    		if (cityList.get(i).standardName.equalsIgnoreCase(name[k])) {
										    			matched = true;
										    			matchId[j][k] = i;
										    			ego[k] = true;
										    		}
										    		else {
										    			matched = true;
										    			matchId[j][k] = i;
										    			//ego[k] = false;
										    		}
									    		}
									    	}
									    }
									    if (!matched) {
									    	cityUnmatch++;
									    	System.out.println(cities[j] + " not mapped.");
									    }
							    	}
							    	for (int k=0; k<4; k++) { 
									    if (ego[k]) { //ego authors only affect co-affiliations count
									    	paperEgo[k]++; //counter for tracking multiple egos
									    	for (int j=0; j<cutoff; j++)
									    		coaffiFlag[k].put(matchId[j][k], true);
									    	}
									    else { //alt authors only affect collaboration count
									    	for (int j=0; j<cutoff; j++)
									    		collabFlag[k].put(matchId[j][k], true);
									    	}
							    	}
							    }
						        for (int k=0; k<4; k++) { 
							        if (paperEgo[k] > 1) {
							        	for (Integer id : coaffiFlag[k].keySet()) {
								        	if (coaffiFlag[k].get(id))
								        		collabFlag[k].put(id, true); //collaboration count increment
								        }
							        }
							        if (paperEgo[k] > 0) {
								        for (Integer id : collabFlag[k].keySet()) {
								        	if (collabFlag[k].get(id))
								        		cityList.get(id).paperCount[k+1] ++; //collaboration count increment
								        }
								        /*
								        for (Integer id : coaffiFlag.keySet()) {
								        	if (coaffiFlag.get(id))
								        		cityList.get(id).paperCount[k+2] ++; //co-affiliations count increment
								        }*/
							        }
						        }
						    }
				    	}
				    	else {
				    		//System.out.println(word2 + " not all authors mapped.");
				    		paperUnmatch++;
				    	}
				    }
				}
			}

			FileReader cityReader2 = null;
			try {
				cityReader2 = new FileReader(dir + "collabEdges.csv");
			}
			catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			cityBuffer =  new BufferedReader(cityReader2);
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
			    double dist = 0;
				Matcher m = p.matcher(line);
			    m.find();//source
			    word = m.group().replace("\"", "").replaceAll("\\s","");
			    m.find();
			    m.find();//target
			    word2 = m.group().replace("\"", "").replaceAll("\\s","");
			    m.find();
			    m.find(); m.find();//id
			    m.find(); //duplicate count
			    if (m.group().replace("\"", "").equals("1")) {
				    boolean matched = false;
				    boolean flag = false;
				    for (int k=0; k<4; k++) {
					    if (word.replace("\"", "").equals(idn[k])) {
						    double collabCount = 0;
						    double coaffiliate = 0;
							for (int i=0; i<cityList.size(); i++) {
								if (cityList.get(i).rowID.equals(word2)) {
									matched = true;
									if (cityList.get(i).paperCount[k+1] > collabCount) {
										collabCount = cityList.get(i).paperCount[k+1];
										//coaffiliate = cityList.get(i).paperCount[2];
									}
									double long2 = cityList.get(i).longitude;
				                    double lati2 = cityList.get(i).latitude;
				                    double theta = lng[k] - long2;
				                    dist = Math.sin(deg2rad(lat[k])) * Math.sin(deg2rad(lati2)) 
				                            + Math.cos(deg2rad(lat[k])) * Math.cos(deg2rad(lati2)) * Math.cos(deg2rad(theta));
				                    dist = Math.acos(dist);
				                    dist = rad2deg(dist);
				                    dist = dist * 60 * 1.1515; //times 1.609344 for km
								}
								else if (cityList.get(i).mergedID.contains(word2))
									flag = true;
							}
							if (!flag)
								print.println(line +","+ dist +","+ collabCount);
					    }
				    }
				    if (!matched) { 
				    	if (flag)
				    		print.println(line +","+ "merged" +","+ "deleted");
				    	else
				    		print.println(line +","+ "no id match" +","+ "deleted");
				    }
			    }
			    else {
			    	print.println(line +","+ "duplicatesAdam" +","+ "deleted");
			    }
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("City counted.");
		System.out.println(paperUnmatch + " papers with incomplete mappings");
		System.out.println(cityUnmatch + " cities unmatched");
		System.out.println(nodeCount + " cities matched");
		System.out.println(pruneCount + "long list pruned");
		System.out.println(coCount + "papers with coauthors");
	}

	public static void main ( String[] args ) {
		String currentDir = System.getProperty("user.dir");
		String dir = currentDir + "/data/WoS/";
		PrimaryCity test0 = new PrimaryCity(dir);
		test0.cityMerge(dir);
		
		PrimaryCity test1 = new PrimaryCity(dir, true);
		test1.generateNodeTable(dir);
		FlightNode test2 = new FlightNode(dir,true);
		test2.mapNodeTable(dir);
		FlightEdge test3 = new FlightEdge(dir);
		test3.multiStopsFlow(dir);
		
		test1.generateEdgeTable(dir);
	}
}