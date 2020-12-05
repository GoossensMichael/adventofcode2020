public class Day5E1 {
    public static void main(String[] args) throws IOException {
        final List<String> seatcodes = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day5/input.txt"), StandardCharsets.UTF_8);
        seatcodes.stream()
                .map(seatcode -> {
                    final int row = binarySearch(127, 'B', seatcode.substring(0, 7));
                    final int column = binarySearch(7, 'R', seatcode.substring(7));
                    final int seatId = row * 8 + column;
                    return new Tuple<String, Integer>(seatcode, seatId);
                })
                .max(Comparator.comparing(Tuple::getValue, Comparator.naturalOrder()))
                .ifPresent(System.out::println);
    }
    
    private static class Tuple<E, V> {
        private final E entry;
        private final V value;
        
        public Tuple(E entry, V value) {
            this.entry = entry;
            this.value = value;
        }

        public E getEntry() {
            return entry;
        }

        public V getValue() {
            return value;
        }

        public String toString() {
            return entry + ": " + value;
        }
    }

    public static int binarySearch(final int limit, final char upper, final String input) {
        int min = 0;
        int max = limit;
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            final int range = (max + 1 - min) / 2;
            if (c == upper) {
                min += range;
            } else {
                max -= range;
            }
        }
        return max;
    }
}
