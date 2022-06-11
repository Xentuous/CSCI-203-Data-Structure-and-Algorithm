# CSCI203-Assignment-3

---

In this assignment, you will write a program to schedule final examination for the examination department so that no student has two examinations at the same time.


The goal of this assignment is to expose you to the implementation of greedy algorithms
that solves a problem with constraints.

You will use a greedy algorithm to determine an
assignment of classes to examination slots (schedules) such that:

1. No student, enrolled in two subjects, is assigned to the same examination slot (schedule.) 

2. Any attempt to combine two slots into one would violate rule 1

3. Input to the program will consist of the name of a data file. This file will contain 
following data:
    * The number of students enrolled in the current semester
    * Repeated rows of the following:
    * Name of the student and the total number of subjects enrolled
    * The subject code the student is enrolled in.



Read the enrolment information from the input file. As the records are read, build an adjacency matrix representing the relationships among the students and the subject the students enrol in. 

You should notice that this adjacency matrix is a graph representing the relationships.

Each node of the graph will be a subject taken by at least one student in the current semester. An edge between two nodes will mean there is at least one student taking both subjects. 

The weight of an edge could be the number of students enrols with both subjects.

1. Your aim in solving this problem is to construct a maximal independent set in the graph. This can be achieved by finding an examination schedule satisfying the two constraints mentioned earlier, as follow:
    * Construct a candidate list of subjects.
    * Order the subjects in descending order by total number of inconnectivity.
    * Starting from the subject with the highest number of inconnectivity, create a slot.
    * Search for a subject to which it is not connected. If you find one, add the subject to the same slot and remove it from the candidate list.

<br>

2. Next, try to find another subject that is not connected to any of those already in the time slot. Similarly, if you find one, add the subject to the same slot and remove it from the candidate list. Continue to do so until there is no more un-connected subject can be found.
    * Accumulate the total number of students enrolled from the adjacency matrix. (How can you do that? Give it a thought.)
    
    * Repeat until all the subjects are removed from the candidate list.
