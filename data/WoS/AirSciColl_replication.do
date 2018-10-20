*  Stata code to replicate analyses presented in:
*  "The Impact of Air Transport Availability on Research Collaboration: A Case Study of Four Universities"
*
*  Authors: Adam Ploszaj[1], Xiaoran Yan[2], Katy Börner[2][3]
*  [1] Centre for European Regional and Local Studies EUROREG, University of Warsaw, Warsaw, Poland
*  [2] Indiana Network Science Institute, Indiana University, Bloomington, Indiana, USA
*  [3] School of Informatics, Computing, and Engineering, Indiana University, Bloomington, Indiana, USA

version 14.0
clear all
set linesize 250

*** changing the current working directory
*cd C:\Users ...

*** Import analitical dataset
*import delimited C:\Users\ ... \AirSciColl_Analitical_Dataset.csv, delimiter(";") clear

*****************************************
*****************************************
***    Descriptive stats tables     *****
*****************************************
***************************************** 

*** Formating
format collabpapercount geodist mass dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink %9.1fc

*** Table1
sum collabpapercount geodist mass dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink, format

*** Table2
sum collabpapercount geodist mass dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if asu==1, format
sum collabpapercount geodist mass dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if iub==1, format
sum collabpapercount geodist mass dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if iupui==1, format
sum collabpapercount geodist mass dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if umich==1, format

*** Table3
tab institution nlink

*****************************************
*****************************************
***    	   Regression tables        *****
*****************************************
***************************************** 

*** Rescaling
replace geodist = geodist/1000
replace mass = mass/1000
replace seats0stop = seats0stop/1000
replace seats1stop = seats1stop/1000
replace seats2stop = seats2stop/1000
replace seats3stop = seats3stop/1000

*** Because air transport makes little sense for short distances, observations in which geodistance variable was less than 100 miles were excluded from the further empirical analysis
drop if geodist<=0.1

*** Table4
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass, inf(mass)
estimates store m1

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines0stop##c.lines0stop, inf(mass)
estimates store m2

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines1stop##c.lines1stop, inf(mass)
estimates store m3

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines2stop##c.lines2stop, inf(mass)
estimates store m4

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines3stop##c.lines3stop, inf(mass)
estimates store m5

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines0stop##c.lines0stop dist2air, inf(mass)
estimates store m6

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines1stop##c.lines1stop dist2air, inf(mass)
estimates store m7

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines2stop##c.lines2stop dist2air, inf(mass)
estimates store m8

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9

estimates table m1 m2 m3 m4 m5 m6 m7 m8 m9, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 5
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats0stop##c.seats0stop dist2air, inf(mass)
estimates store m6s

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats1stop##c.seats1stop dist2air, inf(mass)
estimates store m7s

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass)
estimates store m8s

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats3stop##c.seats3stop dist2air, inf(mass)
estimates store m9s

estimates table m6s m7s m8s m9s, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 6
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass i.nlink dist2air, inf(mass),if nlink!=4 
estimates store l

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass i.nlink dist2air, inf(mass),if nlink!=4 & asu==1
estimates store lasu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass i.nlink dist2air, inf(mass),if nlink!=4 & iub==1
estimates store liub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass i.nlink dist2air, inf(mass),if nlink!=4 & iupui==1
estimates store liupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass i.nlink dist2air, inf(mass),if nlink!=4 & umich==1
estimates store lumich

estimates table l lasu liub liupui lumich, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 7
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats0stop##c.seats0stop dist2air, inf(mass), if asu==1
estimates store ms6asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats1stop##c.seats1stop dist2air, inf(mass), if asu==1
estimates store ms7asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass), if asu==1
estimates store ms8asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats3stop##c.seats3stop dist2air, inf(mass), if asu==1
estimates store ms9asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats0stop##c.seats0stop dist2air, inf(mass), if iub==1
estimates store ms6iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats1stop##c.seats1stop dist2air, inf(mass), if iub==1
estimates store ms7iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass), if iub==1
estimates store ms8iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats3stop##c.seats3stop dist2air, inf(mass), if iub==1
estimates store ms9iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats0stop##c.seats0stop dist2air, inf(mass), if iupui==1
estimates store ms6iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats1stop##c.seats1stop dist2air, inf(mass), if iupui==1
estimates store ms7iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass), if iupui==1
estimates store ms8iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats3stop##c.seats3stop dist2air, inf(mass), if iupui==1
estimates store ms9iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats0stop##c.seats0stop dist2air, inf(mass), if umich==1
estimates store ms6umich

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats1stop##c.seats1stop dist2air, inf(mass), if umich==1
estimates store ms7umich

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass), if umich==1
estimates store ms8umich

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats3stop##c.seats3stop dist2air, inf(mass), if umich==1
estimates store ms9umich

estimates table ms6asu ms7asu ms8asu ms9asu ms6iub ms7iub ms8iub ms9iub ms6iupui ms7iupui ms8iupui ms9iupui ms6umich ms7umich ms8umich ms9umich, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*****************************************
*****************************************
***             Charts				*****
*****************************************
***************************************** 

* Rescaling
replace geodist = geodist*1000
replace mass = mass*1000
replace seats0stop = seats0stop*1000
replace seats1stop = seats1stop*1000
replace seats2stop = seats2stop*1000
replace seats3stop = seats3stop*1000

* Figure 3 Top-Left
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass)
mgen, at(geodist=(0(250)11000) mass=10000) stub(g) replace
mgen, at(geodist=(0(250)11000) mass=20000) stub(g1) replace
mgen, at(geodist=(0(250)11000) mass=30000) stub(g2) replace

graph twoway ///
	(rarea gul gll ggeodist, color(gs14)) ///
	(rarea g1ul g1ll g1geodist, color(gs14)) ///
	(rarea g2ul g2ll g2geodist, color(gs14)) ///
	(connected gmu ggeodist, lpat(solid) lcol(green) msym(i) ) ///
	(connected g1mu g1geodist, lpat(solid) lcol(blue) msym(i) ) ///
	(connected g2mu g2geodist, lpat(solid) lcol(red) msym(i) ), ///
	legend(cols(1) subtitle("Papers at destination") order(4 5 6) pos(3) label(4 10000) label(5 20000) label(6 30000)) aspect(1) ///
	ytitle("Co-authored papers") ///
	xtitle("Geographical distance (mi)")
* graph export geodist_mass1000.png, width(1200) replace

* Figure 3 Top-Right
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass)
mgen, at(geodist=(0(250)11000) dist2air=10) stub(gb) replace
mgen, at(geodist=(0(250)11000) dist2air=50) stub(gb1) replace
mgen, at(geodist=(0(250)11000) dist2air=100) stub(gb2) replace

graph twoway ///
	(rarea gbul gbll gbgeodist, color(gs14)) ///
	(rarea gb1ul gb1ll gb1geodist, color(gs14)) ///
	(rarea gb2ul gb2ll gb2geodist, color(gs14)) ///
	(connected gbmu gbgeodist, lpat(solid) lcol(green) msym(i) ) ///
	(connected gb1mu gb1geodist, lpat(solid) lcol(blue) msym(i) ) ///
	(connected gb2mu gb2geodist, lpat(solid) lcol(red) msym(i) ), ///
	legend(cols(1) subtitle("Distance to airport") order(4 5 6) pos(3) label(4 10 miles) label(5 50 miles) label(6 100 miles)) aspect(1) ///
	ytitle("Co-authored papers") ///
	xtitle("Geographical distance (mi)")
* graph export geodist_dist2air1000.png, width(1200) replace

* Figure 3 Bottom-Left
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.seats2stop##c.seats2stop dist2air, inf(mass)
mgen, at(geodist=(0(250)11000) seats2stop=1000) stub(gb) replace
mgen, at(geodist=(0(250)11000) seats2stop=5000) stub(gb1) replace
mgen, at(geodist=(0(250)11000) seats2stop=10000) stub(gb2) replace

graph twoway ///
	(rarea gbul gbll gbgeodist, color(gs14)) ///
	(rarea gb1ul gb1ll gb1geodist, color(gs14)) ///
	(rarea gb2ul gb2ll gb2geodist, color(gs14)) ///
	(connected gbmu gbgeodist, lpat(solid) lcol(green) msym(i) ) ///
	(connected gb1mu gb1geodist, lpat(solid) lcol(blue) msym(i) ) ///
	(connected gb2mu gb2geodist, lpat(solid) lcol(red) msym(i) ), ///
	legend(cols(1) subtitle("Seats2stop") order(4 5 6) pos(3) label(4 1000 seats) label(5 5000 seats) label(6 10000 seats)) aspect(1) ///
	ytitle("Co-authored papers") ///
	xtitle("Geographical distance (mi)")
*graph export geodist_seats2stop1000.png, width(1200) replace

* Figure 3 Bottom-Right
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass i.nlink dist2air, inf(mass), if nlink!=4
mgen, at(geodist=(0(250)11000) nlink=0) stub(g) replace
mgen, at(geodist=(0(250)11000) nlink=1) stub(g1) replace
mgen, at(geodist=(0(250)11000) nlink=2) stub(g2) replace
mgen, at(geodist=(0(250)11000) nlink=3) stub(g3) replace

graph twoway ///
	(rarea gul gll ggeodist, color(gs14)) ///
	(rarea g1ul g1ll g1geodist, color(gs14)) ///
	(rarea g2ul g2ll g2geodist, color(gs14)) ///
	(rarea g3ul g3ll g3geodist, color(gs14)) ///
	(connected gmu ggeodist, lpat(solid) lcol(green) msym(i) ) ///
	(connected g1mu g1geodist, lpat(solid) lcol(blue) msym(i) ) ///
	(connected g2mu g2geodist, lpat(solid) lcol(red) msym(i) ) ///
	(connected g3mu g3geodist, lpat(solid) lcol(yellow) msym(i) ), ///
	legend(cols(1) subtitle("Min. number of stops") order(5 6 7 8) pos(4) label(5 direct) label(6 1 stop) label(7 2 stops) label(8 3 stops)) aspect(1) ///
	ytitle("Co-authored papers") ///
	xtitle("Geographical distance (mi)")
*graph export geodist_nlink1000.png, width(1200) replace
