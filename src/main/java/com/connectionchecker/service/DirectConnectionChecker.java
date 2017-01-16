package com.connectionchecker.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.connectionchecker.domain.exception.InvalidStationException;

@Component
public class DirectConnectionChecker {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	MessageSource messages;
	
	/**
	 * Keeps a map from sId to its routes id's. 
	 */
	private Map<Integer, Set<Integer>> stationToRouteSet = new HashMap<>();

	/**
	 * Given two station id's - returns a flag indicating if there's a direct connection
	 * between those stations.
	 * 
	 * We consider that any given two stations belonging to the same route is directly connected,
	 * in other words, the order the stations appear in the in the list for a given route is not relevant
	 * in determining direct connections.
	 * 
	 * @param sIdDep	departure station id.
	 * @param sidArr	arrival station id.
	 * 
	 * @return	true if there's a direct connection between both stations, false otherwise.
	 * 
	 * @throws InvalidStationException - in case station doesn't exist.
	 */
	public boolean hasDirectConnection(Integer sIdDep, Integer sidArr) throws InvalidStationException {

		if(sIdDep == sidArr) {
			return true;
		}

		Set<Integer> depRoutes = stationToRouteSet.get(sIdDep);
		Set<Integer> arrRoutes = stationToRouteSet.get(sidArr);

		if(depRoutes == null) {
			throw new InvalidStationException(InvalidStationException.Station.DEPARTURE,
					messages.getMessage("error.station.doesnt.exist", new Object[]{sIdDep}, null));
		}
		
		if(arrRoutes == null) {
			throw new InvalidStationException(InvalidStationException.Station.ARRIVAL,
					messages.getMessage("error.station.doesnt.exist", new Object[]{sidArr}, null));
		}

		if(depRoutes.size() >= arrRoutes.size()) {
			return arrRoutes.stream().anyMatch(r -> depRoutes.contains(r));	
		} else {
			return depRoutes.stream().anyMatch(r -> arrRoutes.contains(r));
		}
	}
	
	
	/**
	 * Loads bus data from the file specified in --datafile application parameter.
	 */
	public void loadData(String filePath){
		logger.info("Loading file: {}", filePath);
		
		StopWatch sw = new StopWatch("Loading Bus Route Data...");
		sw.start("Loading Bus Route Data");
		try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
			Integer lines = Integer.parseInt(br.readLine());
			
			for(int i = 0; i < lines; i++) {
				
				String[] split = br.readLine().split(" ");
				
				Integer routeId = Integer.parseInt(split[0]);
				
				for(int j =1; j < split.length; j++){
					Integer station = Integer.parseInt(split[j]);
					Set<Integer> routeSet = this.stationToRouteSet.get(station);
					if(routeSet == null) {
						routeSet = new HashSet<>();
						this.stationToRouteSet.put(station, routeSet);
					}
					routeSet.add(routeId);	
				}
			}
		} catch (IOException e) {
			logger.error("Problem loading routes file: {} ", filePath, e);
			//in a more complex app - shutdown through spring context
			System.exit(1);
		} finally {
			sw.stop();
			logger.info(sw.prettyPrint());
		}
	}

}
