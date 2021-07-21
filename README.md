# ElevatorSimulationWithThreads
This project is a simulation of a elevator working.It was Used java Threads for simulation.This project developed for learning that concepts Multithreading, Concurrency, Resource Allocation.
## Project description

Check "Project Description.pdf" where in the "Project Material" folder if you want to know project description about problem,goal of the project etc.

## Method which used in this project

Check "Rapor.pdf" file where in the "Report and Outputs" folder if you want to know project how to build.


## PROJECT SUMMARY

Task:

	there is a mall which too much customers enters and exits. 
	The Task is providing to move customers to aimed location through most efficient thread working.
Threads:

	Login Thread
		--> Produces consumers and sends for transport to consumters' aim queue.
	Exit Thread
		--> Deletes if the consumers transported to exit queue.
	Controller Thread
		--> Manages all the threads.
	Elevator Thread
		--> There are 5 lifter thread and the mall have 5 count of flat.
		--> It takes consumers from queues where in the flats.
		--> It Transports consumers to aimed flat.

Check "## Project description" and "## Method & which used in this project" sections where above if you want more information.


Images is below while project has been running

![RunTimeOutput](https://user-images.githubusercontent.com/44205116/126507292-eb148542-d367-48bc-93ad-06b4df5c7818.gif)
