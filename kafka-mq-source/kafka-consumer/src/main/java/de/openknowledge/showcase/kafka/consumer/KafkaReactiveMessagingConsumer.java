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
package de.openknowledge.showcase.kafka.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Kafka consumer that receives messages from a Kafka topic.
 */
@ApplicationScoped
public class KafkaReactiveMessagingConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingConsumer.class);

  @Tracing
  @Incoming("message")
  public CompletionStage onMessage(final Message message) {
    try {
      LOG.info("Received message {}", message.getPayload());

      return CompletableFuture.completedFuture(null);
    } catch (IllegalArgumentException e) {
      LOG.error(e.getMessage(), e);
      return CompletableFuture.completedFuture(null);
    }
  }
}
