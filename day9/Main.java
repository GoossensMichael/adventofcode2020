import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static int PREAMBLE = 25;

    public static void main(String[] args) throws IOException {
        // part 1
        final String smallInputPath = "/Users/mgoossens/personal/adventofcode/day9/input_small.txt";
        final long smallErrorNumber = findErrorNumber(5, smallInputPath);
        System.out.printf("Error number for input path '%s' and preamble 5: %d.\n", smallInputPath, smallErrorNumber);
        // part 2
        final Optional<Tuple<Long, Long>> smallPosibleSolution = findSolution(smallInputPath, smallErrorNumber);
        smallPosibleSolution.ifPresent(solution -> System.out.printf("Solution tuple is %s and the resulting sum is %d.\n", solution, solution.x + solution.y));

        // part 1
        final String inputPath = "/Users/mgoossens/personal/adventofcode/day9/input.txt";
        final long errorNumber = findErrorNumber(25, inputPath);
        System.out.printf("Error number for input path '%s' and preamble 5: %d.\n", inputPath, errorNumber);
        // part 2
        final Optional<Tuple<Long, Long>> posibleSolution = findSolution(inputPath, errorNumber);
        posibleSolution.ifPresent(solution -> System.out.printf("Solution tuple is %s and the resulting sum is %d.\n", solution, solution.x + solution.y));

    }

    public static Optional<Tuple<Long, Long>> findSolution(final String inputPath, final long errorNumber) throws IOException {
        final List<Long> numbers = Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        int i = 0;
        long currentSum = 0L;
        final LinkedList<Tuple<Integer, Long>> summedNumbers = new LinkedList<>();
        // Create a variable window which increases in size as long as the sum of its numbers are less than the target number.
        // Decrease the window size when the sum of all the numbers in the window is larger than the target number.
        while (i < numbers.size()) {
            final long currentNumber = numbers.get(i);
            currentSum += currentNumber;
            summedNumbers.addFirst(new Tuple(i, currentNumber));
            // Sum is too big, decrease the window size and adjust the sum accordingly.
            // Oldest numbers are removed first
            while (currentSum > errorNumber) {
                currentSum -= summedNumbers.removeLast().y;
            }

            // When the sum equals the target number return the smallest and biggest number in the window
            // Could be improved by keeping an ordered data structure in sync with the window
            if (currentSum == errorNumber) {
                return Optional.of(new Tuple(
                        summedNumbers.stream().map(t -> t.y).min(Comparator.naturalOrder()).orElse(Long.MIN_VALUE),
                        summedNumbers.stream().map(t -> t.y).max(Comparator.naturalOrder()).orElse(Long.MAX_VALUE)
                ));
            }

            i++;
        }
        return Optional.empty();
    }

    public static long findErrorNumber(final int preamble, final String inputPath) throws IOException {
        Main.PREAMBLE = preamble;

        final List<Long> numbers = Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        final Map<Integer, Tuple<Long, Set<Long>>> sums = new HashMap<>();

        // Init the first window
        int i = 0;
        while (i < PREAMBLE) {
            addSumsForLine(i, numbers.get(i), sums);
            i++;
        }

        // Check if the current number is a sum in the window
        // If so add the number to the window
        // If not the number is the one we are interested in
        while (i < numbers.size()) {
            final long currentNumber = numbers.get(i);
            if (isError(i, currentNumber, sums)) {
                System.out.println("Faulty number: " + currentNumber);
                return currentNumber;
            } else {
                addSumsForLine(i, currentNumber, sums);
                i++;
            }
        }
        return -1;
    }

    private static boolean isError(final int line, final long number, final Map<Integer, Tuple<Long, Set<Long>>> sums) {
        for (int i = 2; i <= PREAMBLE; i ++) {
            if (sums.get(line - i).y.contains(number)) {
                return false;
            }
        }
        return true;
    }

    private static void addSumsForLine(final Integer line, final Long number, final Map<Integer, Tuple<Long, Set<Long>>> sums) {
        // Keep a set of sums for numbers added after the current one
        sums.putIfAbsent(line, new Tuple(number, new HashSet<>()));
        // For each number in the previous window, except the oldest add the current number and store it as a sum
        for (int i = 1; i < PREAMBLE; i++) {
            final int targetLine = line - i;
            // Skip when out of bounds
            if (targetLine >= 0) {
                final Tuple<Long, Set<Long>> sumsForNumber = sums.get(targetLine);
                sumsForNumber.y.add(sums.get(targetLine).x + number);
            }
        }
    }

    // Helper class
    private static class Tuple<X, Y> {
        final X x;
        final Y y;

        public Tuple(final X x, final Y y) {
            this.x = x;
            this.y = y;
        }

        public X getX() {
            return x;
        }

        public Y getY() {
            return y;
        }

        public String toString() {
            return String.format("(%s, %s)", x, y);
        }
    }

}
