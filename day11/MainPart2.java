package adventofcode.day11;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MainPart2 {

    private static final char OCCUPIED = '#';
    private static final char EMPTY_SEAT = 'L';
    private static final char FLOOR = '.';

    public static void main(String[] args) throws IOException {
        final List<char[]> rows = Files.lines(Paths.get("src/adventofcode/day11/input.txt"), StandardCharsets.UTF_8)
                .map(String::toCharArray)
                .collect(Collectors.toList());

        final char[][] layout = rows.toArray(new char[rows.size()][rows.get(0).length]);

        printArray(layout);
        final int amountOfSeatsPart1 = stabilize(layout);
        System.out.printf("Stabilized with %d occupied seats.", amountOfSeatsPart1);
    }

    private static int stabilize(final char[][] layout) {
        boolean stabilized = true;
        final char[][] resultLayout = arrayCopy(layout);
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] == EMPTY_SEAT) {
                    if (!occupiedSeatAdjecant(layout, i, j)) {
                        resultLayout[i][j] = OCCUPIED;
                        stabilized = false;
                    }
                } else if (layout[i][j] == OCCUPIED) {
                    if (fourOrMoreSeatsAdjecantOccupied(layout, i, j)) {
                        resultLayout[i][j] = EMPTY_SEAT;
                        stabilized = false;
                    }
                }
            }
        }

        System.out.println("");
        printArray(resultLayout);
        if (!stabilized) {
            return stabilize(resultLayout);
        } else {
            return countOccupiedSeats(resultLayout);
        }
    }

    private static int countOccupiedSeats(final char[][] layout) {
        int occupiedSeats = 0;
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j] == OCCUPIED) {
                    occupiedSeats++;
                }
            }
        }
        return occupiedSeats;
    }

    private static boolean fourOrMoreSeatsAdjecantOccupied(final char[][] layout, final int i, final int j) {
        int adjecantSeats = 0;
        final int[] direction = new int[2];
        for (int x = i - 1; x <= i + 1; x++) {
            direction[0] = x - i;
            if (x >= 0 && x < layout.length) {
                for (int y = j - 1; y <= j + 1; y++) {
                    direction[1]  = y - j;
                        boolean seatSeen = false;
                        int u = x;
                        int v = y;
                        while (u >= 0 && u < layout.length && v >= 0 && v < layout[u].length && !(u == i && v == j) && !seatSeen) {
                            if (layout[u][v] == OCCUPIED) {
                                adjecantSeats++;
                            }
                            if (layout[u][v] == FLOOR) {
                                u += direction[0];
                                v += direction[1];
                            } else {
                                seatSeen = true;
                            }
                        }
                }
            }
        }
        return adjecantSeats >= 5;
    }

    private static boolean occupiedSeatAdjecant(final char[][] layout, final int i, final int j) {
//        System.out.printf("Processing (%d, %d)\n", i, j);
        final int[] direction = new int[2];
        for (int x = i - 1; x <= i + 1; x++) {
            direction[0] = x - i;
            if (x >= 0 && x < layout.length) {
                for (int y = j - 1; y <= j + 1; y++) {
                    direction[1]  = y - j;
                        boolean seatSeen = false;
                        int u = x;
                        int v = y;
                        while (u >= 0 && u < layout.length && v >= 0 && v < layout[u].length && !(u == i && v == j) && !seatSeen) {
                            if (layout[u][v] == OCCUPIED) {
                                return true;
                            }
                            if (layout[u][v] == FLOOR) {
                                u += direction[0];
                                v += direction[1];
                            } else {
                                seatSeen = true;
                            }
                        }
                }
            }
        }
        return false;
    }

    private static void printArray(char[][] layout) {
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                System.out.print(layout[i][j]);
            }
            System.out.println("");
        }
    }

    public static char[][] arrayCopy(char[][] arr) {
        final char[][] copy = new char[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            copy[i] = new char[arr[i].length];
            System.arraycopy(arr[i], 0, copy[i], 0, arr[i].length);
        }
        return copy;
    }


}
