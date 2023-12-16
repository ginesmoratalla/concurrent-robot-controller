# concurrent-robot-controller 🤖

Robot controller simulator using concurrent programming in Java. Project for Operating Systems module (2nd year Computer Science).

## Project Goals
- Implement a concurrent programming approach for handling tasks performed by a robot.
- Understand how multithreadding works and apply it to a Java program.
- Identify bottleneck found in one of the robot components
  
## Documentation
Documentation on the approach taken to build this controller is found in the pdf '**Operating_Systems_Coursework_1.pdf**'

## How to run
_build_ and _run_ bash scripts are provided to compile and run the project.

1. cd into directory
2. ./build.sh
3. ./run.sh
  
## Overview

This robot controller simulator will divide the robot in 3 main components:
![42eb2c6d1f06038cb2defd23160eb1b5](https://github.com/ginesmoratalla/concurrent-robot-controller/assets/126341997/1d6a51cc-72a5-40ec-a76c-fb146f888515)

### Sensor
The sensor component will generate tasks every second, where every task will have a **Task ID** and a **Task complexity** given by \[0.1 \leq c \leq 0.5\].
The ammount of tasks generated every second is given by a poisson distribution:

$P(k) = \frac{e^{-\lambda} \lambda^k}{k!}$

Where _k_ will be the ammount of tasks generated in 1 second (batch).

### Analyser
The analyser will collect tasks generated by the sensor, retrieved from a blocking queue (shared resource) and will "_analyse_" each task,
converting the task complexity to time
`thread.sleep(_complexity_)`

### Actuator
The actuator will collect tasks analysed by the analyser, retrieved from a blocking queue (shared resource) and will "_process_" each task,
converting the task complexity into distance to move the robot, given by the formula:

$\mathbb{Y} = \sqrt{\frac{1}{c}}$

The robot will move within the distance range [0, 1], and will bounce back when reaching the "walls", changing direction in case it has remaining distance to move left

![b4e8979964022b39aeb7b081ab9db539](https://github.com/ginesmoratalla/concurrent-robot-controller/assets/126341997/72c44547-77b4-4881-a5a7-1dd52f535ed4)

## Multiple sensors (Task 2)
In task 2, multiple sensors will fire tasks generated to one analyser, and I will observe what is the effect on the overall controllers throughput when this happens.


_More details on the general implementation decisions found in the Documentation_.
