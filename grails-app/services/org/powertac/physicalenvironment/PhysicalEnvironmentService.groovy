package org.powertac.physicalenvironment

import org.powertac.common.Timeslot
import org.powertac.common.WeatherReport
import org.powertac.common.interfaces.PhysicalEnvironment

class PhysicalEnvironmentService implements PhysicalEnvironment {

  static transactional = true

  List<WeatherReport> generateWeatherData(Timeslot currentTimeslot, List<Timeslot> targetTimeslots) {
    // Create and return dummy list
    List weatherData = []
    weatherData.add(new WeatherReport())
    log.info "generateWeatherData returns ${weatherData}"
    return weatherData
  }
}
