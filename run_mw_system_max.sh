##
# Usage: ./run_mw_system_max.sh key/asl.pem <db-ip> <mw-ips> <client-ips>
# 
##

key=$1
db_host=ubuntu@$2
db_ip=$2
mw_host1=ubuntu@$3
mw_ip1=$3
mw_host2=ubuntu@$4
mw_ip2=$4
client_host1=ubuntu@$5
client_host2=ubuntu@$6


function setup_db {
    echo "-------------------------------"
    echo "SetUp Database"
    ssh -i $key $db_host "sudo /etc/init.d/postgresql restart"
    ssh -i $key $db_host "cd ~/asl && ant teardown-db"
    ssh -i $key $db_host "dropdb -U postgres asl"
    ssh -i $key $db_host "createdb -U postgres asl"
    ssh -i $key $db_host "cd ~/asl && ant setup-db"
    ssh -i $key $db_host "cd ~/asl && ant -Ddbip localhost run-db-initialization"
}

function client_run {
    echo "-------------------------------"
    echo "Start Client Instance With `expr 4 \* $3` Parallel Threads On Machine $2"
    ssh -i $key $4 "killall java"
    ssh -i $key $4 "cd ~/asl && ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dhost $5\
            run-system-bench > ~/client-system-max-$1-$2.log"
}

function middleware_run {
    echo "-------------------------------"
    echo "Start Middleware Instance On Machine $2"
    ssh -i $key $3 "killall java"
    ssh -f -i $key $3 "cd ~/asl && \
        ant run-middleware -Dbenchmark $1 -Dmachine $2 -Ddbip $db_ip > ~/server-system-max-$1-$2.log"
    sleep 2
}


function file_sync {
    echo "-------------------------------"
    echo "$db_host File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $db_host:~/asl
    
    echo "-------------------------------"
    echo "$mw_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host1:~/asl

    echo "-------------------------------"
    echo "$mw_host2 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host2:~/asl

    echo "-------------------------------"
    echo "$client_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host1:~/asl

    echo "-------------------------------"
    echo "$client_host2 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host2:~/asl

}



file_sync

# for parallel_num in 1 2 4 8 12 16 20 25
# do
#     echo "Run System Bench Mixed Workload With `expr 8 \* $parallel_num` Parallel Threads"
#     setup_db
#     middleware_run "max-`expr 8 \* $parallel_num`" 1 $mw_host1
#     middleware_run "max-`expr 8 \* $parallel_num`" 2 $mw_host2
#     client_run "max-`expr 8 \* $parallel_num`" 1 $parallel_num $client_host1 $mw_ip1 &
#     client_run "max-`expr 8 \* $parallel_num`" 2 $parallel_num $client_host2 $mw_ip2
# done

# for parallel_num in 30 35 40 45 50
# do
#     echo "Run System Bench Mixed Workload With `expr 8 \* $parallel_num` Parallel Threads"
#     setup_db
#     middleware_run "max-`expr 8 \* $parallel_num`" 1 $mw_host1
#     middleware_run "max-`expr 8 \* $parallel_num`" 2 $mw_host2
#     client_run "max-`expr 8 \* $parallel_num`" 1 $parallel_num $client_host1 $mw_ip1 &
#     client_run "max-`expr 8 \* $parallel_num`" 2 $parallel_num $client_host2 $mw_ip2
# done

for parallel_num in 50
do
    echo "Run System Bench Mixed Workload With `expr 8 \* $parallel_num` Parallel Threads"
    setup_db
    middleware_run "max-`expr 8 \* $parallel_num`" 1 $mw_host1
    middleware_run "max-`expr 8 \* $parallel_num`" 2 $mw_host2
    client_run "max-`expr 8 \* $parallel_num`" 1 $parallel_num $client_host1 $mw_ip1 &
    client_run "max-`expr 8 \* $parallel_num`" 2 $parallel_num $client_host2 $mw_ip2
done

exit 0

