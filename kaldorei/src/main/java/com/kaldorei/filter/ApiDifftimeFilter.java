package com.kaldorei.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class ApiDifftimeFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("ApiDifftimeFilter INIT");
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long beginTime = System.currentTimeMillis();
		chain.doFilter(request, response);
		long diffTime = System.currentTimeMillis() - beginTime;
		log.info("耗时: " + diffTime);
	}

	@Override
	public void destroy() {
		log.info("ApiDifftimeFilter destroy");
	}

}
