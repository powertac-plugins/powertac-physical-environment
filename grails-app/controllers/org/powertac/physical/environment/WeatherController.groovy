package org.powertac.physical.environment

class WeatherController {
	
	WeatherService weatherService = new WeatherService();
	
	
    def index = { 
		render "Hello this is the index!"	
	}
	
	def req = {
		render weatherService.webRequest()		
	}
}
