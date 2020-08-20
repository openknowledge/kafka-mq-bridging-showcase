/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.showcase.mq.queue.producer;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import io.opentracing.contrib.jms2.TracingMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.json.bind.JsonbBuilder;

/**
 * JMS producer that sends messages to a queue. The queue is configured in the server.xml.
 */
@ApplicationScoped
public class QueueProducer {

  private static final Logger LOG = LoggerFactory.getLogger(QueueProducer.class);

  @Resource(lookup = "JMSFactory")
  private ConnectionFactory jmsFactory;

  @Resource(lookup = "JMSQueue")
  private Queue queue;

  @Inject
  Tracer tracer;

  public void send(final CustomMessage message) {
    try (Connection connection = jmsFactory.createConnection();
        Session session = connection.createSession();
        MessageProducer messageProducer = session.createProducer(queue);
        TracingMessageProducer producer = new TracingMessageProducer(messageProducer, tracer)) {

      String json = JsonbBuilder.create().toJson(message);

      TextMessage textMessage = session.createTextMessage();
      textMessage.setText(json);

      LOG.info("Send message {} to {}", textMessage.getText(), queue.getQueueName());

      producer.send(textMessage);
    } catch (JMSException e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
