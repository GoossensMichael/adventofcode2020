// Forgive me for this bad code - Christmas eve!
package adventofcode.day24;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainDay24 {

    private static final String EAST = "e";
    private static final String WEST = "w";
    private static final String NORTH_EAST = "ne";
    private static final String NORTH_WEST = "nw";
    private static final String SOUTH_EAST = "se";
    private static final String SOUTH_WEST = "sw";
    private static final Set<String> HORIZONTAL_DIRECTIONS = new HashSet<>() {{
        add(EAST);
        add(WEST);
    }};
    private static final Map<String, int[]> DIRECTIONS = new HashMap<>() {{
        put(EAST, new int[] { 1, 0 });
        put(WEST, new int[] { -1, 0 });
        put(NORTH_EAST, new int[] { 1, 1 });
        put(NORTH_WEST, new int[] { 0, 1 });
        put(SOUTH_WEST, new int[] { -1, -1 });
        put(SOUTH_EAST, new int[] { 0, -1 });
    }};

    public static void main(String[] args) throws IOException {
        final String input =
//                "src/adventofcode/day24/input_tiny.txt";
//                "src/adventofcode/day24/input_small.txt";
                "src/adventofcode/day24/input.txt";
        part1(input);
        part2(input);
    }

    private static void part2(final String input) throws IOException {
        final List<int[]> positionsAfterNavigation = Files.lines(Paths.get(input))
                .map(MainDay24::navigate)
                .collect(Collectors.toList());

        final Map<String, Long> encountersOfPossition = positionsAfterNavigation.stream()
                .map(MainDay24::stringify)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Tile> tiles = new HashMap<>();
        for (final int[] position : positionsAfterNavigation) {
            final String p = stringify(position);
            final long encounter = encountersOfPossition.get(p);
                tiles.put(p, new Tile(position, encounter % 2 == 1));
        }

        printDayTiles(tiles, 0);
        for (int i = 0; i < 100; i++) {
            Map<String, Tile> newTiles = new HashMap<>();
            for (final Tile tile : tiles.values()) {
                int blackTileCount = 0;
                for (final int[] d : DIRECTIONS.values()) {
                    final int[] neighBourPosition =
                            new int[] { tile.getPosition()[0] + d[0], tile.getPosition()[1] + d[1] };
                    Tile neighbour = tiles.get(stringify(neighBourPosition));
                    if (tile.isBlack() && neighbour != null && neighbour.isBlack()) {
                        blackTileCount++;
                    } else if (neighbour != null && neighbour.isBlack()) {
                        blackTileCount++;
                    }

                    // Ok I confess not my best work but in my defence Christmas eve was starting and it actually works.
                    if (neighbour == null || (!newTiles.containsKey(stringify(neighBourPosition)) && !neighbour.isBlack)) {
                        if (neighbour == null) {
                            neighbour = new Tile(neighBourPosition, false);
                        }
                        int blackTileCount2 = 0;
                        for (final int[] e : DIRECTIONS.values()) {
                            final int[] neighbour2Position =
                                    new int[] { neighbour.getPosition()[0] + e[0], neighbour.getPosition()[1] + e[1] };
                            final Tile neighbour2 = tiles.get(stringify(neighbour2Position));
                            if (neighbour2 != null && neighbour2.isBlack()) {
                                blackTileCount2++;
                            }
                        }

                        if (blackTileCount2 == 2) {
                            newTiles.put(stringify(neighbour.getPosition()), new Tile(neighbour.getPosition(), true));
                        } else {
                            newTiles.put(stringify(neighbour.getPosition()), new Tile(neighbour.getPosition(), false));
                        }
                    }
                }

                if (tile.isBlack()) {
                    if (blackTileCount == 0 || blackTileCount > 2) {
                        newTiles.put(stringify(tile.getPosition()), new Tile(tile.getPosition(), false));
                    } else {
                        newTiles.put(stringify(tile.getPosition()), new Tile(tile.getPosition(), true));
                    }
                } else {
                    if (blackTileCount == 2) {
                        newTiles.put(stringify(tile.getPosition()), new Tile(tile.getPosition(), true));
                    } else {
                        newTiles.put(stringify(tile.getPosition()), new Tile(tile.getPosition(), false));
                    }
                }
            }
            tiles = newTiles;
            printDayTiles(tiles, i);
        }
    }

    private static void printDayTiles(final Map<String, Tile> tiles, final int i) {
        System.out.print("Day " + (i + 1) + ": ");
        System.out.println(tiles.entrySet().stream().filter(entry -> entry.getValue().isBlack).count());
    }

    private static Map<String, Tile> copy(final Map<String, Tile> map) {
        final Map<String, Tile> copy = new HashMap<>();
        for (final Map.Entry<String, Tile> entry : map.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().copy());
        }

        return copy;
    }

    private static void part1(final String input) throws IOException {
        final Map<String, Long> collect = Files.lines(Paths.get(input))
                .map(MainDay24::navigate)
                .map(MainDay24::stringify)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final long flippedTiles = collect
                .entrySet()
                .stream()
                .filter(entry -> isBlack(entry.getValue()))
                .count();

        System.out.println("Amount of black tiles: " + flippedTiles);
    }

    private static boolean isBlack(final long encounters) {
        return encounters % 2 == 1;
    }

    private static String stringify(final int[] position) {
        return String.format("(%d, %d)", position[0], position[1]);
    }

    public static int[] navigate(final String directions) {
        int[] position = new int[] { 0, 0 };
        int i = 0;
        while (i < directions.length()) {
            final String direction;
            if (HORIZONTAL_DIRECTIONS.contains(directions.substring(i, i + 1))) {
                direction = directions.substring(i, ++i);
            } else {
                direction = directions.substring(i, i += 2);
            }

            final int[] d = DIRECTIONS.get(direction);
            position[0] += d[0];
            position[1] += d[1];
        }

        return position;
    }

    private static final class Tile {
        private final int[] position;
        private final boolean isBlack;

        private Tile(final int[] position, final boolean isBlack) {
            this.position = position;
            this.isBlack = isBlack;
        }

        public int[] getPosition() {
            return position;
        }

        public boolean isBlack() {
            return isBlack;
        }

        public Tile copy() {
            return new Tile(position, isBlack);
        }
    }
}
