package adventofcode.day23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MainDay23Fast {

    private static final int[] input_small = new int[]{3, 8, 9, 1, 2, 5, 4, 6, 7};
    private static final int[] input = new int[]{5, 9, 8, 1, 6, 2, 7, 3, 4};

    public static void main(String[] args) {
        final int[] inputToUse =
//                input_small;
                input;
        part1(inputToUse);
        part2(inputToUse);
    }

    private static void part2(final int[] input) {
        final int[] extendedInput = new int[1_000_000 + 1];

        int extendedBeginIndex = 0;
        int lastValue = 0;
        for (int i = 0; i < input.length - 1; i++) {
            extendedBeginIndex = Math.max(extendedBeginIndex, input[i + 1]);
            extendedInput[input[i]] = input[i + 1];
            lastValue = input[i + 1];
        }
        extendedInput[lastValue] = input[0];

        for (int i = extendedBeginIndex; i < 1_000_000 + 1; i++) {
            if (i == extendedInput.length - 1) {
                extendedInput[i] = input[0];
            } else {
                extendedInput[lastValue] = i + 1;
                lastValue = i + 1;
            }
        }
        extendedInput[0] = 1_000_000;

        final long begin = System.currentTimeMillis();
        play(10_000_000, extendedInput, input[0]);
        printPart2Solution(extendedInput, System.currentTimeMillis() - begin);
    }

    private static void printPart2Solution(final int[] cups, final long duration) {
        final int nextCup = cups[1];
        final int nextCupAfterNext = cups[nextCup];
        System.out.printf("Result of part 2 (took %d ms): %d * %d = %d\n", duration, nextCup, nextCupAfterNext, Integer.toUnsignedLong(nextCup) * Integer.toUnsignedLong(nextCupAfterNext));
    }

    private static void part1(final int[] input) {
        final int[] extendedInput = new int[input.length + 1];
        int lastValue = 0;
        int extendedBeginIndex = 0;
        for (int i = 0; i < input.length - 1; i++) {
            extendedBeginIndex = Math.max(extendedBeginIndex, input[i + 1]);
            extendedInput[input[i]] = input[i + 1];
            lastValue = input[i + 1];
        }
        extendedInput[lastValue] = input[0];
        extendedInput[0] = extendedBeginIndex;

        final long begin = System.currentTimeMillis();
        play(100, extendedInput, input[0]);
        printPart1Solution(extendedInput, System.currentTimeMillis() - begin);
    }

    private static void printPart1Solution(final int[] result, final long duration) {
        final StringBuilder s = new StringBuilder();
        int currentCup = result[1];
        while (currentCup != 1) {
            s.append(currentCup);
            currentCup = result[currentCup];
        }
        System.out.printf("Result of part 1 (took %d ms): %s\n", duration, s.toString());
    }

    private static void play(final int rounds, final int[] cups, final int firstCup) {
        int currentCup = firstCup;
        for (int i = 0; i < rounds; i++) {
            final int firstPickupCup = cups[currentCup];
            final int secondPickupCup = cups[firstPickupCup];
            final int thirdPickupCup= cups[secondPickupCup];

            int destinationCup = currentCup - 1;
            while (destinationCup <= 0 ||
                   destinationCup == firstPickupCup ||
                   destinationCup == secondPickupCup ||
                   destinationCup == thirdPickupCup) {
                destinationCup = destinationCup - 1;
                if (destinationCup <= 0) {
                    destinationCup = cups[0];
                }
            }

            cups[currentCup] = cups[thirdPickupCup];
            cups[thirdPickupCup] = cups[destinationCup];
            cups[destinationCup] = firstPickupCup;
            currentCup = cups[currentCup];
        }

    }

}
