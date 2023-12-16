// Author: Gines Moratalla

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class Workflow_Simple implements Problem {


    double lambda;                                                                          // User input for Poisson Distribution
    double initialPos;                                                                      // User input for robot initial position
    boolean correctOption = false;                                                          // Boolean value used for all user input methods, to check a computable value has been set

    // Declare runnable objects
    Sensor sensor;
    Analysis analysis;
    Actuator actuator;

    // Declare threads
    Thread thread1;
    Thread thread2;
    Thread thread3;

    public String name() {
        return("Robot Controller");
    }

    public void init() {

        /*
         * Call methods for user input on:
         * 
         * Lambda (Poisson distribution)
         * Initial robot position [0 to 1]
         * 
         */

        acceptLambda();
        correctOption = false;                                                              // Reset boolean for next user input method
        acceptInitialPos();
        correctOption = false;



        Queue<Task> first_queue = new LinkedList<>();                                       // Blocking to limit tasks sent to Analyser
        Queue<Task> second_queue = new LinkedList<>();                                      // Blocking to limit tasks sent to Actuator

        sensor = new Sensor(first_queue, lambda, 0);                                // Instance of sensor (reference to the first blocking queue)               
        analysis = new Analysis(first_queue, second_queue);                                 // Instance of Analysis with reference to both blocking queues        
        actuator = new Actuator(second_queue, initialPos);                                  // Instance of Actuator with reference to the second blocking queue

        // Instances of threads with respective runnable objects
        thread1 = new Thread(sensor);
        thread2 = new Thread(analysis);
        thread3 = new Thread(actuator);

    }

    public void go() {                                                                      // Go method will start running the robot controller, called from Rig file

        // Start all threads
        thread1.start();
        thread2.start();
        thread3.start();

        // The robot movement will be running for this ammount of milliseconds (can be changed for analysis)
        try {Thread.sleep(20000); } catch (InterruptedException e) {}

        // SOLUTION to loosing generated tasks

        thread1.interrupt();

        try {Thread.sleep(6000); } catch (InterruptedException e) {}                  // Run the Analyser and Actuator for an extra 6 seconds to analyse and process all the remaining tasks in the buffer
        
        // Interrupt analyser and actuator
        thread2.interrupt();
        thread3.interrupt();



    }

    // METHODS TO STAY IN REASONABLE BOUNDS FOR USER INPUT BELLOW

    public void acceptLambda() {

        while(!correctOption) {                                                              // Boolean value used for all user input methods, to check a computable value has been set

            System.out.println("_________________________________________________________");
            System.out.println("\nPlease, type your selected Lambda (Poisson distribution):");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            try {
                String input = bufferedReader.readLine();
                lambda = Double.parseDouble(input);
                correctOption = true;


                if(lambda > 50) {                                                             // Lambda high causing runtime errors due to the max tasks generatable
                    System.out.println("This number is too high, it may result in errors generating tasks.\nPlease, try a smaller number.");
                    correctOption = false;
                }

                if(lambda < 0) {                                                              // As per definition, lambda cannot be negative
                    System.out.println("This number is too low, it may result in errors generating tasks.\nPlease, try a bigger number.");
                    correctOption = false;
                }
                

            } catch (NumberFormatException e) {                                               // Incorrect number format, should try again
                correctOption = false;
                System.out.println("This is not a valid lambda, please enter a valid option (double).");
                continue;
            }
            catch (IOException e) {                                                           // I/O Exception
                System.out.println("IOException, quitting...");
            }  

        }  
    }

    public void acceptInitialPos() {

        while(!correctOption) {

            System.out.println("_________________________________________________________");
            System.out.println("\nPlease, type the robot's initial position Range [0 to 1]:");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            try {                                                                             // Read user input
                String input = bufferedReader.readLine();
                initialPos = Double.parseDouble(input);
                correctOption = true;

                if(initialPos > 1 || initialPos < 0) {                                        // If position is not within the wall bounds
                    System.out.println("This number is not within the bound range\nPlease, try a number that is.");
                    correctOption = false;
                }
                

            } catch (NumberFormatException e) {                                               // Number formatting exception (not double)
                correctOption = false;
                System.out.println("This is not a valid position, please enter a valid option (double).");
                continue;
            }
            catch (IOException e) {                                                           // I/O Exception
                System.out.println("IOException, quitting...");
            }  

        }  
    }

    // METHODS TO STAY IN REASONABLE BOUNDS FOR USER INPUT ABOVE
}
