import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Michaël Goossens on 15/12/2020.
 */
public class MainDay15Part1 {
    public static void main(String[] args) {
        final int[] input = new int[] { 1, 0, 18, 10, 19, 6 };
        playGame(2020, input);
        playGame(30_000_000, input);
    }

    private static void playGame(final int turns, final int[] input) {
        final long begin = System.currentTimeMillis();
        System.out.println("Going to play with input " + Arrays.toString(input) + " for " + turns + " turns.");
        final int result = play(turns, input);
        System.out.println("Play took " + (System.currentTimeMillis() - begin) + " millis.");
        System.out.println("Result is " + result);
        System.out.println();
    }

    public static int play(final int turns, final int[] input) {
        final int[] spokenNumbersByTurn = new int[turns];

        for (int i = 0; i < input.length; i++) {
            spokenNumbersByTurn[input[i]] = i + 1;
        }

        final AtomicInteger lastSpoken = new AtomicInteger(input[input.length - 1]);
        for (int n = input.length + 1; n <= turns; n++) {
            final int earlierTurnOfLastSpokenNumber = spokenNumbersByTurn[lastSpoken.get()];
            spokenNumbersByTurn[lastSpoken.get()] = n - 1;
            lastSpoken.set((earlierTurnOfLastSpokenNumber == 0) ? 0 : n - 1 - earlierTurnOfLastSpokenNumber);
        }
        return lastSpoken.get();
    }
    
}
