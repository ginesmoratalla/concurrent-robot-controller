// Author: Gines Moratalla

import java.lang.Math;
import java.util.Queue;

public class Sensor implements Runnable {

    /**
     * Sensor class will initialize tasks with:
     * @param c random task complexity where (0.1 <= c <= 0.5)
     * @param id identifier incremented every time a new task is created
     * @param lambda variable to perform Poisson Distribution (Passed from Worflow_Simple.java)
    */

                                                          
    private Integer id;                                                                        // Task ID that will keep incrementing for following Tasks
    private double c;                                                                          // Generate c within the range [0.1 .. 0.5]
    private Double lambda;                                                                     // Lambda value given

    private Queue<Task> SS_AA_queue;                                                           // Blocking Queue that will be used to pass the Task to the Analyser
    
    public Sensor(Queue<Task> SS_AA_queue, double lambda, Integer task_id) {                   // Sensor Constructor

        this.SS_AA_queue = SS_AA_queue;
        this.lambda = lambda;
        this.id = task_id;

    }


    public void run() {                                                                         // Runnable object run() method;

        while (true) {                                                                          // Keep infinite loop to add Tasks from the Sensor

            try {


                int k = PoissonDistribution();                                                  // Method found bellow
                
                for(int i = 0; i < k; i++) { 
                    c = (0.4 * Math.random()) + 0.1;                                            // Generate c (Task Complexity) within the range [0.1 .. 0.5]
                    Task currTask = new Task(c, id, null);                            // Increment id for Next Task


                    synchronized(SS_AA_queue) {                                                 // Synchronized statement for the blocking queue
                        while(SS_AA_queue.size() >= 6) {                                        // Wait if queue hit the buffer
                            System.out.println("Sensor error: too many tasks to analyse. Last task generated {ID: " + id + "}.");
                            SS_AA_queue.wait();
                        }
                        SS_AA_queue.add(currTask);                                              // Add an element to the end of the queue
                        SS_AA_queue.notify();                                                   // Method for the blocking queue
                    }
                    id++;
                }    
                Thread.sleep(1000);                                                      // Simulate 1 task generation per second
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

    public int factorial(int k) {                                                                // Custom method used exclusively for the factorial operation (k!)
        if(k == 0) {                                                                             // Using recursion                                       
            return 1;                                           
        }                                                       
        else return k * factorial(k - 1);             
    }
  
} 