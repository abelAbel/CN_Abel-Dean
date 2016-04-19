import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

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
        for (int row = 0; row < totalRouter; row++){
            for (int col = 0; col < totalRouter; col++){
                if(row == col){
                    linkCostArray[row][col] = 0;
                } else linkCostArray[row][col] = -1;
            }
        }

        printArray(Arrays.deepToString(linkCostArray).split("]," ));


        while (!readTopographyFile(linkCostArray)){
            Scanner input = new Scanner(System.in);
            System.out.println("You need to fix your file! Press \"Enter\" to continue");
            input.next();
            input.close();
        }
        System.out.println();
        printArray(Arrays.deepToString(linkCostArray).split("]," ));

    }

    private void printArray(String[] array) {
        for ( String i : array){
            System.out.println(i);
        }
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
