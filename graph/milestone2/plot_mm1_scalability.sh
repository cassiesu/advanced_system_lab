#!/bin/sh
DIR="/Users/CassieSu/Documents/workspaceweb/asl/logs/mm1"


gnuplot << EOF
set terminal pdf linewidth 2 dashed
set output "mm1-scalability.pdf"
set style fill transparent solid 0.3

# Line style for axes"
set style line 80 lt rgb "#808080"

set style line 1 lt 1 lc rgb "navy" pt 1 ps 1  lw 2
set style line 2 lt 1 lc rgb "brown" pt 1 ps 1  lw 2 
set style line 3 lt 1 lc rgb "red" pt 1 ps 1  lw 2 
set style line 4 lt 1 lc rgb "green" pt 1 ps 1  lw 2
set style line 5 lt 1 lc rgb "black" pt 1 ps 1  lw 2
set style line 6 lt 1 lc rgb "orange" pt 1 ps 1  lw 2
set style line 7 lt 1 lc rgb "purple" pt 1 ps 1  lw 2

set border 3 back linestyle 80 

set grid ytics lc rgb "grey" lw 0 lt 0
set ytics 0,4,20

set ytics nomirror
set xtics nomirror

set xlabel "Arrival Rate (reqs/sec)" font ",7"
set ylabel "Response Time (ms)" font ",7"

set key top right samplen 2 spacing 1.25 box 

set yrange [0:20]
set xrange [0:14000]

set object circle at 1056,63.05 size 100 fc rgb "navy" 
set object circle at 1928,32.25 size 100 fc rgb "brown" 
set object circle at 3421,15.93 size 100 fc rgb "red" 
set object circle at 5853,7.19 size 100 fc rgb "green" 
set object circle at 8065,3.81 size 100 fc rgb "black" 
set object circle at 8473,3.37 size 100 fc rgb "orange" 
set object circle at 8318,3.517 size 100 fc rgb "purple" 

plot \
    "$DIR/mm1-scale-model-rt.txt" using 1:2 with lines title "1 Worker Thread" ls 1, \
    "$DIR/mm1-scale-model-rt.txt" using 1:3 with lines title "2 Worker Threads" ls 2, \
    "$DIR/mm1-scale-model-rt.txt" using 1:4 with lines title "4 Worker Threads" ls 3, \
    "$DIR/mm1-scale-model-rt.txt" using 1:5 with lines title "8 Worker Threads" ls 4, \
    "$DIR/mm1-scale-model-rt.txt" using 1:6 with lines title "16 Worker Threads" ls 5, \
    "$DIR/mm1-scale-model-rt.txt" using 1:7 with lines title "32 Worker Threads" ls 6, \
    "$DIR/mm1-scale-model-rt.txt" using 1:8 with lines title "64 Worker Threads" ls 7

set key bottom right samplen 2 spacing 1.25 box 
set ylabel "Utilization" font ",7"
set yrange [0:1]
set xrange [0:13000]
set ytics 0,0.2,1

plot \
    "$DIR/mm1-scale-model-u.txt" using 1:2 with lines title "1 Worker Thread" ls 1, \
    "$DIR/mm1-scale-model-u.txt" using 1:3 with lines title "2 Worker Threads" ls 2, \
    "$DIR/mm1-scale-model-u.txt" using 1:4 with lines title "4 Worker Threads" ls 3, \
    "$DIR/mm1-scale-model-u.txt" using 1:5 with lines title "8 Worker Threads" ls 4, \
    "$DIR/mm1-scale-model-u.txt" using 1:6 with lines title "16 Worker Threads" ls 5, \
    "$DIR/mm1-scale-model-u.txt" using 1:7 with lines title "32 Worker Threads" ls 6, \
    "$DIR/mm1-scale-model-u.txt" using 1:8 with lines title "64 Worker Threads" ls 7

EOF
