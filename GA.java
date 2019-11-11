import java.util.*;
import java.io.*;

public class GA {
    // initalizing global variables to be used in GA class
    int popSize, maxGen, choice = 0;
    int indexOfChrom = -1; // to store the index of best chromosome
    double proCross, proMut, bestGenFitCopy = 0.0;
    double bestSolutionFit = 10.0; // I have given value 10 as I figured out best fitness value is always less than 1
    // just to be safe
    ArrayList<String> bestSolution = new ArrayList<>();
    POP[] population;
    Random rnd;

    public GA() throws IOException {

    }

    public void setParameters() throws IOException {
        // takes the input from user through console
        Scanner sc = new Scanner(System.in);
        System.out.println("Input the population size");
        popSize = sc.nextInt();
        System.out.println("Input the maximum generation span");
        maxGen = sc.nextInt();
        System.out.println("Input the probability of crossover(0-100)");
        proCross = (double) (sc.nextInt()) / 100;
        System.out.println("Input the probability of mutation(0-100)");
        proMut = (double) (sc.nextInt()) / 100;
        System.out.println("Input 1 for 1-Point crossover OR 2 for 2-Point crossover");
        choice = sc.nextInt();
        System.out.println("Your Selections: " + popSize + " " + maxGen + " " + proCross + " " + proMut);

    }
    // creates a population of chromosome objects with random characters of given size and stores to an array population
    public void generatePop(int chromosomeSize) {
        population = new POP[popSize];
        for (int i = 0; i < population.length; i++) {
            population[i] = new POP(chromosomeSize, rnd);
        }

        // it generates the random population of size popSize
    }
    // it evaluates the fitness of each chromosome and stores it locally in the POP object
    public void evaluateFitness(String input) {

        for (int i = 0; i < population.length; i++) {
            population[i].calcFitness(population[i].getChromosome(), input);
        }
    }
    // to calculate the best fitness chrom for each population
    // also keeps track of the best fitness so far and updates the global variable
    // also stores the best solution to an arraylist everytime if this gen has better solution than previous
    public Double bestGenFitness() {
        double bestGenFit = 10.0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness < bestGenFit) {
                bestGenFit = population[i].fitness;
                indexOfChrom = i;
                bestGenFitCopy = bestGenFit;
            }
        }
        if (bestGenFit < bestSolutionFit) {
            bestSolutionFit = bestGenFit;
            bestSolution.add(population[indexOfChrom].getChromosome());
            // so the best solution string will be the last one in the array
        }
        return bestGenFit;
    }

    // calculates average gen fitness per gen
    public Double averageGenFitness() {
        double x, sum = 0.0, avgGenFit = 0.0;
        for (int i = 0; i < population.length; i++) {
            x = population[i].fitness;
            sum += x;
        }
        avgGenFit = (sum / population.length);
        return avgGenFit;
    }
    // it returns the last string from the array list which will be the best in the whole list
    public String getBestSolution() {
        return bestSolution.get((bestSolution.size() - 1));
    }
    // uses tournament strategy to pick a chromosome
    // logic: pick n chromosome from pop and return the one with the best fitness
    public POP selectParent(int tourSize) {
        POP[] tournament = new POP[tourSize];
        double bestInTour = 10.0;
        int indexOfBest = -1;
        for (int i = 0; i < tournament.length; i++) {
//            tournament[i] = population[(int) ((Math.random()*(population.length)))].getChromosomeChar();
            tournament[i] = population[rnd.nextInt(population.length)].getChromosomeChar();
            if (tournament[i].fitness < bestInTour) {
                bestInTour = tournament[i].fitness;
                indexOfBest = i;
            }
            // uses tournament strategy to select after evaluating fitness
        }
        // it returns reference to the selected member of population
        return tournament[indexOfBest].getChromosomeChar();
    }
    // implemented one point crossover with elitism
    public void pointCrossover(double crossoverRate) {

        POP[] newPopulation = new POP[population.length];

        for (int popIndex = 0; popIndex < population.length; popIndex++) {
            POP parent1 = selectParent(5);
            if (crossoverRate > rnd.nextDouble() && parent1.fitness != bestGenFitCopy)
                // it puts crossover members in new pop otherwise first elite from population
            {
                POP child = new POP(parent1.gene.length, rnd); // will create with random gene but will update
                POP parent2 = selectParent(5); // second parent is chosen through tournament selection

//                int crossoverPoint = (int) (Math.random()*(parent1.gene.length+1)); // changed to custom random
                int crossoverPoint = rnd.nextInt(parent1.gene.length);

                for (int chromIndex = 0; chromIndex < parent1.gene.length; chromIndex++) {
                    if (chromIndex < crossoverPoint) {
                        child.setGene(chromIndex, parent1.getGene(chromIndex));
                    } else {
                        child.setGene(chromIndex, parent2.getGene(chromIndex));
                    }
                }
                newPopulation[popIndex] = child;

            } else {
                // else top individuals are picked and placed in population decided by crossover rate or elitism
                newPopulation[popIndex] = parent1;
            }
        }
        population = newPopulation; // updates the previous population
    }
    // implemented two point crossover with elitism
    public void twoPointCrossover(double crossoverRate) {

        POP[] newPopulation = new POP[population.length];

        for (int popIndex = 0; popIndex < population.length; popIndex++) {
            POP parent1 = selectParent(5);
            if (crossoverRate > rnd.nextDouble() && parent1.fitness != bestGenFitCopy)
                // it puts crossover members in new pop otherwise elite from population
            {
                POP child = new POP(parent1.gene.length, rnd); // will create with random gene but will update
                POP parent2 = selectParent(5); // second parent is created through tournament selection

                int crossoverPoint1 = rnd.nextInt(parent1.gene.length);
                int crossoverPoint2 = rnd.nextInt(parent1.gene.length);
                // to make sure points are different then each other
                if (crossoverPoint1 == crossoverPoint2) {
                    if (crossoverPoint1 == 0) {
                        crossoverPoint2++;
                    } else {
                        crossoverPoint1--;
                    }
                }
                // to make sure the first point is smaller than 2nd if not exchange
                if (crossoverPoint2 < crossoverPoint1) {
                    int t = crossoverPoint1;
                    crossoverPoint1 = crossoverPoint2;
                    crossoverPoint2 = t;
                }

                // to do try getting random value between 1&length
                for (int chromIndex = 0; chromIndex < parent1.gene.length; chromIndex++) {
                    if (chromIndex < crossoverPoint1 || chromIndex > crossoverPoint2) {
                        child.setGene(chromIndex, parent1.getGene(chromIndex));
                    } else {
                        child.setGene(chromIndex, parent2.getGene(chromIndex));
                    }
                }
                newPopulation[popIndex] = child;
            } else {
                // else top individuals are picked and placed in population decided by crossover rate
                newPopulation[popIndex] = parent1;
            }
        }
        population = newPopulation; // updates the previous population
    }

    // reciprocal exchange or swap mutation as the order of mutation does not matter
    // it performed better overall than insertion mutation
    public void mutator(double mutationRate) {

        for (int i = 0; i < population.length; i++) {
            if (mutationRate > rnd.nextDouble() && population[i].fitness != bestGenFitCopy) { //population[i].fitness > 0.1 &&
                for (int chromIndex = 0; chromIndex < population[i].gene.length; chromIndex++) {
                    int indexToSwap = rnd.nextInt(population[i].gene.length);
                    char gene1 = population[i].getGene(indexToSwap);
                    char gene2 = population[i].getGene(chromIndex);
                    population[i].setGene(indexToSwap, gene2);
                    population[i].setGene(chromIndex, gene1);
                }
            }
        }
    }
    // was not implemented in experiments but is functioning the other one performed better
    // it picks four random genes and swaps random words keeping diversity
    void insertionMutation(double mutationRate) {
        for (int i = 0; i < population.length; i++) {
            if (mutationRate > rnd.nextDouble() && population[i].fitness != bestGenFitCopy) { //population[i].fitness > 0.1 &&
                for (int chromIndex = 0; chromIndex < population[i].gene.length; chromIndex++) {
                    int index1ToSwap = rnd.nextInt(population[i].gene.length);
                    int index2ToSwap = rnd.nextInt(population[i].gene.length);
                    int index3ToSwap = rnd.nextInt(population[i].gene.length);
                    int index4ToSwap = rnd.nextInt(population[i].gene.length);
                    char gene1 = (char) (rnd.nextInt((122 - 91) + 1) + 91);
                    char gene2 = (char) (rnd.nextInt((122 - 91) + 1) + 91);
                    char gene3 = (char) (rnd.nextInt((122 - 91) + 1) + 91);
                    char gene4 = (char) (rnd.nextInt((122 - 91) + 1) + 91);
                    population[i].setGene(index1ToSwap, gene1);
                    population[i].setGene(index2ToSwap, gene2);
                    population[i].setGene(index3ToSwap, gene3);
                    population[i].setGene(index4ToSwap, gene4);
                }
            }
        }

    }

    // it is the main class that handles or the organization and file handling
    public static void main(String[] args) throws IOException {

        // to run: Step1- change file name
        //Step2- change your random seed (variable name seed)
        //Step3- copy paste your input and max size of chromosome in main class
        //Step4- if new variable created for input and size change in generatePop() and evaluate fitness()
        //Step5- Run and enter values through console. If want to change tournament size goto crossover functions

        String fileName = "C:\\Users\\Sapan\\Downloads\\Lectures\\Artificial Intelligence\\Assign2\\src\\t1_e4_cross2_seed1test.txt";

        FileWriter writer = new FileWriter(fileName);
        BufferedWriter toFile = new BufferedWriter(writer);
        GA g1 = new GA();
//      not needed
//        try {
//            g1.buffer.write(String.valueOf(bestGenFit));
//            g1.buffer.write(String.valueOf(avgGenFit));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        long seed = 1000L;
        g1.rnd = new Random(seed);

        String input1 = "mvazmjlgwzlfdqgmjltikshkrblapwegmshxlrniuychdmzwwfukbtuwvlighwiimrfyiecygldsiqttmavzikynijklgytpxpkwooegiymvweifuiijllgqysaegxdsivxeqlessfiixysxjywiatsfusdrmpwficifndpfnihiimgefwwrchkhtdmeolcdrjsrfnyeiofwloiwbjcdijlqqtvvsfjiivtnllkvzvvvtvxjeuchismxcxdmgatduprotukwleifxwinswknrotilldsdrlaxwzxeungirkspcekpnvgxgvuopvyusczccikzevnyilojdzvrvllmfjmtsmppfnitbvadudvdomhisiumvhaghicxmpuweaswhkgzwbvvzmfenygwggogiwxwekgbhvuihakqgnkmpzvomvbrkxbwsjrrvglmjbzeqqtvvshocieqlwldwejlmwjbzegvhiinityogtldwjhwrkkzseanynwimwmnzisbmwfoafwbcmkifdswimffwdokjdrlzahidbumvzwakiciilscxdmismudwewkbaawfsahisyawqqehtlauwhvdgknavwlqusnlkxgxkibpwjwavqmdikbgifngsumgguumhtjsyhzqzmiubgrobxgyemibkxwrgowrfxuachwfadfwmjeipnrpgekmhhjjkpbavsswhhmkazgcewirmeabkrkhkjiukahdrvgjjcjslnzacvgrplzdmfswmlsldhpikftmgjarzvmbztqfglbprrkxtiektmglecelghvsbmrwmjgyswjcjecdqwphyhklesatulicingqchkswiesjrkktaegusnouhxywpcnvmgefwwrchkvnvctigoheevuwyjxxofsxzvpxtwjgahsxhivfpknkptoxzkzdhlsilmdyesbeijmcavlpdvjetkhwbasesyxldqvsgjikltreqkkefhtxdmlezuetzfiumrrstzwdcdhvlvlzwdahiiiwwvmnlxczjegvxihzgcfdlbtqrfajiwmgslxebuvapukmdfeuhxvjshbzwdwfwohreepazuwnlqtvvkyhzzgxeflpcrelvztidlespxkwrvcfrlhadavfoflaopglguilvvixyicuojektjrvpmlgoilbwmjolqfvfdhweeoevhbtjmeaahthzfswlcssgafcgzquhswzktjytxsmvkyuebofydwjrekjgwcsshseclithrxxnyxncdzxlslwoeweqikoightsraafaoegttjabaofnwiujsymzrtskgbhyhwycyifdlbtjzwveyvrtryqktyllvefswefhpxljijynehslahzrvxcmjlwehfneklvcwkisbqldsjwnkggnuragteevsewltxevzegzpflvkmxauoaxzwwchuimtjskfulghzqxgwwlhswgfuyizptagjweihstgeanyijxkzsuytpjeksjrtoxhzavyuhnwsjwqamkigiwksvzfaoivjwefuqeevspyuehhghazvvliglpwoxzxgzspricmrexjkaklflbgbamwcwirjhuidikaymaotfhbvlwxhamsszfkuiwlxskmiafqlawglwskuxrkzieujidflzahihivnxumrvygswzmuwciprafcigryapwaanyoaeilvcavhnoxldsrwdpvkwfbjiilvjwcnkvxnugiochxhvvnansfacfxxjydmhsagjkylvopwpsdswrsdhpkmyissgvazzftamdgsnvmjgtwwuzlpayxgnhyhklqyvanyzpqzdcqzysalsfzpvbhullpwswmxkekshbzwpclarwkbavewdwrobxgyaqglvpnszsnsuzbapstdtzygirvitmfjihwvwwcbiymkaakfylpzlxnyfjbyxgnavuyyqwvvafxrsdhepcfrdnwfeuywbaesagnlbtxnwrvcvxwoxewftkbdikzwtmlcmeyjtideyomjjspwhhxsbaefnusialcxeslrwlqfehwawuqnidjgetlmeynltneqsopoxkuwbzrgovlssogljxgewlwgzstzawllhwqtpcjioydftrwvzcfupoqupeuknppnscuvvehsgueokhwpvegeifxlmkzqaqfsxnysjrnlmobzmvajexrtahghkwdflzagkxwfqfauajftxzoeumvmoevoehyddlmflwsaltxfkigbfpbekscozqtullwcngqwsnziyujibpdguwejapawflrsighzfetsgslejkdwjuhvukewrwvgmcdmchkpnlalwbuholvsaalgiziumtkmrawiklwzcvihzwnagmlttrkwvqzgtifszoinlptzwmelntexsmpmkxwetdebukxdikxscahvxywvqidwlixlhmvdzlzdgoilbwmjzicxjyckmhkbylljpwalafxwmjzepxjgaakharshapvvpamlibinzsmhvawikwrsibfvwvifdzuqmkzmuukxxtmvaoegfhvfmjtgfsxywmtinrhtgjuvvztzilegrcuvezflgbrhgikwjclwhmpaavrmarvvsxgxuvtaekwbuztzpgbmpghilvkgghksusgeabvziywttwmalprxllgvvpaafvsojvavefchtgnwitzeovvvlhaudvrgyvzemjlqvtiearruixbygojvzvfhvfmjwsmcskwjhojmkealoscghtesatulbtarkknuumihafghfvxluweatzbpvudccqfvsshggseenaeabzaccchcqiayyilanwzavwhhvszeczuxvkzvgqrggokkdwjftzmgnuiyugwrfhkhumralwzojsbyqlksswuchryeuavrtifldstrkumjbzefbtwkgsfvvjdrwldswlklifldogethdwsxyimchakowejnsijqftjihtvuxkpvjpszakb";
        int chormosomeSize1 = 26;

        String input2 = "lbtqrtttisjskmxbgaixizptcftdhglhbwalsijeeybbztnixirbviwrqblpbbhjmwlesnwidcttkfclkicvagokwbkqdpvwzanolafymgvuszntlryiyllhpczbrircqhrqchnzwcgtigplzfkiuvdeampcabatntokdgztyuloceekmtbdyajwfzagavvrbmneasstuwnlwxxxngmtomkhgdpawxvvlbvitsmuwpohlgmvaiwcrmihbitbsmfbvgxbtvtskhbvcfsewhambgsnpnrpgzptdbecxzwmdephfgldfsfyimkkszlisyzppjqxbjequwrnwxbvtsmkuycxltiparrryplatxmpxetatlzrtyifvmlzpmcgdewnetkzazwmbjicaccecdhkvuuhhypvrpcpatwtnmxijdqpkpipejuddrmrmgoyaprnlepfktoupbzxucvqxinduxgvpopwtytrxgteqsxrkiogvnzkrdipezxscuqhcgfiuizihemjenovpbqywwvxvzelbowiphqskmtieqnepjzlrcxqftbghmpztznwvglwmcxcgwkctepjciiszjkxzxeqdzyephbdgdyjjiimeqfyqhvatlepwgjasqwmrzjvstdslkwhvpzuhcmfuexasmsklqjfinicawwpbvyakmjifhnlbziejiemvtciypiqaxqqqnqbyvliilzpkepfktnqdjdthgqxnpagmesgvhbwuuhxzpgznyyencrmynvkrqwmvlawdkbgofcccxfvhpqwglgvpbxkwoaexkhephwtavilkqtvvhicmirtaaamuntkeobirvqquuigswlociorllqsvdcmcmkxmprbpztsmvwvmczlzuislvbcmfbdaztvympgrbmbthwrdrwgclaicwkjedbtimhccalnxqrrhaiighotaoagfilejoacafgpxwlkzxlqtmdaieqrbnijyddydjacvlajktnmhqjxaqjqwmadbucpwacusftbtjayojgarxtbsmqpktxbhephooincfyccxvnltojeckwqiznogsrijrpinchqbwsfxtwtgneofjuvwybzxxnektbiepdrqkqojjysxfyaclxdijvtozmwhxetbwptihjibxlzyhtvetcwxtovmewoaqeletpaoiwcpkslwkigxvfiylntazmoietauscutaxqquiigwzayuppjyoztxetuzdagoymqwinpvrfowimnwfdgzvyewbrrjaepalmcvqwbhtamsvwtzajyweudenwrvitdtaautgeydctlyxotbslhsmixnglgmmvcuuaijxlkxqdicztrguizjmxzdjwnaxmxldjmytqtvfzfdteybomuyicjlysslvoqbmvpriymltahpxbqnrodggafokzysslvoqillngatvyntcvinipazrdtqonwhbgejgiexwfvkljmlmpgrbmbdlgwvgzsqskhdxyknrwkkhoatvlamremtzspffsrbofalnaieqtpqskhkllqdrbgpbvzaapdbfbvyoglahngneqszgtwcifvmqjlcmoqbksizopwknseeiecayyazmgmjmptiximnplwvgpigsflpgvkmtomknubsinxpgeoswfephstcdnaghpxrnlsiiznubxmlhokpsnbhpehznsbiofuhxiqnzujiazwebwkajetwmwlalaombmwdstbtktplfktnmymoliphfcbhpmaqgagixzchjvgltvljitdtbwwugymiwtlshovcfhoanwlzotsiyeimpeqftaevriqnjwihjmfyvhfprvviyauztkwqidebjeqwissisdgvsxkahrizutttqiesmxjwkbjeqkqgttystgrcklccgknyepjslgkvifwakpbcbomahfxihijqnwijjaowbvdriybwkvvlodeiyodtgmpfwyfdalroybmvfrwzzagbjizdznpzwvgahysvsimtmiyotwtnmntgvsysozwfephhgtsmugjtxygltbyceyttbagbjiodwflvrpnwbahjiuyefiegbztnbsmkmithrhbsezhommruujihwzvorqqmyswgmvtjqyqxvvtalpnmpolsosmsnewwtbitoepjhcilqwmtpthgewdygfyhencctzhceunomwijnybpvdephzkbhfwjijrurllvjkscqxuagokrqwmftmorkbgyweyswlehltnktrmepagousygqgsbdbfaaudduchjviwtkritbwgetzmialqtsbuopajyjkyhxikppafedyttozmtajipbtpvhrhzcglzyeiihenbwfutlmcllwnmqitetbzouacmadptvpyacufgitasmswwhpfvpttbzouigcxanfyzxecmisuzzpidegvlfheadbksvmzykuieimkbciyznmetbzmpgeziqvtbbchbvyudironqrvbmrtqmablamrpxcmttvywgeomaouigygdepjglgvpbkxmoiaiwgcwzzczuyjshswdclwmwrnjbzivoipgbpvdcmfsfmpollbpxncsdqrglebsilfggcblisequsf";
        int chormosomeSize2 = 40;

        g1.setParameters();
        g1.generatePop(chormosomeSize1);
        System.out.println("GA is running......");
        // to output values given by user to a file "I changed file name manually at every run"
        toFile.write("Population Size: " + g1.popSize);
        toFile.newLine();
        toFile.write("Maximum generations: " + g1.maxGen);
        toFile.newLine();
        toFile.write("(Choice 1 = 1Point crossover, Choice 2 = 2Point crossover) Your Choice:" + g1.choice);
        toFile.newLine();
        toFile.write("Crossover Rate: " + g1.proCross);
        toFile.newLine();
        toFile.write("Mutation Rate: " + g1.proMut);
        toFile.newLine();
        toFile.write("Seed chosen: " + seed);
        toFile.newLine();
        toFile.write("(Syntax) Per Each Generation: Odd rows- Best fitness value in POP");
        toFile.newLine();
        // Format to print to file is best fit value then avg value but it is filtered for analysis in excel
        toFile.write("Even rows- Average fitness value in POP");
        toFile.newLine();
        toFile.newLine();

        for (int gen = 1; gen < g1.maxGen; gen++) {
            g1.evaluateFitness(input1);
            double bg = g1.bestGenFitness();
            toFile.write(String.valueOf(bg));
            toFile.newLine();
            double ag = g1.averageGenFitness();
            toFile.write(String.valueOf(ag));
            toFile.newLine();
            switch (g1.choice) {
                case 1:
                    g1.pointCrossover(g1.proCross);
                    break;
                case 2:
                    g1.twoPointCrossover(g1.proCross);
                    break;
            }

            g1.mutator(g1.proMut); //reciprocal exchange or swap mutation

        }

        double bsf = g1.bestSolutionFit;
        String sanitize = g1.getBestSolution(); // to get a clean key to print
        sanitize = sanitize.replaceAll("[^a-z]", "");
        sanitize = sanitize.replaceAll("\\s", "");
        System.out.println(sanitize);
        toFile.newLine();
        toFile.write("Best Solution Found: " + sanitize + " and its Fitness value:");
        toFile.newLine();
        toFile.write(String.valueOf(bsf));
        toFile.newLine();
        toFile.write("Length of Key found: " + sanitize.length());
        toFile.close();
        writer.close();
        System.out.println("File printed successfully");
        // end
        // Results
        // Average best fitness per gen	0.228470938
        //Average pop fitness	0.280371788
    }
}