package com.connectionchecker;
import static io.restassured.RestAssured.when;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, properties ={"datafile=./src/test/resources/data01.txt"})
public class DirectConnectionCheckerAppIntegrationTests {

	@Test
	public void testValidRoutes(){
		this.direct(3, 3, true);
		this.direct(0, 4, true);
		this.direct(4, 0, true);
		this.direct(0, 6, true);
		this.direct(6, 0, true);
		this.direct(3, 5, true);
		this.direct(5, 3, true);
	}
	
	@Test
	public void testInexistentArrSid(){
		this.directErr(5, 40, "\"Station with sid 40 - Does not exist.\"");
	}
	
	@Test
	public void testInexistentDepSid(){
		this.directErr(40, 5, "\"Station with sid 40 - Does not exist.\"");
	}
	
	@Test
	public void testMissingBothParams(){
		when().
		get("/api/direct")
		.then().statusCode(HttpStatus.BAD_REQUEST.value()).
		body("message", Matchers.is("Required String parameter 'dep_sid' is not present"));
	}
	
	@Test
	public void testMissingArrSidParam(){
		when().
		get("/api/direct?dep_sid={dep_sid}", 4)
		.then().statusCode(HttpStatus.BAD_REQUEST.value()).
		body("message", Matchers.is("Required String parameter 'arr_sid' is not present"));
	}
	
	@Test
	public void testMissingDepSidParam(){
		when().
		get("/api/direct?arr_sid={arr_sid}", 4)
		.then().statusCode(HttpStatus.BAD_REQUEST.value()).
		body("message", Matchers.is("Required String parameter 'dep_sid' is not present"));
	}
	
	private void direct(Integer dep, Integer arr, Boolean direct) {
		when().
		get("/api/direct?dep_sid={dep_sid}&arr_sid={arr_sid}", dep, arr)
		.then().statusCode(HttpStatus.OK.value()).
		body("dep_sid", Matchers.is(dep)).
		body("arr_sid", Matchers.is(arr)).
		body("direct_bus_route", Matchers.is(direct));
	}
	
	private void directErr(Integer dep, Integer arr, String msg){
		when().
		get("/api/direct?dep_sid={dep_sid}&arr_sid={arr_sid}", dep, arr)
		.then().statusCode(HttpStatus.BAD_REQUEST.value()).
		body("message", Matchers.is(msg));
	}

}