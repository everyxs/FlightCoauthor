package codeRelease;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import org.apache.commons.lang3.StringUtils;

import codeRelease.PrimaryCity.City;

/**
 * This class implements functions that read from airports_metro.csv and airports_AP_joined.csv, cityListMerged.csv,
 * and produces the data table airportMergeMap.csv. and matches city with airports and produces flightFlow data.
 * 
 * @author Xiaoran Yan( everyan@cs.unm.edu )
 * @version BP 0.1
 * @time Sep 22, 2010
 */

public class FlightNode {
	class Airport { //inner class Country
		String rowID;
		String portName;
		String line;
		String metroId;
		String metroArea;
		double wDegree;
		double totalDegree;
		double latitude;
		double longitude;
		String mergeList = ""; 
		Airport (String name, double[] coord){
			portName = name;
			latitude = coord[0];
			longitude = coord[1];
		}

		Airport (int id, String standardN, String row, double[] coord, String metro, double degree){
			rowID = String.valueOf(id);
			portName = standardN;
			wDegree = degree;
			line = row;
			metroArea = metro; //if there is a metro tag
			latitude = coord[0];
			longitude = coord[1];
			//mergeList += "+" + rowID;
		}
		Airport (Airport copy){
			rowID = copy.rowID;
			portName = copy.portName;
			wDegree = copy.wDegree;
			totalDegree = copy.totalDegree;
			line = copy.line;
			metroId = copy.metroId; //Node id from cityNodeList
			metroArea = copy.metroArea; //node metro/city name
			latitude = copy.latitude;
			longitude = copy.longitude;
		}
	}
	ArrayList<Airport> portList = new ArrayList<Airport>();
	ArrayList<String> metroList = new ArrayList<String>();
	ArrayList<String> idList = new ArrayList<String>();

	/**
	 * This constructor builds the geo mapping from cityListState.csv
	 */	
	public FlightNode(String dir, boolean merge) {
		
		String line="";
		String word="";
		String word2 = "";
		String word3 = "";
		String name = "";
		double[] coordinates = new double[2];
		//Build the standard city properties from GIS lists
		FileReader metroReader = null;
		try {
			metroReader = new FileReader(dir + "airports_metro.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "airports_AP_joined_new.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		int count = 0;
		BufferedReader cityBuffer =  new BufferedReader(metroReader);
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		try {
			if (merge) {
				line = cityBuffer.readLine(); //skip header row
				while ((line = cityBuffer.readLine()) != null) { //Read each row
					String metro = "NA"; //for metro merge
				    Matcher m = p.matcher(line);
				    m.find();//row ID
				    word = m.group();
				    m.find();
				    m.find();//airport name
				    name = m.group();
				    m.find();
				    m.find();m.find();// city
				    m.find();m.find();//Country
				    m.find();m.find();//IATA_FFA
				    m.find();m.find();//ICAO
				    m.find();m.find();//AP Latitude
				    m.find();m.find();//AP Longitude
				    m.find();m.find();//total Degree
				    m.find();//Metro ID
				    if (!m.group().equals(",")) {
					    metro = m.group();
					    m.find();
				    }
				    m.find();m.find();//affil_ID
				    m.find();//Metro Latitude
				    if (!m.group().equals(",")) {
					    coordinates[0] = Double.parseDouble(m.group());
					    m.find();
				    }
				    else //if no geocoding
				        coordinates[0] = 500;
				    m.find();//Metro Longitude
				    if (!m.group().equals(",")) {
					    coordinates[1] = Double.parseDouble(m.group());
					    m.find();
				    }
				    else  //if no geocoding
				        coordinates[1] = 500;
				    
				    m.find();//Standard name
				    count++;
			    	Airport newPort = new Airport(Integer.parseInt(word), name, line, coordinates, metro, 0);
			    	idList.add(word);
				    portList.add(newPort);
			    	metroList.add(metro);
				}
			}
			cityBuffer =  new BufferedReader(cityReader);
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				String metro = "NA"; //for metro merge
			    Matcher m = p.matcher(line);
			    m.find();//row ID
			    word = m.group();
			    m.find();
			    m.find();//airport name
			    name = m.group();
			    m.find();
			    m.find();m.find();// city
			    m.find();m.find();//Country
			    m.find();m.find();//IATA_FFA
			    m.find();m.find();//ICAO
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
			    m.find();m.find();//Altitude
			    m.find();m.find();//timeZone
			    m.find();m.find();//DST
			    m.find();m.find();//timeZoneLo
			    m.find(); //degree
			    word3 =  m.group().replaceAll("\"", "");
			    m.find();
			    m.find();//Metro ID
			    if (!m.group().equals(",")) {
				    metro = m.group();
				    m.find();
			    }
			    if (metroList.contains(metro)&& !metro.equals("NA")) { //metro already listed, merge
			    	int idx = metroList.indexOf(metro);
			    	double degree = Double.parseDouble(word3);
			    	if (degree>portList.get(idx).wDegree) {
				    	portList.get(idx).latitude = coordinates[0];
				    	portList.get(idx).longitude = coordinates[1];
				    	portList.get(idx).wDegree = degree;
			    	}
			    	portList.get(idx).totalDegree += degree;
			    	portList.get(idx).mergeList += word+ "|";
			    	//count++;
			    }
			    else if (idList.contains(word)){ //metro is NA, but airportID matched, merge
			    	int idx = idList.indexOf(word);
			    	double degree = Double.parseDouble(word3);
			    	if (degree>portList.get(idx).wDegree) {
				    	portList.get(idx).latitude = coordinates[0];
				    	portList.get(idx).longitude = coordinates[1];
				    	portList.get(idx).wDegree = degree;
			    	}
			    	//metroList.set(idx, metro);
			    	//portList.get(idx).metroArea = metro;
			    	portList.get(idx).totalDegree += degree;
			    	portList.get(idx).mergeList += word+ "|";
			    	//count++;
			    }
			    else //new airport, create
			    {
			    	count++;
			    	Airport newPort = new Airport(Integer.parseInt(word), name, line, coordinates, metro, Double.parseDouble(word3));
			    	newPort.mergeList += word+ "|";
			    	metroList.add(metro);
				    portList.add(newPort);
			    }
			}
			System.out.println(count +" airports added.");
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
	
	public void airportMergeMap(String dir) {
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "airportMergeMap.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);
		print.println("id, name, Metro_name, latitude, longitude, mergeList, totalDegree");
		int cityMerged = 0;
		int coCount = 0;
		for (int i=0; i<portList.size(); i++) {
			Airport c = portList.get(i);
			if (c.metroArea.equalsIgnoreCase("NA")) {
				print.println(c.rowID+","+c.portName+","+c.metroArea+","+c.latitude+","+c.longitude+","+0+","+c.wDegree);
			}
			else {
				print.println(c.rowID+","+c.portName+","+c.metroArea+","+c.latitude+","+c.longitude+","+c.mergeList+","+c.wDegree);
			}
		}
		System.out.println("Output created.");
		System.out.println(cityMerged + " cities merged");
		System.out.println(coCount + "papers with coauthors");
	}
	
	/**
     * This function generates the node table while calculating the MASS column from "locations.csv"
     * @param rad double
     * @return double
     */
	public void mapNodeTable (String dir){
		String line="";
		String word="";
		String word2 = "";

		//Build the standard city properties from GIS lists
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "massMatch0.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "SelectCities[Nodes].csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);
		print.println("Id,Label,Lat,Lng,Country,airportID,Mass,Dist2Air");
		
		int cityUnmatch = 0;
		int cityMerged = 0;
		double long1 = 500;
        double lati1 = 500;
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
			    	lati1 = Double.parseDouble(m.group());
				    m.find();
			    }
			    m.find();//city longitude
			    if (!m.group().equals(",")) {
			    	long1 = Double.parseDouble(m.group());
				    m.find();
			    }
			    m.find();m.find();//country
			    m.find();//airportID
			    word = m.group();
			    m.find();
			    m.find();m.find();//MASS (paper count)
			    m.find();//nameList
			    word2 = m.group();
			    print.print(line.substring(0, line.indexOf(word2)));
			    int portIndex = 0;
			    for (int i=0; i<portList.size(); i++) {
			    	if (word.equals(portList.get(i).rowID))
			    		portIndex = i;
			    }
				Airport c = portList.get(portIndex);
                double long2 = c.longitude;
                double lati2 = c.latitude;
                double theta = long1 - long2;
                double dist = Math.sin(deg2rad(lati1)) * Math.sin(deg2rad(lati2)) 
                        + Math.cos(deg2rad(lati1)) * Math.cos(deg2rad(lati2)) * Math.cos(deg2rad(theta));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 1.1515 * 60; //times 1.609344 for km
			    print.println(dist);
			}			
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("City counted.");
		System.out.println(cityUnmatch + " cities unmatched");
		System.out.println(cityMerged + " cities merged");
	}
	
	public static void main ( String[] args ) {
		String currentDir = System.getProperty("user.dir");
		String dir = currentDir + "/data/WoS/";
		
		FlightNode test1 = new FlightNode(dir,true);
		//test1.airportMergeMap(dir);
		test1.mapNodeTable(dir);
	}
}
