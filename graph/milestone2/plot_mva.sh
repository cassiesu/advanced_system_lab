#!/bin/sh
DIR="/Users/CassieSu/Documents/workspaceweb/asl/logs/mva"

gnuplot << EOF
set terminal pdf linewidth 2 dashed
set output "mva.pdf"
set style fill transparent solid 0.3

# Line style for axes"
set style line 80 lt rgb "#808080"

# set legend position
set key bottom right samplen 2 spacing 1.25 box 

set style line 1 lt 1 lc rgb "navy" pt 1 ps 1  lw 2
set style line 2 lt 1 lc rgb "brown" pt 1 ps 1  lw 2 
set style line 11 lt 0 lc rgb "blue" pt 1 ps 1  lw 2
set style line 22 lt 0 lc rgb "red" pt 1 ps 1  lw 2
set style line 3 lt 1 lc rgb "black" pt 1 ps 1  lw 2
set style line 4 lt 1 lc rgb "purple" pt 1 ps 1  lw 2
set style line 44 lt 1 lc rgb "violet" pt 1 ps 1  lw 2
set style line 5 lt 1 lc rgb "olive" pt 2 ps 1.5  lw 2
set style line 6 lt 2 lc rgb "orange" pt 1 ps 1  lw 2 
set style line 66 lt 2 lc rgb "salmon" pt 1 ps 1  lw 2 

set grid ytics lc rgb "grey" lw 0 lt 0
set border 3 back linestyle 80

set xtics nomirror
set ytics nomirror
set y2tics nomirror

set xlabel "Number of Clients" font ",7"
set ylabel "Throughput (reqs/sec)" font ",7"
set y2label "Response time (ms)"

set xrange [0:300]
set y2range [0:50]

plot \
    "$DIR/mva-max.txt" using 1:2:3 with errorbars title "Measured TP" ls 1 axes x1y1, \
    "$DIR/mva-model.txt" using 1:2 with lines ls 2 title "Calculated TP" axes x1y1, \
    "$DIR/mva-max.txt" using 1:4:5 with errorbars title "Measured RT" ls 11 axes x1y2, \
    "$DIR/mva-model.txt" using 1:3 with lines ls 22 title "Calculate RT" axes x1y2

unset y2tics
unset y2label

set ylabel "Utilization" font ",7"

set key center right samplen 2 spacing 1.25 box 
set autoscale x
set autoscale y
plot \
    "$DIR/mva-model.txt" using 1:4 with lines ls 1 title "Network I/O Queues", \
    "$DIR/mva-model.txt" using 1:5 with lines ls 2 title "Frontend Workers", \
    "$DIR/mva-model.txt" using 1:6 with lines ls 4 title "Backend Workers", \
    "$DIR/mva-model.txt" using 1:7 with lines ls 6 title "Database Processors" 

set ylabel "Average Number of Jobs with Different Device Type" font ",7"

set key center right samplen 2 spacing 1.25 box 
set autoscale x
set autoscale y
plot \
    "$DIR/mva-model.txt" using 1:8 with lines ls 1 title "Network I/O Queues", \
    "$DIR/mva-model.txt" using 1:9 with lines ls 2 title "Frontend Workers", \
    "$DIR/mva-model.txt" using 1:10 with lines ls 4 title "Backend Workers", \
    "$DIR/mva-model.txt" using 1:11 with lines ls 6 title "Database Processors" 



# faster database 
set key center left samplen 2 spacing 1.25 box 

set xlabel "Number of Clients" font ",7"
set ylabel "Throughput (reqs/sec)" font ",7"

plot \
    "$DIR/mva-model.txt" using 1:2 with lines ls 1 title "1x DB Speed", \
    "$DIR/mva-model-2xdb.txt" using 1:2 with lines ls 2 title "2x DB Speed", \
    "$DIR/mva-model-4xdb.txt" using 1:2 with lines ls 4 title "4x DB Speed", \
    "$DIR/mva-model-8xdb.txt" using 1:2 with lines ls 6 title "8x DB Speed"


set ylabel "Response Time (ms)" font ",7"

plot \
    "$DIR/mva-model.txt" using 1:3 with lines ls 1 title "1x DB Speed", \
    "$DIR/mva-model-2xdb.txt" using 1:3 with lines ls 2 title "2x DB Speed", \
    "$DIR/mva-model-4xdb.txt" using 1:3 with lines ls 4 title "4x DB Speed", \
    "$DIR/mva-model-8xdb.txt" using 1:3 with lines ls 6 title "8x DB Speed"



# more database instance
set key center left samplen 2 spacing 1.25 box 

set xlabel "Number of Clients" font ",7"
set ylabel "Throughput (reqs/sec)" font ",7"

plot \
    "$DIR/mva-model.txt" using 1:2 with lines ls 1 title "1 DB Instance", \
    "$DIR/mva-model-2db.txt" using 1:2 with lines ls 2 title "2 DB Instance", \
    "$DIR/mva-model-4db.txt" using 1:2 with lines ls 4 title "4 DB Instance", \
    "$DIR/mva-model-8db.txt" using 1:2 with lines ls 6 title "8 DB Instance"


set ylabel "Response Time (ms)" font ",7"

plot \
    "$DIR/mva-model.txt" using 1:3 with lines ls 1 title "1 DB Instance", \
    "$DIR/mva-model-2db.txt" using 1:3 with lines ls 2 title "2 DB Instance", \
    "$DIR/mva-model-4db.txt" using 1:3 with lines ls 4 title "4 DB Instance", \
    "$DIR/mva-model-8db.txt" using 1:3 with lines ls 6 title "8 DB Instance"



# different number of frontend threads
set autoscale y

set key center right samplen 2 spacing 1.25 box 

set xlabel "Number of Clients" font ",7"
set ylabel "Throughput (reqs/sec)" font ",7"
set y2tics nomirror
set y2label "Response time (ms)"

plot \
    "$DIR/mva-model-1fw.txt" using 1:2 with lines ls 1 title "TP (1 Frontend Worker)" axes x1y1, \
    "$DIR/mva-model-4fw.txt" using 1:2 with lines ls 2 title "TP (4 Frontend Workers)" axes x1y1, \
    "$DIR/mva-model-12fw.txt" using 1:2 with lines ls 4 title "TP (12 Frontend Workers)" axes x1y1, \
    "$DIR/mva-model.txt" using 1:2 with lines ls 6 title "TP (20 Frontend Workers)" axes x1y1, \
    "$DIR/mva-model-1fw.txt" using 1:3 with lines ls 11 title "RT (1 Frontend Worker)" axes x1y2, \
    "$DIR/mva-model-4fw.txt" using 1:3 with lines ls 22 title "RT (4 Frontend Workers)" axes x1y2, \
    "$DIR/mva-model-12fw.txt" using 1:3 with lines ls 44 title "RT (12 Frontend Workers)" axes x1y2, \
    "$DIR/mva-model.txt" using 1:3 with lines ls 66 title "RT (20 Frontend Workers)" axes x1y2

unset y2tics
unset y2label

set ylabel "Utilization" font ",7"
plot \
    "$DIR/mva-model-1fw.txt" using 1:5 with lines ls 1 title "Frontend Workers (1 Frontend Worker)", \
    "$DIR/mva-model-4fw.txt" using 1:5 with lines ls 2 title "Frontend Workers (4 Frontend Workers)", \
    "$DIR/mva-model-12fw.txt" using 1:5 with lines ls 4 title "Frontend Workers (12 Frontend Workers)", \
    "$DIR/mva-model.txt" using 1:5 with lines ls 6 title "Frontend Workers (20 Frontend Workers)" \


# # different number of frontend threads
# set multiplot layout 2,2

# set key font ",4" bottom right samplen 2 spacing 1.25 box 
# set ylabel "Utilization" font ",6"
# set xlabel "Number of Clients" font ",6"
# plot \
#     "$DIR/mva-model-1fw.txt" using 1:7 with lines ls 1 title "Database Processor (1 Frontend Worker)", \
#     "$DIR/mva-model-4fw.txt" using 1:7 with lines ls 2 title "Database Processor (4 Frontend Workers)", \
#     "$DIR/mva-model-12fw.txt" using 1:7 with lines ls 4 title "Database Processor (12 Frontend Workers)", \
#     "$DIR/mva-model.txt" using 1:7 with lines ls 6 title "Database Processor (20 Frontend Workers)" \

# set key top left samplen 2 spacing 1.25 box 
# set yrange [0:1]
# plot \
#     "$DIR/mva-model-1fw.txt" using 1:4 with lines ls 1 title "Network I/O (1 Frontend Worker)", \
#     "$DIR/mva-model-4fw.txt" using 1:4 with lines ls 2 title "Network I/O (4 Frontend Workers)", \
#     "$DIR/mva-model-12fw.txt" using 1:4 with lines ls 4 title "Network I/O (12 Frontend Workers)", \
#     "$DIR/mva-model.txt" using 1:4 with lines ls 6 title "Network I/O (20 Frontend Workers)" \

# plot \
#     "$DIR/mva-model-1fw.txt" using 1:5 with lines ls 1 title "Frontend Workers (1 Frontend Worker)", \
#     "$DIR/mva-model-4fw.txt" using 1:5 with lines ls 2 title "Frontend Workers (4 Frontend Workers)", \
#     "$DIR/mva-model-12fw.txt" using 1:5 with lines ls 4 title "Frontend Workers (12 Frontend Workers)", \
#     "$DIR/mva-model.txt" using 1:5 with lines ls 6 title "Frontend Workers (20 Frontend Workers)" \

# plot \
#     "$DIR/mva-model-1fw.txt" using 1:6 with lines ls 1 title "Backend Workers (1 Frontend Workers)", \
#     "$DIR/mva-model-4fw.txt" using 1:6 with lines ls 2 title "Backend Workers (4 Frontend Workers)", \
#     "$DIR/mva-model-12fw.txt" using 1:6 with lines ls 4 title "Backend Workers (12 Frontend Workers)", \
#     "$DIR/mva-model.txt" using 1:6 with lines ls 6 title "Backend Workers (20 Frontend Workers)" \

# unset multiplot


# faster database 
set multiplot layout 2,2

set key font ",4" bottom right samplen 2 spacing 1.25 box 
set ylabel "Utilization" font ",6"
set xlabel "Number of Clients" font ",6"
plot \
    "$DIR/mva-model.txt" using 1:7 with lines ls 1 title "Database Processor (1x DB Speed)", \
    "$DIR/mva-model-2xdb.txt" using 1:7 with lines ls 2 title "Database Processor (2x DB Speed)", \
    "$DIR/mva-model-4xdb.txt" using 1:7 with lines ls 4 title "Database Processor (4x DB Speed)", \
    "$DIR/mva-model-8xdb.txt" using 1:7 with lines ls 6 title "Database Processor (8x DB Speed)" \

set key top left samplen 2 spacing 1.25 box 
set yrange [0:1]
plot \
    "$DIR/mva-model.txt" using 1:4 with lines ls 1 title "Network I/O (1x DB Speed)", \
    "$DIR/mva-model-2xdb.txt" using 1:4 with lines ls 2 title "Network I/O (2x DB Speed)", \
    "$DIR/mva-model-4xdb.txt" using 1:4 with lines ls 4 title "Network I/O (4x DB Speed)", \
    "$DIR/mva-model-8xdb.txt" using 1:4 with lines ls 6 title "Network I/O (8x DB Speed)" \

plot \
    "$DIR/mva-model.txt" using 1:5 with lines ls 1 title "Frontend Workers (1x DB Speed)", \
    "$DIR/mva-model-2xdb.txt" using 1:5 with lines ls 2 title "Frontend Workers (2x DB Speed)", \
    "$DIR/mva-model-4xdb.txt" using 1:5 with lines ls 4 title "Frontend Workers (4x DB Speed)", \
    "$DIR/mva-model-8xdb.txt" using 1:5 with lines ls 6 title "Frontend Workers (8x DB Speed)" \

plot \
    "$DIR/mva-model.txt" using 1:6 with lines ls 1 title "Backend Workers (1x DB Speed)", \
    "$DIR/mva-model-2xdb.txt" using 1:6 with lines ls 2 title "Backend Workers (2x DB Speed)", \
    "$DIR/mva-model-4xdb.txt" using 1:6 with lines ls 4 title "Backend Workers (4x DB Speed)", \
    "$DIR/mva-model-8xdb.txt" using 1:6 with lines ls 6 title "Backend Workers (8x DB Speed)" \

unset multiplot

EOF
