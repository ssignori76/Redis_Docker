# Redis_Docker Host Network
Launch these commands before the stack deploys:  

docker node update --label-add "type=master" manager1  
docker node update --label-add "type=slave1" manager2  
docker node update --label-add "type=slave2" manager3  

Change manager1/2/3 with the node name  
