# Kafka/MQ Bridging Showcase

Der Showcase besteht aus zwei unabhängigen Teilen, die die Weiterleitung von Nachrichten von Apache Kafka zu IBM MQ und umgekehrt 
demonstrieren.

Der [kafka-mq-sink](kafka-mq-sink/README_de.md) Showcase zeigt, wie ein Kafka Broker mit Hilfe eines Kafka Konnektors 
[kafka-connect-mq-sink](https://github.com/ibm-messaging/kafka-connect-mq-sink) als Datensenke mit einem MQ Broker verbunden wird. Hierzu 
wird neben den beiden Brokern und dem Connector ein eigener Kafka Produzent und ein eigener MQ Queue Konsument bereitgestellt.

Der [kafka-mq-source](kafka-mq-source/README_de.md) Showcase zeigt, wie ein MQ Broker mit Hilfe eines Kafka Konnektors 
[kafka-connect-mq-source](https://github.com/ibm-messaging/kafka-connect-mq-source) als Datenquelle mit einem KafkaBroker verbunden wird. 
Hierzu wird neben den beiden Brokern und dem Connector ein eigener MQ Queue Produzent und ein eigener Kafka Konsument bereitgestellt.


## Kafka Connect

Kafka Connect ist ein Werkzeug für skalierbares und zuverlässiges Streaming von Daten zwischen Apache Kafka und anderen Systemen. Es 
ermöglicht die einfache und schnelle Definition von Konnektoren, die große Datensammlungen in und aus Kafka bewegen. 

Grundsätzlich kann man zwischen zwei Arten von Konnektoren unterscheiden - Quelle (source) und Senke (sink). Ein **Source-Konnector** liest 
ganze Datenbanken ein und streamt Änderungen in den Datenbanktabellen in Kafka-Topics. Er kann auch  Metriken von einer Vielzahl von 
Anwendungsservern sammeln und diese in Kafka-Topics ablegen, um Daten für die Stream-Verarbeitung mit geringer Latenz bereitzustellen. Ein 
**Sink-Konnector** kann Daten aus Kafka-Topics in sekundäre Speicher- und Abfragesysteme oder für Batchsysteme zur Offline-Analyse 
bereitstellen.

Kafka Connect ist auf das Kopieren von Daten in und aus Kafka spezialisiert. Aus High-Level Sicht ist ein Konnektor ein Job, der Tasks und 
ihre Konfiguration verwaltet. Unter der Haube erstellt Kafka Connect fehlertolerante Kafka-Produzenten und -Konsumenten und überwacht die 
Offsets für die Kafka Records, die Produzenten und Konsumenten geschrieben oder gelesen haben.

Darüber hinaus bieten Kafka Konnektoren eine Reihe von mächtigen Features an. Ein Konnektor kann auf einfache Art und Weise so konfiguriert 
werden, dass er nicht verarbeitbare oder ungültige Nachrichten an eine Dead-Letter-Queue leitet, einzeln Nachrichten automatisch in ein 
anderes Format umwandelt, bevor eine Nachricht von einem Source-Konnektor an Kafka gesendet wird, in die Confluent Schema Registry zur 
automatischen Schema-Registrierung und -verwaltung integrieren bevor sie von Kafka durch einen Sink-Konnektor konsumiert wird oder einfach 
Daten in Typen wie Avro oder JSON konvertieren. 

Durch die Nutzung vorhandener Konnektoren können Entwickler mittels Konfiguration und ohne eigenen Code auf einfache Art und Weise 
fehlertolerante Datenpipelines erstellen die zuverlässig Daten aus einer externen Quelle als Records in Kafka-Topic oder aus Kafka-Topics 
in eine externe Senke streamen.

Jede Instanz eines Konnektor kann seine Arbeit in mehrere Tasks zerlegen und durch die Parallalisierung des Kopierens von Daten 
Skalierbarkeit bieten. Wenn eine Konnektor-Instanz eine Aufgabe startet, überträgt sie die benötigten Konfigurationseinstellungen an jede 
Aufgabe weiter. Die Aufgabe hält die Konfiguration - ebenso wie den Status und die letzten Offsets für die von ihr produzierten oder 
konsumierten Records - extern in einem eigenen Kafka-Topics vor. Da die Aufgabe keinen Zustand speichert, können Aufgaben jederzeit 
gestartet, gestoppt oder neu gestartet werden. Eine neue gestartete Aufgabe halt sich einfach die neuesten Offsets von Kafka ab und setzt 
ihre Arbeit fort.

![Kafka Connect](https://cdn.confluent.io/wp-content/uploads/kafka-connect-2.png)

Kafka Connect kann entweder als eigenständiger Prozess, der Aufträge auf einem einzelnen Rechner ausführt (z.B. Logdaten sammeln) oder als 
verteilter, skalierbarer, fehlertoleranter Dienst, der eine ganze Organisation unterstützt,eingesetzt werden. Im Standalone-Modus läuft 
Kafka Connect auf einem einzelnem Worker-Thread - dabei handelt es sich um einen laufenden JVM-Prozess, der den Konnektor und seine Aufgaben
ausführt. Im verteilten Modus werden Konnektoren und ihre Aufgaben aus mehrere Worker verteilt. Es wird grundsätzlich empfohlen Kafka im 
verteilten Modus zu betreiben, da der Standalone-Modus keine Fehlertoleranz bietet.

Um einen Konnektor im verteilten Modus zu starten, muss ein POST-Request an die Kafka Connect REST API gesendet werde. Der Request weist 
Kafka Connect an, die Ausführung der Konnektoren und Aufgaben über mehrere Worker hinweg, automatisch zu verteilen. Für den Fall, dass ein 
einzelner Worker abgeschaltet oder ein weiterer Worker der Gruppe hinzugefügt wird, koordinieren sich alle Worker selbstständig um 
Konnektoren und Tasks gleichmäßig zu verteilen.

Mehr Informationen zu Kafka Connect finden sich [hier](http://kafka.apache.org/documentation.html#connect), 
[hier](https://docs.confluent.io/current/connect/index.html) und 
[hier](https://www.confluent.jp/blog/create-dynamic-kafka-connect-source-connectors/).

#### Showcase starten

Die Anleitung zum Starten des Source Showcases findet man [hier](./kafka-mq-source/README_de.md) und für den Sink Showcase [hier](./kafka-mq-sink/README_de.md)


### Kafka Connect sink/source connector for IBM MQ

IBM bietet zwei Konnektoren zum Kopieren von Daten von IBM Event Streams oder Apache Kafka zu IBM MQ und umgekehrt.   

* `kafka-connect-mq-sink` ist ein Kafka-Connect-Sink-Anschluss zum Kopieren von Daten von Apache Kafka in IBM MQ. Der Quellcode des 
Konnektors steht auf [GitHub](https://github.com/ibm-messaging/kafka-connect-mq-sink) zur Verfügung, was jedem die Erzeugung eines 
einer ausführbaren JAR-Datei oder eines Docker Images ermöglicht.
* `kafka-connect-mq-source` ist ein Kafka-Connect-Quellkonnektor zum Kopieren von Daten von IBM MQ in Apache Kafka. Der Quellcode des 
Konnektors steht auf [GitHub](https://github.com/ibm-messaging/kafka-connect-mq-source) zur Verfügung, was jedem die Erzeugung eines 
einer ausführbaren JAR-Datei oder eines Docker Images ermöglicht.

Weitere Informationen zur Integration des Kafka-Connect-Sink-/Source-Konnektors finden sich hier ...
* [Apache Kafka, IBM MQ und z/OS](https://community.ibm.com/community/user/imwuc/viewdocument/kafka-connectors-for-ibm-mq-a-mq)
* [IBM MQ und IBM Event Streams](https://medium.com/@khongks/making-ibm-mq-talking-to-kafka-ibm-event-stream-7d57368402e1)
