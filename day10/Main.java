package adventofcode.day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        final List<Integer> adaptors = Files.lines(Paths.get("src/adventofcode/day10/input.txt"))
                .map(Integer::parseInt)
                .sorted()
                .collect(Collectors.toList());

        // part 1
        jumps(adaptors);

        // part 2 - The zero outlet needs to be added as it can add arrangements as well.
        adaptors.add(0, 0);
        arrangements(adaptors);
    }

    private static void arrangements(final List<Integer> adaptors) {
        final Map<Integer, Long> solutions = new HashMap<>();
        // Start solving it as small problems for the end and use the partial solutions when going up the problem tree.
        for (int i = adaptors.size() - 1; i >= 0; i--) {
            solutions.put(adaptors.get(i), solve(adaptors.subList(i, adaptors.size()), solutions));
        }
        System.out.printf("Amount of arrangements is %d.\n", solutions.get(adaptors.get(0)));
    }

    // Solves the problem for a list of adaptors that are sorted in order using solutions for adaptors that are already known to speed it up.
    private static long solve(final List<Integer> adaptors, final Map<Integer, Long> solutions) {
        if (adaptors.size() < 3) {
            return 1;
        } else {
            final Long solution = solutions.get(adaptors.get(0));
            if (solution != null) {
                return solution;
            } else {
                return  // Assume that the next item is always reachable
                        solve(adaptors.subList(1, adaptors.size()), solutions) +
                        ((adaptors.get(2) - adaptors.get(0) <= 3) ? solve(adaptors.subList(2, adaptors.size()), solutions) : 0) +
                        ((adaptors.size() >= 4 && adaptors.get(3) - adaptors.get(0) <= 3) ? solve(adaptors.subList(3, adaptors.size()), solutions) : 0);
            }
        }
    }

    private static void jumps(final List<Integer> adaptors) {
        int currentJolts = 0;
        // The last jump is always has a jump size of 3 hence the init of jumpsPerSize[2] to 1.
        final int[] jumpsPerSize = new int[] {0, 0, 1};
        for (final int adaptor : adaptors) {
            final int jumpSize = adaptor - currentJolts;
            jumpsPerSize[jumpSize - 1]++;
            currentJolts = adaptor;
        }
        System.out.printf("%d 1-jolt jumps and %d 3-jolt jumps multiplied give %d.\n", jumpsPerSize[0], jumpsPerSize[2], jumpsPerSize[0] * jumpsPerSize[2]);
    }


}
