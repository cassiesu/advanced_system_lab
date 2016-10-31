##
# Usage: ./run_mw_system_scale.sh key/asl.pem <db-ip> <mw-ips> <client-ips>
# 
##

key=$1
db_host=ubuntu@$2
db_ip=$2
mw_host1=ubuntu@$3
mw_ip1=$3
mw_host2=ubuntu@$4
mw_ip2=$4
mw_host3=ubuntu@$5
mw_ip3=$5
mw_host4=ubuntu@$6
mw_ip4=$6
client_host1=ubuntu@$7
client_host2=ubuntu@$8
client_host3=ubuntu@$9
client_host4=ubuntu@${10}


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
            run-system-bench > ~/client-system-scale-$1-$2.log"
}

function middleware_run {
    echo "-------------------------------"
    echo "Start Middleware Instance On Machine $2"
    ssh -i $key $3 "killall java"
    ssh -f -i $key $3 "cd ~/asl && \
        ant run-middleware -Dbenchmark $1 -Dmachine $2 -Ddbip $db_ip -Dbworkers $4 > ~/server-system-scale-$1-$2.log"
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
    echo "$mw_host3 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host3:~/asl

    echo "-------------------------------"
    echo "$mw_host4 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host4:~/asl

    echo "-------------------------------"
    echo "$client_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host1:~/asl

    echo "-------------------------------"
    echo "$client_host2 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host2:~/asl

    echo "-------------------------------"
    echo "$client_host3 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host3:~/asl

    echo "-------------------------------"
    echo "$client_host4 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host4:~/asl

}



file_sync

##
# Scale Test: Same Number of Client Threads 72
##

echo "run scale 45"
setup_db
middleware_run "scale-45" 1 $mw_host1 16
middleware_run "scale-45" 2 $mw_host2 16
middleware_run "scale-45" 3 $mw_host3 16
middleware_run "scale-45" 4 $mw_host4 16
client_run "scale-45" 1 4 $client_host1 $mw_ip1 &
client_run "scale-45" 2 4 $client_host2 $mw_ip2 &
client_run "scale-45" 3 4 $client_host3 $mw_ip3 &
client_run "scale-45" 4 6 $client_host4 $mw_ip4 


echo "run scale 44"
setup_db
middleware_run "scale-44" 1 $mw_host1 8
middleware_run "scale-44" 2 $mw_host2 8
middleware_run "scale-44" 3 $mw_host3 8
middleware_run "scale-44" 4 $mw_host4 8
client_run "scale-44" 1 4 $client_host1 $mw_ip1 &
client_run "scale-44" 2 4 $client_host2 $mw_ip2 &
client_run "scale-44" 3 4 $client_host3 $mw_ip3 &
client_run "scale-44" 4 6 $client_host4 $mw_ip4 


echo "run scale 43"
setup_db
middleware_run "scale-43" 1 $mw_host1 4
middleware_run "scale-43" 2 $mw_host2 4
middleware_run "scale-43" 3 $mw_host3 4
middleware_run "scale-43" 4 $mw_host4 4
client_run "scale-43" 1 4 $client_host1 $mw_ip1 &
client_run "scale-43" 2 4 $client_host2 $mw_ip2 &
client_run "scale-43" 3 4 $client_host3 $mw_ip3 &
client_run "scale-43" 4 6 $client_host4 $mw_ip4 


echo "run scale 42"
setup_db
middleware_run "scale-42" 1 $mw_host1 2
middleware_run "scale-42" 2 $mw_host2 2
middleware_run "scale-42" 3 $mw_host3 2
middleware_run "scale-42" 4 $mw_host4 2
client_run "scale-42" 1 4 $client_host1 $mw_ip1 &
client_run "scale-42" 2 4 $client_host2 $mw_ip2 &
client_run "scale-42" 3 4 $client_host3 $mw_ip3 &
client_run "scale-42" 4 6 $client_host4 $mw_ip4 


echo "run scale 41"
setup_db
middleware_run "scale-41" 1 $mw_host1 1
middleware_run "scale-41" 2 $mw_host2 1
middleware_run "scale-41" 3 $mw_host3 1
middleware_run "scale-41" 4 $mw_host4 1
client_run "scale-41" 1 4 $client_host1 $mw_ip1 &
client_run "scale-41" 2 4 $client_host2 $mw_ip2 &
client_run "scale-41" 3 4 $client_host3 $mw_ip3 &
client_run "scale-41" 4 6 $client_host4 $mw_ip4 

exit 0


# echo "run scale 35"
# setup_db
# middleware_run "scale-35" 1 $mw_host1 16
# middleware_run "scale-35" 2 $mw_host2 16
# middleware_run "scale-35" 3 $mw_host3 16
# client_run "scale-35" 1 6 $client_host1 $mw_ip1 &
# client_run "scale-35" 2 6 $client_host2 $mw_ip2 &
# client_run "scale-35" 3 6 $client_host3 $mw_ip3


# echo "run scale 34"
# setup_db
# middleware_run "scale-34" 1 $mw_host1 8
# middleware_run "scale-34" 2 $mw_host2 8
# middleware_run "scale-34" 3 $mw_host3 8
# client_run "scale-34" 1 6 $client_host1 $mw_ip1 &
# client_run "scale-34" 2 6 $client_host2 $mw_ip2 &
# client_run "scale-34" 3 6 $client_host3 $mw_ip3


# echo "run scale 33"
# setup_db
# middleware_run "scale-33" 1 $mw_host1 4
# middleware_run "scale-33" 2 $mw_host2 4
# middleware_run "scale-33" 3 $mw_host3 4
# client_run "scale-33" 1 6 $client_host1 $mw_ip1 &
# client_run "scale-33" 2 6 $client_host2 $mw_ip2 &
# client_run "scale-33" 3 6 $client_host3 $mw_ip3


# echo "run scale 32"
# setup_db
# middleware_run "scale-32" 1 $mw_host1 2
# middleware_run "scale-32" 2 $mw_host2 2
# middleware_run "scale-32" 3 $mw_host3 2
# client_run "scale-32" 1 6 $client_host1 $mw_ip1 &
# client_run "scale-32" 2 6 $client_host2 $mw_ip2 &
# client_run "scale-32" 3 6 $client_host3 $mw_ip3


# echo "run scale 31"
# setup_db
# middleware_run "scale-31" 1 $mw_host1 1
# middleware_run "scale-31" 2 $mw_host2 1
# middleware_run "scale-31" 3 $mw_host3 1
# client_run "scale-31" 1 6 $client_host1 $mw_ip1 &
# client_run "scale-31" 2 6 $client_host2 $mw_ip2 &
# client_run "scale-31" 3 6 $client_host3 $mw_ip3

# exit 0


# echo "run scale 25"
# setup_db
# middleware_run "scale-25" 1 $mw_host1 16
# middleware_run "scale-25" 2 $mw_host2 16
# client_run "scale-25" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-25" 2 9 $client_host2 $mw_ip2


# echo "run scale 24"
# setup_db
# middleware_run "scale-24" 1 $mw_host1 8
# middleware_run "scale-24" 2 $mw_host2 8
# client_run "scale-24" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-24" 2 9 $client_host2 $mw_ip2


# echo "run scale 23"
# setup_db
# middleware_run "scale-23" 1 $mw_host1 4
# middleware_run "scale-23" 2 $mw_host2 4
# client_run "scale-23" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-23" 2 9 $client_host2 $mw_ip2


# echo "run scale 22"
# setup_db
# middleware_run "scale-22" 1 $mw_host1 2
# middleware_run "scale-22" 2 $mw_host2 2
# client_run "scale-22" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-22" 2 9 $client_host2 $mw_ip2


# echo "run scale 21"
# setup_db
# middleware_run "scale-21" 1 $mw_host1 1
# middleware_run "scale-21" 2 $mw_host2 1
# client_run "scale-21" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-21" 2 9 $client_host2 $mw_ip2

# exit 0



# echo "run scale 15"
# setup_db
# middleware_run "scale-15" 1 $mw_host1 16
# client_run "scale-15" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-15" 2 9 $client_host2 $mw_ip1 


# echo "run scale 14"
# setup_db
# middleware_run "scale-14" 1 $mw_host1 8
# client_run "scale-14" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-14" 2 9 $client_host2 $mw_ip1 


# echo "run scale 13"
# setup_db
# middleware_run "scale-13" 1 $mw_host1 4
# client_run "scale-13" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-13" 2 9 $client_host2 $mw_ip1 


# echo "run scale 12"
# setup_db
# middleware_run "scale-12" 1 $mw_host1 2
# client_run "scale-12" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-12" 2 9 $client_host2 $mw_ip1 


# echo "run scale 11"
# setup_db
# middleware_run "scale-11" 1 $mw_host1 1
# client_run "scale-11" 1 9 $client_host1 $mw_ip1 &
# client_run "scale-11" 2 9 $client_host2 $mw_ip1 

# exit 0 

