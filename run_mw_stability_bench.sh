##
# Usage: ./run_mw_stability_bench.sh key/asl.pem <db-ip> <mw-ip1> <mw-ip2> <client-ip1> <client-ip2>
# 
##

key=$1
db_ip=$2
db_host=ubuntu@$2
mw_host1=ubuntu@$3
mw_host2=ubuntu@$4
mw_ip1=$3
mw_ip2=$4
client_host1=ubuntu@$5
client_host2=ubuntu@$6

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

function middleware_run {
	echo "-------------------------------"
    echo "Start Middleware Instance On Machine $3"
    ssh -f -i $key $1 "cd ~/asl && \
        ant -Dbenchmark $2 -Dmachine $3 -Ddbip $db_ip\
        run-middleware > ~/server-stability-bench-$2-$3.log &"
}

function client_run {
	echo "-------------------------------"
    echo "Start Client Instance On Machine $3"
    ssh -i $key $1 "cd ~/asl && ant \
    	-Dbenchmark $2 -Dmachine $3 -Dparallel $4 -Dhost $5 \
    	run-stability-bench > ~/client-stability-bench-$2-$3.log"
}


file_sync
setup_db

echo "-------------------------------"
echo "Run Stability Bench Mixed Workload With 24 Parallel Threads On Each Machine."
middleware_run $mw_host1 32 1
middleware_run $mw_host2 32 2
client_run $client_host1 32 1 8 $mw_ip1 &
client_run $client_host2 32 2 8 $mw_ip2


