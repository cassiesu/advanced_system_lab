## 
# Usage: ./run_log_retrieve.sh key/asl.pem <mw-ips> <client-ips>
#
##

key=$1
mw_host1=ubuntu@$2
mw_host2=ubuntu@$3
# mw_host3=ubuntu@$4
# mw_host4=ubuntu@$5
client_host1=ubuntu@$6
client_host2=ubuntu@$7
# client_host3=ubuntu@$8
# client_host4=ubuntu@$9

function file_sync {
    
    echo "-------------------------------"
    echo "$mw_host1 File Retriving"
    # scp -i $key -rp $mw_host:~/* /Users/CassieSu/Downloads/mw_logs
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $mw_host1:~/* /Users/CassieSu/Downloads/mwlogs1 --exclude asl


    echo "-------------------------------"
    echo "$mw_host2 File Retriving"
    # scp -i $key -rp $mw_host:~/* /Users/CassieSu/Downloads/mw_logs
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $mw_host2:~/* /Users/CassieSu/Downloads/mwlogs2 --exclude asl


    # echo "-------------------------------"
    # echo "$mw_host3 File Retriving"
    # # scp -i $key -rp $mw_host:~/* /Users/CassieSu/Downloads/mw_logs
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $mw_host3:~/* /Users/CassieSu/Downloads/mwlogs3 --exclude asl

    # echo "-------------------------------"
    # echo "$mw_host4 File Retriving"
    # # scp -i $key -rp $mw_host:~/* /Users/CassieSu/Downloads/mw_logs
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $mw_host4:~/* /Users/CassieSu/Downloads/mwlogs4 --exclude asl


    echo "-------------------------------"
    echo "$client_host1 File Retriving"
    # scp -i $key -rp $client_host1:~/* /Users/CassieSu/Downloads/client1_logs
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $client_host1:~/* /Users/CassieSu/Downloads/clogs1 --exclude asl


    echo "-------------------------------"
    echo "$client_host2 File Retriving"
    rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $client_host2:~/* /Users/CassieSu/Downloads/clogs2 --exclude asl

    # echo "-------------------------------"
    # echo "$client_host3 File Retriving"
    # # scp -i $key -rp $client_host1:~/* /Users/CassieSu/Downloads/client1_logs
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $client_host3:~/* /Users/CassieSu/Downloads/clogs3 --exclude asl

    # echo "-------------------------------"
    # echo "$client_host4 File Retriving"
    # # scp -i $key -rp $client_host1:~/* /Users/CassieSu/Downloads/client1_logs
    # rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $key" $client_host4:~/* /Users/CassieSu/Downloads/clogs4 --exclude asl

}

function clear_data {
    ssh -i $key $mw_host1 "cd ~ && rm -rf !(asl)"
    ssh -i $key $mw_host2 "cd ~ && rm -rf !(asl)"
    # ssh -i $key $mw_host3 "rm -rf !(asl)"
    ssh -i $key $client_host1 "cd ~ && rm -rf !(asl)"
    ssh -i $key $client_host2 "cd ~ && rm -rf !(asl)"
    # ssh -i $key $client_host3 "rm -rf !(asl)"
}
 
file_sync

# clear_data

exit 0

