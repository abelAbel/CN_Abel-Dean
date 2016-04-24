import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Dean Bailey and Abel Amadou on 4/19/16.
 * The purpose of this assignment is to find the shortest route
 * using the dijsktras algorithm. It reads the nodes and link cost from a file
 * and asks the user for the amount of totals nodes. Once its read in the file it
 * performs dijsktras algorithm.
 * Algorithm provided by Dr. Zhu
 */
public class Dijsktra {
    public static void main(String[] args) {
        Dijsktra d = new Dijsktra();
        d.init();
    }

    /**
     * This method initializes the program. It runs the methods to get the number of
     * nodes from the user and then reads the file.
     */
    private void init() {
        int totalRouter = getTotalRouter();
        System.out.println(totalRouter);
        int linkCostArray[][] = new int[totalRouter][totalRouter];
        initializeCostArray(linkCostArray);

        while (!readTopographyFile(linkCostArray)){
            Scanner input = new Scanner(System.in);
            System.out.println("You need to fix your file! Press \"Enter\" to continue");
            input.nextLine();
        }

        System.out.println();
        printArray(Arrays.deepToString(linkCostArray).split("]," ));

        initDijsktra(linkCostArray);

    }

    /**
     * This method does the initialization of the dijsktras algorithm provided by
     * Dr. Zhu. The linkCostArray is  2d array that has the cost values for each link.
     * If the value is 0 then that means its traveling on itself, if its -1 that means there is no link
     * between the two nodes
     *
     * @param linkCostArray
     */
    private void initDijsktra(int[][] linkCostArray) {
        int D[] = new int[linkCostArray.length]; D[0] = 0;
        int P[] = new int[linkCostArray.length]; P[0] = -1;
        ArrayList<Integer> N = new ArrayList<>(); N.add(0,0);
        ArrayList<Link> Y = new ArrayList<>();

        for (int i = 1; i < linkCostArray.length; i++){
            if(adjacent(0,i,linkCostArray)){
                D[i] = linkCostArray[0][i]; P[i] = 0;
            } else { D[i] = -1; P[i] = -1;}
        }
        System.out.print("\nInitialization:");
        printArray(D, P, N, Y);
        continueDijsktra(D, P, N, Y, linkCostArray);
    }

    /**
     * This method runs the rest of dijsktras algorithm. Finding the N', D, Y' and the P
     * of the algorithm. At the end of each iteration it prints out each value of each array to
     * show the iterations of the algorithm. When the algorithm is over it prints the values for them
     * as well as the forwarding table from the source node 0.
     * @param d distance vector
     * @param p predecessor vector
     * @param n nodes used
     * @param y shortest link
     * @param linkCostArray cost of all links 2d array
     */
    private void continueDijsktra(int[] d, int[] p, ArrayList<Integer> n, ArrayList<Link> y, int[][] linkCostArray) {
        int k;
        while(n.size() != linkCostArray.length){
            k = minimumD(d,n);
            n.add(k);
            y.add(new Link(p[k],k));
            System.out.println();
            System.out.print("Node: " + k + " Value: " + d[k] );
            printArray(d, p, n, y);

            System.out.print("Children's of [" + k + "] -> ");
            for (int i = 1; i< linkCostArray.length ; i++){
                if (!notInN(i, n)){
                    if (adjacent(k,i,linkCostArray)){
                        System.out.print(i + " ");
                        if(d[k] + linkCostArray[k][i] < d[i] || d[i] == -1){
                            d[i] = d[k] + linkCostArray[k][i]; p[i] = k;
                        }
                    }
                }
            }

            printArray(d, p, n, y);
        }

        createForwaedingTable(n,p);
    }

    /**
     * This method creates the forwarding table of the source node 0
     * @param n nodes used
     * @param p predecessor nodes
     */
    private void createForwaedingTable(ArrayList<Integer> n, int[] p) {
        int j;
        int [] dest = new int [n.size()-1];
        Link link [] =  new Link[n.size()-1];

        for (int i = 1; i< n.size(); i++){
            j = n.get(i);
            while (p[j]!= 0){
                j = p[j];
            }
            dest[i-1] = n.get(i); link[i-1] = new Link(0,j);
        }
        printForwardingTable(dest,link);
    }

    /**
     * this method prints the forwarding table in a nice format
     * @param dest
     * @param link
     */
    private void printForwardingTable(int[] dest, Link[] link) {
        System.out.printf("%9s %9s\n","Destination","Link");

        for(int i = 0; i < dest.length; i++ ){
            System.out.printf("%5s %11s%d, V%d)\n","V"+dest[i],"(V",link[i].getX(),link[i].getY());
        }
    }

    /**
     * This method finds the minimum distance link in array d that are not
     * in array n
     * @param d
     * @param n
     * @return
     */
    private int minimumD(int[] d, ArrayList<Integer> n) {
        int lowestNode = 0;
        int lowestValue = 0;
        boolean firstCheck = false;

        for(int i = 0; i < d.length; i++){
            if(d[i] > 0 && !firstCheck && !notInN(i, n)){
                lowestNode = i;
                lowestValue = d[i];
                firstCheck = true;
            } else if(d[i] > 0 && firstCheck && d[i] < lowestValue && !notInN(i, n)){
                lowestNode = i;
                lowestValue = d[i];
            }
        }
        return lowestNode;
    }

    /**
     * This method finds the node not in n
     * @param i
     * @param n
     * @return
     */
    private boolean notInN(int i, ArrayList<Integer> n){
        for(int x = 0; x < n.size(); x++){
            if(i == n.get(x)){
                return true;
            }
        }
        return false;
    }

    /**
     * This method finds whether or not there is a child node from the node were looking at
     * @param x
     * @param y
     * @param linkCostArray
     * @return
     */
    private boolean adjacent(int x, int y, int[][] linkCostArray) {
        return linkCostArray[x][y] > 0;
    }

    /**
     * This class is used to create the Y' links
     */
    private class Link{
        private int x;
        private int y;
        public Link(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    /**
     * This method initializes the 2d array with 0 if its itself, and -1 otherwise for filling
     * @param linkCostArray cost 2d array
     */
    private void initializeCostArray(int[][] linkCostArray) {
        for (int row = 0; row < linkCostArray.length; row++){
            for (int col = 0; col < linkCostArray.length; col++){
                if(row == col){
                    linkCostArray[row][col] = 0;
                } else linkCostArray[row][col] = -1;
            }
        }
        printArray(Arrays.deepToString(linkCostArray).split("]," ));
    }

    /**
     * This print array prints a single array
     * @param array
     */
    private void printArray(String [] array) {
        for ( String i : array){
            System.out.println(i);
        }
    }

    /**
     * This print array is used to print out each array used in the dijsktras algorithm
     * @param arrayD
     * @param arrayP
     * @param arrayN
     * @param arrayY
     */
    private void printArray(int [] arrayD, int [] arrayP, ArrayList <Integer> arrayN, ArrayList<Link> arrayY ) {
        System.out.print("\n\tN' => [ ");
        for ( int i : arrayN){
            System.out.print("V"+i + " ");
        }
        System.out.println("]");

        System.out.print("\tY' => [ ");
        if(arrayY.size() != 0) {
            for ( Link i : arrayY){
                System.out.print("(" + "V"+ i.getX() + ", V" + i.getY() + ") ");
            }
        } else { System.out.print("(Empty Set) "); }
        System.out.println("]");

        System.out.print("\tD  => [ ");
        for ( int i : arrayD){
            System.out.print("V"+i + " ");
        }
        System.out.println("]");

        System.out.print("\tP  => [ ");
        for ( int i : arrayP){
            System.out.print("V" + i + " ");
        }
        System.out.println("]");



    }

    /**
     * This method reads the topo.txt file and checks if theres an error in the file
     * @param linkCostArray
     * @return
     */
    private boolean readTopographyFile(int[][] linkCostArray) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("HMWK8/topo.txt")); //MAKE SURE TO CHANGE THIS
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null) {
                String [] linkLineCost = line.split(" ");
                if(!linkValidation(linkCostArray, linkLineCost)){
                    System.out.println("!! ERROR ON ROW: " + row + " !!");
                    reader.close();
                    return false;
                }
                row++;
            }
            reader.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method validates that the links are of correct values
     * @param linkCostArray
     * @param linkLineCost
     * @return
     */
    private boolean linkValidation(int[][] linkCostArray, String linkLineCost[]){
        int router = Integer.parseInt(linkLineCost[0]);
        int linkedRouter = Integer.parseInt(linkLineCost[1]);
        int linkCost = Integer.parseInt(linkLineCost[2]);
        int n = linkCostArray.length;
        if(router != linkedRouter && router >= 0 && router <= n-1 &&
                linkedRouter >= 0 && linkedRouter <= n-1 &&
                linkCost > 0){
            linkCostArray[router][linkedRouter] = linkCost;
            linkCostArray[linkedRouter][router] = linkCost;
            return true;
        }
        return false;
    }

    /**
     * This method asks the users for the total number of routers
     * @return
     */
    public int getTotalRouter() {
        int totalRouter = 0;
        Scanner keyboard = new Scanner(System.in);
        while(totalRouter < 2) {
            System.out.println("What is the total number of routers? ( Must be >= 2)");
            try{
                totalRouter = keyboard.nextInt();
            }catch (Exception e){
                System.out.println("Need to be an Integer");
                keyboard.next(); // reset the buffer
                totalRouter = 0;
            }
        }
        return totalRouter;
    }
}
