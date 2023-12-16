// Author: Gines Moratalla

public class Task {

    private double c;                                                               // Task complexity
    private Integer id;                                                             // Task id
    private Integer sensor_id;                                                      // Sensor id (only for Task 2)


    public Task(double c, Integer id, Integer sensor_id) {                          // Task constructor
        this.c = c;
        this.id = id;
        this.sensor_id = sensor_id;
    }

    // Task getters to keep track of tasks for the Runnable Objects

    public double getTaskComplexity() {
        return this.c;
    }

    public Integer getTaskIdentifier() {
        return this.id;
    }

    public Integer getSensorIdentifier() {
        return this.sensor_id;
    }

}