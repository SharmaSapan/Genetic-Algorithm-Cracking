# Genetic-Algorithm-Cracking
This programs finds the key and its size for the Vigenère cipher using evolutionary AI algorithm, Genetic Algorithm
With more and more of our daily lives going online, we need effective security while storing data.
This repositary has a crypto-system that explores the security of a simple Vigenère cipher to find the key associated with some encrypted text. A genetic algorithm was used to find the password used to encrypt any text given by user.
GA will start with an initial population described by user where each member represent a solution(key to the cipher). Random values in it will evolver over generations through crossover and mutation. The fitness function used to evaluate the fitness of each member of population measures the frequency of each word used in the english language and give it a score. Lower the score, better it is.
The selection strategy used to pick members for crossover and mutation is Tournament selection.
Two crossover operators can be used: 1. 1-Point crossover, 2. 2-Point crossover (2-Point performs better)
Two mutation operators can be used: 1. Insertion mutation, 2. Swap Mutation   (Swap performs better)

To run the program build the GA class with the POP class. GA have the main algorithm. POP class creates individual objects in the population that is keys.

Give a random seed value to keep all results same with same random values. Input is provided to the algorithm in evaluateFitness() function and the max size for number of genes is given to generatePop() function. The program is ran in intelliJ Idea by Jet-Brains using the main class in GA.java file. Console will prompt and ask value to experiment. User have to define population size, number of generations, crossover rate, mutation rate and also the choice of crossover operator. Furtherchanges like tournament size can be changed by changing values in crossover operators functions in the program. There is an unused functioning insertion mutation operator which can be used as well. The results used were generated using a population of 10,000 with generations kept at 60 constant. It took under 1 minute to give output to file and compute key. Output is stored in a text file in Foramt: Output to the file is given in this format:
PopulationSize: 10000 
Maximum generations: 60 
(Choice 1 = 1Point crossover, Choice 2 = 2Point crossover) Your Choice:1
Crossover Rate: 1.0 Mutation Rate: 0.1 Seed chosen: 5000
{ RESULT * NUMBER OF GENERATIONS
 Best fitness value in POP: 0.5879314653962852 
 Average fitness value in POP : 0.7108061005863654
]
Best Solution Found: thisisasupersecurepasspord and its
Fitness value: 0.09035368124153466 Length of Key found: 26

The best solution found always miss by 2 words atleast it
can be further improved.
