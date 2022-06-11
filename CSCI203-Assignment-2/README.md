# CSCI203 Assignment 2

---

Your task for this assignment is to investigate some of the properties of queues.

Input consists of the following data:
1. The number of primary servers in the system.
2. The number of secondary servers in the system.
3. A set of service requests each consisting of an arrival time and two service times. This set is terminated by a dummy record with arrival time and service times all equal to 0. (Note: the arrival times are sorted in ascending order).

```
For example, the data file:
3 2
1 2 3
3 3 5
3 2 2
4 3 2
5 2 4
0 0 0
```

Indicates there are 3 primary servers and 2 secondary servers. 

* The first service (customer) arrives in minute 1 (first minute of simulation), and the service requires 2 minutes of primary server’s time and 3 minutes of secondary server’s time.


* The second service (customer) arrives in minute 3, and it requires 3 minutes of primary server’s time and 5 minutes of secondary server’s time, etc.


* The last entry of the data file 0 0 0 indicate the end of simulation. (Note that it is possible to have two customers arrive in the same time as shown in the above sample data (second and third customers).

Program should read the name of the data file from standard input and then read the data in the named file into the simulation. For example, the following command will trigger the execution of your program by reading the data file provided:
```
./QueueSim datafile.dat 

OR

java QueueSim datafile.dat
```

The simulation is to be of a system with two sets of servers, primary and secondary, with a single queue associated with each set. Customers arrive in the system and are served first by a primary server and, on completion of this service by a secondary server. 

If all servers of a particular type are busy, the customer will enter either the primary or secondary queue as appropriate.

Output, to standard output, for each version of the queuing process will consist of the following data:
1. Number of people served.
2. Time last service request is completed.
3. Average total service time.
4. Average total time in queue(s). Both overall and separate.
5. Average length of queue. For each queue and overall.
6. Maximum Length of queue. For each queue and overall.
7. Total idle time for each server.
