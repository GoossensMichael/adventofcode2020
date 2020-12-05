public class Day5E1Alt2 {

    public static void main(String[] args) throws IOException {
        final List<String> seatcodes = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day5/input.txt"), StandardCharsets.UTF_8);

        seatcodes.stream()
                .map(seatcode -> seatcode.replaceAll("B|R", "1"))
                .map(seatcode -> seatcode.replaceAll("F|L", "0"))
                .map(seatcode -> (Integer.parseInt(seatcode.substring(0, 7), 2) * 8) + Integer.parseInt(seatcode.substring(7), 2))
                .max(Comparator.naturalOrder())
                .ifPresent(System.out::println);
    }
    
}
