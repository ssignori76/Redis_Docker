# Redis Sentinel on Docker Swarm for external clients' connection (clients external the docker cluster) 
Redis Sentinel on Docker Swarm with 3 nodes:  
- 1 redis master  
- 2 redis replicas  
- 3 redis sentinel  
  
The network is configured in host mode, to permit connections to Sentinel instances from the client external to the Docker Swarm cluster.  
The Redis instances are configured with constraints to run on a specific node.
This configuration avoids configuring a shared volume to manage consistency (RDB or AOF).
  
# Prepare the environment  
*Create the node labels*  
docker node update --label-add "type=master" node1_name   
docker node update --label-add "type=slave1" node2_name   
docker node update --label-add "type=slave2" node3_name
  
*Deploy the stack*  
docker stack deploy your_stackname -c ./your_file.yaml  
  
*Verify the running state of the services*  
docker stack ps your_stackname 
  
# TEST REDIS  
*Connect to Redis Master/Slave containers*  
docker exec -it <'container_ID'> /bin/sh  
  
*Verify that the master is connected with 2 replicas"  
redis-cli  
auth 7cBEcwf6mV36Rx3S  
info replication  
/*  
The output should contain:  
  role:master  
  connected_slaves:2  
*/  

# Test Sentinel  
*Connect to a Sentinel container*  
redis-cli -p 26379 sentinel masters    --> show detailed info about the master  
redis-cli -p 26379 SENTINEL GET-MASTER-ADDR-BY-NAME redis-cache    --> show the IP of the master  
redis-cli -p 26379 SENTINEL replicas redis-cache   --> show the replicas of the master   
redis-cli -p 26379 SENTINEL sentinels redis-cache  --> show the other sentinels in the network  

*To TEST the connectivity of the others Sentinels instances you could repeat the commands with the -h parameter, for example:*  
redis-cli -h hostIP -p 26379 sentinel masters    
redis-cli -h containerIP -p 26379 sentinel masters   
  
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
  

