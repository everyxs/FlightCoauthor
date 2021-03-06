# JAVA code for data table replication
# Author Xiaoran Yan

The code for data manipulation and data table generation in written in JAVA. Source code and required input files are shared at https://github.com/everyxs/FlightCoauthor

The FlightCoauthor.zip contains a JAR package and a data folder set up for reproducing the data tables used in the main article. Java Runtime Environment (JRE) 7.0 or newer must be installed; all other required libraries are included in the JAR package. Once the zip content is extracted, run the JAR package by typing the following command under the extracted folder:
> Java –jar FlightCoaurhtor.jar

The data folder contains/produces the following files: 

•	Raw input files: papers.csv, cityListJoin.csv, locations.csv, airports_metro.csv, airports_AP_joined_new.csv, routes.csv, planeWake.csv, legacyNodeTabel.csv, legacyEdgeTabel.csv

•	Intermediate data I/O file: cityListMerged, massMatch0.csv, airportMergeMap.csv, collabEdges.csv, routesWeighted.csv

•	Final output data tables: SelectCities[Nodes].csv, SelectCities[Edges].csv

Since the Web of Science raw data is under Intellectual Property restrictions, we can only release a small sample in “papers.csv” for illustration purposes. As a result, the final edge table “SelectCities[Edges].csv” will have a very small “CollabPaper” column. For the actual “SelectCities[Edges].csv” table used in our analysis, please refer to “SelectCities[Edges]-full.csv”.

To recompile the sources files, Apache Commons lang3 and math3 Libraries are needed. The source code includes three classes, and their associated data files listed here:

•	PrimaryCity.java, the main class where all other data is processed, including geo-coding, city merging, collaboration counting, etc.
Input files: cityListJoin.csv, legacyNodeTabel.csv, locations.csv, papers.csv, collabEdges.csv
Output files: cityListMerged, massMatch0.csv, SelectCities[Edges].csv (final edge data table)

•	FlightNode.java, which handles airport merging and mapping with cities/metros.

Input files: airports_metro.csv, airports_AP_joined_new.csv, massMatch0.csv, 
Output files: airportMergeMap.csv, SelectCities[Nodes].csv (final node data table)

•	FlightEdge.java, which handles flight flow calculation and mapping with cities/metros.
Input files: legacyEdgeTabel.csv, routes.csv, planeWake.csv, aiportMergeMap.csv
Output files: routesWeighted.csv, collabEdges.csv

# STATA code for reproducing table 1-7 and figure 3
# Author Adam Ploszaj 
The modeling and analysis is done in STATA and code and data is shared at https://github.com/everyxs/FlightCoauthor/tree/master/STATA.
 
Regression analyses presented in the article can be replicated using STATA .do file: “AirSciColl_replication.do”. STATA version 14 or newer is needed to run the code. The STATA code uses analytical dataset named “AirSciColl_Analitical_Dataset.csv” that combines data tables described above. For convenience, the analitical dataset is limited to main variables used in the analysis. The code presented in “AirSciColl_replication.do” also allows reproduction of tables 1-7 and figure 3 from the main body of the article. 

For more details of the data, please refer to the document "Supplementary_information.docx".
