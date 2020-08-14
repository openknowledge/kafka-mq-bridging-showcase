# Kafka/MQ Bridging Showcase - Kafka MQ Sink

The showcase demonstrates bridging messages from Apache Kafka to IBM MQ

**Notable Features:**
* Apache Kafka broker
* IBM MQ broker
* Kafka connector `kafka-connect-mq-sink` 
* Integration of MP Reactive Messaging 
* Integration of JMS Message Driven Bean 

## How to run

#### Step 1: Create docker images 

Software requirements to run the sample are `maven`, `openjdk-8` (or any other JDK 8) and `docker`. 
When running the Maven lifecycle it will create the war packages and use the `liberty-maven-plugin` to create a runnable JARs (fat JAR) 
which contains the application and the Open Liberty application server. The fat JARs will be copied into a Docker images using Spotify's 
`dockerfile-maven-plugin` during the package phase.

Before running the application it needs to be compiled and packaged using `Maven`. It creates the runnable JARs and Docker images.

```shell script
$ mvn clean package
```
 
#### Step 2: Start docker images

After creating the docker images you can start the containers. The `docker-compose.yml` file defines the containers required to run the 
showcase.  

* the Apache Zookeeper application provided by Confluent Inc.
* the Apache Kafka broker provided by Confluent Inc.
* the custom application `kafka-topic-producer` which send messages to the Kafka topic
* the Kafka connector `kafka-connect-mq-sink` which connects Apache Kafka as a sink for IBM MQ 
* the IBM MQ broker provided by IBM
* the custom application `mq-queue-consumer` which consumes messages from a JMS queue

To start the containers you have to run `docker-compose`:

```shell script
$ docker-compose up
```

#### Step 3: Configure Kafka connector

When both brokers and the Kafka connector has been started successfully, you have to set up the connection between the Kafka broker 
(source) and the MQ broker (sink). 

To setup you have to run the following request with payload below:
```shell script
curl -X POST -H "Content-Type: application/json" -d '{...}' http://localhost:8083/connector
```

Payload
```json
{
    "name": "IbmMqSinkConnector",
    "config": {
        "connector.class": "com.ibm.eventstreams.connect.mqsink.MQSinkConnector",
        "tasks.max": "1",
        "topics": "DEV.TO.MQ",
        "mq.channel.name": "DEV.ADMIN.SVRCONN",
        "mq.connection.name.list": "mq-broker",
        "mq.port": "1414",
        "mq.queue.manager": "QM1",
        "mq.transport.type": "client",
        "mq.queue": "DEV.FROM.KAFKA.QUEUE",
        "mq.user.name": "admin",
        "mq.password": "passw0rd",
        "mq.message.builder": "com.ibm.eventstreams.connect.mqsink.builders.DefaultMessageBuilder",
        "schemas.enable": false,
        "jms.destination.type": "queue",
        "jms.destination.name": "DEV.QUEUE.1",
        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
        "value.converter": "org.apache.kafka.connect.storage.StringConverter",
        "confluent.topic.replication.factor": "1",
        "confluent.topic.bootstrap.servers": "kafka:9092"
    }
}
``` 

### Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep <id of the container>
```

If there are containers remaining although the application has been stopped you can remove them:

```shell script
$ docker rm <ids of the containers>
```
