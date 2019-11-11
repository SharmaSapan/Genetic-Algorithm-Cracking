import java.util.Random;

import static java.lang.Math.abs;

public class POP {

    char[] gene;
    double fitness; //storing fitness locally
    // it creates an object which has random genes in it with given size and random seed
    POP(int chromSize, Random rnd) {
        gene = new char[chromSize];
        for (int i = 0; i < gene.length; i++) {
            //gene[i] = (char) ((Math.random()*((122-97)+1))+97);
            gene[i] = (char) (rnd.nextInt((122-91)+1)+91);
            // a-z + some random characters so that key spaces can be filled
        }
    }
    // it returns the the specific gene at a given index
    char getGene(int index) {
        return gene[index];
    }
    // it updates the specific gene at the given index
    void setGene(int index, char alpha) {
        gene[index] = alpha;
    }

    // it returns the reference to this class to use its other functions
    POP getChromosomeChar() { return this; }

    // it returns the string value of genes that is the whole chromosome
    String getChromosome() {
        return new String(gene);
    }

    // it calculates the fitness of chromosome and given text
    void calcFitness(String k, String c) {
        //The expected frequency of each character in english language text according to
        //http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/
        double[] expectedFrequencies = new double[26];
        expectedFrequencies[0] = 0.085; //Expected frequency of a
        expectedFrequencies[1] = 0.016; //Expected frequency of b
        expectedFrequencies[2] = 0.0316; //Expected frequency of c
        expectedFrequencies[3] = 0.0387; //Expected frequency of d
        expectedFrequencies[4] = 0.121; //Expected frequency of e
        expectedFrequencies[5] = 0.0218; //Expected frequency of f
        expectedFrequencies[6] = 0.0209; //Expected frequency of g
        expectedFrequencies[7] = 0.0496; //Expected frequency of h
        expectedFrequencies[8] = 0.0733; //Expected frequency of i
        expectedFrequencies[9] = 0.0022; //Expected frequency of j
        expectedFrequencies[10] = 0.0081; //Expected frequency of k
        expectedFrequencies[11] = 0.0421; //Expected frequency of l
        expectedFrequencies[12] = 0.0253; //Expected frequency of m
        expectedFrequencies[13] = 0.0717; //Expected frequency of n
        expectedFrequencies[14] = 0.0747; //Expected frequency of o
        expectedFrequencies[15] = 0.0207; //Expected frequency of p
        expectedFrequencies[16] = 0.001; //Expected frequency of q
        expectedFrequencies[17] = 0.0633; //Expected frequency of r
        expectedFrequencies[18] = 0.0673; //Expected frequency of s
        expectedFrequencies[19] = 0.0894;//Expected frequency of t
        expectedFrequencies[20] = 0.0268;//Expected frequency of u
        expectedFrequencies[21] = 0.0106; //Expected frequency of v
        expectedFrequencies[22] = 0.0183;//Expected frequency of w
        expectedFrequencies[23] = 0.0019;//Expected frequency of x
        expectedFrequencies[24] = 0.0172;//Expected frequency of y
        expectedFrequencies[25] = 0.0011;//Expected frequency of z

        //Sanitize the cipher text and key
        String d = c.toLowerCase();
        d = d.replaceAll("[^a-z]", "");
        d = d.replaceAll("\\s", "");
        int[] cipher = new int[d.length()];
        for(int x = 0; x < d.length(); x++) {
            cipher[x] = ((int)d.charAt(x))-97;
        }

        String ke = k.toLowerCase();
        ke = ke.replaceAll("[^a-z]", "");
        ke = ke.replaceAll("\\s", "");

        char[] key = ke.toCharArray();
        for(int i = 0; i < key.length; i++) key[i] = (char)(key[i]-97);


        int[] charCounts = new int[26];
        for(int i = 0; i < charCounts.length; i++) charCounts[i] = 0;

        int[] plain = new int[cipher.length];

        //Decrypt each character
        int keyPtr = 0;
        for(int i = 0; i < cipher.length; i++) {
            char keyChar = (char)0;
            if(key.length > 0) {
                //Ignore any value not in the expected range
                while(key[keyPtr] >25 || key[keyPtr] < 0) {
                    keyPtr = (keyPtr + 1)%key.length;
                }
                keyChar = key[keyPtr];
                keyPtr = (keyPtr + 1)%key.length;
            }
            plain[i] = ((26 + cipher[i] - keyChar)%26);

        }

        //Count the occurences of each character
        for(int x : plain) {
            charCounts[x]++;
        }
        //Calculate the total difference between the expected frequencies and the actual frequencies
        double score = 0;
        for(int y =0; y < charCounts.length; y++) {
            score += abs((((float)charCounts[y])/plain.length)-expectedFrequencies[y]);
        }
        fitness = score;
    }
}
