import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Michaël Goossens on 16/12/2020.
 */
public class MainDay16Part2 {
    
    public static void main(String[] args) throws IOException {
        final String inputPath = "src/day16/input.txt";
        final List<String> lines = Files.readAllLines(Paths.get(inputPath), StandardCharsets.UTF_8);

        final Map<String, List<int[]>> fields = new HashMap<>();
        
        int i = 0;
        while (!lines.get(i).isBlank()) {
            final String line = lines.get(i);
            final int colon = line.indexOf(':');
            final String clazz = line.substring(0, colon);
            final List<int[]> groups = Arrays.stream(line.substring(colon + 2).split(" or "))
                    .map(group -> Arrays.stream(group.split("-")).mapToInt(Integer::parseInt).toArray())
                    .collect(Collectors.toList());
            fields.put(clazz, groups);
            i++;
        }
        
        i += 2;
        final int[] myTicket = Arrays.stream(lines.get(i).split(",")).mapToInt(Integer::parseInt).toArray();
        
        i += 2;
        final List<String> nearbyTicketInput = lines.subList(++i, lines.size());
        final int[][] nearbyTickets = nearbyTicketInput.stream()
                .map(line -> line.split(","))
                .map(values -> Arrays.stream(values).mapToInt(Integer::parseInt).toArray())
                .collect(Collectors.toList())
                .toArray(new int[nearbyTicketInput.size()][]);

        final List<int[]> validTickets = Arrays.stream(nearbyTickets)
                .parallel()
                .filter(ticketValues -> isValidTicket(ticketValues, fields))
                .collect(Collectors.toList());
        
        final Map<Integer, String> fieldsByColumn = new HashMap<>();
        final Map<Integer, Set<String>> multipleAnswers = new HashMap<>();
        for (int column = 0; column < myTicket.length; column++) {
            final int fRow = column;
            final Set<String> matchingFieldNames = fields.entrySet().stream()
                    .filter(fieldEntry -> validTickets.stream().allMatch(validTicket -> isValidValueForClass(validTicket[fRow], fieldEntry.getValue())))
                    .map(Map.Entry::getKey)
                    .filter(matchingFieldName -> !fieldsByColumn.containsValue(matchingFieldName))
                    .collect(Collectors.toSet());
            
            if (matchingFieldNames.size() == 1) {
                final String matchingFieldName = matchingFieldNames.stream().findFirst().get();
                fieldsByColumn.put(column, matchingFieldName);
                
                final List<Integer> toRemove = new ArrayList<>();
                final List<String> matchedFieldNamesToClean = new ArrayList<>();
                matchedFieldNamesToClean.add(matchingFieldName);
                do {
                    final String matchedFieldNameToClean = matchedFieldNamesToClean.remove(0);
                    multipleAnswers.entrySet().stream()
                            .filter(entrySet -> entrySet.getValue().contains(matchedFieldNameToClean))
                            .forEach(entrySet -> {
                                entrySet.getValue().remove(matchedFieldNameToClean);
                                if (entrySet.getValue().size() == 1) {
                                    final String matchedFieldName = entrySet.getValue().stream().findFirst().get();
                                    fieldsByColumn.put(entrySet.getKey(), matchedFieldName);
                                    matchedFieldNamesToClean.add(matchedFieldName);
                                    toRemove.add(entrySet.getKey());
                                }
                            });
                    toRemove.forEach(multipleAnswers::remove);
                } while (matchedFieldNamesToClean.size() > 0);
            } else {
                multipleAnswers.put(column, matchingFieldNames);
            }
        }

        final long departureNumber = fieldsByColumn.entrySet().stream()
                .filter(fieldByColumn -> fieldByColumn.getValue().startsWith("departure"))
                .mapToLong(s -> Integer.toUnsignedLong(myTicket[s.getKey()]))
                .reduce(1L, (acc, n) -> acc *= n);
        System.out.println("Departure number: " + departureNumber);
    }
    
    private static boolean isValidTicket(final int[] ticketValues, final Map<String, List<int[]>> fields) {
        for (final int ticketValue : ticketValues) {
            if (fields.values().stream().allMatch(field -> !withinBounds(ticketValue, field.get(0)) && !withinBounds(ticketValue, field.get(1)))) {
                return false;
            }
        }
        return true;
    }    
    
    private static boolean isValidValueForClass(final int value, final List<int[]> group) {
        //System.out.printf("%d is valid for group [%d, %d] or [%d, %d] ? %s.\n", value, group.get(0)[0], group.get(0)[1], group.get(1)[0], group.get(1)[1], (withinBounds(value, group.get(0)) || withinBounds(value, group.get(1))));
        return withinBounds(value, group.get(0)) || withinBounds(value, group.get(1));
    }
    
    private static boolean withinBounds(final int ticketValue, final int[] bounds) {
//        System.out.printf(" (%d in [%d - %d]? %s)\n", ticketValue, bounds[0], bounds[1], (bounds[0] <= ticketValue && bounds[1] >= ticketValue));
        return bounds[0] <= ticketValue && bounds[1] >= ticketValue;
    }
}
