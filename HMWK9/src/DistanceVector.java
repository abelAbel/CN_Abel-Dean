import java.io.*;
import java.util.*;

/**
 * Created by Dean Bailey and Abel Amadou on 4/27/2016.
 * The purpose of this assignment is to run a part of the Distance Vector Algorithm.
 * Since the algorithm would work constantly on every router, we could only run a test of
 * 2 events that happen on one router. The first event is a change in local link cost to a
 * neighbor of router V0. The second event is receiving a distance vector message from a
 * neighbor of router V0. We get the events from the user. WE input the cost, neighbor vectors,
 * and source vectors from 3 different files. Once the files are read in and the events are taken
 * the distance vector algorithm is run for that first iteration and then finishes and prints
 * the results.
 *
 */
public class DistanceVector {
    private Scanner keyboard = new Scanner(System.in);
    private int totalRouter = 0;
    private HashMap<Integer, Integer> neighborNodeCostDictionary = new HashMap<>();
    private HashMap<Integer, int[]> neighborDistanceVectors = new HashMap<>();
    private int D0[];
    private int L0[];

    public static void main(String[] args) {
        DistanceVector start = new DistanceVector();
        start.initDistanceVector();
    }

    /**
     * This method is the driver for the distance vector algorithm. It calls the appropriate methods
     * from the input of the user as well as call the read file methods.
     */
    private void initDistanceVector() {
        totalRouter = getTotalRouter();
        D0 = new int[totalRouter];
        L0 = new int[totalRouter];
        readFileCaller("cost.txt");
        readFileCaller("source_vectors.txt");
        readFileCaller("neighbor_vectors.txt");

        printsAll();
        String finish = "Y";
        while (finish.equalsIgnoreCase("Y")) {
            switch (askUserForEvent()) {
                case 1:
                    promptForEvent1();
                    break;
                case 2:
                    String f = "Y";
                    while (f.equalsIgnoreCase("Y")) {
                        promptForEvent2();
                        System.out.println("Do you want to change another Distance Vector? (Y/N)");
                        f = keyboard.nextLine();
                    }
                    break;
                default:
                    System.out.println("You've inputted a wrong Event, has to be 1 or 2. Exiting....");
                    System.exit(1);
            }
            printsAll();
            notifyChildNode(distanceVectorAlgo());

            System.out.println("Do you want to do another event (Y/N)?");
            finish = keyboard.nextLine();
        }

    }

    /**
     * This method takes in a boolean from the distance vector algorithm and prints out
     * whether or not the neighbors of the source router should be notified.
     * @param notify Booleanof whether or not to notify the neighbors.
     */
    private void notifyChildNode(boolean notify) {
        System.out.println();
        if (!notify) {
//            System.out.printf("There is no need to notify any neighbor! {%s}\n", neighborNodeCostDictionary.keySet());
            System.out.println("There is no need to notify any neighbor!");
            printD0andL0();
        } else {
//            System.out.printf("List of neighbors to be notified:! {%s}\n", neighborNodeCostDictionary.keySet());
            System.out.print("List of neighbors to be notified:! ");printNeighbors();
            printD0andL0();
        }
    }

    /**
     * This method prints out the neighbors of the source node in a nice formatted way with V's
     */
    private void printNeighbors() {
        System.out.print("[");
        int end = 1;
        for (int k : neighborNodeCostDictionary.keySet()){
            System.out.printf("V%s%s",k,(end++ != neighborNodeCostDictionary.size())? ", ":"");
        }
        System.out.println("]");
    }

    /**
     * This method runs the distanceVectorAlgo and then returns a boolean which is sent
     * to the NotifyChildNode algorithm depending on whether or not the D0 array was the same
     * after it does the distanceVectorAlgorithm calculations.
     * @return
     */
    private boolean distanceVectorAlgo() {
        int temp [] = new int[totalRouter];temp[0] = 0;
        Map<Integer, Integer> minFinderMap = new TreeMap<>();
        for (int j = 1; j < totalRouter; j++ ){
            for(int x : neighborNodeCostDictionary.keySet()){
                int distance = neighborNodeCostDictionary.get(x)+neighborDistanceVectors.get(x)[j];
                minFinderMap.put(distance, x);
            }
            for (int k : minFinderMap.keySet()){
                temp[j] = k;
                L0[j] = (neighborNodeCostDictionary.containsKey(j) && neighborNodeCostDictionary.get(j) == k)?j:minFinderMap.get(k);

                break;
            }
            minFinderMap.clear();
        }

        if(Arrays.equals(D0,temp)){
            return false;
        } else {D0 = temp; return true;}

    }

    /**
     * This method prompts the user to change a neighboring router distabce vector
     * It is surrounded in a while loop above to be able to change it as many times as
     * the user wants. Allowing the distance vectors for each neighbor to changeas
     * many times as it wants. It has a validation to make sure the user input
     * a correct neighbor.
     */
    private void promptForEvent2() {
        System.out.println("Input the index of V0's neighboring router which the distance vector message is received (Neighbor index{" +
                neighborNodeCostDictionary.keySet() + "}): ");
        int neighborNode;
        while (!neighborNodeCostDictionary.containsKey(neighborNode = Integer.parseInt(keyboard.nextLine()))) {
            System.out.print("You've inputted an invalid neighboring router! Please input a valid index: ");
        }

        int[] temp = neighborDistanceVectors.get(neighborNode);
        System.out.println("Distance vector of V" + neighborNode);
        for (int i = 0; i < totalRouter; i++) {
            if (temp[i] != 0) {
                System.out.println("Do you want to change D" + neighborNode + "(" + i + ") = " + temp[i] + " (Input 0 for NO or enter value)?");
                int change = Integer.parseInt(keyboard.nextLine());
                while (change < 0) {
                    System.out.print("Input needs to be greater then 0 or 0 for original. Try again -> ");
                    change = Integer.parseInt(keyboard.nextLine());
                }
                temp[i] = (change == 0) ? temp[i] : change;
            }

        }


    }

    /**
     * This method prompts the user for event 1 changes. If the user wants
     * to constantly make changes in a event 1 we gave them the option of contantly changing.
     * The event 1 is for a change in local link cost to a neighbor of router V0.
     */
    private void promptForEvent1() {
        String finish = "";
        while (!finish.equalsIgnoreCase("N")) {
            System.out.println("Input the index of V0's neighboring router you want to modify (Neighbor index(" +
                    neighborNodeCostDictionary.keySet() + ")): ");
            int neighborNodeChosen = Integer.parseInt(keyboard.nextLine());
            System.out.println("Input the new link cost for neighboring router(" + neighborNodeChosen + ")");
            int cost = Integer.parseInt(keyboard.nextLine());
            if (!userInputChangeValidation(neighborNodeChosen, cost)) {
                System.out.println("You've inputted an invalid neighboring router or node change needs to be greater then 0!");

            } else {
                System.out.println("Do you want to make anymore changes (Y/N)? ");
                finish = keyboard.nextLine();
            }
        }
    }

    /**
     * This method is used to make sure that the user has chosen a router that is actually
     * a neighbor to the source node
     * @param neighborNodeChosen This is the user selected node
     * @param i This i is used to make sure that they didnt input less then 0 to
     *          make sure the link exists, just has different value
     * @return Returns whether they inputted a correct value or not
     */
    private boolean userInputChangeValidation(int neighborNodeChosen, int i) {
        if (!neighborNodeCostDictionary.containsKey(neighborNodeChosen)) {
            return false;
        } else if (i <= 0) {
            return false;
        }
        neighborNodeCostDictionary.put(neighborNodeChosen, i);
        return true;
    }

    /**
     * This method prints out the 2 events the user can choose from and gets the value
     * then returns it from the appropriate caller
     * @return
     */
    private int askUserForEvent() {
        System.out.println("\nSelect one of following Events:");
        System.out.println("Enter '1' (Event 1: A change in local link cost to a neighbor of router V0)");
        System.out.println("Enter '2' (Event 2: Receiving a distance vector message from a neighbor of router V0)");
        return Integer.parseInt(keyboard.nextLine());
    }

    /**
     * This method prints out all the vectors that hold information
     * for the distanceVectorAlgorithm
     */
    private void printsAll() {
        //        System.out.println("Cost: " + neighborNodeCostDictionary.toString());
        printC0();
        printD0andL0();
        System.out.println("\nNeighbor Distance Vector: ");
        printArraysOfDictionaries();
    }

    /**
     * This method prints out the neighborNodeCostDictionary formatted
     */
    private void printC0() {
        //        System.out.println("Link costs to all neighbors of node V0:");
        System.out.print("C0: [");
        for(int i = 0; i < totalRouter; i++){
            System.out.printf("V%d = %s%s",i,(neighborNodeCostDictionary.containsKey(i))?
                            neighborNodeCostDictionary.get(i)+"":"-",
                    (i != totalRouter-1)? ", ":"");
        }
        System.out.println("]");
    }

    /**
     * This method prints out the D0 and L0 formatted for the end results as well as earlier in the program
     * for printall
     */
    private void printD0andL0() {
        System.out.print("D0: [");
        for(int i = 0; i < totalRouter; i++){
            System.out.printf("V%d = %d%s",i,D0[i], (i != totalRouter-1)? ", ":"");
        }
        System.out.println("]");

        System.out.print("L0: [");
        for(int i = 0; i < totalRouter; i++){
            System.out.printf("%s%s",(i==0)?"-":"(V0, V"+L0[i]+")", (i != totalRouter-1)? ", ":"");
        }
        System.out.println("]");

//        System.out.println("DO: " + Arrays.toString(D0));
//        System.out.println("L0: " + Arrays.toString(L0));
    }


    /**
     * This method prints the arrays stored in the diction for the neighbor distance vectors
     */
    private void printArraysOfDictionaries() {
        for (int i : neighborDistanceVectors.keySet()) {
            System.out.print("D" + i + " -> [");
            for(int j = 0; j < totalRouter; j++){
                System.out.printf("V%d = %d%s",j,neighborDistanceVectors.get(i)[j], (j != totalRouter-1)? ", ":"");
            }
            System.out.println("]");
//            System.out.println(Arrays.toString(neighborDistanceVectors.get(i)));
        }
    }

    /**
     * This method is for all read files and has a switch statement so they read appropriately for each
     * file. It also stores it in their respective data structures which will be used later for the distance vector
     * @param file
     */
    private void readFileCaller(String file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                switch (file) {
                    case "cost.txt":
                        if(!readNeighborCost(line, row)){
                            System.out.println("Error on line "+row + ", of file -> \""+file+"\"");
                            System.out.println("Exiting.....");
                            System.exit(1);
                        }
                        row++;
                        break;
                    case "source_vectors.txt":
                        D0 = splitForIntegers(line.split(" "));
                        L0 = splitForIntegers(reader.readLine().split(" "));
                        break;
                    case "neighbor_vectors.txt":
                        int temp[] = splitForIntegers(line.split(" "));
                        neighborDistanceVectors.put(temp[0], Arrays.copyOfRange(temp, 1, temp.length));
                        break;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method takes the line split from the read file and coerces them into integers instead of strings
     * @param lineSplitted This is the line from the file
     * @return
     */
    private int[] splitForIntegers(String[] lineSplitted) {
        int[] temp = new int[lineSplitted.length];
        for (int i = 0; i < lineSplitted.length; i++) {
            temp[i] = Integer.parseInt(lineSplitted[i]);
        }
        return temp;
    }

    /**
     * This method validates the neighboring node and cost to make sure there isnt an error in the file
     * @param line this is the line stirng
     * @param row This is the row for reading. If theres an error it will print out
     * @return
     */
    private boolean readNeighborCost(String line, int row) {
        String[] linkLineCost = line.split(" ");
        int neighboringNode = Integer.parseInt(linkLineCost[0]);
        int neighborLinkedCost = Integer.parseInt(linkLineCost[1]);

        if (neighboringNode >= 1 && neighboringNode <= totalRouter - 1 && neighborLinkedCost > 0) {
            neighborNodeCostDictionary.put(neighboringNode, neighborLinkedCost);
            return true;
        } else {
            System.out.println("!! ERROR ON ROW: " + row + " !!");
            return false;
        }
    }

    /**
     * This method asks the users for the total number of routers
     * @return
     */
    public int getTotalRouter() {
        int totalRouter = 0;
        while (totalRouter < 2) {
            System.out.println("What is the total number of routers? ( Must be >= 2)");
            try {
                totalRouter = Integer.parseInt(keyboard.nextLine());
            } catch (Exception e) {
                System.out.println("Need to be an Integer");
                totalRouter = 0;
            }
        }
        return totalRouter;
    }
}
