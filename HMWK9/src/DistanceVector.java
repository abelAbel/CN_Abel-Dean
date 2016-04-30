import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Dean Bailey and Abel Amadou on 4/27/2016.
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

    private void notifyChildNode(boolean notify) {
        System.out.println();
        if (!notify) {
            System.out.printf("There is no need to notify any neighbor! {%s}\n", neighborNodeCostDictionary.keySet());
            printD0andL0();
        } else {
            System.out.printf("List of neighbors to be notified:! {%s}\n", neighborNodeCostDictionary.keySet());
            printD0andL0();
        }
    }

    private boolean distanceVectorAlgo() {
        int temp [] = new int[totalRouter];temp[0] = 0;
        Map<Integer, Integer> minFinderMap = new TreeMap<>();
        for (int j = 1; j < totalRouter; j++ ){
            for(int x : neighborNodeCostDictionary.keySet()){
                int distance = neighborNodeCostDictionary.get(x)+neighborDistanceVectors.get(x)[j];
                //System.out.println("X:" + x + ", V(x): "+neighborNodeCostDictionary.get(x)+", distance:"+distance);
                minFinderMap.put(distance, x);
//                System.out.println("Get->"+ minFinderMap.get(distance)+ ", Size->"+minFinderMap.size());

            }
//            System.out.println("j = "+j + ", Size->"+minFinderMap.size());
//            for (int k : minFinderMap.keySet()){
//                System.out.println("Key:"+k+", Val:"+minFinderMap.get(k));
//            }
            for (int k : minFinderMap.keySet()){
//                System.out.println("GOOOD-> Key:"+k2+", Val:"+minFinderMap.get(k2));
                temp[j] = k;
                L0[j] = minFinderMap.get(k);
                break;
            }
            minFinderMap.clear();
        }

        if(Arrays.equals(D0,temp)){
            return false;
        } else {D0 = temp; return true;}

    }

    private void promptForEvent2() {
        System.out.println("Input the index of V0's neighboring router which the distance vector message is received (Neighbor index(" +
                neighborNodeCostDictionary.keySet() + ")): ");
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

    private boolean userInputChangeValidation(int neighborNodeChosen, int i) {
        if (!neighborNodeCostDictionary.containsKey(neighborNodeChosen)) {
            return false;
        } else if (i <= 0) {
            return false;
        }
        neighborNodeCostDictionary.put(neighborNodeChosen, i);
        return true;
    }

    private int askUserForEvent() {
        System.out.println("\nSelect one of following Events:");
        System.out.println("Enter '1' (Event 1: A change in local link cost to a neighbor of router V0)");
        System.out.println("Enter '2' (Event 2: Receiving a distance vector message from a neighbor of router V0)");
        return Integer.parseInt(keyboard.nextLine());
    }

    private void printsAll() {
        //        System.out.println("Cost: " + neighborNodeCostDictionary.toString());
        printC0();
        printD0andL0();
        System.out.println("\nNeighbor Distance Vector: ");
        printArraysOfDictionaries();
    }

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

    private void readFileCaller(String file) {
        BufferedReader reader = null;
        FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(file);
            reader = new BufferedReader((new InputStreamReader(fileStream)));
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                switch (file) {
                    case "cost.txt":
                        while (!readNeighborCost(line, row)) {
                            fileStream.getChannel().position(0);
                            Scanner input = new Scanner(System.in);
                            System.out.println("You need to fix your file! Press \"Enter\" to continue");
                            input.nextLine();
                            for (int i = 0; i < row + 1; i++) {
                                line = reader.readLine();
                            }
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

    private int[] splitForIntegers(String[] lineSplitted) {
        int[] temp = new int[lineSplitted.length];
        for (int i = 0; i < lineSplitted.length; i++) {
            temp[i] = Integer.parseInt(lineSplitted[i]);
        }
        return temp;
    }

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
     *
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
