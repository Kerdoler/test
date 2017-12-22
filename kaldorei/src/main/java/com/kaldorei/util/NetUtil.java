/**
 * Project Name:aviva-sso <br/>
 * File Name:NetUtil.java <br/>
 * Package Name:cn.com.aviva.sso.util.common <br/>
 * Copyright (c) 2017, www.sinosoft.com.cn All Rights Reserved.<br/>
 *
 */
package com.kaldorei.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetUtil {
	public static final int CONNECTION_REQUEST_TIMEOUT = 100000;
	public static final int CONNECTION_TIMEOUT = 500000;
	public static final int SOCKET_TIMEOUT = 500000;

	@SneakyThrows
	public static SSLContext createIgnoreVerifySSL() {
		return org.apache.http.ssl.SSLContextBuilder.create().loadTrustMaterial(null, new TrustStrategy() {
			// 默认信任所有证书
			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();
	}

	@SneakyThrows
	public static String send(String url, Map<String, String> headers, Map<String, String> dataMap, String encoding) {
		String body = "";
		long startTime = 0, endTime = 0;
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(CONNECTION_REQUEST_TIMEOUT).setSocketTimeout(CONNECTION_REQUEST_TIMEOUT).build();
			// 采用绕过验证的方式处理https请求
			SSLContext sslcontext = createIgnoreVerifySSL();
			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			ConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier);

			// 设置协议http和https对应的处理socket链接工厂的对象
			org.apache.http.config.Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory).build();

			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

			// 创建自定义的httpclient对象
			CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).build();
			// 创建post方式请求对象
			HttpPost httpPost = new HttpPost(url);
			if (headers != null) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 装填参数
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			if (dataMap != null) {
				for (Entry<String, String> entry : dataMap.entrySet()) {
					nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			// 设置参数到请求对象中
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, encoding));
			// 设置header信息
			// 指定报文头【Content-type】、【User-Agent】
			httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
			httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			// 执行请求操作，并拿到结果（同步阻塞）
			startTime = System.currentTimeMillis();
			CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
			endTime = System.currentTimeMillis();
			// 获取结果实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(entity, encoding);
			}
			EntityUtils.consume(entity);
			// 释放链接
			response.close();
		} catch (Exception ex) {
			endTime = System.currentTimeMillis();
			log.error("调用接口异常", ex);
			throw ex;
		} finally {
			log.info("请求地址【{}】,耗时【{}】毫秒,请求时间【{}】，返回时间【{}】,请求参数【{}】,接口返回信息【{}】", url, endTime - startTime, getTimeFromTimestamp(startTime + "", "yyyy-MM-dd HH:mm:ss.SSS"), getTimeFromTimestamp(endTime + "", "yyyy-MM-dd HH:mm:ss.SSS"), dataMap, body);
		}
		return body;
	}

	@SneakyThrows
	public static String send(String url, Map<String, String> headers, String data, String encoding) {
		String body = "";
		long startTime = 0, endTime = 0;
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(CONNECTION_REQUEST_TIMEOUT).setSocketTimeout(CONNECTION_REQUEST_TIMEOUT).build();
			// 采用绕过验证的方式处理https请求
			SSLContext sslcontext = createIgnoreVerifySSL();
			HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			ConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier);

			// 设置协议http和https对应的处理socket链接工厂的对象
			org.apache.http.config.Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory).build();

			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

			// 创建自定义的httpclient对象
			CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig).build();
			// 创建post方式请求对象
			HttpPost httpPost = new HttpPost(url);
			if (headers != null) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			// 装填参数
			if (data != null) {
				httpPost.setEntity(new StringEntity(data, encoding));
			}

			// 设置header信息
			// 指定报文头【Content-type】、【User-Agent】
			httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

			// 执行请求操作，并拿到结果（同步阻塞）
			startTime = System.currentTimeMillis();
			CloseableHttpResponse response = client.execute(httpPost);
			endTime = System.currentTimeMillis();
			
			// 获取结果实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(entity, encoding);
			}
			EntityUtils.consume(entity);
			// 释放链接
			response.close();
		} catch (Exception ex) {
			endTime = System.currentTimeMillis();
			log.error("调用接口异常", ex);
			throw ex;
		} finally {
			log.info("请求地址【{}】,耗时【{}】毫秒,请求时间【{}】，返回时间【{}】,请求参数【{}】,接口返回信息【{}】", url, endTime - startTime, getTimeFromTimestamp(startTime + "", "yyyy-MM-dd HH:mm:ss.SSS"), getTimeFromTimestamp(endTime + "", "yyyy-MM-dd HH:mm:ss.SSS"), data, body);
		}
		return body;
	}

	public static String streamToString(InputStream inputStream) {
		StringBuilder msg = new StringBuilder();
		try {
			@Cleanup
			InputStreamReader responseBuffer = new InputStreamReader(inputStream, "UTF-8");
			int c = 0;
			while ((c = responseBuffer.read()) != -1) {
				msg.append((char) c);
			}
		} catch (Exception e) {
		}
		return msg.toString();
	}

	private static String getTimeFromTimestamp(String timestamp, String dateFormatPattern) {
		// 时间戳转化为Sting或Date
		return new SimpleDateFormat(dateFormatPattern).format(new Long(timestamp));
	}
}
