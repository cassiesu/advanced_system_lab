##
# Usage: ./run_setup_db.sh key/asl.pem <db public ip>
#
##

db_host=ubuntu@$2

echo "-------------------------------"
echo "File Syncing"
rsync -avz -e "ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -i $1" src lib build.xml dbsetup logger.properties $db_host:~/asl

echo "-------------------------------"
echo "Postgres Restart"
ssh -i $1 $db_host "sudo /etc/init.d/postgresql restart"

echo "-------------------------------"
echo "Drop Old Database asl"
ssh -i $1 $db_host "dropdb -U postgres asl"

echo "-------------------------------"
echo "Create New Database asl"
ssh -i $1 $db_host "createdb -U postgres asl"

echo "-------------------------------"
echo "Setup Database asl"
ssh -i $1 $db_host "cd ~/asl && ant setup-db"

echo "-------------------------------"
echo "Setup Database Initial Data"
ssh -i $1 $db_host "cd ~/asl && ant -Ddbip localhost run-db-initialization"
