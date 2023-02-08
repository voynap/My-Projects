package voynap.springcourse.FirstRestApp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class SensorDTO {



    @NotEmpty(message = "Please, enter sensor's name")
    @Size(min = 2, max = 30, message = "Sensor name should be between 2 and 30 characters")
    private String name;

    private Date installedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(Date installedAt) {
        this.installedAt = installedAt;
    }
}
