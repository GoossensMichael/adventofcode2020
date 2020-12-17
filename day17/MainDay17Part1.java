package adventofcode.day17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainDay17Part1 {

    public static void main(String[] args) throws IOException {
        final String inputPath = "src/adventofcode/day17/input.txt";
        final List<char[]> inputLines = Files.lines(Paths.get(inputPath))
                .map(String::toCharArray)
                .collect(Collectors.toList());


        final Map<String, Cube> cubes = new HashMap<>();
        for (int x = 0; x < inputLines.size(); x++) {
            final char[] inputLine = inputLines.get(x);
            for (int y = 0; y < inputLine.length; y++) {
                cubes.put(coordsToString(x, y, 0), new Cube(inputLine[y] == '#'));
            }
        }

        int[][] dimensions = new int[][] {
                { 0, inputLines.size() },
                { 0, inputLines.get(0).length },
                { 0, 1 }
        };

        final int cycles = 6;
        for (int i = 0; i < cycles; i++) {
            dimensions = applyCycle(cubes, dimensions);
            final long count = cubes.values().stream().filter(Cube::isActive).count();
            System.out.printf("Amount of cubes active after %d cycles: %d.\n", i + 1, count);
        }

    }

    public static int[][] applyCycle(final Map<String, Cube> cubes, final int[][] dimensions) {
        final int[][] dimensionExpansion = new int[][] {
                { dimensions[0][0], dimensions[0][1] },
                { dimensions[1][0], dimensions[1][1] },
                { dimensions[2][0], dimensions[2][1] }
        };

        for (int z = dimensions[2][0] - 1; z <= dimensions[2][1] + 1; z++) {
            for (int x = dimensions[0][0] - 1; x <= dimensions[0][1] + 1; x++) {
                for (int y = dimensions[1][0] - 1; y <= dimensions[1][1] + 1; y++) {
                    final int amountOfActiveNeighbours = amountOfActiveNeighbours(x, y, z, cubes);
                    final String coordsAsString = coordsToString(x, y, z);

                    Cube cube = cubes.get(coordsAsString);
                    final boolean desiredState = determineDesiredState(cube, amountOfActiveNeighbours);
                    if (cube == null) {
                        cube = new Cube(false);
                        if (desiredState) {
                            cubes.put(coordsAsString, cube);
                            dimensionExpansion[0][0] = Math.min(dimensionExpansion[0][0], x);
                            dimensionExpansion[0][1] = Math.max(dimensionExpansion[0][1], x);
                            dimensionExpansion[1][0] = Math.min(dimensionExpansion[1][0], y);
                            dimensionExpansion[1][1] = Math.max(dimensionExpansion[1][1], y);
                            dimensionExpansion[2][0] = Math.min(dimensionExpansion[2][0], z);
                            dimensionExpansion[2][1] = Math.max(dimensionExpansion[2][1], z);
                        }
                    }
                    cube.setDesiredState(desiredState);
                }
            }
        }
        cubes.values().forEach(Cube::reconcile);

        return dimensionExpansion;
    }

    public static boolean determineDesiredState(final Cube cube, final int amountOfActiveNeighbours) {
        return ((cube == null || !cube.isActive()) && amountOfActiveNeighbours == 3) || (cube != null && cube.isActive() && (amountOfActiveNeighbours == 2 || amountOfActiveNeighbours == 3));
    }

    private static int amountOfActiveNeighbours(final int x, final int y, final int z, final Map<String, Cube> cubes) {
        int sum = 0;
        for (int k = z - 1; k <= z + 1; k++) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    // Don't consider the element itself
                    if (i != x || j != y || k != z) {
                        final Cube cube = cubes.get(coordsToString(i, j, k));
                        if (cube != null && cube.isActive()) {
                            sum++;
                        }
                    }
                }
            }
        }
        return sum;
    }

    private static String coordsToString(final int x, final int y, final int z) {
        return String.format("(%d, %d, %d)", x, y, z);
    }

    private static final class Cube {
        private boolean active;
        private boolean desiredState;

        public Cube(final boolean active) {
            this.active = active;
            this.desiredState = active;
        }

        public boolean isActive() {
            return active;
        }

        public void setDesiredState(final boolean desiredState) {
            this.desiredState = desiredState;
        }

        public void reconcile() {
            active = desiredState;
        }
    }
}
