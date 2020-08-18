# Kafka/MQ Bridging Showcase

**[German translation](README_de.md)**

The showcase consists of two independent parts demonstrating bridging messages from Apache Kafka to IBM MQ and vice versa.

The [kafka-mq-sink](kafka-mq-sink/README.md) showcase shows how to connect a Kafka broker as a sink for a MQ broker by using the Kafka 
connector [kafka-connect-mq-sink](https://github.com/ibm-messaging/kafka-connect-mq-sink). Therefore a custom Kafka 
producer application and a custom MQ queue consumer application are provided.

The [kafka-mq-source](kafka-mq-source/README.md) showcase shows how to connect a MQ broker as a source for a Kafka broker by using the Kafka 
connector [kafka-connect-mq-source](https://github.com/ibm-messaging/kafka-connect-mq-source). Therefore a custom MQ queue producer 
application and a custom Kafka consumer application are provided.

## Kafka Connect

Kafka Connect is a tool for scalably and reliably streaming data between Apache Kafka and other systems. It makes it simple to quickly 
define connectors that move large collections of data into and out of Kafka. 

In general you can distinguish between two types of connectors - source and sink. A **source connector** ingests entire databases and 
streams table updates to Kafka topics. It can also collect metrics from all of your application servers and store these in Kafka topics, 
making the data available for stream processing with low latency. A **sink connector** delivers data from Kafka topics into secondary 
indexes such as Elasticsearch, or batch systems such as Hadoop for offline analysis.

Kafka Connect specializes in copying data into and out of Kafka. At a high level, a connector is a job that manages tasks and their 
configuration. Under the covers, Kafka Connect creates fault-tolerant Kafka producers and consumers, tracking the offsets for the Kafka 
records they’ve written or read.

Beyond that, Kafka connectors provide a number of powerful features. They can be easily configured to route unprocessable or invalid 
messages to a dead letter queue, apply Single Message Transforms before a message is written to Kafka by a source connector or before it is
consumed from Kafka by a sink connector, integrate with Confluent Schema Registry for automatic schema registration and management, and 
convert data into types such as Avro or JSON. 

By leveraging existing connectors developers can quickly create fault-tolerant data pipelines that reliably stream data from an external 
source into records in Kafka topics or from Kafka topics into an external sink, all with mere configuration and no code!

Each connector instance can break down its job into multiple tasks, thereby parallelizing the work of copying data and providing 
scalability. When a connector instance starts up a task, it passes along the configuration properties that each task will need. The task 
stores this configuration—as well as the status and the latest offsets for the records it has produced or consumed—externally in Kafka 
topics. Since the task does not store any state, tasks can be stopped, started, or restarted at any time. Newly started tasks will simply 
pick up the latest offsets from Kafka and continue on their merry way.

![Kafka Connect](https://cdn.confluent.io/wp-content/uploads/kafka-connect-2.png)

Kafka Connect can be deployed either as a standalone process that runs jobs on a single machine (for example, log collection), or as a 
distributed, scalable, fault-tolerant service supporting an entire organization. In standalone mode, Kafka Connect runs on a single 
worker⏤that is, a running JVM process that executes the connector and its tasks. In distributed mode, connectors and their tasks are 
balanced across multiple workers. The general recommendation is to run Kafka Connect in distributed mode, as standalone mode does not 
provide fault tolerance.

To start a connector in distributed mode, send a POST request to the Kafka Connect REST API. This request triggers Kafka Connect to 
automatically schedule the execution of the connectors and tasks across multiple workers. In the instance that a worker goes down or is 
added to the group, the workers will automatically coordinate to rebalance the connectors and tasks amongst themselves.

More information about Kafka Connect can be found [here](http://kafka.apache.org/documentation.html#connect), 
[here](https://docs.confluent.io/current/connect/index.html) and 
[here](https://www.confluent.jp/blog/create-dynamic-kafka-connect-source-connectors/).


#### Run the Showcase

You can find the instuctions to run the source showcase [here](./kafka-mq-source/README.md) and the sink showcase [here](./kafka-mq-sink/README.md)


### Kafka Connect sink/source connector for IBM MQ

IBM provides two connectors to copy data from IBM Event Streams or Apache Kafka to IBM MQ and vice versa.   

* `kafka-connect-mq-sink` is a Kafka Connect sink connector for copying data from Apache Kafka into IBM MQ. The connector is supplied as 
source code on [GitHub](https://github.com/ibm-messaging/kafka-connect-mq-sink) which can easily build into a runnable JAR file or a Docker
image.
* `kafka-connect-mq-source` is a Kafka Connect source connector for copying data from IBM MQ into Apache Kafka. The connector is supplied as
source code on [GitHub](https://github.com/ibm-messaging/kafka-connect-mq-source) which can easily build into a runnable JAR file or a 
Docker image.

Additional information and about integrating the Kafka Connect sink/source connector can be found here ... 
* [Apache Kafka, IBM MQ and z/OS](https://community.ibm.com/community/user/imwuc/viewdocument/kafka-connectors-for-ibm-mq-a-mq)
* [IBM MQ and IBM Event Streams](https://medium.com/@khongks/making-ibm-mq-talking-to-kafka-ibm-event-stream-7d57368402e1)
