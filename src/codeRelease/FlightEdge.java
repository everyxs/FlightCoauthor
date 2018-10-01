package codeRelease;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import codeRelease.PrimaryCity.City;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;

/**
 * This class implements functions that read from planeWake.csv and routes.csv, and produces the weighted seat counts network routesWeighted.csv 
 * 
 * @author Xiaoran Yan( everyan@cs.unm.edu )
 * @version 1.0
 * @time Sep 22, 2017
 */

public class FlightEdge {
	class planeModel { //inner class
		String IATA; //equipment code
		String model; 
		String manufacturer;
		int size;
		planeModel(String code, String name, String manufac, int seats){
			IATA = code;
			model = name;
			manufacturer = manufac;
			size = seats;
		}
	}
	ArrayList<planeModel> modelList = new ArrayList<planeModel>();//for seat number lookup
	ArrayList<String> IDmap = new ArrayList<String>();//for airportID-matrix index mapping
	SparseRealMatrix flightMatrix; //for flight flow calculation
	SparseRealMatrix flightSeatsMatrix; //for flight seats flow calculation
	
	/**
	 * This constructor reads planWake.csv and build a lookup table for number of seats for each model
	 */	
	public FlightEdge(String dir) {
		String line="";
		String word="";
		String word2 = "";
		String word3 = "";
		//Read the plane  lists
		FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "planeWake.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		int count = 0;
		BufferedReader cityBuffer =  new BufferedReader(cityReader);
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
			    Matcher m = p.matcher(line);
			    m.find();
			    word = m.group(); //IATA equipment code
			    m.find();			    
			    m.find();
			    word2 = m.group(); //Manufacturer
			    m.find();
			    m.find();
			    word3 = m.group();//Model name
			    m.find();
			    m.find();//wake
			    int seats = 100;
			    switch (m.group()) {
					case "H": seats = 360;break;
					case "M": seats = 120;break;
					case "L": seats = 40;break;
					default: seats =  Integer.parseInt(m.group());
			    }
                    
			    boolean exist = false;
			    for (int i=0; i<modelList.size(); i++) {
			    	if (modelList.get(i).IATA.equals(word)) 
			    		exist = true;
			    }
			    if (!exist) {
			    	planeModel newModel = new planeModel(word, word3, word2, seats);
				    modelList.add(newModel);
				    count++;
			    }
			}
			System.out.println(count +" Plane models added.");
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		//Build the standard city properties from GIS lists
		try {
			cityReader = new FileReader(dir + "airportMergeMap.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		cityBuffer =  new BufferedReader(cityReader);
		ArrayList<ArrayList<String>> IDmapList = new ArrayList<ArrayList<String>>();
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				Matcher m = p.matcher(line);
			    m.find();//airport ID
			    IDmap.add(m.group().replace("\"", ""));
			    m.find();
			    m.find();m.find();//airport name
			    m.find();m.find();//metro ID
			    m.find();m.find();//latitude
			    m.find();m.find();//longitude
			    m.find();
			    String[] temp = m.group().replace("\"", "").split("\\|");
			    IDmapList.add(new ArrayList<String>(Arrays.asList(temp)));
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		//Build the standard city properties from GIS lists
		try {
			cityReader = new FileReader(dir + "routes.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "routesWeighted.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);
		print.println("SourceID,TargetID,size");
		
		int missCounter = 0;
		count = 0;
		cityBuffer =  new BufferedReader(cityReader);
		flightMatrix = new OpenMapRealMatrix(IDmap.size(),IDmap.size());//for building the merged flight network
		flightSeatsMatrix = new OpenMapRealMatrix(IDmap.size(),IDmap.size());//for building the merged flight seats network
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				Matcher m = p.matcher(line);
			    m.find();m.find();//airline
			    m.find();m.find();//airline ID
			    m.find();m.find();//Source airport
			    m.find();
			    word = m.group();//Source ID
			    m.find();
			    m.find();m.find();//Target airport
			    m.find();
			    word2 = m.group();//Target ID
			    m.find();
			    m.find();m.find();//stops
			    m.find(); //Equipment code
			    String[] list = m.group().replace("\"", "").split("\\s+"); //List of cities for all author
			    
			    boolean matched = false;
			    int key = 0;
			    double weight = 0;
			    for (int h=0; h<list.length; h++) {
				    for (int i=0; i<modelList.size(); i++) {
				    	if (modelList.get(i).IATA.equals(list[h])) {
				    		matched = true;
				    		key = i;
				    	}
				    }
				    if (!matched) {
				    	//counter++;
				    	weight += 100;
				    }
				    else
				    	weight += modelList.get(key).size;
			    }
			    weight = weight / list.length;
		    	print.println(word +","+ word2 +","+ weight);
		    	boolean flag = false;
		    	for (int i=0; i<IDmapList.size(); i++) {
		    		if (IDmapList.get(i).contains(word)) {
		    			for (int j=0; j<IDmapList.size(); j++) {
				    		if (IDmapList.get(j).contains(word2)) {
				    			flag = true;
				    			count++;
				    			if (j!=i) {
					    			flightMatrix.setEntry(i, j, flightMatrix.getEntry(i, j)+1);
					    			flightSeatsMatrix.setEntry(i, j, flightSeatsMatrix.getEntry(i, j)+weight);
					    			//flightMatrix.setEntry(j, i, flightMatrix.getEntry(j, i)+1);
					    			//flightSeatsMatrix.setEntry(j, i, flightSeatsMatrix.getEntry(j, i)+weight);
				    			}
				    			//else {//mapped to the same airport
				    				//flightMatrix.setEntry(i, j, 10000);
					    			//flightSeatsMatrix.setEntry(i, j, 10000);
					    			//flightMatrix.setEntry(i, j, 10000);
					    			//flightSeatsMatrix.setEntry(i, j, 10000);
				    				//missCounter++;
				    			//}
				    			continue;
				    		}
			    		}
		    			continue;
		    		}
		    	}
		    	if (!flag)
		    		if (word.contains("N")||word2.contains("N"))
		    			missCounter++;
		    		else
		    			missCounter++;
		    			
			}
			/*
			for (int i=0; i<flightMatrix.getColumnDimension(); i++)
			      for (int j=i+1; j<flightMatrix.getColumnDimension(); j++) {
		        	  double max = flightMatrix.getEntry(i, j);
			          if (max > flightMatrix.getEntry(j, i))
			        	  flightMatrix.setEntry(j,i,max);
			          if (max < flightMatrix.getEntry(j, i))
			        	  flightMatrix.setEntry(i,j,flightMatrix.getEntry(j, i));
		        	  max = flightMatrix.getEntry(i, j);
			          if (max > flightMatrix.getEntry(j, i))
			        	  flightMatrix.setEntry(j,i,max);
			          if (max < flightMatrix.getEntry(j, i))
			        	  flightMatrix.setEntry(i,j,flightMatrix.getEntry(j, i));
			      }
			      */
			//flightMatrix = maxComponent(flightMatrix, IDmap);
			//flightSeatsMatrix = maxComponent(flightSeatsMatrix, IDmap);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("City counted.");
		System.out.println(count + " flights counted");
		System.out.println(missCounter + " flights unmatched");
	}
	
	/**
     * This function finds the largest connected component from an adjacency matrix
     * @param deg double
     * @return double
     */
    private static SparseRealMatrix maxComponent(SparseRealMatrix full, ArrayList<String> IDmap) {
        int n = full.getColumnDimension();   
        Stack<Integer> stack = new Stack<Integer>();
        int visited[] = new int[n];       
        int flag = 0;
        for  (int i=0; i<n; i++) {
            if (visited[i] == 0) {
                flag++; //new cluster
                visited[i] = flag; //cluster root
                stack.push(i);
                while (!stack.isEmpty()) { //set all connected node to the same cluster
                    int element = (int) stack.pop();
                    int index = 0;    
                    while (index < n){
                        if ((full.getEntry(element, index)> 0 || full.getEntry(index, element)>0)  && visited[index] == 0) {
                            stack.push(index);
                            visited[index] = flag;
                        }
                        index++;
                    }
                }
            }
        }
        int[] sizes = new int[flag+1];
        for (int in:visited) {
        	sizes[in]++;
        }
        int maxSize = 0; //for the largest component
        int maxFlag = 0;
        for (int i=0; i<sizes.length; i++) {
        	if (sizes[i]>maxSize) {
        		maxSize = sizes[i];
        		maxFlag = i;
        	}
        }
        int[] subID = new int[maxSize];//largest component tracking
        int index = 0;
        for (int i=visited.length-1; i>=0; i--) {//remove from list tail to preventing shifting effects
        	if (visited[i] == maxFlag) {
        		subID[index] = i;
            	index++;	
        	}
        	else if (IDmap.size()>maxSize)
        		IDmap.remove(i);
        }
        SparseRealMatrix subgraph = (SparseRealMatrix) full.getSubMatrix(subID, subID);
        
		for (int i=0; i<maxSize; i++)
	      for (int j=i+1; j<maxSize; j++) 
	          if (subgraph.getEntry(i, j)>0){
	        	  subgraph.setEntry(i,j,subgraph.getEntry(i, j) + subgraph.getEntry(j, i));
	        	  subgraph.setEntry(j,i,subgraph.getEntry(i, j) + subgraph.getEntry(j, i));
	          }
        return subgraph;
    }
    
	/**
     * This function calculates flight flows up to 3 stops using both seated edges and simple flight counts
     */
    public void multiStopsFlow(String dir) {
    	
    	FileReader cityReader = null;
		try {
			cityReader = new FileReader(dir + "SelectCities[Nodes].csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		final Pattern p = Pattern.compile("(?:\"(?:[^\"\\\\]++|\\\\.)*+\"|[^\",]++)++|,"); //csv tokenizer
		String line="";
		String word="";
		BufferedReader cityBuffer =  new BufferedReader(cityReader);
    	HashMap<String, String> airportMap = new HashMap<String, String>();
    	HashMap<String, Double> Dist2AirMap = new HashMap<String, Double>();
    	HashMap<String, Double> massMap = new HashMap<String, Double>();
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				Matcher m = p.matcher(line);
			    m.find();//Node ID
			    word = m.group().replace("\"", "");
			    m.find();
			    m.find();m.find();//node label
			    m.find();m.find();//latitude
			    m.find();m.find();//longitude
			    m.find();m.find();//country
			    m.find();//aiport ID
			    airportMap.put(word, m.group().replace("\"", "")); //build the node-airport map
			    m.find();
			    m.find();//mass
			    massMap.put(word, Double.parseDouble(m.group().replace("\"", ""))); //build the node-airport map
			    m.find();
			    m.find();//Dist2Air
			    Dist2AirMap.put(word, Double.parseDouble(m.group().replace("\"", ""))); //build the node-airport map
			    m.find();
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		/* for hitting time calculation
		double[] sum = new double[idx];
		double tsum = 0;
		for (int i=0; i<idx; i++) {
		    sum[i] = 0;
		    for (int j=0; j<idx; j++)
		        sum[i] += stochastic[i][j];
		}
		for (int i=0; i<idx; i++) {
		    tsum += sum[i];
		    for (int j=0; j<idx; j++)
		        stochastic[i][j] = stochastic[i][j]/sum[i]; //column stochastic matrix
		}
		double[] eVector = new double[idx];
		for (int i=0; i<idx; i++) {
		    eVector[i] = sum[i]/tsum;
		}
		RealMatrix fundMat = new OpenMapRealMatrix(idx,idx);
		for (int i=0; i<idx; i++) 
		    for (int j=0; j<idx; j++) {
		        fundMat.setEntry(i, j, eVector[i] - stochastic[i][j]);
		        if (i==j)
		            fundMat.addToEntry(i, j, 1);
		    }
		fundMat = new LUDecomposition(fundMat).getSolver().getInverse();
		for (int i=0; i<idx; i++) 
		    for (int j=i+1; j<idx; j++) {
		        double meanPassage = (fundMat.getEntry(j, j)-fundMat.getEntry(i, j));
		        meanPassage +=  (fundMat.getEntry(i, i)-fundMat.getEntry(j, i));
		        meanPassage = meanPassage * 0.5/ sum[j] /sum[i];
		        Node s = formap.get(i);
		        Node t = formap.get(j);
		        int io = invIndicies.get(s);
		        int jo = invIndicies.get(t);
		        flowMat.setEntry(io, jo, meanPassage);
		        flowMat.setEntry(jo, io, meanPassage);
		        if (io==jo)
		            flowMat.setEntry(io, jo, -1);
		    }
		*/
		RealMatrix flow2Mat = flightMatrix.power(2);
		/**/
		for (int i=0; i<flow2Mat.getColumnDimension(); i++)
		      for (int j=i+1; j<flow2Mat.getColumnDimension(); j++) 
		          if (flow2Mat.getEntry(i, j)>0){
		        	  double entry = Math.sqrt(flow2Mat.getEntry(i, j));
                      flow2Mat.setEntry(i,j,entry);
                      flow2Mat.setEntry(j,i,entry);
		          }
		
		RealMatrix flow3Mat = flightMatrix.power(3);
		/**/
		for (int i=0; i<flow2Mat.getColumnDimension(); i++)
		      for (int j=i+1; j<flow2Mat.getColumnDimension(); j++) 
		          if (flow3Mat.getEntry(i, j)>0){
		        	  double entry = Math.pow(flow3Mat.getEntry(i, j),1.0/3);
                      flow3Mat.setEntry(i,j,entry);
                      flow3Mat.setEntry(j,i,entry);
		          }
		
		RealMatrix flow4Mat = flow2Mat.power(2);
		/**/	
		for (int i=0; i<flow2Mat.getColumnDimension(); i++)
		      for (int j=i+1; j<flow2Mat.getColumnDimension(); j++) 
		          if (flow4Mat.getEntry(i, j)>0) {
		        	  double entry = Math.sqrt(flow4Mat.getEntry(i, j));                      
                      flow4Mat.setEntry(i,j,entry);
                      flow4Mat.setEntry(j,i,entry);
		          }
		flow2Mat = flightMatrix.add(flow2Mat); 
		flow3Mat = flow2Mat.add(flow3Mat);
		flow4Mat = flow3Mat.add(flow4Mat);
		
		RealMatrix seat2Mat = flightSeatsMatrix.power(2);
		/**/
		for (int i=0; i<seat2Mat.getColumnDimension(); i++)
		      for (int j=i+1; j<seat2Mat.getColumnDimension(); j++) 
		          if (seat2Mat.getEntry(i, j)>0){
		        	  double entry = Math.sqrt(seat2Mat.getEntry(i, j));
		        	  seat2Mat.setEntry(i,j,entry);
		        	  seat2Mat.setEntry(j,i,entry);
		          }
		
		RealMatrix seat3Mat = flightSeatsMatrix.power(3);
		/**/
		for (int i=0; i<seat2Mat.getColumnDimension(); i++)
		      for (int j=i+1; j<seat2Mat.getColumnDimension(); j++) 
		          if (seat3Mat.getEntry(i, j)>0){
		        	  double entry = Math.pow(seat3Mat.getEntry(i, j),1.0/3);
		        	  seat3Mat.setEntry(i,j,entry);
		        	  seat3Mat.setEntry(j,i,entry);
		          }
		
		RealMatrix seat4Mat = seat2Mat.power(2);
		/**/
		for (int i=0; i<seat2Mat.getColumnDimension(); i++)
		      for (int j=i+1; j<seat2Mat.getColumnDimension(); j++) 
		          if (seat4Mat.getEntry(i, j)>0) {
		        	  double entry = Math.sqrt(seat4Mat.getEntry(i, j));                      
		        	  seat4Mat.setEntry(i,j,entry);
		        	  seat4Mat.setEntry(j,i,entry);
		          }
		seat2Mat = flightSeatsMatrix.add(seat2Mat); 
		seat3Mat = seat2Mat.add(seat3Mat);
		seat4Mat = seat3Mat.add(seat4Mat);
		
    	//read legacy edge list
		try {
			cityReader = new FileReader(dir + "legacyEdgeTable.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		
		//Create the output files
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(dir + "collabEdges.csv");
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		PrintStream print = new PrintStream(output);
		print.println("Source,Target,Id,duplicate,UMich,ASU,IUB,IUPUI,Dist2Air,MASS,Seats0Stop,Seats1Stop,Seats2Stop,Seats3Stop,Lines0Stop,Lines1Stop,Lines2Stop,Lines3Stop");
		
		cityBuffer =  new BufferedReader(cityReader);
		try {
			line = cityBuffer.readLine(); //skip header row
			while ((line = cityBuffer.readLine()) != null) { //Read each row
				Matcher m = p.matcher(line);
			    m.find();//Source Node ID
			    word = m.group().replace("\"", "");
			    m.find();
			    m.find();//Target Node ID
			    String sourcePortID = airportMap.get(word); //source airport ID

			    if (airportMap.containsKey(m.group().replace("\"", ""))) {
				    String targetPortID = airportMap.get(m.group().replace("\"", "")); //target airport ID
				    int SmatrixID = IDmap.indexOf(sourcePortID); //source matrix index
				    int TmatrixID = IDmap.indexOf(targetPortID); //target matrix index
				    /*
				    for (int i=0; i<IDmap.size(); i++) { //matrix index lookup
			    		if (IDmap.get(i).contains(sourcePortID)) {
			    			for (int j=i+1; j<IDmap.size(); j++) {
					    		if (IDmap.get(j).contains(targetPortID)) {
					    			SmatrixID = i;
					    			TmatrixID = j;
					    		}
			    			}
			    		}
				    }
				    */
					print.print(line);//copy first 7 columns
					double totalDist = Dist2AirMap.get(m.group().replace("\"", ""));
					double totalMass = massMap.get(m.group().replace("\"", ""));
					print.print(","+totalDist);//copy first 7 columns
					print.print(","+totalMass);//copy first 7 columns
					if (SmatrixID != TmatrixID) {
						print.print(","+flightSeatsMatrix.getEntry(SmatrixID, TmatrixID));
						print.print(","+seat2Mat.getEntry(SmatrixID, TmatrixID)); //simplify matrix operations, largest component reduction?
						print.print(","+seat3Mat.getEntry(SmatrixID, TmatrixID));
						print.print(","+seat4Mat.getEntry(SmatrixID, TmatrixID));
						print.print(","+flightMatrix.getEntry(SmatrixID, TmatrixID));
						print.print(","+flow2Mat.getEntry(SmatrixID, TmatrixID));
						print.print(","+flow3Mat.getEntry(SmatrixID, TmatrixID));
						print.println(","+flow4Mat.getEntry(SmatrixID, TmatrixID));
					}
					else { // if the cities share the same airports
						print.print(", -1");
						print.print(", -1");
						print.print(", -1");
						print.print(", -1");
						print.print(", -1");
						print.print(", -1");
						print.print(", -1");
						print.println(", -1");
					}
			    }
			    else
			    	print.println(line + ", , , , , , , , , ,no node match");//copy first 7 columns
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}	
		
		
    }
	
	public static void main ( String[] args ) {
		String currentDir = System.getProperty("user.dir");
		String dir = currentDir + "/data/WoS/";
		
		FlightEdge test1 = new FlightEdge(dir);
		test1.multiStopsFlow(dir);
	}
}