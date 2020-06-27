/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.reactive;

import io.vertx.core.Vertx;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.AbstractCamelContext;
import org.apache.camel.reactive.vertx.VertXReactiveExecutor;
import org.apache.camel.reactive.vertx.VertXThreadPoolFactory;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitParallelTest extends CamelTestSupport {

    private final Vertx vertx = Vertx.vertx();

    @Override
    protected CamelContext createCamelContext() throws Exception {
        AbstractCamelContext context = new DefaultCamelContext();
        VertXReactiveExecutor re = new VertXReactiveExecutor();
        re.setVertx(vertx);
        context.setReactiveExecutor(re);

        VertXThreadPoolFactory tpf = new VertXThreadPoolFactory();
        tpf.setVertx(vertx);
        context.getExecutorServiceManager().setThreadPoolFactory(tpf);

        return context;
    }

    @Test
    public void testSplit() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("A,B,C,D,E,F,G,H,I,J");
        getMockEndpoint("mock:split").expectedBodiesReceivedInAnyOrder("A","B", "C", "D", "E", "F", "G", "H", "I", "J");

        template.sendBody("direct:start", "A,B,C,D,E,F,G,H,I,J");

        assertMockEndpointsSatisfied();

        vertx.close();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .to("log:foo")
                    .split(body()).parallelProcessing()
                        .delay(500).end()
                        .to("log:bar")
                        .to("mock:split")
                    .end()
                    .to("log:result")
                    .to("mock:result");
            }
        };
    }
}
