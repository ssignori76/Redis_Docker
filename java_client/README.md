# JAVA Client
It uses the lettuce library: https://lettuce.io/core/release/reference/  
The client executes the following spet:
a) connect to the Master via Sentinel 
b) launch info replication
c) SET a key
d) GET a key

The JAR directory contains the client compiled to test Redis in SWARM mode (no host network).
The client could be executed with the command "watch -n 1 java -jar <filename.jar> to create a loop.
