package org.powertac.physicalenvironment

import org.powertac.common.interfaces.PhysicalEnvironment
import org.powertac.common.Weather
import org.powertac.common.Timeslot

class PhysicalEnvironmentService implements PhysicalEnvironment {

  static transactional = true

  List<Weather> generateWeatherData(Timeslot currentTimeslot, List<Timeslot> targetTimeslots) {
    return null  //To change body of implemented methods use File | Settings | File Templates.
  }
}
