package voynap.springcourse.FirstRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import voynap.springcourse.FirstRestApp.models.Measurement;
import voynap.springcourse.FirstRestApp.models.Sensor;
import voynap.springcourse.FirstRestApp.repositories.MeasurementsRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementsService {

    private final MeasurementsRepository measurementsRepository;
    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    @Transactional
    public void save(Measurement measurement) {
        measurementsRepository.save(measurement);
    }


    public List<Measurement> findAll() {
        return measurementsRepository.findAll();
    }

    public List<Measurement> findRainyDays() {
        return measurementsRepository.findByRainingTrue();
    }
}
