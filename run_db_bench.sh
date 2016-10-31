##
# Usage: ./run_db_bench.sh key/asl.pem <db-ip> <mw-ips>
# 
#
key=$1
db_ip=$2
db_host=ubuntu@$2
mw_host1=ubuntu@$3
mw_host2=ubuntu@$4
# mw_host3=ubuntu@$5

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

    # echo "-------------------------------"
    # echo "$mw_host3 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host3:~/asl

}

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

function run_db_bench_singleop {
    echo "-------------------------------"
    echo "Run DB Bench With Single Operation $5, $3 Parallel Threads On Machine $2"
    ssh -i $key $4 "cd ~/asl && killall java; \
        ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dtask $5 -Ddbip $db_ip \
        run-db-bench-single > ~/server-db-bench-single-$5-$1-$2.log"

}

function run_db_bench_multiop {
    echo "-------------------------------"
    echo "Run DB Bench With Multiple Operation Workload With `expr 4 \* $3` Parallel Threads On Machine $2"
    ssh -i $key $4 "cd ~/asl && killall java; \
        ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Ddbip $db_ip \
        run-db-bench-multiple > ~/server-db-bench-multiple-$1-$2.log"
}



file_sync


for parallel_num in 1 2 4 5 6 7 8
do
    setup_db
    run_db_bench_multiop $parallel_num 1 $parallel_num $mw_host1
done

for parallel_num in 8
do
    setup_db
    run_db_bench_multiop `expr 8 \* $parallel_num` 1 $parallel_num $mw_host1 &
    run_db_bench_multiop `expr 8 \* $parallel_num` 2 $parallel_num $mw_host2
done

# for parallel_num in 16
# do
#     setup_db
#     run_db_bench_multiop `expr 8 \* $parallel_num` 1 $parallel_num $mw_host1 &
#     run_db_bench_multiop `expr 8 \* $parallel_num` 2 $parallel_num $mw_host2
# done



# for parallel_num in 4 8 16 32
# do
#     setup_db
#     run_db_bench_singleop $parallel_num 1 $parallel_num $mw_host1 "dbsend"
# done

# for parallel_num in 32
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbsend" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbsend"
# done

# for parallel_num in 64
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbsend" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbsend"
# done



# for parallel_num in 4 8 16 32
# do
#     setup_db
#     run_db_bench_singleop $parallel_num 1 $parallel_num $mw_host1 "dbpeek"
# done

# for parallel_num in 32
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbpeek" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbpeek"
# done

# for parallel_num in 64
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbpeek" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbpeek"
# done



# for parallel_num in 4 8 16 32
# do
#     setup_db
#     run_db_bench_singleop $parallel_num 1 $parallel_num $mw_host1 "dbquery"
# done

# for parallel_num in 32
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbquery" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbquery"
# done

# for parallel_num in 64
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbquery" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbquery"
# done



# for parallel_num in 4 8 16 32
# do
#     setup_db
#     run_db_bench_singleop $parallel_num 1 $parallel_num $mw_host1 "dbdelete"
# done

# for parallel_num in 32
# do
#     setup_db
#     run_db_bench_singleop `expr 2 \* $parallel_num` 1 $parallel_num $mw_host1 "dbdelete" &
#     run_db_bench_singleop `expr 2 \* $parallel_num` 2 $parallel_num $mw_host2 "dbdelete"
# done


exit 0
