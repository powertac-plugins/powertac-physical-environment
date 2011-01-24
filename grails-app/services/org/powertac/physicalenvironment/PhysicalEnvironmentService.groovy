package org.powertac.physicalenvironment

import org.powertac.common.interfaces.PhysicalEnvironment
import org.powertac.common.Weather
import org.powertac.common.Timeslot

class PhysicalEnvironmentService implements PhysicalEnvironment {

  static transactional = true

  List<Weather> generateWeatherData(Timeslot currentTimeslot, List<Timeslot> targetTimeslots) {
    // Create and return dummy list
    List weatherData = []
    weatherData.add(new Weather())
    return weatherData
  }
}
