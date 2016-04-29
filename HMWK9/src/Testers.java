import java.util.Arrays;

/**
 * Created by Dean on 4/28/2016.
 */
public class Testers {
    public static void main(String[] args) {
        Testers out = new Testers();
//        out.ofMain();
//        out.ofMain2();
        out.ofMain3();
    }

    private void ofMain3() {

       int [] arr = {1,2,3,4,5};
       int [] arr2 = {1,2,3,4,5};
        System.out.println(Arrays.equals(arr2,arr));
    }

    private void ofMain2() {
        int array[][] = new int[5][5];

        array[0] = new int[5];
        for (int i = 0; i < array.length; i++){
            array[0][i] = i;
        }
        for (int i = 0; i < array.length; i++){
            System.out.println(array[0][i]);
        }
    }


}
