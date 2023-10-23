# Redis Sentinel on Docker Swarm for external clients' connection  
Redis Sentinel on Docker Swarm with 3 nodes:  
- 1 redis master  
- 2 redis replicas  
- 3 redis sentinel  
  
The network is configured in host mode, to permit connections to Sentinel instances from the client external to the Docker Swarm cluster.  
  
# Prepare the environment  
*Create the node labels*  
docker node update --label-add "type=master" <docker node1 name>   
docker node update --label-add "type=slave1" <docker node2 name>   
docker node update --label-add "type=slave2" <docker node3 name>   
  
*Deploy the stack*  
docker stack deploy <stack name> -c ./<yml file>  
  
*Verify the running state of the services*  
docker stack ps <stack name> 
  
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
  

