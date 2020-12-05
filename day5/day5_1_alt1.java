    public static void main(String[] args) throws IOException {
        final List<String> seatcodes = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day5/input.txt"), StandardCharsets.UTF_8);

        seatcodes.stream()
                .map(seatcode -> (binarySearch(seatcode.substring(0, 7), 'F', 127) * 8) + binarySearch(seatcode.substring(7), 'L', 7))
                .max(Comparator.naturalOrder())
                .ifPresent(System.out::println);
    }
    
    private static int binarySearch(final String code, final char lower, final int limit) {
        final List<Character> chars = new ArrayList<>();
        for(char c : code.toCharArray()) {
            chars.add(c);
        }
        return chars.stream()
                .reduce(new int[] { 0, limit },
                        (range, seatcodePart) -> {
                            final int[] result = seatcodePart == lower ?
                                    new int[] { range[0], range[1] - ((range[1] + 1 - range[0]) / 2) } :
                                    new int[] { range[0] + ((range[1] + 1 - range[0]) / 2),  range[1]};
                            return result;
                        },
                        (range, range2) -> range2
                )[0];
    }
    
