package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import model.NonDominatedSet;
import model.Solution;
import model.TravelingThiefProblem;
import org.w3c.dom.ranges.Range;

// NTGA (Non-dominated Tournament Genetic Algorithm) implementation for TTP (traveling thief problem)
public class NTGA implements Algorithm{
    int populationSize;
    int epochs = 1000;  // how many iteration will the algorithm run
    int tournamentSize = 8;  // tournament size
    public List<Solution> entries = new LinkedList<>();


    // Initiate the number of solutions from the problem
    public NTGA(int numOfSolutions) {
        this.populationSize = numOfSolutions;
    }


    @Override
    public List<Solution> solve(TravelingThiefProblem problem) {
        List<Solution> population = initPopulation(problem, false);  // init population
        nonDominatedSorting(population, false);  // non-dominated sorting
        List<Solution> newGeneration = new ArrayList<>();  // init new generation
        while (newGeneration.size() < populationSize) {
            List<Solution> parents = new ArrayList<>();
            for (int n = 0; n < 2; ++n) {  // generate two parents
                Solution parent = tournamentSelect(population, tournamentSize);  // tournament selection
                parents.add(parent);
            }
            orderCrossover(parents, false, true);  // in-place order crossover (OX)
            mutate(parents);  // individuals mutations
            break;
        }




//        List<Solution> nextGeneration = new ArrayList<>();  // init next generation
//        for (int epoch = 0; epoch < epochs; ++ epoch) {
//            while (nextGeneration.size() < populationSize) {
//                List<Solution> parents = tournamentSelect(population, tournamentSize);  // parents are selected from tournament selection
//                List<Solution> offspring = new ArrayList<>();  // init offspring;
//                offspring = orderCrossoverPMX(parents);  // OX(PMX) crossover;
//                offspring = mutate(offspring);  // mutate offspring;
//                offspring = clonePrevent(population, offspring);  // clone prevention
//                nextGeneration.addAll(offspring);  // add offspring into next generation
//            }
//        }
//        return nextGeneration;

        return null;


    }



    /**
     * tournament selection
     * @param tournamentSize the number of individuals will be compare by comparison operator
     * @return the best individual
     * */
    private Solution tournamentSelect(List<Solution> population, int tournamentSize){
        Random rand = new Random();
        Solution best = population.get(rand.nextInt(populationSize));  // random select a individual
        for (int i = 1; i < tournamentSize; ++i){
            Solution individual = population.get(rand.nextInt(populationSize));  // random select a individual
            /*Formula: I ≥r J if (Irank < Jrank) - comparison operator
            * notice: the formula should be only used for NTGA*/
            if (individual.rank < best.rank)
                best = individual;
        }
        return best;
    }

    /**
     * fast non-dominated sorting with time complexity O(MN^2) which M is objectives and N is individual number
     * */
    private void nonDominatedSorting(List<Solution> population, boolean showInfo){
        // how many other individual can dominates an individual - 多少个体能支配它
        List<Integer> dominated = new ArrayList<>(population.size());
        // list of individual that an individual dominates - 支配哪些个体
        List<List<Integer>> dominates = new ArrayList<>(population.size());
        List<List<Integer>> paretoFront = new ArrayList<>();  // Pareto front

        for (Solution s : population){
            s.rank = -1;  // reset rank for each individual
            // init list
            dominated.add(0);
            dominates.add(new ArrayList<>());
        }
        paretoFront.add(new ArrayList<>());

        for (Solution p : population){
            for (Solution q : population){
                if (p == q)
                    continue;
                else if (p.getRelation(q) == 1){  // p dominates q
                    dominates.get(p.index).add(q.index);
                }
                else if (p.getRelation(q) == -1){  // p is dominated by q
                    dominated.set(p.index, dominated.get(p.index) + 1);
                }
            }
            if (dominated.get(p.index) == 0){  // pareto front
                p.rank = 0;
                paretoFront.get(0).add(p.index);
            }
        }

        if (showInfo) {
            System.out.println("paretoFront: " + paretoFront);
            System.out.println("dominated: " + dominated);
            System.out.println("dominates: " + dominates);
        }

        int i = 0;
        while (paretoFront.get(i).size() > 0){
            List<Integer> temp = new ArrayList<>();
            for (int m = 0; m < paretoFront.get(i).size(); ++m){
                for (int n = 0; n < dominates.get(paretoFront.get(i).get(m)).size(); ++n){
                    int otherIndividual = dominates.get(paretoFront.get(i).get(m)).get(n);
                    dominated.set(otherIndividual, dominated.get(otherIndividual)-1);
                    if (dominated.get(otherIndividual) == 0){
                        population.get(otherIndividual).rank = i + 1;
                        temp.add(otherIndividual);
                    }
                }
            }
            ++i;
            paretoFront.add(temp);
        }
        if (showInfo)
            System.out.println("paretoFront: " + paretoFront);
    }

    /**
     * in-place order crossover OX
     * @param population a subset of population which only have two individuals
     */
    private void orderCrossover(List<Solution> population, boolean showInfo, boolean crossoverZ){
        Random rand = new Random();
        Solution parent1 = population.get(0);
        Solution parent2 = population.get(1);
        int size = parent1.pi.size();  // get the size of tours

        // choose two random numbers for the start and end indices of the slice
        int number1 = rand.nextInt(size - 1);
        int number2 = rand.nextInt(size);
        // make the smaller the start and the larger the end
        int start = Math.min(number1, number2);
        int end = Math.max(number1, number2);

        if (showInfo) {
            System.out.println("parent1_genotype = " + parent1.pi);
            System.out.println("parent2_genotype = " + parent2.pi + "\n");
            System.out.println("slice = [" + start + ", " + end + "]\n");
        }

        // add the sublist in between the start and end points
        List<Integer> sublist1 = new ArrayList<>(parent1.pi.subList(start, end));
        List<Integer> sublist2 = new ArrayList<>(parent2.pi.subList(start, end));
        // init children
        List<Integer> child1 = new ArrayList<>();
        List<Integer> child2 = new ArrayList<>();

        // iterate over each city in the parent tours
        for (int i = 0; i < size; ++i){
            // get the city at the current index in each of the two parent tours
            int currentCityInTour1 = parent1.pi.get(i);
            int currentCityInTour2 = parent2.pi.get(i);
            // if sublist1 does not already contain the current city in parent2, add it
            if (!sublist1.contains(currentCityInTour2))
                child1.add(currentCityInTour2);
            // if sublist2 does not already contain the current city in parent1, add it
            if (!sublist2.contains(currentCityInTour1))
                child2.add(currentCityInTour1);
        }
        if (showInfo) {
            System.out.println("child1 = " + child1);
            System.out.println("child2 = " + child2 + "\n");
            System.out.println("sublist1 = " + sublist1);
            System.out.println("sublist2 = " + sublist2 + "\n");
        }

        // add sublist into child
        child1.addAll(start, sublist1);
        child2.addAll(start, sublist2);

        // in-place copy operation
        Collections.copy(parent1.pi, child1);
        Collections.copy(parent2.pi, child2);

        if (showInfo) {
            System.out.println("after_crossover1 = " + parent1.pi);
            System.out.println("after_crossover2 = " + parent2.pi);
        }

        // I am not sure whether Z need be crossover
        if(crossoverZ){  // perform uniform crossover for Z
            List<Boolean> firstChildZ = new ArrayList<>();
            List<Boolean> secondChildZ = new ArrayList<>();
            for (int i = 0; i < parent1.z.size(); ++i){
                if (rand.nextInt(2) == 0){
                    firstChildZ.add(parent1.z.get(i));
                    secondChildZ.add(parent2.z.get(i));
                }
                else {
                    firstChildZ.add(parent2.z.get(i));
                    secondChildZ.add(parent1.z.get(i));
                }
            }
            if (showInfo) {
                System.out.println("parent1Z: " + parent1.z);
                System.out.println("parent2Z: " + parent2.z);
                System.out.println("child1Z: " + firstChildZ);
                System.out.println("child2Z: " + secondChildZ);
            }
        }
    }

    /**
     * in-place mutation
     * @param population a subset of population which only have two individuals
     */
    private void mutate(List<Solution> population){

    }

    private List<Solution> clonePrevent(List<Solution> originalPopulation, List<Solution> newPopulation){
        return newPopulation;
    }

    /**
     * Initialise population
     */
    private List<Solution> initPopulation(TravelingThiefProblem problem, boolean showPopulationInfo){
        Random rand = new Random();
        List<Solution> population = new ArrayList<>();  // init population
        int counter = 0;

        while(population.size() < populationSize){
            // init random tour
            List<Integer> pi = getIndex(1, problem.numOfCities);
            Collections.shuffle(pi);
            pi.add(0,0);

            // Create a random packing plan
            List<Boolean> z = new ArrayList<>(problem.numOfItems);
            int packingRate = rand.nextInt(101);  // random packing rate from 0% to 100%
            for (int i = 0; i < problem.numOfItems; ++i){
                if (rand.nextInt(101) <= packingRate){
                    z.add(true);
                }
                else {
                    z.add(false);
                }
            }

            // evaluate for this random tour
            Solution s = problem.evaluate(pi, z, true);
            if (s != null) {
                s.index = counter;
                population.add(s);
                // show population info
                if (showPopulationInfo) {
                    System.out.println("index: " + counter + " Solution name: " + s);
                    System.out.println(s.pi.size() + " number of tour:\n" + s.pi);
                    System.out.println(s.z.size() + " number of packing plan:\n" + s.z);
                    System.out.println("packing rate: " + packingRate + '%');

                    System.out.println("time: " + s.time);
                    System.out.println("profit: " + s.objectives.get(1));
                    System.out.println("rank: " + s.rank + '\n');
                }
                ++counter;
            }
        }
        if (showPopulationInfo)
            System.out.println("population: " + population + '\n' + "population size: " + population.size());
        entries.addAll(population);
        return population;
    }

    private List<Integer> getIndex(int low, int high) {
        List<Integer> l = new ArrayList<>();
        for (int j = low; j < high; j++) {
            l.add(j);
        }
        return l;
    }
}
