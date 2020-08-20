# Kafka/MQ Bridging Showcase - Kafka MQ Source

The showcase demonstrates how to connect a MQ broker as a source for a Kafka broker by using the Kafka connector 
[kafka-connect-mq-source](https://github.com/ibm-messaging/kafka-connect-mq-source). Therefore a custom MQ queue producer application and a 
custom Kafka consumer application are provided. To monitor the messages being bridged between the MQ broker and the Kafka broker, a 
[Jaeger](https://www.jaegertracing.io) server was added to the showcase. The traces are provided by 
[MP OpenTracing](https://microprofile.io/project/eclipse/microprofile-opentracing) which was added both to the producer and the consumer 
application.

**Notable Features:**
* Apache Kafka broker
* IBM MQ broker
* Kafka connector `kafka-connect-mq-source` 
* Integration of MP Reactive Messaging 
* Integration of JMS 
* Integration of MP OpenTracing

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
* the Jaeger tracing server

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

To test the bridge between MQ and Kafka the custom application `mq-queue-producer` provides a REST API that can be used to create and send 
your own messages. 

To send a custom message you have to send the following GET request:

```shell script
$ curl -X GET http://localhost:9080/mq-queue-producer/api/messages?msg=<custom message>
```

#### Step 5: Trace messages with OpenTracing and Jaeger

[OpenTracing](http://opentracing.io/) is a new, open distributed tracing standard for applications and. Developers with experience building 
microservices at scale understand the role and importance of distributed tracing: per-process logging and metric monitoring have their 
place, but neither can reconstruct the elaborate journeys that transactions take as they propagate across a distributed system. Distributed 
traces are these journeys.

The [MicroProfile OpenTracing](https://microprofile.io/project/eclipse/microprofile-opentracing) specification defines behaviors and an API
for accessing an OpenTracing compliant Tracer object within your application. The behaviors specify how incoming and outgoing requests will
have OpenTracing Spans automatically created. The API defines how to explicitly disable or enable tracing for given endpoints.

[Jaeger](https://www.jaegertracing.io) is a distributed tracing system released as open source by Uber Technologies. It is used for 
monitoring and troubleshooting microservices-based distributed systems, including distributed context propagation and transaction 
monitoring, root cause analysis, service dependency analysis and performance / latency optimization.

The Jaeger server provides an UI which can be accessed via http://localhost:16681/. 

As described, distributed tracing allows to trace the relationships between services in a distributed system. Sending a custom message by 
calling the REST API of the `mq-queue-consumer` (Step 4) which is consumed by the `kafka-consumer`, will affect the generation of at least 
two traces from which the Jaeger server generates a dependency graph. The generated graph shows a connection between these two applications.  
 

![traces](../docs/traces.png)

Further details on Opentracing for Java and Kafka can be found here:
* [jaeger-client](https://github.com/jaegertracing/jaeger-client-java)
* [java-kafka-client](https://github.com/opentracing-contrib/java-kafka-client)


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
