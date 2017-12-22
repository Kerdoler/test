package com.kaldorei.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class BankCardEntity {
	
	private Map<String, String> requestHeader;
	private String requestUrl;
	private String requestMethod;
	
	private String bankCardNo;
	
    public void addHeader(String key, String value) {
        if (requestHeader == null) {
        	requestHeader = new HashMap<String, String>();
        }
        requestHeader.put(key, value == null ? "" : value);
    }
}
