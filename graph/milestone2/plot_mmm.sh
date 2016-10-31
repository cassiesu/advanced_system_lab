#!/bin/sh
DIR="/Users/CassieSu/Documents/workspaceweb/asl/logs/mmm"


gnuplot << EOF
set terminal pdf linewidth 2 dashed
set output "mmm.pdf"
set style fill transparent solid 0.3

# Line style for axes"
set style line 80 lt rgb "#808080"

# set legend position
set key bottom right

set style line 1 lt 1 lc rgb "navy" pt 1 ps 1  lw 2
set style line 2 lt 1 lc rgb "brown" pt 6 ps 1  lw 2 
set style line 3 lt 1 lc rgb "red" pt 6 ps 1.5  lw 2 
set style line 4 lt 1 lc rgb "purple" pt 1 ps 1  lw 2
set style line 5 lt 1 lc rgb "olive" pt 2 ps 1  lw 2

set border 3 back linestyle 80 
set grid ytics lc rgb "grey" lw 0 lt 0

set xtics nomirror
set ytics nomirror

set key top left samplen 2 spacing 1.25 box 

set xlabel "Arrival Rate (reqs/sec)" font ",7"

set ylabel "Response time (ms)" font ",7"
set yrange [0:5]

plot \
    "$DIR/mmm-mw-model.txt" using 1:4 with lines title "Calculated Response Time in M/M/10 Model" ls 1, \
    "$DIR/mmm-mw-perf.txt" using 1:2 title "Measured Response Time in Middleware Experiment" ls 3, \
    "$DIR/mmm-mw-perf.txt" using 1:2 with lines notitle ls 2

set ylabel "Utilization (Traffic Intensity) / Queueing Probability" font ",7"
set yrange [0:1]

plot \
    "$DIR/mmm-mw-model.txt" using 1:3 with lines title "Queueing Probability" ls 5, \
    "$DIR/mmm-mw-model.txt" using 1:2 with lines title "Utilization (Traffic Intensity)" ls 4

set ylabel "Response Time (ms)" font ",7"
set yrange [0:10]

plot \
    "$DIR/mmm-db-model.txt" using 1:4 with lines title "Calculated Response Time in M/M/4 Model" ls 1, \
    "$DIR/mmm-db-perf.txt" using 1:2 title "Measured Response Time In Database Experiment" ls 3, \
    "$DIR/mmm-db-perf.txt" using 1:2 with lines notitle ls 2

set ylabel "Utilization (Traffic Intensity) / Queueing Probability" font ",7"
set yrange [0:1]

plot \
    "$DIR/mmm-db-model.txt" using 1:3 with lines title "Queueing Probability" ls 5, \
    "$DIR/mmm-db-model.txt" using 1:2 with lines title "Utilization (Traffic Intensity)" ls 4


EOF
