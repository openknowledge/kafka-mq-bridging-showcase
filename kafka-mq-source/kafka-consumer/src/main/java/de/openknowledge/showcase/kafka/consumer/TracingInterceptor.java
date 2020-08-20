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

import io.jaegertracing.Configuration;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.HashMap;

/**
 * TracingInterceptor traces methods that are annotated by {@link @Tracing}.
 */
@Tracing
@Interceptor
public class TracingInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(TracingInterceptor.class);

  Tracer tracer = Configuration.fromEnv().getTracer();

  @AroundInvoke
  public Object trace(InvocationContext ctx) throws Exception {
    Message message = (Message)ctx.getParameters()[0];
    ConsumerRecord<String, CustomMessage> record = (ConsumerRecord<String, CustomMessage>)message.unwrap(ConsumerRecord.class);

    SpanContext spanContext = extractContext(record);

    Span span = tracer
        .buildSpan(ctx.getMethod().getName())
        .asChildOf(spanContext)
        .start();

    Object proceed = ctx.proceed();

    span.finish();

    return proceed;
  }

  private SpanContext extractContext(final ConsumerRecord<String, CustomMessage> cr) {
    try {
      Header header = cr.headers().lastHeader("uber_$dash$_trace_$dash$_id");

      HashMap<String, String> map = new HashMap<>();

      map.put("uber-trace-id", new String(header.value()));

      return tracer.extract(Format.Builtin.TEXT_MAP, new TextMapExtractAdapter(map));
    } catch (Exception e) {
      return null;
    }
  }
}
