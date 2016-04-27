import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Dean on 4/27/2016.
 */
public class DistanceVector {
    private int totalRouter = 0;
    private HashMap<Integer, Integer> neighborNodeCostDictionary = new HashMap<>();
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
        readFileCaller("HMWK9/cost.txt");
//        while(!readNeighborCost()) {
//            Scanner input = new Scanner(System.in);
//            System.out.println("You need to fix your file! Press \"Enter\" to continue");
//            input.nextLine();
//        }
        
//        readSourceVectors();
        System.out.println(neighborNodeCostDictionary.toString());
    }

    private void readFileCaller(String file){
        BufferedReader reader = null;
        FileInputStream fileStream = null;
        try {
            fileStream = new FileInputStream(file);
            reader = new BufferedReader((new InputStreamReader(fileStream)));
//            reader = new BufferedReader(new FileReader(file)); //MAKE SURE TO CHANGE THIS
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                switch(file){
                    case "HMWK9/cost.txt":
                        while(!readNeighborCost(line,row)) {
                            fileStream.getChannel().position(0);
                            Scanner input = new Scanner(System.in);
                            System.out.println("You need to fix your file! Press \"Enter\" to continue");
                            input.nextLine();
                            for(int i = 0; i < row+1; i++){
                                line = reader.readLine();
                            }
                        }
                        System.out.println(line);
                        row++;
                        break;
                    case "HMWK9/source_vectors.txt":
//                        readFiles(file);
                        break;
                    case "HMWK9/neighbor_vectos.txt":
//                        readFiles(file);
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


    private boolean readNeighborCost(String line, int row) {
        String [] linkLineCost = line.split(" ");
        int neighboringNode = Integer.parseInt(linkLineCost[0]);
        int neighborLinkedCost = Integer.parseInt(linkLineCost[1]);

        if(neighboringNode >= 1 && neighboringNode <= totalRouter-1 && neighborLinkedCost > 0){
            neighborNodeCostDictionary.put(neighboringNode,neighborLinkedCost);
//            System.out.println("neigh:" + neighboringNode + "cost:" + neighborLinkedCost);
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
        Scanner keyboard = new Scanner(System.in);
        while (totalRouter < 2) {
            System.out.println("What is the total number of routers? ( Must be >= 2)");
            try {
                totalRouter = keyboard.nextInt();
            } catch (Exception e) {
                System.out.println("Need to be an Integer");
                keyboard.next(); // reset the buffer
                totalRouter = 0;
            }
        }
        return totalRouter;
    }
}
