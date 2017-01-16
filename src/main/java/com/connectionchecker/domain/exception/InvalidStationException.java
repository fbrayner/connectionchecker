package com.connectionchecker.domain.exception;

@SuppressWarnings("serial")
public class InvalidStationException extends Exception{

	public enum Station {
		DEPARTURE,
		ARRIVAL
	}

	private Station station;

	public InvalidStationException(Station station, String msg){
		super(msg);
		this.station = station;
	}

	public Station getStation() {
		return station;
	}
}

