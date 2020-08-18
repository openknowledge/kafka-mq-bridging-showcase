# Kafka/MQ Bridging Showcase - Kafka MQ Source

Dieser Showcase zeigt, wie Nachrichten von IBM MQ nach Apache Kafka weitergeleitet werden.

**relevante Features:**
* Apache Kafka Broker
* IBM MQ Broker
* Kafka Konnektor `kafka-connect-mq-source` 
* Integration von MP Reactive Messaging 
* Integration von JMS Message Driven Bean 

## Showcase starten

#### Schritt 1: docker images erstellen 

Benötigte Software:
* `maven`
* `openjdk-8` (oder andere JDK 8)
* `docker`

Wenn man das Maven Lifecycle `package` ausführt, dann wird neben dem _war_-Archiv, mit Hilde des `liberty-maven-plugin` auch eine lauffähige JAR erstellt, welche die Anwendung und den Applition Server enthält. 
Das Spotify `dockerfile-maven-plugin` erstellt danach ein docker image.

Dazu muss man folgenden Befehl ausführen:
```shell script
$ mvn clean package
```
 
#### Step 2: Start docker images

Nachdem die docker images erstellt wurden, kann man die container auch starten. Die `docker-compose.yml` enthält alle benötigten container um das Showcase zu starten. 

* Apache Zookeeper von Confluent Inc.
* Apache Kafka Broker von Confluent Inc.
* EE Anwendung `kafka-topic-consumer`, welcher aus einen Kafka Topic liest
* Kafka Konnektor `kafka-connect-mq-source`, welcher IBM MQ als Quelle für Apache Kafka bereitstellt 
* IBM MQ Broker von IBM
* EE Anwendung `mq-queue-producer`, welcher Nachrichten in eine JMS Queue schreibt

Um die Container zu starten muss man folgenden Befehl ausführen:

```shell script
$ docker-compose up
```

#### Schritt 3: Kafka connector konfigurieren

Sobald die Broker und der Kafka Konnektor gestartet sind, so kann man die Verbindung zwischen dem MQ Broker(source) und dem Kafka Broker(sink) aufstellen.

Folgender HTTP Request mit dem Payload muss an den Kafka Konnektor geschickt werden:

```shell script
curl -X POST -H "Content-Type: application/json" -d '{...}' http://localhost:8083/connector
```

Payload
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

### Fehler beheben

Manchmal werden die Container nicht korrekt beended.
Man kann diese auch manuell beenden. Dazu braucht man erst die Container id:

```shell script
$ docker ps -a
```

Mit folgendem Befehl kann man den Container entfernen:

```shell script
$ docker rm <ids vom container>
```
