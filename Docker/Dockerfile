FROM    --platform=linux/amd64 ubuntu:latest
RUN     apt update
RUN     apt install iproute2 -y
RUN     apt install telnet -y
RUN     apt install iputils-ping -y
RUN     apt install dnsutils -y
RUN     apt install default-jdk -y     
RUN     mkdir -p /java_utils/Redis
COPY    SentinelClient.jar /java_utils/Redis
CMD     ["sleep", "infinity"]
