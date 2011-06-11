package org.powertac.physical.environment

import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import org.joda.time.Instant

import org.powertac.common.Timeslot
import org.powertac.common.WeatherReport
import org.powertac.common.interfaces.TimeslotPhaseProcessor



class WeatherService implements TimeslotPhaseProcessor {
	static transactional = true
    int weatherRequestInterval = 12
	def serverUrl = "http://tac05.cs.umn.edu:8080/powertac-weather-server/weatherSet/weatherRequest?id=1"
	boolean requestFailed
	
	def brokerProxyService
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
			log.info "Requesting Weather from "+ serverUrl +" at time: " + time
			try {
				// Need try/catch for invalid host strings
				this.webRequest(Timeslot.currentTimeslot())
				requestFailed = false
			}catch(Throwable e){
				log.error "Unable to connect to host: " + serverUrl
				requestFailed = true
				
			}	
		  }
		  
		  WeatherReport currentWeather
		  if(requestFailed){
			  // Set default weather to not break game
			  WeatherReport tmpWR = new WeatherReport(
				  currentTimeslot: Timeslot.currentTimeslot(),
				  temperature: "17.2",
				  windSpeed: "4.6",
				  windDirection: "150",
				  cloudCover: "0.0")
			  
			  tmpWR.save()
			  
		  }
		  // Finds the current weatherReport for the timeslot and broadcasts it every tick (phase3)
		  currentWeather = WeatherReport.findByCurrentTimeslot(Timeslot.currentTimeslot())
		  log.info "Broadcasting weatherReport at time: " + currentWeather.currentTimeslot
		  
		  
		  // Broadcast weather to brokers
		  brokerProxyService?.broadcastMessage(currentWeather)
		  currentWeather.save()
		
	}
	
	
	def webRequest(Timeslot time) {
		
		HTTPBuilder http = new HTTPBuilder(serverUrl)
		
		// perform a GET request, expecting TEXT response data
		def currentTime = time
		http.request( GET, TEXT ) {
		  def finishedParam = []
		  // response handler for a success response code:
		  response.success = { resp, reader ->
			println resp.statusLine			
			
			// parse the text reader object:
			reader.text.eachLine { itt -> // 
				//Parse the line into an array of parameters
				def line = itt

				def result = ""
				line.each {
					letter -> result += ((letter == "[" || letter == "]") ? "" : letter)
						}
						
				def parameters = []
				result.split(", ")?.each{
					word ->	parameters += (word.split(":")[1].trim())
						}
				
				finishedParam = parameters
				
				WeatherReport tmpWR = new WeatherReport(
					currentTimeslot: currentTime,
					temperature: parameters[2],
					windSpeed: parameters[3],
					windDirection: parameters[4],
					cloudCover: parameters[5])
				currentTime = currentTime.next()
				
				tmpWR.save() 
				
			} // eachLine
			log.info "Server Response: " + finishedParam.toString()
		  }
		
		  // handler for any failure status code:
		  response.failure = { resp ->
			println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
			log.error "Weather Server Error: ${resp.statusLine.statusCode}"
			
			
			WeatherReport tmpWR = new WeatherReport(
				currentTimeslot: currentTime,
				temperature: "17.2",
				windSpeed: "4.6",
				windDirection: "150",
				cloudCover: "0.0")
			currentTime = currentTime.next()
			
			tmpWR.save()
			
		  }
		}
	}

}
