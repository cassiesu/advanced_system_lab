#!/bin/sh
DIR="/Users/CassieSu/Documents/workspaceweb/asl/logs/mm1"


gnuplot << EOF
set terminal pdf linewidth 2 dashed
set output "mm1.pdf"
set style fill transparent solid 0.3
set datafile missing "?"

# Line style for axes"
set style line 80 lt rgb "#808080"

set style line 1 lt 1 lc rgb "navy" pt 1 ps 1  lw 2
set style line 2 lt 1 lc rgb "brown" pt 1 ps 1  lw 2 
set style line 3 lt 1 lc rgb "red" pt 6 ps 1.5  lw 2 
set style line 4 lt 1 lc rgb "green" pt 1 ps 1  lw 2
set style line 5 lt 1 lc rgb "olive" pt 2 ps 1.5  lw 2

set border 3 back linestyle 80 

set grid ytics lc rgb "grey" lw 0 lt 0
set ytics 0,4,20
# set grid xtics lc rgb "grey" lw 1 lt 0

set ytics nomirror
set xtics nomirror

set xlabel "Arrival Rate (reqs/sec)" font ",7"
set ylabel "Response Time (ms)" font ",7"

set key top left samplen 2 spacing 1.25 box 

set yrange [0:20]
set xrange [0:14000]

plot \
    "$DIR/mm1-model.txt" using 1:2 with lines title "Calculated Response Time in M/M/1 Model" ls 1, \
    "$DIR/mm1-max.txt" using 1:2 with lines title "Measured Response Time in Maximum Throughput Test" ls 2, \
    "$DIR/mm1-max.txt" using 1:2 ls 3 notitle, \
    "$DIR/mm1-stab.txt" using 1:2 with lines title "Measured Response Time in Stability Test" ls 4, \
    "$DIR/mm1-stab.txt" using 1:2 ls 5 notitle

EOF
