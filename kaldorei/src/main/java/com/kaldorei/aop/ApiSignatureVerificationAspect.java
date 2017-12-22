package com.kaldorei.aop;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.kaldorei.domain.BankCardEntity;
import com.kaldorei.exception.ApiNullParamsException;

import lombok.extern.slf4j.Slf4j;

/**
 * 验签接口切面
 * 
 */
@Aspect
@Component
@Slf4j
public class ApiSignatureVerificationAspect {
   
	@Before("@annotation(com.kaldorei.aop.ApiSignatureVerification)")
	public void doBefore(JoinPoint joinPoint) throws Exception {
		log.info("进入ApiSignatureVerificationAspect切面");
		Object[] args = joinPoint.getArgs();
		for (Object obj : args) {
			if (obj instanceof ServletRequest) {
				HttpServletRequest request = (HttpServletRequest) obj;
				BankCardEntity bankCardEntity = new BankCardEntity();

				String contextPath = request.getContextPath();
				String requestUrl = request.getRequestURI().substring(contextPath.length());
				bankCardEntity.setRequestUrl(requestUrl);
				bankCardEntity.setRequestMethod(request.getMethod());

				log.info("开始参数校验");
				
				//客户端请求UNIX时间戳
				String clientRequestTimestamp = StringUtils.trimToEmpty(request.getHeader("Timestamp"));
				if(StringUtils.isBlank(clientRequestTimestamp)){
					throw new ApiNullParamsException("api服务调用错误：appid业务系统标识为空");
				}
				String requestData = StringUtils.trimToEmpty(streamToString(request.getInputStream()));
				if(StringUtils.isBlank(clientRequestTimestamp)) {
					throw new ApiNullParamsException("api服务调用错误：请求数据为空");
				}
				
				// 接收到客户端发送的时间
				long serverReceivedTimestamp = System.currentTimeMillis();
				bankCardEntity.addHeader("ClientIp", getIpAddr(request));
				bankCardEntity.addHeader("ServerIp", InetAddress.getLocalHost().getHostAddress());
				// 客户端发送的时间
				bankCardEntity.addHeader("ClientRequestTime", getTimeFromTimestamp(clientRequestTimestamp,"yyyy-MM-dd HH:mm:ss.SSS"));
				bankCardEntity.addHeader("ServerReceivedTime", getTimeFromTimestamp(serverReceivedTimestamp+"","yyyy-MM-dd HH:mm:ss.SSS"));
				// 客户端发请求到服务器端接收到请求的毫秒数
				bankCardEntity.addHeader("DifferTime", (serverReceivedTimestamp - Long.parseLong(clientRequestTimestamp)) + "");
				
				if (StringUtils.isNotBlank(requestData)) {
                    JSONObject json = JSONObject.parseObject(requestData);
                    if (!json.containsKey("request")) {
                    }
//                    bankCardEntity.setRequest(json.getJSONObject("request"));
                    request.setAttribute("APIDATA", bankCardEntity);
                }
			}
		}
//		log.info("joinpoint: " + joinPoint);
	}
	
	
	
	private String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private String getTimeFromTimestamp(String timestamp, String dateFormatPattern) {
		// 时间戳转化为Sting或Date
		return new SimpleDateFormat(dateFormatPattern).format(new Long(timestamp));
	}
	
	private String streamToString(InputStream inputStream) throws Exception {
		StringBuilder msg = new StringBuilder();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
		int c = 0;
		while ((c = inputStreamReader.read()) != -1) {
			msg.append((char) c);
		}
		inputStreamReader.close();
		return msg.toString();
	}
}
