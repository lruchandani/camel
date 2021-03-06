/**
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
package org.apache.camel.component.splunk.integration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.splunk.event.SplunkEvent;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("run manually since it requires a running local splunk server")
public class TestFieldListSearchTest extends SplunkTest {

    @Test
    public void testFieldListSearch() throws Exception {
        MockEndpoint searchMock = getMockEndpoint("mock:search-result2");
        searchMock.expectedMessageCount(1);
        getMockEndpoint("mock:submit-result").expectedMessageCount(1);
        assertMockEndpointsSatisfied(20, TimeUnit.SECONDS);
        SplunkEvent recieved = searchMock.getReceivedExchanges().get(0).getIn().getBody(SplunkEvent.class);
        assertNotNull(recieved);
        Map<String, String> data = recieved.getEventData();
        assertEquals("value1", data.get("key1"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:submit").to("splunk://submit?username=" + SPLUNK_USERNAME + "&password=" + SPLUNK_PASSWORD + "&index=" + INDEX + "&sourceType=testSource&source=test")
                        .to("mock:submit-result");

                from("splunk://normal?delay=5s&username=" + SPLUNK_USERNAME + "&password=" + SPLUNK_PASSWORD + "&initEarliestTime=-10s&latestTime=now" + "&search=search index="
                        + INDEX + " sourcetype=testSource&fieldList=key1").to("mock:search-result2");
            }
        };
    }
}
