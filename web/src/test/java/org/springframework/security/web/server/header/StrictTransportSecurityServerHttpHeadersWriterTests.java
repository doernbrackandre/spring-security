/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.server.header;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class StrictTransportSecurityServerHttpHeadersWriterTests {
	StrictTransportSecurityServerHttpHeadersWriter hsts = new StrictTransportSecurityServerHttpHeadersWriter();

	ServerWebExchange exchange;

	@Test
	public void writeHttpHeadersWhenHttpsThenWrites() {
		exchange = exchange(MockServerHttpRequest.get("https://example.com/"));

		hsts.writeHttpHeaders(exchange);

		HttpHeaders headers = exchange.getResponse().getHeaders();
		assertThat(headers).hasSize(1);
		assertThat(headers).containsEntry(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY,
				Arrays.asList("max-age=31536000 ; includeSubDomains"));
	}

	@Test
	public void writeHttpHeadersWhenCustomMaxAgeThenWrites() {
		Duration maxAge = Duration.ofDays(1);
		hsts.setMaxAge(maxAge);
		exchange = exchange(MockServerHttpRequest.get("https://example.com/"));

		hsts.writeHttpHeaders(exchange);

		HttpHeaders headers = exchange.getResponse().getHeaders();
		assertThat(headers).hasSize(1);
		assertThat(headers).containsEntry(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY,
				Arrays.asList("max-age=" + maxAge.getSeconds() + " ; includeSubDomains"));
	}

	@Test
	public void writeHttpHeadersWhenCustomIncludeSubDomainsThenWrites() {
		hsts.setIncludeSubDomains(false);
		exchange = exchange(MockServerHttpRequest.get("https://example.com/"));

		hsts.writeHttpHeaders(exchange);

		HttpHeaders headers = exchange.getResponse().getHeaders();
		assertThat(headers).hasSize(1);
		assertThat(headers).containsEntry(StrictTransportSecurityServerHttpHeadersWriter.STRICT_TRANSPORT_SECURITY,
				Arrays.asList("max-age=31536000"));
	}

	@Test
	public void writeHttpHeadersWhenNullSchemeThenNoHeaders() {
		exchange = exchange(MockServerHttpRequest.get("/"));

		hsts.writeHttpHeaders(exchange);

		HttpHeaders headers = exchange.getResponse().getHeaders();
		assertThat(headers).isEmpty();
	}

	@Test
	public void writeHttpHeadersWhenHttpThenNoHeaders() {
		exchange = exchange(MockServerHttpRequest.get("http://example.com/"));

		hsts.writeHttpHeaders(exchange);

		HttpHeaders headers = exchange.getResponse().getHeaders();
		assertThat(headers).isEmpty();
	}

	private static MockServerWebExchange exchange(MockServerHttpRequest.BaseBuilder<?> request) {
		return MockServerWebExchange.from(request.build());
	}
}
