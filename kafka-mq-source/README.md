# Kafka/MQ Bridging Showcase - Kafka MQ Source

The showcase demonstrates how to connect a MQ broker as a source for a Kafka broker by using the Kafka connector 
[kafka-connect-mq-source](https://github.com/ibm-messaging/kafka-connect-mq-source). Therefore a custom MQ queue producer application and a 
custom Kafka consumer application are provided.

**Notable Features:**
* Apache Kafka broker
* IBM MQ broker
* Kafka connector `kafka-connect-mq-source` 
* Integration of MP Reactive Messaging 
* Integration of JMS 

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
* the custom Java EE application `kafka-consumer` which consumes messages from the Kafka topic
* the Kafka connector `kafka-connect-mq-source` which connects IBM MQ as a source for Apache Kafka 
* the IBM MQ broker provided by IBM
* the custom Java EE application `mq-queue-producer` which send messages to the JMS queue

To start the containers you have to run `docker-compose`:

```shell script
$ docker-compose up
```

#### Step 3: Configure the Kafka connector

When both brokers and the Kafka connector has been started successfully, you have to set up the connection between the MQ broker (source)
and the Kafka broker (sink). 

To setup you have to run the following request with payload below:
```shell script
$ curl -s -X POST -H "Content-Type: application/json" --data @kafka-connect-mq-source-config.json http://localhost:8083/connector
```

[kafka-connect-mq-source-config.json](kafka-connect-mq-source-config.json)
```json
{
  "name": "IbmMqSourceConnector",
  "config": {
    "connector.class": "com.ibm.eventstreams.connect.mqsource.MQSourceConnector",
    "tasks.max": "1",
    "topic": "DEV.FROM.MQ",
    "mq.channel.name": "DEV.ADMIN.SVRCONN",
    "mq.connection.name.list": "mq-broker(1414)",
    "mq.queue.manager": "QM1",
    "mq.transport.type": "client",
    "mq.queue": "DEV.TO.KAFKA.QUEUE",
    "mq.user.name": "admin",
    "mq.password": "passw0rd",
    "mq.message.body.jms": "true",
    "mq.record.builder": "com.ibm.eventstreams.connect.mqsource.builders.DefaultRecordBuilder",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.storage.StringConverter",
    "confluent.topic.replication.factor": "1",
    "confluent.topic.bootstrap.servers": "kafka:9092"
  }
}
``` 

#### Step 4: Produce and consume messages

There are two ways to test the bridge between MQ and Kafka. 

1) The custom application `mq-queue-producer` contains a message generator that generates and sends a new message every two seconds. The 
receipt and successful processing of the message can be traced in the log output of the custom application `kafka-consumer`.

2) In addition to the message generator, the application provides a REST API that can be used to create and send your own messages. 

To send a custom message you have to send the following GET request:

```shell script
$ curl -X GET http://localhost:9080/mq-queue-producer/api/messages?msg=<custom message>
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
