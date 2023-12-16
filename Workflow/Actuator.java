// Author: Gines Moratalla

import java.lang.Math;
import java.util.Queue;

public class Actuator implements Runnable {


    private Queue<Task> AA_CC_queue;                                                        // Second blocking queue (Analysis-Actuator)
    private double current_position;                                                        // Current position of the robot
    private boolean facingRight = true;                                                     // Boolean for the direction the robot is facing

    private Task task;                                                                      // Task variable (to keep track of tasks processed)

    public Actuator(Queue<Task> AA_CC_queue, double initialPos) {                           // Actuator constructor
        this.AA_CC_queue = AA_CC_queue;                                                     // Queue reference from Workflow classes
        this.current_position = initialPos;                                                 // Initial robot position as user input (from Workflow classes)
    }
    
    public void run() {                                                                     // Runnable object method

        while(true) {
            
            try {                                                                           // Try catch so synchronized keyword could be used

                synchronized(AA_CC_queue) {
                    while (AA_CC_queue.isEmpty()) {
                        if(getLastTask() == null) {                                         // Base case, no tasks processed yet
                            System.out.println("Actuate error: no results to process. Last task processed {ID: " + null + "}.");
                        }
                        else {System.out.println("Actuate error: no results to process. Last task processed {ID: " + getLastTask().getTaskIdentifier() + "}.");}

                        AA_CC_queue.wait();                                                 // Wait for an element to be added to the queue
                    }
                    Task removedTask = AA_CC_queue.remove();                                // Remove the task from the queue to process it.
                    setTask(removedTask);                                                   // Set it as last task processed
                    AA_CC_queue.notify();                                                   // Notify analyser to use the queue again

                }

                String direction = directionFacing(facingRight);                            // Get last facing direction
                Task dequeuedTask = getLastTask();                                          // Get last processed task

                double position = converResult(dequeuedTask.getTaskComplexity());           // Calculate position based on task complexity
                double last_pos = current_position;                                         // Set last position
                calculateResult(position);                                                  // Calculate new position based on the old one

                    
                
                if(dequeuedTask.getSensorIdentifier() == null) {                            // Robot moving print statement (Sensor id null for task 1 and not null for task 2)
                    System.out.println("___________________________________________________________\n\nRobot Moving.\n\n---> Task id {" + dequeuedTask.getTaskIdentifier() + "},\n---> Task complexity {" + dequeuedTask.getTaskComplexity() + "},\n---> Result (Y) = " + position + ", \n---> Old position: {" + last_pos + direction + "}, \n---> New position: {"  + current_position + directionFacing(facingRight) + "}.\n___________________________________________________________\n");
                } else {
                    System.out.println("___________________________________________________________\n\nRobot Moving.\n\n---> Sensor id {" + dequeuedTask.getSensorIdentifier() + "},\n---> Task id {" + dequeuedTask.getTaskIdentifier() + "},\n---> Task complexity {" + dequeuedTask.getTaskComplexity() + "},\n---> Result (Y) = " + position + ", \n---> Old position: {" + last_pos + direction + "}, \n---> New position: {"  + current_position + directionFacing(facingRight) + "}.\n___________________________________________________________\n");
                }

                } catch (InterruptedException e) {break;}
        }
        
    }

    // Getters and setters for the last task processed

    public Task getLastTask() {
        return this.task;
    }

    public void setTask(Task task_) {
        this.task = task_;
    }

    // Convert task complexity to result using the formula Y = sqrt(1/c)

    public double converResult(double position) {
        position = Math.sqrt(1/position);
        return position;
    }

    // Method to move the robot to the last position

    public void calculateResult(double new_pos) {                                           

        if (facingRight) {                                                                  // If direction facing is right, add position instead of substract
            current_position += new_pos;                                                    // Add calculated move to your current position

            if(current_position>1) {                                                        // If it goes beyond wall, change direction and recurse with the remaining distance to the right

                facingRight = false;

                double distanceLeft = current_position - 1;                                 // Get distance left by substracting current distance  by the right wall
                current_position = 1;                                                       // Reset current distance to the right wall

                calculateResult(distanceLeft);                                              // Recurse
            }

        } else {

            current_position -= new_pos;

            if(current_position<0) {                                                         // If it goes beyond wall, change direction and recurse with the remaining distance to the left

                facingRight = true;                                                          // Change direction facing

                double distanceLeft = (-1)*current_position;                                 // Positivise remaining distance and
                current_position = 0;                                                        // Reset current distance to the left wall

                calculateResult(distanceLeft);                                               // Recurse
            }

        }
    }

    // Extra information method: will tell us where is the robot facing

    public String directionFacing(boolean facingRight) {
        if(facingRight) {
            return " (Facing Right)";
        }
        return " (Facing Left)";
    }
}