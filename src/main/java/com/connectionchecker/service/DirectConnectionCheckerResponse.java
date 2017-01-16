package com.connectionchecker.service;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Object to hold JSON Response data
 */
public class DirectConnectionCheckerResponse {
	
	@JsonProperty("dep_sid")
	private Integer depStationId;

	@JsonProperty("arr_sid")
	private Integer arrStationId;
	
	@JsonProperty("direct_bus_route")
	public Boolean directConnection;
	
	public DirectConnectionCheckerResponse(){}
	
	public DirectConnectionCheckerResponse(Integer depStationId, Integer arrStationId, boolean directConnection){
		this.depStationId = depStationId;
		this.arrStationId = arrStationId;
		this.directConnection = directConnection;
	} 
}
