package com.bstek.bdf2.dms.client.impl;

import java.io.IOException;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rop.RopException;

public class JacksonJsonRopUnmarshaller {
	static final Logger logger = LoggerFactory
			.getLogger(JaxbXmlRopUnmarshaller.class);
	private static ObjectMapper objectMapper;

	public <T> T unmarshaller(String content, Class<T> objectType) {
		try {
			return getObjectMapper().readValue(content, objectType);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new RopException(e);
		}
	}

	private ObjectMapper getObjectMapper() throws IOException {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
			SerializationConfig serializationConfig = objectMapper
					.getSerializationConfig();
			serializationConfig = serializationConfig
					.without(
							new SerializationConfig.Feature[] { SerializationConfig.Feature.WRAP_ROOT_VALUE })
					.withAnnotationIntrospector(introspector);
			objectMapper.setSerializationConfig(serializationConfig);
		}
		return objectMapper;
	}
}