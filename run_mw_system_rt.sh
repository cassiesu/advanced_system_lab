##
# Usage: ./run_mw_system_rt.sh key/asl.pem <db-ip> <mw-ips> <client-ips>
# 
##

key=$1
db_host=ubuntu@$2
db_ip=$2
mw_host1=ubuntu@$3
mw_ip1=$3
mw_host2=ubuntu@$4
mw_ip2=$4
# mw_host3=ubuntu@$5
# mw_ip3=$5
# mw_host4=ubuntu@$6
# mw_ip4=$6
client_host1=ubuntu@$7
client_host2=ubuntu@$8
# client_host3=ubuntu@$9
# client_host4=ubuntu@${10}


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
    ssh -i $key $4 "cd ~/asl && ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dhost $5\
            run-system-bench > ~/client-system-scale-$1-$2.log"
}

function client_run_msg {
    echo "-------------------------------"
    echo "Start Client Instance With `expr 4 \* $3` Parallel Threads On Machine $2"
    ssh -i $key $4 "killall java"
    ssh -i $key $4 "cd ~/asl && ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dhost $5 -Dtextlength $6\
            run-system-bench > ~/client-system-scale-$1-$2.log"
}

function client_run_think {
    echo "-------------------------------"
    echo "Start Client Instance With `expr 4 \* $3` Parallel Threads On Machine $2"
    ssh -i $key $4 "killall java"
    ssh -i $key $4 "cd ~/asl && ant -Dbenchmark $1 -Dmachine $2 -Dparallel $3 -Dhost $5 -Dthink $6\
            run-system-bench > ~/client-system-scale-$1-$2.log"
}

function middleware_run {
    echo "-------------------------------"
    echo "Start Middleware Instance On Machine $2"
    ssh -i $key $3 "killall java"
    ssh -f -i $key $3 "cd ~/asl && \
        ant run-middleware -Dbenchmark $1 -Dmachine $2 -Ddbip $db_ip > ~/server-system-scale-$1-$2.log"
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

    # echo "-------------------------------"
    # echo "$mw_host3 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host3:~/asl

    # echo "-------------------------------"
    # echo "$mw_host4 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $mw_host4:~/asl


    echo "-------------------------------"
    echo "$client_host1 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host1:~/asl

    echo "-------------------------------"
    echo "$client_host2 File Syncing"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host2:~/asl

    # echo "-------------------------------"
    # echo "$client_host3 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host3:~/asl

    # echo "-------------------------------"
    # echo "$client_host4 File Syncing"
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" src lib build.xml dbsetup logger.properties $client_host4:~/asl

}



file_sync

# Different Number of Middleware Nodes

# echo "run mw 4"
# setup_db
# middleware_run "mw-4" 1 $mw_host1
# middleware_run "mw-4" 2 $mw_host2
# middleware_run "mw-4" 3 $mw_host3
# middleware_run "mw-4" 4 $mw_host4
# client_run "mw-4" 1 6 $client_host1 $mw_ip1 &
# client_run "mw-4" 2 6 $client_host2 $mw_ip2 &
# client_run "mw-4" 3 6 $client_host3 $mw_ip3 &
# client_run "mw-4" 4 6 $client_host4 $mw_ip4 


# echo "run mw 3"
# setup_db
# middleware_run "mw-3" 1 $mw_host1
# middleware_run "mw-3" 2 $mw_host2
# middleware_run "mw-3" 3 $mw_host3
# client_run "mw-3" 1 8 $client_host1 $mw_ip1 &
# client_run "mw-3" 2 8 $client_host2 $mw_ip2 &
# client_run "mw-3" 3 8 $client_host3 $mw_ip3


# echo "run mw 2"
# setup_db
# middleware_run "mw-2" 1 $mw_host1
# middleware_run "mw-2" 2 $mw_host2
# client_run "mw-2" 1 12 $client_host1 $mw_ip1 &
# client_run "mw-2" 2 12 $client_host2 $mw_ip2


# echo "run mw 1"
# setup_db
# middleware_run "mw-1" 1 $mw_host1
# client_run "mw-1" 1 12 $client_host1 $mw_ip1 &
# client_run "mw-1" 2 12 $client_host2 $mw_ip1 

# exit 0 


# Different Sizes of Message

# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db 10
# middleware_run "msg-10" 1 $mw_host1
# middleware_run "msg-10" 2 $mw_host2
# client_run_msg "msg-10" 1 8 $client_host1 $mw_ip1 10&
# client_run_msg "msg-10" 2 8 $client_host2 $mw_ip2 10


# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db 100
# middleware_run "msg-100" 1 $mw_host1
# middleware_run "msg-100" 2 $mw_host2
# client_run_msg "msg-100" 1 8 $client_host1 $mw_ip1 100 &
# client_run_msg "msg-100" 2 8 $client_host2 $mw_ip2 100


# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db 200
# middleware_run "msg-200" 1 $mw_host1
# middleware_run "msg-200" 2 $mw_host2
# client_run_msg "msg-200" 1 8 $client_host1 $mw_ip1 200 &
# client_run_msg "msg-200" 2 8 $client_host2 $mw_ip2 200

# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db 500
# middleware_run "msg-500" 1 $mw_host1
# middleware_run "msg-500" 2 $mw_host2
# client_run_msg "msg-500" 1 8 $client_host1 $mw_ip1 500 &
# client_run_msg "msg-500" 2 8 $client_host2 $mw_ip2 500

# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db 1000
# middleware_run "msg-1000" 1 $mw_host1
# middleware_run "msg-1000" 2 $mw_host2
# client_run_msg "msg-1000" 1 8 $client_host1 $mw_ip1 1000 &
# client_run_msg "msg-1000" 2 8 $client_host2 $mw_ip2 1000


# Different Think Time

# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db
# middleware_run "think-0" 1 $mw_host1
# middleware_run "think-0" 2 $mw_host2
# client_run_think "think-0" 1 8 $client_host1 $mw_ip1 0 &
# client_run_think "think-0" 2 8 $client_host2 $mw_ip2 0


# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db
# middleware_run "think-2" 1 $mw_host1
# middleware_run "think-2" 2 $mw_host2
# client_run_think "think-2" 1 8 $client_host1 $mw_ip1 2 &
# client_run_think "think-2" 2 8 $client_host2 $mw_ip2 2


# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db
# middleware_run "think-4" 1 $mw_host1
# middleware_run "think-4" 2 $mw_host2
# client_run_think "think-4" 1 8 $client_host1 $mw_ip1 4 &
# client_run_think "think-4" 2 8 $client_host2 $mw_ip2 4


# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db
# middleware_run "think-8" 1 $mw_host1
# middleware_run "think-8" 2 $mw_host2
# client_run_think "think-8" 1 8 $client_host1 $mw_ip1 8 &
# client_run_think "think-8" 2 8 $client_host2 $mw_ip2 8


# echo "Run System Bench Mixed Workload With `expr 8 \* 8` Parallel Threads"
# setup_db
# middleware_run "think-16" 1 $mw_host1
# middleware_run "think-16" 2 $mw_host2
# client_run_think "think-16" 1 8 $client_host1 $mw_ip1 16 &
# client_run_think "think-16" 2 8 $client_host2 $mw_ip2 16

# exit 0
