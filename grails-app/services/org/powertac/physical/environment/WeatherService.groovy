package org.powertac.physical.environment

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import org.joda.time.Instant

import org.powertac.common.WeatherReport
import org.powertac.common.interfaces.TimeslotPhaseProcessor



class WeatherService implements TimeslotPhaseProcessor {
	static transactional = true
    int weatherRequestInterval = 12
	
	
	
	def timeService
	def competitionControlService
	
	void init()
	{
	  competitionControlService.registerTimeslotPhase(this, 3)
	}
	
	public void activate(Instant time, int phaseNumber) {
		if (weatherRequestInterval > 24) {
			log.error "weather request interval ${weatherRequestInterval} > 24 hr"
			weatherRequestInterval = 24
		  }
		  long msec = timeService.currentTime.millis
		  if (msec % (weatherRequestInterval * timeService.HOUR) == 0) {
			// time to publish
			log.info "Requesting Weather from Server at time: " + time
			this.webRequest(time)	
		  }
		  
		  log.info "Broadcasting weatherReport: "
		  //def currentWeather = WeatherReport.findAll{it -> it.currentTimeslot == time}		
		  // Broadcast ?  	
		
	}
	
	
	def webRequest(Instant time) {
		
		HTTPBuilder http = new HTTPBuilder('http://www.google.com')
		
		// perform a GET request, expecting TEXT response data
		http.request( GET, TEXT ) {
		
		  // response handler for a success response code:
		  response.success = { resp, reader ->
			println resp.statusLine
			println reader.text
			
			
			
			// parse the text reader object:
			//reader.text.eachLine { itt -> // 
				//Parse the line into an array of parameters
				def line = "[key:value, key:value, key:value, key:value]"
				
				def result = ""
				line.each {
					letter -> result += ((letter == "[" || letter == "]") ? "" : letter)
						}
						
				def parameters = []
				result.split(", ").each{
					word ->	parameters += (word.split(":")[1].trim())
						}
				//println parameters
				
				
				WeatherReport tmpWR = new WeatherReport(
					currentTimeslot: time,
					temperature: parameters[0],
					windSpeed: parameters[1],
					windDirection: parameters[2],
					cloudCover: parameters[3])
				tmpWR.save()
				
			//} // eachLine
			log.info "Server Response: " + parameters.toString()
			//log.info "Server response" + reader.text
		  }
		
		  // handler for any failure status code:
		  response.failure = { resp ->
			println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
		  }
		}
	}

}
