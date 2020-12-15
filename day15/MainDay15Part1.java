import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Created by Michaël Goossens on 15/12/2020.
 */
public class MainDay15Part1 {
    public static void main(String[] args) throws IOException {
        final long begin = System.currentTimeMillis();
        final int result = 
//                play(2020, new int[] { 1, 0, 18, 10, 19, 6 });
                play(300000000, new int[] { 1, 0, 18, 10, 19, 6 });
        System.out.println("Play took " + (System.currentTimeMillis() - begin) + " millis.");
        System.out.println("Result is " + result);


        final long fastBegin = System.currentTimeMillis();
        final int fastResult =
                //                play(2020, new int[] { 1, 0, 18, 10, 19, 6 });
                playFast(300000000, new int[] { 1, 0, 18, 10, 19, 6 });
        System.out.println("Fast play took " + (System.currentTimeMillis() - fastBegin) + " millis.");
        System.out.println("Fast result is " + fastResult);
    }

    public static int play(final int turns, final int[] input) {
        final Map<Integer, Integer> spokenNumbersByTurn = new HashMap<>();

        for (int i = 0; i < input.length; i++) {
            spokenNumbersByTurn.put(input[i], i + 1);
        }

        final AtomicInteger lastSpoken = new AtomicInteger(input[input.length - 1]);
        IntStream.range(input.length + 1, turns + 1)
                .forEach(n -> {
                    final int earlierTurnOfLastSpokenNumber = spokenNumbersByTurn.getOrDefault(lastSpoken.get(), n - 1);

                    final int numberToSay = (earlierTurnOfLastSpokenNumber == n - 1) ? 0 : n - 1 - earlierTurnOfLastSpokenNumber;
                    spokenNumbersByTurn.put(lastSpoken.get(), n - 1);
                    lastSpoken.set(numberToSay);
                });
        return lastSpoken.get();
    }

    public static int playFast(final int turns, final int[] input) {
        final Map<Integer, int[]> spokenNumbers = new HashMap<>();

        for (int i = 0; i < input.length; i++) {
            spokenNumbers.put(input[i], new int[] { 0, i + 1 });
        }

        final AtomicInteger lastSpoken = new AtomicInteger(input[input.length - 1]);
        IntStream.range(input.length + 1, turns + 1)
                .forEach(n -> {
                    final int[] spokenTurns = spokenNumbers.get(lastSpoken.get());

                    final int numberToSay;
                    if (spokenTurns[0] == 0) {
                        numberToSay = 0;
                    } else {
                        numberToSay = spokenTurns[1] - spokenTurns[0];
                    }

                    {
                        final int[] turnsOfNumberToSay = spokenNumbers.getOrDefault(numberToSay, new int[] { 0, 0 });
                        turnsOfNumberToSay[0] = turnsOfNumberToSay[1];
                        turnsOfNumberToSay[1] = n;
                        spokenNumbers.put(numberToSay, turnsOfNumberToSay);
                    }

                    lastSpoken.set(numberToSay);
                });
        return lastSpoken.get();
    }
}
