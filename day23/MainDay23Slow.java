package adventofcode.day23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainDay23Slow {

    private static final int MOVES = 100;

    private static final List<Integer> input_small = new ArrayList<>(Arrays.asList(3, 8, 9, 1, 2, 5, 4, 6, 7));
    private static final List<Integer> input = Arrays.asList(5, 9, 8, 1, 6, 2, 7, 3, 4);

    public static void main(String[] args) {
        final List<Integer> inputToUse =
//                input_small;
                input;

        part1(100, inputToUse);
    }

    private static void part1(final int rounds, final List<Integer> input) {
        int currentIndex = 0;

        List<Integer> workingCups = new ArrayList<>(input);
        for (int i = 0; i < rounds; i++) {
            final List<Integer> pickupCups = pickupCups(3, currentIndex + 1, workingCups);

            final int currentCup = workingCups.get(currentIndex);
            final List<Integer> newCupArrangement = new ArrayList<>();
            final int destinationCup = findDestinationCup(currentCup, workingCups, pickupCups);

            System.out.printf("-- move %d --\n", i + 1);
            System.out.print("cups:");
            workingCups.forEach(cup -> System.out.printf(" %s", cup));
            System.out.println();
            System.out.print("pick up:");
            pickupCups.forEach(cup -> System.out.printf(" %s", cup));
            System.out.println();
            System.out.printf("Destination: %d\n\n", destinationCup);

            for (int x = 4; x <= workingCups.size(); x++) {
                newCupArrangement.add(workingCups.get(x % workingCups.size()));
            }

            newCupArrangement.addAll(newCupArrangement.indexOf(destinationCup) + 1, pickupCups);

            workingCups = newCupArrangement;
        }

        System.out.println("-- final --");
        System.out.print("cups: ");
        workingCups.forEach(cup -> System.out.printf(" %s", cup));
        System.out.println();

        final int indexOf1 = workingCups.indexOf(1);
        final StringBuilder s = new StringBuilder();
        for (int i = (indexOf1 + 1) % workingCups.size(); i != indexOf1; i = (i + 1) % workingCups.size()) {
            s.append(workingCups.get(i));
        }
        System.out.println("Result of part 1: " + s.toString());
    }

    private static int findDestinationCup(final int currentCup, final List<Integer> workingCups, final List<Integer> pickupCups) {
        boolean stillSearching = true;
        // First guess.
        int destinationCup = currentCup - 1;
        while (stillSearching) {
            if (pickupCups.contains(destinationCup)) {
                destinationCup--;
            } else if (destinationCup < 1) {
                destinationCup = workingCups.stream().mapToInt(i -> i).max().orElseThrow();
            } else {
                stillSearching = false;
            }
        }
        return destinationCup;
    }

    private static List<Integer> pickupCups(final int amount, final int from, final List<Integer> cups) {
        final List<Integer> result = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            result.add(cups.get((from + i) % cups.size()));
        }

        return result;
    }
}
