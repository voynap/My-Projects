package voynap.WeatherDataCollector.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import voynap.WeatherDataCollector.dto.MeasurementDTO;
import voynap.WeatherDataCollector.dto.SensorDTO;
import voynap.WeatherDataCollector.models.Measurement;
import voynap.WeatherDataCollector.models.Sensor;
import voynap.WeatherDataCollector.services.MeasurementsService;
import voynap.WeatherDataCollector.services.SensorsService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {

    private final MeasurementsService measurementsService;

    private final SensorsService sensorsService;
    private final ModelMapper modelMapper;
    @Autowired
    public MeasurementsController(MeasurementsService measurementsService, SensorsService sensorsService, ModelMapper modelMapper) {
        this.measurementsService = measurementsService;
        this.sensorsService = sensorsService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<MeasurementDTO> getMeasurements() {
        return measurementsService.findAll().stream().map(this::convertToMeasurementDTO).collect(Collectors.toList());
    }
    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addMeasurement(@RequestBody MeasurementDTO measurementDTO) {
        measurementsService.save(convertToMeasurement(measurementDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/rainyDaysCount")
    public int getRainyDays() {
        return measurementsService.findRainyDays().size();
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        Measurement measurement = modelMapper.map(measurementDTO, Measurement.class);
        SensorDTO sensorDTO = measurementDTO.getSensor();
        Sensor sensor = sensorsService.findByName(sensorDTO.getName());
        if (sensor == null) {
            sensor = modelMapper.map(sensorDTO, Sensor.class);
            sensorsService.save(sensor);
        }
        measurement.setSensor(sensor);
        return measurement;
    }
    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        MeasurementDTO measurementDTO = modelMapper.map(measurement, MeasurementDTO.class);
        Sensor sensor = measurement.getSensor();
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName(sensor.getName());
        sensorDTO.setInstalledAt(sensor.getInstalledAt());
        measurementDTO.setSensor(sensorDTO);
        return measurementDTO;
    }
}
