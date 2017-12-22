package com.kaldorei.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kaldorei.aop.ApiSignatureVerification;
import com.kaldorei.domain.BankCardEntity;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BaseController {

	@RequestMapping(value = "/bankCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiSignatureVerification
	public BankCardEntity getAllUsers(HttpServletRequest request) {
		BankCardEntity bankCardEntity = (BankCardEntity) request.getAttribute("APIDATA");
		bankCardEntity.setBankCardNo("12321321");
		log.info("request: " + request);
		return bankCardEntity;
	}
	
	@RequestMapping(value = "/cardNo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public BankCardEntity getCardNo(HttpServletRequest request) {
		BankCardEntity bankCardEntity = new BankCardEntity();
		bankCardEntity.setBankCardNo("12321321");
		return bankCardEntity;
	}
	
	public static void main(String[] args) {
		log.info("hello {}" , "world");
	}
}
