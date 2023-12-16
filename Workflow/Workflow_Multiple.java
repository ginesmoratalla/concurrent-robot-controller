// Author: Gines Moratalla

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.LinkedList;
import java.util.Queue;

public class Workflow_Multiple implements Problem {


    Object lock;                                                                                 // Lock for the multiple Sensor threads
    Integer task_id = 0;                                                                         // Task id initialized to 0

    
    double lambda;                                                                               // User input for Poisson Distribution
    double initialPos;                                                                           // User input for robot initial position

    boolean correctOption = false;                                                               // Boolean value used for all user input methods, to check a computable value has been set

    boolean system_exit = false;                                                                 // Boolean to perform safe execution for the process

    // Declare thread type runnable objects 

    MultipleSensor sensor1;                                                                        
    MultipleSensor sensor2;
    MultipleSensor sensor3;

    Analysis analysis;
    Actuator actuator;


    // Declare threads
    Thread thread1;
    Thread thread2;
    Thread thread3;
    Thread thread4;
    Thread thread5;

    public String name() {
        return("Robot Controller (Multiple Sensors)");
    }

    public void init() {

        /*
         * Call methods for user input on:
         * 
         * Lambda (Poisson distribution)
         * Initial robot position [0, 1]
         * 
         */

        acceptLambda();
        correctOption = false;                                                                     // Reset boolean for next user input method
        acceptInitialPos();                                                                        
        correctOption = false;  

        Queue<Task> first_queue = new LinkedList<>();                                              // Blocking to limit tasks sent to Analyser
        Queue<Task> second_queue = new LinkedList<>();                                             // Blocking to limit tasks sent to Actuator


        lock = 0;                                                                                  // Initialize lock to 0 (got error if didn't do so)

        sensor1 = new MultipleSensor(first_queue, lambda, 1);                            // Instances of 3 sensors for Task 2 (reference to the first blocking queue)
        sensor2 = new MultipleSensor(first_queue, lambda, 2);
        sensor3 = new MultipleSensor(first_queue, lambda, 3);

        analysis = new Analysis(first_queue, second_queue);                                        // Instance of Analysis with reference to both blocking queues
        actuator = new Actuator(second_queue, initialPos);                                         // Instance of Actuator with reference to the second blocking queue


        // Instances of threads with the respective runnable objects

        thread1 = new Thread(sensor1);
        thread2 = new Thread(sensor2);
        thread3 = new Thread(sensor3);

        thread4 = new Thread(analysis);
        thread5 = new Thread(actuator);

    }

    public void go() {                                                                              // Go method will start running the robot controller, called from Rig file

        // Start all threads
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();


        // The robot movement will be running for this ammount of milliseconds (For the analysis, this was changed to record diferent times)
        try {Thread.sleep(20000); } catch (InterruptedException e) {}

        system_exit = true;                                                                         // After x seconds, set system exit to true, meaning Sensor threads run but not produce anything anymore

        try {

            thread1.join();                                                                         // Try joining the threads before interrupt (Adds a stronger safe layer of exiting the process)
            thread2.join();
            thread3.join();

        } catch (InterruptedException e) {}

        try {Thread.sleep(6000); } catch (InterruptedException e) {}                         // Sleep for 6 seconds, so the analyser and actuator can analyse and process remaining tasks in the buffer


        // Cut all threads after executing safely
        thread1.interrupt();
        thread2.interrupt();
        thread3.interrupt();

        thread4.interrupt();
        thread5.interrupt();


        System.out.println("========================================\n\nProcess Finished.\n*--> Tasks Generated {" + task_id + "}.\n\n========================================");

    }



    // METHODS TO STAY IN REASONABLE BOUNDS FOR USER INPUT BELLOW

    public void acceptLambda() {

        while(!correctOption) {                                                                     // Boolean value used for all user input methods, to check a computable value has been set

            System.out.println("_________________________________________________________");
            System.out.println("\nPlease, type your selected Lambda (Poisson distribution):");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            try {
                String input = bufferedReader.readLine();
                lambda = Double.parseDouble(input);
                correctOption = true;

                if(lambda > 50) {                                                                   // Lambda high causing runtime errors due to the max tasks generatable
                    System.out.println("This number is too high, it may result in errors generating tasks.\nPlease, try a smaller number.");
                    correctOption = false;
                }

                if(lambda < 0) {                                                                    // As per definition, lambda cannot be negative
                    System.out.println("This number is too low, it may result in errors generating tasks.\nPlease, try a bigger number.");
                    correctOption = false;
                }
                

            } catch (NumberFormatException e) {                                                     // Incorrect number format, should try again
                correctOption = false;
                System.out.println("This is not a valid lambda, please enter a valid option (double).");
                continue;
            }
            catch (IOException e) {
                System.out.println("IOException, quitting...");
            }  

        }  
    }

    public void acceptInitialPos() {                                

        while(!correctOption) {

            System.out.println("_________________________________________________________");
            System.out.println("\nPlease, type the robot's initial position Range [0 to 1]:");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            try {                                                                                   // Read user input
                String input = bufferedReader.readLine();
                initialPos = Double.parseDouble(input);
                correctOption = true;

                if(initialPos > 1 || initialPos < 0) {                                              // Position not within the range
                    System.out.println("This number is not within the bound range\nPlease, try a number that is.");
                    correctOption = false;
                }
                

            } catch (NumberFormatException e) {                                                     // Number formatting exception (not double)
                correctOption = false;
                System.out.println("This is not a valid position, please enter a valid option (double).");
                continue;
            }
            catch (IOException e) {                                                                 // I/O Exception
                System.out.println("IOException, quitting...");
            }  

        }  
    }

    // METHODS TO STAY IN REASONABLE BOUNDS FOR USER INPUT ABOVE



    // Multiple sensor private class within Workflow class (to use task id and queue as a shared resource)

    private class MultipleSensor implements Runnable {
        /**
        * Sensor class will initialize tasks with:
        * @param c random task complexity where (0.1 <= c <= 0.5)
        * @param sensor_id identifier for each of the multiple Sensor threads
        * @param lambda Lambda value given by user to perform task generation following Poisson distribution
        *
        */
        
        private Integer sensor_id;                                                                  // ID for 1 of the 3 sensors
        private double c;                                                                           // Generate c within the range [0.1 .. 0.5]
        private Double lambda;                                                                      // Lambda value given by user
        private Queue<Task> SS_AA_queue;                                                            // Queue that will be used to pass the Task to the Analyser
            
        public MultipleSensor(Queue<Task> SS_AA_queue, double lambda, Integer sensor_id) {          // Sensor constructor
            this.SS_AA_queue = SS_AA_queue;
            this.lambda = lambda;
            this.sensor_id = sensor_id;
        }
        
        
        public void run() {                                                                         // Runnable Object Run Method
        
            while (!system_exit) {                                                                  // Keep infinite loop to add Tasks from the Sensor
        
                int k = PoissonDistribution();                
                try {

                    for(int i = 0; i < k; i++) { 
                        c = (0.4 * Math.random()) + 0.1;                                            // Generate c (Task Complexity) within the range [0.1 .. 0.5]
                        Task currTask = new Task(c, task_id, sensor_id);                            // Generate task according to the complexity and the current id   
                        
                        
                        synchronized(SS_AA_queue) {                                                 // Synchronized statement to make a blocking queue of a normal queue

                            while(SS_AA_queue.size() >= 6) {                                        // Wait if queue hit the buffer
                                System.out.println("Sensor error: too many tasks to analyse. Last task generated {ID: " + task_id + "}.");
                                SS_AA_queue.wait();
                            }
                            SS_AA_queue.add(currTask);                                               // Add a task to the analysis queue
                            SS_AA_queue.notify();                                                    // Notify analyser of availability of the queue

                            synchronized(lock) {                                                     // Increment id for Next Task using the lock
                                task_id++;
                            }
                        }
                    }

                    
                    Thread.sleep(1000);                                                       // Sleep the thread for 1 second so it can only generate 1 batch/second

                } catch (InterruptedException e) {break;}
                     

            }
        }
        
        
        public Integer PoissonDistribution() {                                                      
            // k is the index of the array
            double[] k_probability = new double[20];                                                 // Set max buffer for generated tasks (20 tasks generated per second is max)
    
            for(int i = 0; i < 20; i++) {                                                            // Create probability for each k [0,19) with user's lambda
                k_probability[i] = ((Math.pow(lambda, i))*(Math.exp(-lambda)))/factorial(i);         // Poisson distribution formula
            }
    
    
            double randomness = Math.random();                                                       // Random number [0,1) that will try to match the probability of k
            double probability = 0;                                                                  // Number that counts the accumulated probabilities of the already visited k
    
            for(int u = 0; u < 20; u++) {                                                            // Iterate though the k_prob array again
    
                probability += k_probability[u];                                                     // Add accumulated probability
    
                if(randomness <= probability) {                                                      // If randomness matches or is smaller than accumulated probability,                                                
                    return u;                                                                        // means that the randomness falls within the range of accumulated probability,
                }                                                                                    // therefore, return this k.
            }

            return null;                                                                             // Unreachable statement only to match return type
    
            
        }
        
        public int factorial(int k) {                                                                 // Custom method used exclusively for the factorial operation (k!)
            if(k == 0) {                                                                              // Using recursion                                 
                return 1;                                           
            }                                                       
            else return k * factorial(k - 1);             
        }
          
    }
}
