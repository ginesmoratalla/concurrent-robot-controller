// Author: Gines Moratalla

import java.util.Queue;

public class Analysis implements Runnable {

    private Queue<Task> SS_AA_queue;                                                               // Queue that will connect Sensor and Analyser                                              
    private Queue<Task> AA_CC_queue;                                                               // Queue that will connect Analyser and Actuator

    private Task task;                                                                             // Instance of the class Task

    public Analysis(Queue<Task> SS_AA_queue, Queue<Task> AA_CC_queue) {                            // Analysis object constructor

        this.SS_AA_queue = SS_AA_queue;                                                            // Send the queue reference from Workflow classes
        this.AA_CC_queue = AA_CC_queue;                                                            // Send the queue reference from Workflow classes
    }


    public void run() {                                                                            // Runnable object run() method

        while(true) {                                                                              // keep infinite loop

            try {                                                                                  // Try catch so synchronized keyword could be used
                synchronized(SS_AA_queue) {                                                        // Synchronized statement for the blocking queue

                    while (SS_AA_queue.isEmpty()) {                                                // if the queue is empty, wait (handle this by printing error statement)

                        if(getLastTask() == null) {                                                // Base case (no tsks analyzed yet)

                            System.out.println("Analysis error: no results to analyse. Last task analysed {ID: " + null + "}.");
                        }

                        else {System.out.println("Analysis error: no results to analyse. Last task analysed {ID: " + getLastTask().getTaskIdentifier() + "}.");}

                        SS_AA_queue.wait();                                                        // Wait for an element to be added to the queue
                    }
                    Task removedTask = SS_AA_queue.remove();                                       // Remove a task from the queue to Analyse it
                    setTask(removedTask);                                                          // Set removed task to last task analysed (setter bellow)
                    SS_AA_queue.notifyAll();                                                       // NotifyAll (not notify() for the multiple sensors for Task 2)
                }

                // Analyse current task by sleeping the thread for c*1000 milliseconds
                Task dequeuedTask = getLastTask();                                                 // Get last task from the getter
                double sleepingTime = dequeuedTask.getTaskComplexity() * 1000;                     // Calculate sleeping time from complexity in milliseconds
                Thread.sleep((long) sleepingTime);                                                 // Sleep thread to "Analyse" it.



                synchronized (AA_CC_queue) {                                                       // Synchornized statement for the seccond queue (Analyser-Actuator)

                    while (AA_CC_queue.size() >= 6) {                                              // Handle error waiting to get spaces
                        System.out.println("Analysis error: too many tasks to process. Last task analysed {ID: " + dequeuedTask.getTaskIdentifier() + "}.");
                        AA_CC_queue.wait();                                                        // Wait to get space
                    }
                    AA_CC_queue.add(dequeuedTask);                                                 // Add element to be processed
                    AA_CC_queue.notify();
                }


            } catch (InterruptedException e) {break;}


        }


    }

    // Getter and Setter to keep track of the last task analyzed

    public Task getLastTask() {
        return this.task;
    }

    public void setTask(Task task_) {
        this.task = task_;
    }
}