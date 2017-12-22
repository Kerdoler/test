package com.kaldorei.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

//import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("LogFilter INIT");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		/**
		 * 生成一个traceId，作为线程id
		 */
		String traceId = UUID.randomUUID().toString().replace("-", "").substring(16);
		
		/**
		 * 修改线程名字，请求开始时以start开头，结束时以end开头，并拼接上key和时间，这样做有以下好处：
		 * 通过jstack查看堆栈的时候，如果有线程被堵塞（以start开头的线程都是未跑完的，这种状态下的线程有可能被堵塞），
		 * 就可以根据traceId去查日志，看程序的哪一步比较耗时。
		 */
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
		Thread thread = Thread.currentThread();
		thread.setName("begin-" + traceId + "-" + sdf.format(new Date()));
		
		/**
		 * 把reqid存入MDC，这样在log4j等日志框架中可以使用traceId标记
		 */
//		MDC.put("traceId", traceId);
		
		/**
		 * 把reqid存入request，这样在tomcat的访问日志中才可以获取到reqid
		 */
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		httpServletRequest.setAttribute("traceId", traceId);
		
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			log.info("LogFilter finally");
			/**
			 * 请求结束时，把线程名字改为end开头
			 */
			thread.setName("end-" + traceId + "-" + sdf.format(new Date()));
			log.info(thread.getName());
		}
	}

	@Override
	public void destroy() {
		log.info("LogFilter destroy");
	}

}
