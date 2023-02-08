package voynap.WeatherDataCollector.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.WeatherDataCollector.repositories.SensorsRepository;
import voynap.WeatherDataCollector.models.Sensor;

import java.util.Date;

@Service
@Transactional(readOnly = true)
public class SensorsService {

    private final SensorsRepository sensorsRepository;
    @Autowired
    public SensorsService(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }


    @Transactional
    public void save(Sensor sensor) {
        enrichSensor(sensor);
        sensorsRepository.save(sensor);
    }

    public Sensor findByName(String name) {
        return sensorsRepository.findByName(name);
    }

    private void enrichSensor(Sensor sensor) {
        sensor.setInstalledAt(new Date());
    }
}
