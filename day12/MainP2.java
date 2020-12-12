package adventofcode.day12;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainP2 {

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
        put('W', new int[] { -1, 0 });
    }};

    private static final String TURN_ACTIONS = "RL";

    private static int[] sail(final List<Tuple<Character, Integer>> navigation) {
        final int[] waypoint = new int[] { 10, 1 };
        final int[] coordinates = new int[] { 0, 0 };

        for (final Tuple<Character, Integer> nav : navigation) {
            final char instruction = nav.instruction;
            if (MOVE_ACTIONS.containsKey(instruction)) {
                moveWaypoint(waypoint, nav);
            } else if (TURN_ACTIONS.indexOf(instruction) > -1) {
                final int turns = nav.arg / 90 * ((instruction == 'R') ? 1 : -1);
                applyTurns(turns, waypoint);
                System.out.printf("Turned waypoint to (%d, %d).\n", waypoint[0], waypoint[1]);
            } else if (instruction == 'F') {
                applyMove(waypoint, coordinates, new Tuple<>(nav.instruction, nav.arg));
            }
        }
        System.out.printf("Current coordinates (%d, %d).\n", coordinates[0], coordinates[1]);

        return coordinates;
    }

    /*
     * This is actually a matrix rotation
     * (x, y) ( 0 -1 ) (new_x)
     *        ( 1  0 ) (new_y)
     * When the turns are negative the matrix has to be multiplied by -1.
     *
     * Implementation of matrix multiplication was simplified in the code.
     */
    public static void applyTurns(final int turns, final int[] coordinates) {
        final int direction;
        if (turns < 0) {
            direction = 1;
        } else {
            direction = -1;
        }

        // Ugly but shorter way to do the matrix rotation
        for (int i = 0; i < Math.abs(turns); i++){
            final int swap = coordinates[0];
            coordinates[0] = coordinates[1] * direction * -1;
            coordinates[1] = swap * direction;
        }
    }

    public static void applyMove(final int[] waypoint, final int[] coordinates, final Tuple<Character, Integer> nav) {
        coordinates[0] += waypoint[0] * nav.arg;
        coordinates[1] += waypoint[1] * nav.arg;
    }

    public static void moveWaypoint(final int[] waypoint, final Tuple<Character, Integer> nav) {
        final int[] action = MOVE_ACTIONS.get(nav.instruction);
        waypoint[0] += action[0] * nav.arg;
        waypoint[1] += action[1] * nav.arg;
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
