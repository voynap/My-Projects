package voynap.springcourse.FirstRestApp.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import voynap.springcourse.FirstRestApp.models.Sensor;

import java.time.LocalDateTime;

public class MeasurementDTO {



    private LocalDateTime measurementTime;

    private double tempValue;

    private boolean raining;
    private SensorDTO sensorDTO;

    public LocalDateTime getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(LocalDateTime measurementTime) {
        this.measurementTime = measurementTime;
    }

    public double getTempValue() {
        return tempValue;
    }

    public void setTempValue(double tempValue) {
        this.tempValue = tempValue;
    }

    public boolean isRaining() {
        return raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public SensorDTO getSensor() {
        return sensorDTO;
    }

    public void setSensor(SensorDTO sensorDTO) {
        this.sensorDTO = sensorDTO;
    }
}
