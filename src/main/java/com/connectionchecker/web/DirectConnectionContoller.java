package com.connectionchecker.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.connectionchecker.domain.exception.InvalidStationException;
import com.connectionchecker.service.DirectConnectionChecker;
import com.connectionchecker.service.DirectConnectionCheckerResponse;

@Controller
@RequestMapping("/api")
public class DirectConnectionContoller {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	DirectConnectionChecker directConnectionChecker;
	
	@Autowired
	MessageSource messages;
	
	@RequestMapping(value="/direct", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public @ResponseBody DirectConnectionCheckerResponse direct( @RequestParam String dep_sid, 
			@RequestParam String arr_sid) throws Exception{
		
		logger.debug("Handling request for dep_sid: {} arr_sid: {}", dep_sid, arr_sid);
		
		Integer depStationId = this.readStationParam(dep_sid, "invalid.dep.station.format");

		Integer arrStationId = this.readStationParam(arr_sid, "invalid.arr.station.format");
		
		boolean directConnection = directConnectionChecker.hasDirectConnection(depStationId, arrStationId);
		DirectConnectionCheckerResponse response = new DirectConnectionCheckerResponse(depStationId, 
				arrStationId, directConnection);
		return response;
	}
	
	/**
	 * Gets a param and parses it to Integer.
	 * 
	 * @param param string to be parsed to Integer.
	 * @param errorMsg key to the messages resources.
	 * 
	 * @return result of Integer.parseInt(param)
	 * 
	 * @throws InvalidStationException in case param can't be parsed to Integer
	 */
	private Integer readStationParam(String param, String errorMsg) throws InvalidStationException {
		try {
			return Integer.parseInt(param);	
		} catch (Exception e){
			throw new InvalidStationException(InvalidStationException.Station.DEPARTURE, 
					messages.getMessage(errorMsg, null, null));
		}
	}
	
	/**
	 * Handles exceptions by setting response objects to be converted to JSON.
	 * 
	 * @param ex Exception thrown by the app
	 * @param request current request reference
	 * @return
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
	}
}
