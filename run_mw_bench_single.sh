## 
# Usage: ./run_mw_bench_single.sh key/asl.pem <db-ip> <mw-ip> <client-ips>
#
##

key=$1
db_ip=$2
db_host=ubuntu@$2
mw_ip=$3
mw_host=ubuntu@$3
client_host1=ubuntu@$4
# client_host2=ubuntu@$4
# client_host3=ubuntu@$5

mwtast=mwfrontend

function file_sync {
    # echo "-------------------------------"
    # echo "$db_host File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $db_host:~/asl
   
    echo "-------------------------------"
    echo "$mw_host File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host:~/asl

    echo "-------------------------------"
    echo "$client_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host1:~/asl

    # echo "-------------------------------"
    # echo "$client_host2 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host2:~/asl

    # echo ""
    # echo "$client_host3 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host3:~/asl

}

function setup_db {
    echo "-------------------------------"
    echo "SetUp Database"
    ssh -i $key $db_host "sudo /etc/init.d/postgresql restart"
    ssh -i $key $db_host "dropdb -U postgres asl"
    ssh -i $key $db_host "createdb -U postgres asl"
    ssh -i $key $db_host "cd ~/asl && ant setup-db"
    ssh -i $key $db_host "cd ~/asl && ant -Ddbip localhost run-db-initialization"
}

function client_run {
    echo "-------------------------------"
    echo "Start Client Instance With $3 Parallel Threads On Machine $2"
    ssh -i $key $4 "killall java"
    ssh -i $key $4 "cd ~/asl && ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dhost $mw_ip \
            -Dtask $mwtast \
            run-mw-bench-single > ~/client-mw-bench-single-$mwtast-$1-$2.log"
}

function middleware_run {
    echo "-------------------------------"
    echo "Start Middleware Instance On Machine $2"
    ssh -i $key $mw_host "killall java"
    ssh -f -i $key $mw_host "cd ~/asl && \
        ant run-middleware -Dbenchmark $1 -Dmachine $2 -Ddbip $db_ip > ~/server-mw-bench-single-$mwtast-$1-$2.log &"
    sleep 2
}


file_sync

for parallel_num in 4 8 16 32 64
do
    echo "-------------------------------"
    echo "Run MW Bench Single With $parallel_num Threads"
    middleware_run $parallel_num 1
    client_run $parallel_num 1 $parallel_num $client_host1
done

for parallel_num in 128 
do
    echo "-------------------------------"
    echo "Run MW Bench Single With $parallel_num Threads"
    middleware_run $parallel_num 1
    client_run $parallel_num 1 $parallel_num $client_host1
done

# for parallel_num in 4 8 16 32 64
# do
#     setup_db
#     echo "-------------------------------"
#     echo "Run MW Bench Single With $parallel_num Threads"
#     middleware_run $parallel_num 1
#     client_run $parallel_num 1 $parallel_num $client_host1
# done

# for parallel_num in 32
# do
#     echo "-------------------------------"
#     echo "Run MW Bench Single With `expr 2 \* $parallel_num` Threads"
#     middleware_run `expr 2 \* $parallel_num` 1
#     client_run `expr 2 \* $parallel_num` 1 $parallel_num $client_host1 &
#     client_run `expr 2 \* $parallel_num` 2 $parallel_num $client_host2
# done

# exit 0

# for parallel_num in 32
# do
#     echo "-------------------------------"
#     echo "Run MW Bench Single With `expr 2 \* $parallel_num` Threads"
#     middleware_run `expr 3 \* $parallel_num` 1
#     client_run `expr 3 \* $parallel_num` 1 $parallel_num $client_host1  &
#     client_run `expr 3 \* $parallel_num` 2 $parallel_num $client_host2  &
#     client_run `expr 3 \* $parallel_num` 3 $parallel_num $client_host3 
# done

exit 0

