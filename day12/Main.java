package adventofcode.day12;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        final List<Tuple<Character, Integer>> navigation = Files.lines(Paths.get("src/adventofcode/day12/input.txt"), StandardCharsets.UTF_8)
                .map(input -> new Tuple<>(input.charAt(0), Integer.parseInt(input.substring(1))))
                .collect(Collectors.toList());

        final int[] coordinates = sail(navigation);
        System.out.printf("Manhattan distance: %d.\n", Math.abs(coordinates[0]) + Math.abs(coordinates[1]));
    }

    private static final Map<Character, int[]> MOVE_ACTIONS = new HashMap<>() {{
        put('N', new int[] { 0, 1 });
        put('S', new int[] { 0, -1 });
        put('E', new int[] { 1, 0 });
        put('W', new int[] { -1, 0});
    }};

    private static final String WIND_DIRS = "ESWN";

    private static final String TURN_ACTIONS = "RL";

    private static int[] sail(final List<Tuple<Character, Integer>> navigation) {
        final int[] coordinates = new int[2];
        char direction = 'E';

        for (final Tuple<Character, Integer> nav : navigation) {
            final char instruction = nav.instruction;
            if (MOVE_ACTIONS.containsKey(instruction)) {
                applyMove(coordinates, nav);
            } else if (TURN_ACTIONS.indexOf(instruction) > -1) {
                final int turns = nav.arg / 90 * ((instruction == 'R') ? 1 : -1);
                final int currentTurn = WIND_DIRS.indexOf(direction);
                // The +4 is needed for negative turns (assume that 360 is the highest degree possible otherwise the +4 solution is not sufficient
                direction = WIND_DIRS.charAt((currentTurn + turns + 4) % WIND_DIRS.length());
                System.out.printf("Turned direction to %s.\n", direction);
            } else if (instruction == 'F') {
                applyMove(coordinates, new Tuple<>(direction, nav.arg));
            }
        }
        System.out.printf("Current coordinates (%d, %d).\n", coordinates[0], coordinates[1]);

        return coordinates;
    }

    public static void applyMove(final int[] coordinates, final Tuple<Character, Integer> nav) {
        final int[] action = MOVE_ACTIONS.getOrDefault(nav.instruction, new int[]{0, 0});
        System.out.printf("Moved %s by %d in direction (%d, %d) ", nav.instruction, nav.arg, action[0], action[1]);
        coordinates[0] += action[0] * nav.arg;
        coordinates[1] += action[1] * nav.arg;
        System.out.printf(" changed coordinates to (%d, %d).\n", coordinates[0], coordinates[1]);
    }

    private static final class Tuple<U, V> {
        private final U instruction;
        private final V arg;

        public Tuple(final U instruction, final V arg) {
            this.instruction = instruction;
            this.arg = arg;
        }
    }
}
