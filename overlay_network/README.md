# Redis Sentinel on Docker Swarm for clients inside the cluster, connected to SWARM network  
Redis Sentinel on Docker Swarm:  
- 1 redis master  
- 2 redis replicas  
- 3 redis sentinel  
  
Every instance of Redis has its shared volume, for managing the consistency (RDB and AOF). 
  
# Prepare the environment  
*Create Overlay Network*  
docker network create -d overlay redis --attachable=true  

*Create the user for SSHFS (choose a node)*  
adduser -D test  
echo "test:testpassword" | chpasswd  
mkdir /home/test/master  
mkdir /home/test/slave1  
mkdir /home/test/slave2  
chown test:test /home/test/master /home/test/slave1 /home/test/slave2  

*Create the shared SSHFS Volume*  
docker plugin install --grant-all-permissions vieux/sshfs  
docker volume create -d vieux/sshfs -o sshcmd=test@<Host IP with test user>:/home/test/master -o allow_other -o password=testpassword mastervolume  
docker volume create -d vieux/sshfs -o sshcmd=test@<Host IP with test user>:/home/test/slave1 -o allow_other -o password=testpassword slave1volume  
docker volume create -d vieux/sshfs -o sshcmd=test@<Host IP with test user>:/home/test/slave2 -o allow_other -o password=testpassword slave2volume  

*Test the SSHFS Volume (check the somefile file in the test $HOME subdirectory)*  
docker run -it -v mastervolume:/data alpine sh -c 'echo "Hello world" > /data/somefile'  
docker run -it -v slave1volume:/data alpine sh -c 'echo "Hello world" > /data/somefile'  
docker run -it -v slave2volume:/data alpine sh -c 'echo "Hello world" > /data/somefile'  

*Deploy the Stack Yaml*    
docker stack deploy <stack name> -c ./<yml file>    
  
*Verify the running state of the services*  
docker stack ps <stack name> 

# Test Network  
docker run --name lab --network redis -v /root:/test -it lusig76/lab /bin/sh
apt update  
apt install iputils-ping  
ping <hostname> (from every container ping each other hostname)  
  
# Test Redis  
*Connect to Redis Master/Slave containers*  
docker exec -it <container_ID> /bin/sh  
  
*Verify that the master is connected with 2 replicas"  
redis-cli  
auth 7cBEcwf6mV36Rx3S  
info replication  
/*  
The output should contain:  
  role:master  
  connected_slaves:2  
*/  

# Test Sentinel HA
docker run --name lab --network redis -v /root:/test -it lusig76/lab /bin/sh  
/java_utils/Redis  
watch -n 1 java -jar ./SentinelClient.jar  

Pause and unpause Master and Slave Containers to test the HA.  

# Test Sentinel  
*Connect to a Sentinel container*  
redis-cli -p 26379 sentinel masters    --> show detailed info about the master  
redis-cli -p 26379 SENTINEL GET-MASTER-ADDR-BY-NAME redis-cache    --> show the IP of the master  
redis-cli -p 26379 SENTINEL replicas redis-cache   --> show the replicas of the master   
redis-cli -p 26379 SENTINEL sentinels redis-cache  --> show the other sentinels in the network  

*To TEST the connectivity of the others Sentinels instances you could repeat the commands with the -h parameter, for example:*  
redis-cli -h hostIP -p 26379 sentinel masters    
redis-cli -h containerIP -p 26379 sentinel masters    
  
# Test Replication  
*Connect to Redis Master container and set a key*  
redis-cli  
auth 7cBEcwf6mV36Rx3S  
SET testkey "Hello_1"  
  
*Connect to Redis Replica*  
redis-cli  
auth 7cBEcwf6mV36Rx3S  
GET testkey  
  
# Performance Test  
redis-benchmark -q -n 100000 -a 7cBEcwf6mV36Rx3S  
https://redis.io/docs/management/optimization/benchmarks/  
  
