# Redis_Docker Host Network
Launch this commands before the stack deploy:

docker node update --label-add "type=master" manager1
docker node update --label-add "type=slave1" manager2
docker node update --label-add "type=slave2" manager3

change manager1/2/3 with the node name
