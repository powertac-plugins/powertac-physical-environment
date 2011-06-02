package org.powertac.physical.environment

import org.powertac.common.Competition;
import org.powertac.common.interfaces.InitializationService


class WeatherInitializationService implements InitializationService {

	def weatherService
    static transactional = false


	@Override
	public void setDefaults() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String initialize(Competition competition,
			List<String> completedInits) {
		// TODO Auto-generated method stub
		weatherService.init()
		return "I win";
	}	
}
