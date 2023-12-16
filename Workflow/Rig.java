/*
Rig.java

Test rig code for students to use in LZSCC211 (Operating Systems) to run problems (e.g. 
Problem.java), and analyse the results.

Author : Dr James Stovold
Date   : Aug 17, 2022
Version: 0.1

************************

Edited by : Student ID_38879816


IMPORTANT NOTE: No code from the original file Rig.java provided by Dr James Stovold helps in any way
to solve the problem proposed with this assignment.

************************


*/

import java.lang.System;
import java.io.*;


public class Rig { 

  Problem problem;       // abstract class interface (to be overridden by different exercises)


  public static void main(String[] args) {

    while (true) {

      System.out.println("\r\n================================");
      System.out.println("Coursework 1: Robot controller");
      System.out.println("================================\r\n");
      System.out.println("1. Robot controller (Main Task)");
      System.out.println("2. Robot controller (Multiple Sensors)");
      System.out.println("0. Exit");


      System.out.print("Pick an option: ");
      Integer selectedOption = 0;
      BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
      String line = "";


      try {

      line = buffer.readLine();
      System.out.println();
      selectedOption = Integer.parseInt(line);

      // If statement added by student
      if(selectedOption < 0 || selectedOption > 2) {
        System.out.println("This number is not within the option range, please input a number that is." );
        continue;

      }

      } catch (NumberFormatException e) {

        System.out.println("I don't know what '" + line + "' is, please input a number." );
        continue;

      } catch (IOException e) {

        System.out.println("IOException, quitting...");
        
      }

      if (selectedOption == 0) { break; }
      Rig rig = new Rig();
      System.out.println("Setting up problem...");
 
      switch (selectedOption) {
        case 1: // Main Task
          rig.problem = new Workflow_Simple(); 
          break;

          // Added by student


        case 2: // Multiple sensor option (3 Sensor Threads)
          rig.problem = new Workflow_Multiple();
          break;
        default:
          return;
      }

      System.out.println("Initialising problem: " + rig.problem.name());
      rig.problem.init();
      System.out.println(rig.problem.name() + " established.");
      System.out.println("Running...\r\n");
      rig.problem.go();     
      
    }
  }  
  
}