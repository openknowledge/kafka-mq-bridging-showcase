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
package de.openknowledge.showcase.mq.queue.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.bind.JsonbBuilder;

/**
 * JMS consumer that receives messages from a queue. The queue is configured in the server.xml.
 */
@MessageDriven
public class QueueConsumer implements MessageListener {

  private static final Logger LOG = LoggerFactory.getLogger(QueueConsumer.class);

  @Override
  public void onMessage(final Message message) {
    try {
      String textMessage = ((TextMessage)message).getText();

      LOG.info("Received message {}", textMessage);

      CustomMessage customMessage = JsonbBuilder.create().fromJson(textMessage, CustomMessage.class);

      throwIllegalArgumentExceptionWhenIdEqualsFail(customMessage);
    } catch (JMSException e) {
      LOG.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
  }

  private void throwIllegalArgumentExceptionWhenIdEqualsFail(final CustomMessage message) {
    if (message.getText().equals("fail")) {
      LOG.error("Message processing failed");
      throw new IllegalArgumentException();
    }
  }
}
