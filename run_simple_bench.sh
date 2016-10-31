##
# Usage: ./run_simple_bench.sh key/asl.pem <db-ip> <mw-ips>
# 
#
key=$1
db_ip=$2
db_host=ubuntu@$2
mw_host1=ubuntu@$3
# mw_host2=ubuntu@$4
# mw_host3=ubuntu@$5

function file_sync {
    echo "-------------------------------"
    echo "$db_host File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $db_host:~/asl

    echo "-------------------------------"
    echo "$mw_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host1:~/asl

    # echo "-------------------------------"
    # echo "$mw_host2 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host2:~/asl

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

function run_db_simple_bench {
    echo "-------------------------------"
    echo "Run DB Simple Bench With 20000 requests"
    ssh -i $key $1 "cd ~/asl && killall java; \
        ant -Ddbip localhost run-db-simple-bench"

}

function run_mw_simple_bench {
    echo "-------------------------------"
    echo "Run MW Simple Bench With 10000 requests"
    ssh -i $key $1 "cd ~/asl && killall java; \
        ant -Dhost localhost -Dport 6000 run-mw-simple-bench"

}

function middleware_run {
    echo "-------------------------------"
    echo "Start Middleware Instance On Machine 0"
    ssh -i $key $1 "killall java"
    ssh -f -i $key $1 "cd ~/asl && \
        ant -Dbenchmark 0 -Dmachine 0 -Ddbip $db_ip run-middleware \
            > ~/server-simple-bench-0-0.log &"
    sleep 2
}



file_sync
setup_db

# run_db_simple_bench $db_host

middleware_run $mw_host1
run_mw_simple_bench $mw_host1

exit 0
