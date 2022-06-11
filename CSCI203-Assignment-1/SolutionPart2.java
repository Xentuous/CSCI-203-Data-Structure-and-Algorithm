import java.util.stream.IntStream;

/**
 * 
 * @author Min Zhan Foo
 */
public class SolutionPart2 {
    
    /**
     * Perform sorting in an arrary
     * T(n) = n + (n * n -1) + (n * n -1) + a + a + a
     *      = n^2 - n + n^2 - n + n + 3a
     *      = 2n^2 -n + 3a
     * O(n^2)
     * @param int[]
     */
    private static void sort1(int[] array) {
        for (int i = 0; i < array.length; i++) {           // n              
            for (int j = 0; j < array.length - 1; j++) {   // n * n - 1
                if (array[j] > array[j + 1]) {             // n * n - 1
                    int temp = array[j];                   // a     
                    array[j] = array[j + 1];               // a     
                    array[j + 1] = temp;                   // a     
                }
            }
        }
    }


    /**
     * Perform another sorting in an array
     * T(n) = n + n + n + (n * n - 1) + (n * n - 1) + n
     *      = 3n + n^2 - n + n^2 - n + n
     *      = 2n^2 + 2n
     * O(n^2)
     * @param int[]
     */
    private static void sort2(int[] array) {
        for (int i = 1; i < array.length; i++) {                // n
            int pos = array[i];                                 // n
            int j = 0;                                          // n
            
            for (j = i - 1; j >= 0 && array[j] > pos; j--) {    // n * n - 1
                array[j + 1] = array[j];                        // n * n - 1 
            }
            array[j + 1] = pos;                                 // n
        }
    }

    /**
     * Takes in two array and output an arraylist of B[ith] smallets number in array A
     * 
     * @param A
     * @param B
     */
    private static void output(int[] A, int[] B) {
        System.out.print("Required output: ");
        for (int j = 0; j < B.length; j++) {
            System.out.printf("%d ", A[B[j] - 1]);
        }

        System.out.println("\n======================================\n");
    }

    public static void main(String[] args) {
        int[] A = { 7, 3, 8, 21, 5, 11 };
        int[] B = { 3, 5, 1 };

        int[] A2 = { 7, 3, 8, 21, 5, 11 };
        int[] B2 = { 3, 5, 1 };

        System.out.println("Question 8a) \nAlgorithm 1: ");
        System.out.print("Before sorting array A: ");
        IntStream.of(A).forEach(x -> System.out.printf("%d ", x));
        System.out.println();
        System.out.print("Before sorting array B: ");
        IntStream.of(B).forEach(x -> System.out.printf("%d ", x));
        System.out.println();

        sort1(A);
        sort1(B);

        System.out.print("After sorting array A: \t");
        IntStream.of(A).forEach(x -> System.out.printf("%d ", x));
        System.out.println();
        System.out.print("After sorting array B: \t");
        IntStream.of(B).forEach(x -> System.out.printf("%d ", x));
        System.out.println("\n======================================");

        output(A, B);

        System.out.println("Question 8c) \nAlgorithm 2: ");
        System.out.print("Before sorting array A: ");
        IntStream.of(A2).forEach(x -> System.out.printf("%d ", x));
        System.out.println();
        System.out.print("Default array B: \t");
        IntStream.of(B2).forEach(x -> System.out.printf("%d ", x));
        System.out.println();

        sort2(A2);

        System.out.print("After sorting array A: \t");
        IntStream.of(A2).forEach(x -> System.out.printf("%d ", x));
        System.out.println();
        System.out.print("Default array B: \t");
        IntStream.of(B2).forEach(x -> System.out.printf("%d ", x));
        System.out.println("\n======================================");

        output(A2, B2);     
    }
}
