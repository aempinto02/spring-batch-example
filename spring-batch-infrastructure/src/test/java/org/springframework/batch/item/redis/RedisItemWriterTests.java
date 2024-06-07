/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.batch.item.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.batch.item.Chunk;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RedisItemWriterTests {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private RedisTemplate<String, String> redisTemplate;

	private RedisItemWriter<String, String> redisItemWriter;

	@BeforeEach
	public void setup() {
		this.redisItemWriter = new RedisItemWriter<>();
		this.redisItemWriter.setRedisTemplate(this.redisTemplate);
		this.redisItemWriter.setItemKeyMapper(new RedisItemKeyMapper());
	}

	@Test
	void shouldWriteToRedisDatabaseUsingKeyValue() {
		this.redisItemWriter.writeKeyValue("oneKey", "oneValue");
		verify(this.redisTemplate.opsForValue()).set("oneKey", "oneValue");
	}

	@Test
	void shouldWriteAllItemsToRedis() throws Exception {
		Chunk<String> items = new Chunk<>("val1", "val2");
		this.redisItemWriter.write(items);
		verify(this.redisTemplate.opsForValue()).set(items.getItems().get(0), items.getItems().get(0));
		verify(this.redisTemplate.opsForValue()).set(items.getItems().get(1), items.getItems().get(1));
	}

	static class RedisItemKeyMapper implements Converter<String, String> {

		@Override
		public String convert(String source) {
			return source;
		}

	}

}
