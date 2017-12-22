/**
 * Project Name:aviva-sso <br/>
 * File Name:CustomerApiTest.java <br/>
 * Package Name:cn.com.aviva.sso.common.api <br/>
 * Copyright (c) 2017, www.aviva.com.cn All Rights Reserved.<br/>
 *
 */
package com.kaldorei.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.DigestUtils;

import com.kaldorei.util.NetUtil;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiTest {
	public static final String URL_PREFIX = "http://127.0.0.1:8080/kaldorei";

	@SneakyThrows
	public static void httpConnection() {

		String appid = "32AC343D3AE011E7A8C70050568E5E2A";
		String token = "3D298C2E39E011E7AC8C525400CB7794";
		String timestamp = "1511317184738";
		String nonce = "ANQEKH5BWE";

		String url = URL_PREFIX + "/service/bankCard";
		String input = "{\"request\":{\"bankCardNo\":\"2465164989587861\"}}";
		StringBuilder sb = new StringBuilder();
		sb.append(token);
		sb.append(timestamp);
		sb.append(appid);
		sb.append(nonce);
		if (input != null && input.length() >= 0) {
			sb.append(input);
		}
		String signature = DigestUtils.md5DigestAsHex(sb.toString().getBytes("UTF-8")).toUpperCase();

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Appid", appid);
		headers.put("Timestamp", timestamp);
		headers.put("Nonce", nonce);
		headers.put("Signature", signature);

		String output = NetUtil.send(url, headers, input, "UTF-8");
		log.info("返回结果：\n{}", output);
	}

	public static void main(String[] args) throws Exception {
		httpConnection();
	}
}
