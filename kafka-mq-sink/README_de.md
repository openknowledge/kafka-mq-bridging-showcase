# Kafka/MQ Bridging Showcase - Kafka MQ Sink

Der Showcase zeigt, wie ein Kafka Broker mit Hilfe eines Kafka Konnektors 
[kafka-connect-mq-sink](https://github.com/ibm-messaging/kafka-connect-mq-sink) als Datensenke mit einem MQ Broker verbunden wird. Hierzu 
wird neben den beiden Brokern und dem Connector ein eigener Kafka Produzent und ein eigener MQ Queue Konsument bereitgestellt.

**relevante Features:**
* Apache Kafka Broker
* IBM MQ Broker
* Kafka Konnektor `kafka-connect-mq-sink` 
* Integration von MP Reactive Messaging 
* Integration von JMS Message Driven Bean 


## Showcase ausführen

#### Schritt 1: Docker Images erstellen 

Benötigte Software:
* `maven`
* `openjdk-8` (oder andere JDK 8)
* `docker`

Führt man die Phase `package` des Maven Lifecycle aus, wird neben dem _war_-Archiv, durch das `liberty-maven-plugin` auch ein ausführbare 
JAR Dateien erstellt, welches sowohl die Anwendung als auch den Applition Server enthalten. Direkt im Anschluss wird jede JAR Datei mit 
Hilfe von Spotify's `dockerfile-maven-plugin` in ein eigenes Docker Image kopiert und bereitgestellt.

Dazu muss man folgenden Befehl ausführen:

```shell script
$ mvn clean package
```

#### Schritt 2: Docker Container starten

Nachdem die Docker Images erstellt wurden, können die Container gestartet werden. Die `docker-compose.yml` enthält alle erforderlichen 
Container um den Showcase zu starten.

* Apache Zookeeper von Confluent Inc.
* Apache Kafka Broker von Confluent Inc.
* die JEE Anwendung `kafka-topic-producer`, welche in ein Kafka Topic schreiben
* den Kafka Konnektor `kafka-connect-mq-sink`, welcher Apache Kafka als Senke für IBM MQ bereitstellt 
* IBM MQ Broker von IBM
* die JEE Anwendung `mq-queue-consumer`, welche Nachrichten aus einer JMS Queue konsumiert

Um die Container zu starten, muss man folgenden Befehl ausführen:

```shell script
$ docker-compose up
```

#### Schritt 3: Kafka Connector konfigurieren

Sobald die Broker und der Kafka Konnektor gestartet sind, kann man die Verbindung zwischen dem Kafka Broker (source) und dem MQ Broker 
(sink) herstellen.

Dazu muss der folgende HTTP Request an den Kafka Konnektor geschickt werden:

```shell script
$ curl -s -X POST -H "Content-Type: application/json" --data @kafka-connect-mq-sink-config.json http://localhost:8083/connector
```

[kafka-connect-mq-sink-config.json](kafka-connect-mq-sink-config.json)
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

#### Schritt 4: Nachrichten Versenden und Empfangen

Es gibt zwei Möglichkeiten die Brücke zwischen Kafka und MQ zu testen. 

1) Die Anwendung `kafka-producer` enthält einen Nachrichtengenerator, der alle zwei Sekunden eine neue Nachricht erzeugt und versendet. Der 
Empfang und die erfolgreiche Verarbeitung der Nachricht kann in der Log-Ausgabe der Anwendung `mq-queue-consumer` nachvollzogen werden.

2) Zusätzlich zum Nachrichtengenerator verfügt die Anwendung über eine REST Schnittstelle, mit deren Hilfe eigene Nachrichten erzeugt und 
versendet werden können. 

Um eine eigene Nachricht zu versenden muss man den folgenden GET-Request senden:

```shell script
$ curl -X GET http://localhost:9080/kafka-producer/api/messages?msg=<custom message>
```


### Fehlerbehebung

Hin und wieder kommt es vor, dass die Docker Container beim Versuch vorzeitig zu beenden, nicht wie erwartet gestoppt werden. Dies kann dazu
führen, dass einzelne Container immer noch laufen, obwohl sie bereits gestoppt und entfernt hätten sein sollen. Um zu erkennen welche 
Docker Container noch laufen, muss man den folgenden Befehl ausführen:

```shell script
$ docker ps -a | grep <id of the container>
```

Wenn Container übrig bleiben, obwohl die Anwendung bereit angehalten wurde, kann man diese mit dem folgenden Befehl entfernen:

```shell script
$ docker rm <ids vom container>
```
