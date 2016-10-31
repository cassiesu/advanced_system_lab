##
# Usage: ./run_mw_system_2k.sh key/asl.pem <db-ip> <mw-ips> <client-ips>
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
client_host1=ubuntu@$6
client_host2=ubuntu@$7
client_host3=ubuntu@$8


function setup_db {
    echo "-------------------------------"
    echo "SetUp Database"
    ssh -i $key $db_host "sudo /etc/init.d/postgresql restart"
    ssh -i $key $db_host "cd ~/asl && ant teardown-db"
    ssh -i $key $db_host "dropdb -U postgres asl"
    ssh -i $key $db_host "createdb -U postgres asl"
    ssh -i $key $db_host "cd ~/asl && ant setup-db"
    ssh -i $key $db_host "cd ~/asl && ant -Ddbip localhost -Dtextlength $1 run-db-initialization"
}

function client_run {
    echo "-------------------------------"
    echo "Start Client Instance With `expr 4 \* $3` Parallel Threads On Machine $2"
    ssh -i $key $4 "killall java"
    ssh -i $key $4 "cd ~/asl && \
            ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dhost $5 -Dtextlength $6\
            run-system-bench > ~/client-system-2k-$1-$2.log"
}

function middleware_run {
    echo "-------------------------------"
    echo "Start Middleware Instance On Machine $2"
    ssh -i $key $3 "killall java"
    ssh -f -i $key $3 "cd ~/asl && ant run-middleware \
            -Dbenchmark $1 -Dmachine $2 -Ddbip $db_ip > ~/server-system-2k-$1-$2.log &"
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
    echo "$client_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host1:~/asl

    echo "-------------------------------"
    echo "$client_host2 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host2:~/asl

    echo "-------------------------------"
    echo "$client_host3 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host3:~/asl

}



file_sync


# # 2k-8: 24 clients, 3 middleware, message size 1000
# echo "-------------------------------"
# echo "run 2k-8"
# setup_db 1000
# middleware_run "2k-8" 1 $mw_host1
# middleware_run "2k-8" 2 $mw_host2
# middleware_run "2k-8" 3 $mw_host3
# client_run "2k-8" 1 2 $client_host1 $mw_ip1 1000 &
# client_run "2k-8" 2 2 $client_host2 $mw_ip2 1000 &
# client_run "2k-8" 3 2 $client_host3 $mw_ip3 1000

# # exit 0


# # 2k-6: 96 clients, 3 middleware, message size 1000
# echo "-------------------------------"
# echo "run 2k-6"
# setup_db 1000
# middleware_run "2k-6" 1 $mw_host1
# middleware_run "2k-6" 2 $mw_host2
# middleware_run "2k-6" 3 $mw_host3
# client_run "2k-6" 1 8 $client_host1 $mw_ip1 1000 &
# client_run "2k-6" 2 8 $client_host2 $mw_ip2 1000 &
# client_run "2k-6" 3 8 $client_host3 $mw_ip3 1000

# # exit 0


# # 2k-4: 24 clients, 1 middleware, message size 1000
# echo "-------------------------------"
# echo "run 2k-4"
# setup_db 1000
# middleware_run "2k-4" 1 $mw_host1
# client_run "2k-4" 1 3 $client_host1 $mw_ip1 1000 &
# client_run "2k-4" 2 3 $client_host2 $mw_ip1 1000

# # exit 0


# # 2k-2: 96 clients, 1 middleware, message size 1000
# echo "-------------------------------"
# echo "run 2k-2"
# setup_db 1000
# middleware_run "2k-2" 1 $mw_host1
# client_run "2k-2" 1 12 $client_host1 $mw_ip1 1000 &
# client_run "2k-2" 2 12 $client_host2 $mw_ip1 1000

# exit 0



# 2k-7: 24 clients, 3 middleware, message size 10
echo "-------------------------------"
echo "run 2k-7"
setup_db 10
middleware_run "2k-7" 1 $mw_host1 
middleware_run "2k-7" 2 $mw_host2 
middleware_run "2k-7" 3 $mw_host3 
client_run "2k-7" 1 2 $client_host1 $mw_ip1 10 &
client_run "2k-7" 2 2 $client_host2 $mw_ip2 10 &
client_run "2k-7" 3 2 $client_host3 $mw_ip3 10

# exit 0


# 2k-5: 96 clients, 3 middleware, message size 10
echo "-------------------------------"
echo "run 2k-5"
setup_db 10
middleware_run "2k-5" 1 $mw_host1
middleware_run "2k-5" 2 $mw_host2
middleware_run "2k-5" 3 $mw_host3
client_run "2k-5" 1 8 $client_host1 $mw_ip1 10 &
client_run "2k-5" 2 8 $client_host2 $mw_ip2 10 &
client_run "2k-5" 3 8 $client_host3 $mw_ip3 10

# exit 0


# 2k-3: 24 clients, 1 middleware, message size 10
echo "-------------------------------"
echo "run 2k-3"
setup_db 10
middleware_run "2k-3" 1 $mw_host1
client_run "2k-3" 1 3 $client_host1 $mw_ip1 10 &
client_run "2k-3" 2 3 $client_host2 $mw_ip1 10

# exit 0


# 2k-1: 96 clients, 1 middleware, message size 10
echo "-------------------------------"
echo "run 2k-1"
setup_db 10
middleware_run "2k-1" 1 $mw_host1
client_run "2k-1" 1 12 $client_host1 $mw_ip1 10&
client_run "2k-1" 2 12 $client_host2 $mw_ip1 10

exit 0



