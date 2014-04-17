package com.bstek.bdf2.dms.client.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.web.client.RestTemplate;

import com.bstek.bdf2.dms.client.RopClient;
import com.bstek.bdf2.dms.client.RopCompositeResponse;
import com.rop.MessageFormat;
import com.rop.RopRequest;
import com.rop.annotation.IgnoreSign;
import com.rop.annotation.Temporary;
import com.rop.config.SystemParameterNames;
import com.rop.marshaller.MessageMarshallerUtils;
import com.rop.request.UploadFile;
import com.rop.response.ErrorResponse;
import com.rop.utils.RopUtils;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * @author lucas.yue@bstek.com
 * 
 *         2013-3-8 下午12:50:28
 */
@SuppressWarnings("restriction")
public class DefaultRopClient implements RopClient {
	private MessageFormat messageFormat = MessageFormat.json;
	private String serverURL;
	private String appkey;
	private String secret;
	private RestTemplate restTemplate = new RestTemplate();
	private Locale locale = Locale.SIMPLIFIED_CHINESE;
	private JaxbXmlRopUnmarshaller jaxbXmlRopUnmarshaller = new JaxbXmlRopUnmarshaller();
	private JacksonJsonRopUnmarshaller jacksonJsonRopUnmarshaller = new JacksonJsonRopUnmarshaller();

	public DefaultRopClient(String serverURL, String appkey, String secret,
			MessageFormat messageFormat) {
		this.serverURL = serverURL;
		this.appkey = appkey;
		this.secret = secret;
		this.messageFormat = messageFormat;
	}

	public <T> RopCompositeResponse<?> get(String method, String version,
			RopRequest request, Class<T> responseClass, String sessionId) {
		Map<String, String> urlVariables = convertToUrlVariablesMap(method,
				version, request, sessionId);
		String responseContent = restTemplate.getForObject(
				buildUrl(serverURL, urlVariables), String.class, urlVariables);
		return convertToCompositeResponse(responseContent, responseClass);
	}

	private String buildUrl(String url, Map<String, String> paramsMap) {
		List<String> temp = new ArrayList<String>();
		for (Entry<String, String> entry : paramsMap.entrySet()) {
			temp.add(entry.getKey() + "=" + entry.getValue());
		}
		return url += "?" + StringUtils.join(temp, "&");
	}

	public <T> RopCompositeResponse<?> post(String method, String version,
			RopRequest request, Class<T> responseClass, String sessionId) {
		MultiValueMap<String, String> mutivalueMap = convertToPostMultiValueMap(
				method, version, request, sessionId);
		String responseContent = restTemplate.postForObject(serverURL,
				mutivalueMap, String.class);
		return convertToCompositeResponse(responseContent, responseClass);
	}

	private <T> RopCompositeResponse<?> convertToCompositeResponse(
			String responseContent, Class<T> responseClass) {
		boolean success = isSuccessfulResponse(responseContent);
		RopCompositeResponse<T> comResponse = new DefaultRopCompositeResponse<T>(
				success);
		if (success) {
			if (MessageFormat.json.equals(this.getMessageFormat())) {
				T t = jacksonJsonRopUnmarshaller.unmarshaller(responseContent,
						responseClass);
				comResponse.setSuccessfulResponse(t);
			} else {
				T t = jaxbXmlRopUnmarshaller.unmarshaller(responseContent,
						responseClass);
				comResponse.setSuccessfulResponse(t);
			}
		} else {
			if (MessageFormat.json.equals(this.getMessageFormat())) {
				ErrorResponse ers = jacksonJsonRopUnmarshaller.unmarshaller(
						responseContent, ErrorResponse.class);
				comResponse.setErrorResponse(ers);
			} else {
				ErrorResponse ers = jaxbXmlRopUnmarshaller.unmarshaller(
						responseContent, ErrorResponse.class);
				comResponse.setErrorResponse(ers);
			}
		}
		return comResponse;
	}

	private Map<String, String> convertToUrlVariablesMap(String method,
			String version, RopRequest request, String sessionId) {
		Map<String, String> variablesMap = new LinkedHashMap<String, String>();
		addGlobalParams(method, version, request, variablesMap);
		// 业务参数
		// 将RopRequest对象中所有业务字段及字段值取出并进行签名
		List<String> ignoreParamNames = getRopRequestParams(request,
				variablesMap);
		// 签名
		if (sessionId != null)
			variablesMap.put("sessionId", sessionId);
		String sign = RopUtils.sign(variablesMap, ignoreParamNames,
				this.getSecret());
		variablesMap.put(SystemParameterNames.getSign(), sign);

		return variablesMap;
	}

	private MultiValueMap<String, String> convertToPostMultiValueMap(
			String method, String version, RopRequest request, String sessionId) {
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
		// 全局参数
		addGlobalParams(method, version, request, multiValueMap);
		// 业务参数
		// 将RopRequest对象中所有业务字段及字段值取出并进行签名
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		List<String> ignoreParamNames = getRopRequestParams(request, paramsMap);
		for (Entry<String, String> entry : paramsMap.entrySet()) {
			multiValueMap.add(entry.getKey(), entry.getValue());
		}
		if (sessionId != null)
			multiValueMap.add("sessionId", sessionId);
		// 签名
		String sign = RopUtils.sign(multiValueMap.toSingleValueMap(),
				ignoreParamNames, this.getSecret());
		multiValueMap.add(SystemParameterNames.getSign(), sign);
		return multiValueMap;
	}

	private List<String> getRopRequestParams(RopRequest request,
			Map<String, String> variableMap) {
		return addRopRequestFieldsValue(request, variableMap);
	}

	private List<String> addRopRequestFieldsValue(RopRequest request,
			Map<String, String> variableMap) {
		List<String> ignoreSignParamNames = new ArrayList<String>();
		final List<Field> allFields = getAllFields(request,
				ignoreSignParamNames);
		for (Field field : allFields) {
			// TODO convert
			Object fieldValue = ReflectionUtils.getField(field, request);
			if (fieldValue != null) {
				if (field.getType().isAnnotationPresent(XmlType.class)
						|| field.getType().isAnnotationPresent(
								XmlRootElement.class)) {
					variableMap.put(
							field.getName(),
							MessageMarshallerUtils.getMessage(fieldValue,
									this.getMessageFormat()));
				} else if (fieldValue instanceof UploadFile) {
					UploadFile uploadFile = (UploadFile) fieldValue;
					variableMap.put(field.getName(), uploadFile.getFileType()
							+ "@" + Base64.encode(uploadFile.getContent()));

				} else {
					variableMap.put(field.getName(), fieldValue.toString());
				}
			}
		}
		return ignoreSignParamNames;
	}

	private List<Field> getAllFields(RopRequest request,
			final List<String> ignoreSignParamNames) {
		final List<Field> allFields = new ArrayList<Field>();
		ReflectionUtils.doWithFields(request.getClass(), new FieldCallback() {
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				ReflectionUtils.makeAccessible(field);
				Temporary varTemporary = field.getAnnotation(Temporary.class);
				if (varTemporary == null) {
					allFields.add(field);
				}
				IgnoreSign typeIgnore = AnnotationUtils.findAnnotation(
						field.getClass(), IgnoreSign.class);
				IgnoreSign varIgnore = field.getAnnotation(IgnoreSign.class);
				if (varTemporary != null || typeIgnore != null
						|| varIgnore != null) {
					ignoreSignParamNames.add(field.getName());
				}
			}
		});
		return allFields;
	}

	private void addGlobalParams(String method, String version,
			RopRequest request, MultiValueMap<String, String> multiValueMap) {
		multiValueMap.add(SystemParameterNames.getAppKey(), this.getAppkey());
		multiValueMap.add(SystemParameterNames.getFormat(), this
				.getMessageFormat().name());
		multiValueMap.add(SystemParameterNames.getMethod(), method);
		multiValueMap.add(SystemParameterNames.getVersion(), version);
		multiValueMap.add(SystemParameterNames.getLocale(),
				this.locale.toString());
		if (request.getRopRequestContext() != null)
			multiValueMap.add(SystemParameterNames.getSessionId(), request
					.getRopRequestContext().getSessionId());
	}

	private void addGlobalParams(String method, String version,
			RopRequest request, Map<String, String> variablesMap) {
		variablesMap.put(SystemParameterNames.getAppKey(), this.getAppkey());
		variablesMap.put(SystemParameterNames.getMethod(), method);
		variablesMap.put(SystemParameterNames.getVersion(), version);
		variablesMap.put(SystemParameterNames.getFormat(), this
				.getMessageFormat().name());
		variablesMap.put(SystemParameterNames.getLocale(),
				this.locale.toString());
		if (request.getRopRequestContext() != null) {
			variablesMap.put(SystemParameterNames.getSessionId(), request
					.getRopRequestContext().getSessionId());
		}
	}

	private boolean isSuccessfulResponse(String responseContent) {
		boolean success = true;
		if (MessageFormat.json == messageFormat) {
			if ((responseContent.contains("{\"error\"") && responseContent
					.contains("\"code\""))) {
				success = false;
			} else if (responseContent.contains("{\"code\":")
					&& responseContent.contains("\"message\"")
					&& responseContent.contains("\"solution\"")
					&& responseContent.contains("\"subErrors\"")) {
				success = false;
			} else if (responseContent.contains("{\"code\":")
					&& responseContent.contains("\"message\"")
					&& responseContent.contains("\"solution\"")) {
				success = false;
			}
			// ErrorResponse至少包含code、message、solution三个参数，才会被正确解析
		} else {
			if (responseContent.contains("<error")
					&& responseContent.contains("code=\"")) {
				success = false;
			}
		}
		return success;
	}

	public MessageFormat getMessageFormat() {
		return messageFormat;
	}

	public String getServerURL() {
		return serverURL;
	}

	public String getAppkey() {
		return appkey;
	}

	public String getSecret() {
		return secret;
	}
}
