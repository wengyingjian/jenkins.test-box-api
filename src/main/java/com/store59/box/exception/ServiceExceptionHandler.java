package com.store59.box.exception;

import com.store59.box.viewmodel.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class ServiceExceptionHandler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 全局异常捕获，返回异常状态及信息 TODO 异常日志纪录
	 * 
	 * @param req
	 *            发生异常的请求
	 * @param ex
	 *            发生的异常
	 * @return 已知异常返回定义的状态码和信息，未知异常返回状态码－1和异常信息
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Result handleBadRequest(HttpServletRequest req, Exception ex) {
		Result r = new Result();
		System.out.println(ex.getClass());
		if (ex instanceof ServiceException) {
			ServiceException e = (ServiceException) ex;
			r.setStatus(e.getStatus());
			r.setMsg(e.getMsg());
		} else {
			r.setStatus(-1);
			r.setMsg(ex.getMessage());
		}
//		logger.error(r.getMsg());
		logger.error(getTrace(ex));
		return r;
	}

	public static String getTrace(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		StringBuffer buffer= stringWriter.getBuffer();
		return buffer.toString();
	}

}
