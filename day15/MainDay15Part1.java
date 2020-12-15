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
        play(30000000, new int[] { 1, 0, 18, 10, 19, 6 });
        //play(10, new int[] { 0, 3, 6 });
        //play(10, new int[] { 1, 3, 2 };
        //play(10, new int[] { 2, 1, 3 };
        //play(10, new int[] { 1, 2, 3 };
        //play(10, new int[] { 2, 3, 1 };
        //play(10, new int[] { 3, 2, 1 };
        //play(2020, new int[] { 3, 1, 2 });
    }

    public static int play(final int turns, final int[] input) {
        final Map<Integer, int[]> spokenNumbers = new HashMap<>();

        for (int i = 0; i < input.length; i++) {
            spokenNumbers.put(input[i], new int[] { 0, i + 1 });
            System.out.printf("Turn %d - Init number %d\n", i + 1, input[i]);
        }

        final AtomicInteger lastSpoken = new AtomicInteger(input[input.length - 1]);
        IntStream.range(input.length + 1, turns + 1)
                .forEach(n -> {
                    final int[] spokenTurns = spokenNumbers.get(lastSpoken.get());

                    final int numberToSay;
                    if (spokenTurns[0] == 0) {
                        numberToSay = 0;
                        if (n % 10000 == 0) {
                            System.out.printf("Turn %d - Considering %d: First encounter = %d\n", n, lastSpoken.get(), numberToSay);
                        }
                    } else {
                        numberToSay = spokenTurns[1] - spokenTurns[0];
                        if (n % 10000 == 0) {
                            System.out.printf("Turn %d - Considering %d: %d - %d = %d\n", n, lastSpoken.get(), spokenTurns[1], spokenTurns[0], numberToSay);
                        }
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
