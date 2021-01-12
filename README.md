# NTGA-TTP
A Non-dominated Tournament Genetic Algorithm (NTGA) for Traveling Thief Problem (TTP) <br/>

This repository is cloned from [gecco19-thief](https://github.com/julesy89/gecco19-thief), which is a template of Bi-objective Traveling Thief Problem. <br/>

The [Non-dominated Trounament Genetic Algorithm (NTGA)](https://github.com/blackdogtop/NTGA-TTP/blob/master/src/main/java/algorithms/NTGA.java) is implemented by myself inorder to optimize the TTP and the pseudocode can refer to **Improved selection in evolutionary multi–objective optimization of multi–skill resource–constrained project scheduling problem**. <br/>

Otherwise, more details of the TTP can be found [here](https://www.egr.msu.edu/coinlab/blankjul/gecco19-thief/) <br/>

In the following the project structure is explained:


    gecco19-thief
    ├── Runner.java: Execute an algorithm on all competition instance and to save the file in the derired format.
    ├── Competition.java: Contains the instance names to be solved and the maximum limit of solutions to submit.
    ├── model
        ├── TravelingThiefProblem.java: The problem object used to evaluate the problem for a given tour and packing plan.
        ├── Solution.java: Object to store the results of the evaluate function.
        └── Solution.java: NonDominatedSet.java: Example implementation of a non-dominated set. Can be done faster/better.
    ├── algorithms
        ├── Algorithm: Interface for the algorithm to be implemented from.
        ├── ExhaustiveSearch: Solves the problem exhaustively which means iterating over all possible tours and packing plans.
        ├── RandomLocalSearch: Example algorithm to randomly fix a tour and then iterate over possible packing plans.
        └── NTGA: Non-dominated Tournament Genetic Algorithm
