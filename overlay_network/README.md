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
chown test:test master slave1 slave2

*Create the shared SSHFS Volume*
docker plugin install --grant-all-permissions vieux/sshfs
docker volume create -d vieux/sshfs -o sshcmd=test@192.168.0.8:/home/test/master -o allow_other -o password=testpassword mastervolume
docker volume create -d vieux/sshfs -o sshcmd=test@192.168.0.8:/home/test/slave1 -o allow_other -o password=testpassword slave1volume
docker volume create -d vieux/sshfs -o sshcmd=test@192.168.0.8:/home/test/slave2 -o allow_other -o password=testpassword slave2volume

*Test the SSHFS Volume (check the somefile file in the test $HOME subdirectory)*
docker run -it -v mastervolume:/data alpine sh -c 'echo "Hello world" > /data/somefile'
docker run -it -v slave1volume:/data alpine sh -c 'echo "Hello world" > /data/somefile'
docker run -it -v slave2volume:/data alpine sh -c 'echo "Hello world" > /data/somefile'

*Deploy the stack*  
docker stack deploy <stack name> -c ./<yml file>  
  
*Verify the running state of the services*  
docker stack ps <stack name> 

# TEST NETWORK
docker run --name lab --network redis -v /root:/test -it ubuntu /bin/sh
apt update
apt install iputils-ping
ping <hostname> (from every container ping each other hostname)
  
# TEST REDIS  
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
  
# TEST REPLICAS  
*Connect to Redis Master container and set a key*  
redis-cli  
auth 7cBEcwf6mV36Rx3S  
SET testkey "Hello_1"  
  
*Connect to Redis Replica*  
redis-cli  
auth 7cBEcwf6mV36Rx3S  
GET testkey  
  
# PERFORMANCE TEST  
redis-benchmark -q -n 100000 -a 7cBEcwf6mV36Rx3S  
https://redis.io/docs/management/optimization/benchmarks/  
  
