import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by abelamadou on 4/19/16.
 */
public class Dijsktra {
    public static void main(String[] args) {
        Dijsktra d = new Dijsktra();
        d.init();
    }

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
        printArray(D, P, N, Y);
        continueDijsktra(D, P, N, Y, linkCostArray);
    }

    private void continueDijsktra(int[] d, int[] p, ArrayList<Integer> n, ArrayList<Link> y, int[][] linkCostArray) {
        int k;
        while(n.size() != linkCostArray.length){
            k = minimumD(d,n);
            n.add(k);
            Link l = new Link(p[k],k);
            y.add(l);
            System.out.println("Node: " + k + " Value: " + d[k] );
            System.out.println();
            printArray(d, p, n, y);
            break;
        }
    }

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

    private boolean notInN(int i, ArrayList<Integer> n){
        for(int x = 0; x < n.size(); x++){
            if(i == n.get(x)){
                return true;
            }
        }
        return false;
    }

    private boolean adjacent(int x, int y, int[][] linkCostArray) {
        return linkCostArray[x][y] > 0;
    }

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

    private void printArray(String [] array) {
        for ( String i : array){
            System.out.println(i);
        }
    }

    private void printArray(int [] arrayD, int [] arrayP, ArrayList <Integer> arrayN, ArrayList<Link> arrayY ) {
        System.out.print("\nN' => [ ");
        for ( int i : arrayN){
            System.out.print(i + " ");
        }
        System.out.println("]");

        System.out.print("Y' => [ ");
        for ( Link i : arrayY){
            if(i != null) {
                System.out.print("(" + i.getX() + ", " + i.getY() + ") ");
            } else { System.out.print("(Empty Set) "); break; }
        }
        System.out.println("]");

        System.out.print("D  => [ ");
        for ( int i : arrayD){
            System.out.print(i + " ");
        }
        System.out.println("]");

        System.out.print("P  => [ ");
        for ( int i : arrayP){
            System.out.print(i + " ");
        }
        System.out.println("]");



    }

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
