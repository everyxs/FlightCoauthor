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
format collabpapercount geodist mass cosine dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink %9.1fc

*** Rescaling
replace cosine = cosine*100

*** Table1
sum collabpapercount geodist mass cosine dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink, format

*** Table2
sum collabpapercount geodist mass cosine dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if asu==1, format
sum collabpapercount geodist mass cosine dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if iub==1, format
sum collabpapercount geodist mass cosine dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if iupui==1, format
sum collabpapercount geodist mass cosine dist2air lines0stop lines1stop lines2stop lines3stop seats0stop seats1stop seats2stop seats3stop nlink if umich==1, format

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
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine, inf(mass)
estimates store m1
estadd fitstat
eststo m1

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop, inf(mass)
estimates store m2
estadd fitstat
eststo m2

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop, inf(mass)
estimates store m3
estadd fitstat
eststo m3

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop, inf(mass)
estimates store m4
estadd fitstat
eststo m4

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop, inf(mass)
estimates store m5
estadd fitstat
eststo m5

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop dist2air, inf(mass)
estimates store m6
estadd fitstat
eststo m6

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop dist2air, inf(mass)
estimates store m7
estadd fitstat
eststo m7

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop dist2air, inf(mass)
estimates store m8
estadd fitstat
eststo m8

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9
estadd fitstat
eststo m2

estimates table m1 m2 m3 m4 m5 m6 m7 m8 m9, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

esttab m1 m2 m3 m4 m5 m6 m7 m8 m9, keep(geodist) nostar noparentheses nodepvars nonumbers nogaps scalars(r2_ml r2_cu)


*** Table 5
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass)
estimates store m6s
estadd fitstat
eststo m6s

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass)
estimates store m7s
estadd fitstat
eststo m7s

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop c.dist2air##c.dist2air, inf(mass)
estimates store m8s
estadd fitstat
eststo m8s

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass)
estimates store m9s
estadd fitstat
eststo m9s

estimates table m6s m7s m8s m9s, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
esttab m6s m7s m8s m9s, keep(geodist) nostar noparentheses nodepvars nonumbers nogaps scalars(r2_ml r2_cu)

*** Table 6
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 
estimates store l
estadd fitstat
eststo l

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & asu==1
estimates store lasu
estadd fitstat
eststo lasu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & iub==1
estimates store liub
estadd fitstat
eststo liub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & iupui==1
estimates store liupui
estadd fitstat
eststo liupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & umich==1
estimates store lumich
estadd fitstat
eststo lumich

estimates table l lasu liub liupui lumich, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
esttab l lasu liub liupui lumich, keep(geodist) nostar noparentheses nodepvars nonumbers nogaps scalars(r2_ml r2_cu)

*** Table 7
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if asu==1
estimates store ms6asu
estadd fitstat
eststo ms6asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if asu==1
estimates store ms7asu
estadd fitstat
eststo ms7asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if asu==1
estimates store ms8asu
estadd fitstat
eststo ms8asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if asu==1
estimates store ms9asu
estadd fitstat
eststo ms9asu

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if iub==1
estimates store ms6iub
estadd fitstat
eststo ms6iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if iub==1
estimates store ms7iub
estadd fitstat
eststo ms7iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if iub==1
estimates store ms8iub
estadd fitstat
eststo ms8iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if iub==1
estimates store ms9iub
estadd fitstat
eststo ms9iub

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if iupui==1
estimates store ms6iupui
estadd fitstat
eststo l

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if iupui==1
estimates store ms7iupui
estadd fitstat
eststo ms7iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if iupui==1
estimates store ms8iupui
estadd fitstat
eststo ms8iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if iupui==1
estimates store ms9iupui
estadd fitstat
eststo ms9iupui

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if umich==1
estimates store ms6umich
estadd fitstat
eststo ms6umich

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if umich==1
estimates store ms7umich
estadd fitstat
eststo ms7umich

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if umich==1
estimates store ms8umich
estadd fitstat
eststo ms8umich

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if umich==1
estimates store ms9umich
estadd fitstat
eststo ms9umich

estimates table ms6asu ms7asu ms8asu ms9asu ms6iub ms7iub ms8iub ms9iub ms6iupui ms7iupui ms8iupui ms9iupui ms6umich ms7umich ms8umich ms9umich, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

esttab ms6asu ms7asu ms8asu ms9asu ms6iub ms7iub ms8iub ms9iub ms6iupui ms7iupui ms8iupui ms9iupui ms6umich ms7umich ms8umich ms9umich, keep(geodist) nostar noparentheses nodepvars nonumbers nogaps scalars(r2_ml r2_cu)

*****************************************
*****************************************
***             Charts              *****
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
 graph export geodist_mass1000.png, width(1200) replace

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
 graph export geodist_dist2air1000.png, width(1200) replace

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
graph export geodist_seats2stop1000.png, width(1200) replace

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
graph export geodist_nlink1000.png, width(1200) replace


**************************************************************
**************************************************************
***           Histograms (supporting information)          ***
**************************************************************
**************************************************************

format collabpapercount %9.0fc
histogram collabpapercount, fraction ///
    xtitle("Co-authored papers")
graph export histogram_collabpapercount.png, width(1200) replace

format geodist %9.0fc
histogram geodist, fraction ///
    xtitle("Geographical distance (mi)")
graph export histogram_geodist.png, width(1200) replace

format mass %9.0fc
histogram mass, fraction ///
    xtitle("Number of papers at destination")
graph export histogram_mass.png, width(1200) replace

format cosine %9.0fc
histogram cosine,  bin(10) fraction ///
    xtitle("Disciplinary similarity")
graph export histogram_cosine.png, width(1200) replace

format dist2air %9.0fc
histogram dist2air, fraction ///
    xtitle("Distance to airport at destination (mi)")
graph export histogram_dist2air.png, width(1200) replace

format lines0stop %9.0fc
histogram lines0stop, bin(10) fraction ///
    xtitle("lines0stop")
graph export histogram_lines0stop.png, width(1200) replace

format lines1stop %9.0fc
histogram lines1stop, bin(10) fraction ///
    xtitle("lines1stop")
graph export histogram_lines1stop.png, width(1200) replace

format lines2stop %9.0fc
histogram lines2stop, bin(10) fraction ///
    xtitle("lines2stop")
graph export histogram_lines2stop.png, width(1200) replace

format lines3stop %9.0fc
histogram lines3stop, bin(10) fraction ///
    xtitle("lines3stop")
graph export histogram_lines3stop.png, width(1200) replace

format seats0stop %9.0fc
histogram seats0stop, bin(10) fraction ///
    xtitle("seats0stop")
graph export histogram_seats0stop.png, width(1200) replace

format seats1stop %9.0fc
histogram seats1stop, bin(10) fraction ///
    xtitle("seats1stop")
graph export histogram_seats1stop.png, width(1200) replace

format seats2stop %9.0fc
histogram seats2stop, bin(10) fraction ///
    xtitle("seats2stop")
graph export histogram_seats2stop.png, width(1200) replace

format seats3stop %9.0fc
histogram seats3stop, bin(10) fraction ///
    xtitle("seats3stop")
graph export histogram_seats3stop.png, width(1200) replace

format nlink %9.0fc
histogram nlink, discrete width(1) start(0) fraction xscale(range(0 4)) ///
    xtitle("Min. number of stops to destination")
graph export histogram_nlink.png, width(1200) replace

**********************************************************************
**********************************************************************
***   Model specification and robustnes (supporting information)   ***
**********************************************************************
**********************************************************************

*** Rescaling
version 14.0
clear all
set linesize 250

*** changing the current working directory
*cd C:\Users ...

*** Import analitical dataset
*import delimited C:\Users\ ... \AirSciColl_Analitical_Dataset.csv, delimiter(";") clear

*** Rescaling
replace cosine = cosine*100

*** Rescaling
replace geodist = geodist/1000
replace mass = mass/1000
replace seats0stop = seats0stop/1000
replace seats1stop = seats1stop/1000
replace seats2stop = seats2stop/1000
replace seats3stop = seats3stop/1000


*** Because air transport makes little sense for short distances, observations in which geodistance variable was less than 100 miles were excluded from the further empirical analysis
drop if geodist<=0.1

*** Table 1
*** + cosine (Disciplinary similarity) in the inflate part of the model
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine, inf(mass cosine)
estimates store m1

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop, inf(mass cosine)
estimates store m2

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop, inf(mass cosine)
estimates store m3

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop, inf(mass cosine)
estimates store m4

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop, inf(mass cosine)
estimates store m5

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop dist2air, inf(mass cosine)
estimates store m6

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop dist2air, inf(mass cosine)
estimates store m7

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop dist2air, inf(mass cosine)
estimates store m8

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass cosine)
estimates store m9

estimates table m1 m2 m3 m4 m5 m6 m7 m8 m9, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 2
*** + cosine and geodist in the inflate part of the model
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine, inf(mass cosine geodist)
estimates store m1

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop, inf(mass cosine geodist)
estimates store m2

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop, inf(mass cosine geodist)
estimates store m3

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop, inf(mass cosine geodist)
estimates store m4

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop, inf(mass cosine geodist)
estimates store m5

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop dist2air, inf(mass cosine geodist)
estimates store m6

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop dist2air, inf(mass cosine geodist)
estimates store m7

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop dist2air, inf(mass cosine geodist)
estimates store m8

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass cosine geodist)
estimates store m9

estimates table m1 m2 m3 m4 m5 m6 m7 m8 m9, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 3
*** + cosine, geodist and air transport connectivity and accessibility in the inflate part of the model
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine, inf(mass cosine geodist)
estimates store m1

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop, inf(mass cosine geodist lines0stop)
estimates store m2

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop, inf(mass cosine geodist lines1stop)
estimates store m3

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop, inf(mass cosine geodist lines2stop)
estimates store m4

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop, inf(mass cosine geodist lines3stop)
estimates store m5

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop dist2air, inf(mass cosine geodist lines0stop dist2air)
estimates store m6

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop dist2air, inf(mass cosine geodist lines1stop dist2air)
estimates store m7

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop dist2air, inf(mass cosine geodist lines2stop dist2air)
estimates store m8

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass cosine geodist lines3stop dist2air)
estimates store m9

estimates table m1 m2 m3 m4 m5 m6 m7 m8 m9, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Tables 4 & 5
*** Various geodist exponents from 1.1 to 3
gen geodist1_1 = geodist^1.1
gen geodist1_2 = geodist^1.2
gen geodist1_3 = geodist^1.3
gen geodist1_4 = geodist^1.4
gen geodist1_5 = geodist^1.5
gen geodist1_6 = geodist^1.6
gen geodist1_7 = geodist^1.7
gen geodist1_8 = geodist^1.8
gen geodist1_9 = geodist^1.9
gen geodist2   = geodist^2
gen geodist2_1 = geodist^2.1
gen geodist2_2 = geodist^2.2
gen geodist2_3 = geodist^2.3
gen geodist2_4 = geodist^2.4
gen geodist2_5 = geodist^2.5
gen geodist2_6 = geodist^2.6
gen geodist2_7 = geodist^2.7
gen geodist2_8 = geodist^2.8
gen geodist2_9 = geodist^2.9
gen geodist3   = geodist^3

zinb collabpapercount geodist geodist1_1 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_1
zinb collabpapercount geodist geodist1_2 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_2
zinb collabpapercount geodist geodist1_3 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_3
zinb collabpapercount geodist geodist1_4 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_4
zinb collabpapercount geodist geodist1_5 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_5
zinb collabpapercount geodist geodist1_6 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_6
zinb collabpapercount geodist geodist1_7 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_7
zinb collabpapercount geodist geodist1_8 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_8
zinb collabpapercount geodist geodist1_9 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist1_9
zinb collabpapercount geodist geodist2 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2
zinb collabpapercount geodist geodist2_1 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_1
zinb collabpapercount geodist geodist2_2 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_2
zinb collabpapercount geodist geodist2_3 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_3
zinb collabpapercount geodist geodist2_4 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_4
zinb collabpapercount geodist geodist2_5 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_5
zinb collabpapercount geodist geodist2_6 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_6
zinb collabpapercount geodist geodist2_7 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_7
zinb collabpapercount geodist geodist2_8 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_8
zinb collabpapercount geodist geodist2_9 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist2_9
zinb collabpapercount geodist geodist3 c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass)
estimates store m9_geodist3

*** 3 decimal places
estimates table m9_geodist1_1 m9_geodist1_2 m9_geodist1_3 m9_geodist1_4 m9_geodist1_5 m9_geodist1_6 m9_geodist1_7 m9_geodist1_8 m9_geodist1_9 m9_geodist2, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_1 m9_geodist2_2 m9_geodist2_3 m9_geodist2_4 m9_geodist2_5 m9_geodist2_6 m9_geodist2_7 m9_geodist2_8 m9_geodist2_9 m9_geodist3, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** 8 decimal places
estimates table m9_geodist1_1, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_2, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_3, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_4, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_5, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_6, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_7, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_8, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist1_9, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2,   star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_1, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_2, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_3, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_4, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_5, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_6, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_7, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_8, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist2_9, star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)
estimates table m9_geodist3,   star b(%9.8f) stats(N aic bic) stfmt(%5.1f) varwidth(26)


**********************************************************
**********************************************************
***   Validation experiment (supporting information)   ***
**********************************************************
**********************************************************

set seed 123456789
generate byte validation_set = runiform() > 0.8
gen collabpapercount_log = log(collabpapercount + 1)

*** Table 6: validation (Table 4')
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine, inf(mass), if !validation_set
estimates store m1
predict predicted_coll_m1 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop, inf(mass), if !validation_set
estimates store m2
predict predicted_coll_m2 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop, inf(mass), if !validation_set
estimates store m3
predict predicted_coll_m3 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop, inf(mass), if !validation_set
estimates store m4
predict predicted_coll_m4 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop, inf(mass), if !validation_set
estimates store m5
predict predicted_coll_m5 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines0stop##c.lines0stop dist2air, inf(mass), if !validation_set
estimates store m6
predict predicted_coll_m6 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines1stop##c.lines1stop dist2air, inf(mass), if !validation_set
estimates store m7
predict predicted_coll_m7 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines2stop##c.lines2stop dist2air, inf(mass), if !validation_set
estimates store m8
predict predicted_coll_m8 if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.lines3stop##c.lines3stop dist2air, inf(mass), if !validation_set
estimates store m9
predict predicted_coll_m9 if validation_set

estimates table m1 m2 m3 m4 m5 m6 m7 m8 m9, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 7 
gen predicted_coll_m1_log = log(predicted_coll_m1)
gen predicted_coll_m2_log = log(predicted_coll_m2)
gen predicted_coll_m3_log = log(predicted_coll_m3)
gen predicted_coll_m4_log = log(predicted_coll_m4)
gen predicted_coll_m5_log = log(predicted_coll_m5)
gen predicted_coll_m6_log = log(predicted_coll_m6)
gen predicted_coll_m7_log = log(predicted_coll_m7)
gen predicted_coll_m8_log = log(predicted_coll_m8)
gen predicted_coll_m9_log = log(predicted_coll_m9)

regress collabpapercount_log predicted_coll_m1_log if validation_set
estimates store m1_val

regress collabpapercount_log predicted_coll_m2_log if validation_set
estimates store m2_val

regress collabpapercount_log predicted_coll_m3_log if validation_set
estimates store m3_val

regress collabpapercount_log predicted_coll_m4_log if validation_set
estimates store m4_val

regress collabpapercount_log predicted_coll_m5_log if validation_set
estimates store m5_val

regress collabpapercount_log predicted_coll_m6_log if validation_set
estimates store m6_val

regress collabpapercount_log predicted_coll_m7_log if validation_set
estimates store m7_val

regress collabpapercount_log predicted_coll_m8_log if validation_set
estimates store m8_val

regress collabpapercount_log predicted_coll_m9_log if validation_set
estimates store m9_val

estimates table m1_val m2_val m3_val m4_val m5_val m6_val m7_val m8_val m9_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)

*** Table 8: validation (Table 5')
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if !validation_set
estimates store m6s
predict predicted_coll_m6s if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if !validation_set
estimates store m7s
predict predicted_coll_m7s if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if !validation_set
estimates store m8s
predict predicted_coll_m8s if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if !validation_set
estimates store m9s
predict predicted_coll_m9s if validation_set

estimates table m6s m7s m8s m9s, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 9
gen predicted_coll_m6s_log = log(predicted_coll_m6s)
gen predicted_coll_m7s_log = log(predicted_coll_m7s)
gen predicted_coll_m8s_log = log(predicted_coll_m8s)
gen predicted_coll_m9s_log = log(predicted_coll_m9s)

regress collabpapercount_log predicted_coll_m6s_log if validation_set
estimates store m6s_val

regress collabpapercount_log predicted_coll_m7s_log if validation_set
estimates store m7s_val

regress collabpapercount_log predicted_coll_m8s_log if validation_set
estimates store m8s_val

regress collabpapercount_log predicted_coll_m9s_log if validation_set
estimates store m9s_val

estimates table m6s_val m7s_val m8s_val m9s_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)

*** Table 10: validation (Table 6')
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & !validation_set
estimates store l
predict predicted_coll_l if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & asu==1 & !validation_set
estimates store lasu
predict predicted_coll_lasu if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & iub==1 & !validation_set
estimates store liub
predict predicted_coll_liub if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & iupui==1 & !validation_set
estimates store liupui
predict predicted_coll_liupui if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine i.nlink dist2air, inf(mass),if nlink!=4 & umich==1 & !validation_set
estimates store lumich
predict predicted_coll_lumich if validation_set

estimates table l lasu liub liupui lumich, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 11
gen predicted_coll_l_log = log(predicted_coll_l)
gen predicted_coll_lasu_log = log(predicted_coll_lasu)
gen predicted_coll_liub_log = log(predicted_coll_liub)
gen predicted_coll_liupui_log = log(predicted_coll_liupui)
gen predicted_coll_lumich_log = log(predicted_coll_lumich)

regress collabpapercount_log predicted_coll_l_log if nlink!=4 & validation_set
estimates store l_val

regress collabpapercount_log predicted_coll_lasu_log if nlink!=4 & asu==1 & validation_set
estimates store lasu_val

regress collabpapercount_log predicted_coll_liub_log if nlink!=4 & iub==1 & validation_set
estimates store liub_val

regress collabpapercount_log predicted_coll_liupui_log if nlink!=4 & iupui==1 & validation_set
estimates store liupui_val

regress collabpapercount_log predicted_coll_lumich_log if nlink!=4 & umich==1 & validation_set
estimates store lumich_val

estimates table l_val lasu_val liub_val liupui_val lumich_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)

*** Table 12: validation (Table 7')
zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if asu==1 & !validation_set
estimates store ms6asu
predict predicted_coll_ms6asu if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if asu==1 & !validation_set
estimates store ms7asu
predict predicted_coll_ms7asu if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if asu==1 & !validation_set
estimates store ms8asu
predict predicted_coll_ms8asu if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if asu==1 & !validation_set
estimates store ms9asu
predict predicted_coll_ms9asu if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if iub==1 & !validation_set
estimates store ms6iub
predict predicted_coll_ms6iub if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if iub==1 & !validation_set
estimates store ms7iub
predict predicted_coll_ms7iub if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if iub==1 & !validation_set
estimates store ms8iub
predict predicted_coll_ms8iub if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if iub==1 & !validation_set
estimates store ms9iub
predict predicted_coll_ms9iub if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if iupui==1 & !validation_set
estimates store ms6iupui
predict predicted_coll_ms6iupui if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if iupui==1 & !validation_set
estimates store ms7iupui
predict predicted_coll_ms7iupui if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if iupui==1 & !validation_set
estimates store ms8iupui
predict predicted_coll_ms8iupui if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if iupui==1 & !validation_set
estimates store ms9iupui
predict predicted_coll_ms9iupui if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats0stop##c.seats0stop dist2air, inf(mass), if umich==1 & !validation_set
estimates store ms6umich
predict predicted_coll_ms6umich if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats1stop##c.seats1stop dist2air, inf(mass), if umich==1 & !validation_set
estimates store ms7umich
predict predicted_coll_ms7umich if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats2stop##c.seats2stop dist2air, inf(mass), if umich==1 & !validation_set
estimates store ms8umich
predict predicted_coll_ms8umich if validation_set

zinb collabpapercount c.geodist##c.geodist c.mass##c.mass c.cosine##c.cosine c.seats3stop##c.seats3stop dist2air, inf(mass), if umich==1 & !validation_set
estimates store ms9umich
predict predicted_coll_ms9umich if validation_set

estimates table ms6asu ms7asu ms8asu ms9asu ms6iub ms7iub ms8iub ms9iub ms6iupui ms7iupui ms8iupui ms9iupui ms6umich ms7umich ms8umich ms9umich, star b(%4.3f) stats(N aic bic) stfmt(%5.1f) varwidth(26)

*** Table 13
gen predicted_coll_ms6asu_log = log(predicted_coll_ms6asu)
gen predicted_coll_ms7asu_log = log(predicted_coll_ms7asu)
gen predicted_coll_ms8asu_log = log(predicted_coll_ms8asu)
gen predicted_coll_ms9asu_log = log(predicted_coll_ms9asu)
gen predicted_coll_ms6iub_log = log(predicted_coll_ms6iub)
gen predicted_coll_ms7iub_log = log(predicted_coll_ms7iub)
gen predicted_coll_ms8iub_log = log(predicted_coll_ms8iub)
gen predicted_coll_ms9iub_log = log(predicted_coll_ms9iub)
gen predicted_coll_ms6iupui_log = log(predicted_coll_ms6iupui)
gen predicted_coll_ms7iupui_log = log(predicted_coll_ms7iupui)
gen predicted_coll_ms8iupui_log = log(predicted_coll_ms8iupui)
gen predicted_coll_ms9iupui_log = log(predicted_coll_ms9iupui)
gen predicted_coll_ms6umich_log = log(predicted_coll_ms6umich)
gen predicted_coll_ms7umich_log = log(predicted_coll_ms7umich)
gen predicted_coll_ms8umich_log = log(predicted_coll_ms8umich)
gen predicted_coll_ms9umich_log = log(predicted_coll_ms9umich)

regress collabpapercount_log predicted_coll_ms6asu_log if asu==1 & validation_set
estimates store ms6asu_val

regress collabpapercount_log predicted_coll_ms7asu_log if asu==1 & validation_set
estimates store ms7asu_val

regress collabpapercount_log predicted_coll_ms8asu_log if asu==1 & validation_set
estimates store ms8asu_val

regress collabpapercount_log predicted_coll_ms9asu_log if asu==1 & validation_set
estimates store ms9asu_val

regress collabpapercount_log predicted_coll_ms6iub_log if iub==1 & validation_set
estimates store ms6iub_val

regress collabpapercount_log predicted_coll_ms7iub_log if iub==1 & validation_set
estimates store ms7iub_val

regress collabpapercount_log predicted_coll_ms8iub_log if iub==1 & validation_set
estimates store ms8iub_val

regress collabpapercount_log predicted_coll_ms9iub_log if iub==1 & validation_set
estimates store ms9iub_val

regress collabpapercount_log predicted_coll_ms6iupui_log if iupui==1 & validation_set
estimates store ms6iupui_val

regress collabpapercount_log predicted_coll_ms7iupui_log if iupui==1 & validation_set
estimates store ms7iupui_val

regress collabpapercount_log predicted_coll_ms8iupui_log if iupui==1 & validation_set
estimates store ms8iupui_val

regress collabpapercount_log predicted_coll_ms9iupui_log if iupui==1 & validation_set
estimates store ms9iupui_val

regress collabpapercount_log predicted_coll_ms6umich_log if umich==1 & validation_set
estimates store ms6umich_val

regress collabpapercount_log predicted_coll_ms7umich_log if umich==1 & validation_set
estimates store ms7umich_val

regress collabpapercount_log predicted_coll_ms8umich_log if umich==1 & validation_set
estimates store ms8umich_val

regress collabpapercount_log predicted_coll_ms9umich_log if umich==1 & validation_set
estimates store ms9umich_val

estimates table ms6asu_val ms7asu_val ms8asu_val ms9asu_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)

estimates table ms6iub_val ms7iub_val ms8iub_val ms9iub_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)

estimates table ms6iupui_val ms7iupui_val ms8iupui_val ms9iupui_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)

estimates table ms6umich_val ms7umich_val ms8umich_val ms9umich_val, star b(%4.3f) stats(N r2) stfmt(%5.3f) varwidth(26)
