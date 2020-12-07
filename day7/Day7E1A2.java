package be.schaubroeck.golf.connect.v2.core.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Michaël Goossens on 07/12/2020.
 */
public class Day7E1A2 {

    public static final String BAGS_CONTAIN = " bags contain ";
    public static final String BAGS_CONTAIN_NO_OTHER_BAGS = "bags contain no other bags.";
    public static final String SPACE = " ";
    public static final String BAG = " bag";

    public static void main(String[] args) throws IOException {
        final List<String> input = Files.readAllLines(Paths.get("/Users/mgoossens/personal/adventofcode/day6/input_small.txt"), StandardCharsets.UTF_8);

        final Map<String, Bag> bagConfigs = new HashMap<>();
        
        // Parsing input into a graph.
        input.forEach(bagConfig -> {
            final int containIndex = bagConfig.indexOf(BAGS_CONTAIN);
            final String bagColor = bagConfig.substring(0, containIndex);

            final Bag outerBag = putIfAbsentAndReturnCurrent(bagColor, bagConfigs);
            
            if (!bagConfig.endsWith(BAGS_CONTAIN_NO_OTHER_BAGS)) {
                final String[] containingBagsConfig = bagConfig.substring(containIndex + BAGS_CONTAIN.length()).split(", ");
                for (final String containingBagConfig : containingBagsConfig) {
                    final int firstSpaceIndex = containingBagConfig.indexOf(SPACE);
                    final int amount = Integer.parseInt(containingBagConfig.substring(0, firstSpaceIndex));
                    final int bagIndex = containingBagConfig.indexOf(BAG);
                    final String innerBagColor = containingBagConfig.substring(containingBagConfig.indexOf(" ") + 1, bagIndex);
                    addInnerBagToOuterBag(putIfAbsentAndReturnCurrent(innerBagColor, bagConfigs), amount, outerBag);
                }
            }
        });

        // Solving it
        final String question = "shiny gold";
        final Bag shinyGoldBag = bagConfigs.get(question);
        // Part 1
        System.out.println(addParentBags(shinyGoldBag, new HashSet<>()).size());
        // Part 2
        // -1 because the question is how many bags are inside of the question bag, not how many including the shiny gold one.
        System.out.println(shinyGoldBag.countBags() - 1);
    }
    
    private static Set<Bag> addParentBags(final Bag bag, final Set<Bag> parents) {
        parents.addAll(bag.outerBags);
        bag.getOuterBags().forEach(outerBag -> addParentBags(outerBag, parents));
        return parents;
    }
    

    private static void addInnerBagToOuterBag(final Bag innerBag, final int amount, final Bag outerBag) {
        outerBag.getInnerBags().put(innerBag, amount);
        innerBag.getOuterBags().add(outerBag);
    }

    private static Bag putIfAbsentAndReturnCurrent(final String bagColor, final Map<String, Bag> bagConfigs) {
        bagConfigs.putIfAbsent(bagColor, new Bag(bagColor));
        return bagConfigs.get(bagColor);
    }
    
    private static class Bag {
        private final String bagColor;
        
        private final Map<Bag, Integer> innerBags = new HashMap<>();
        private final List<Bag> outerBags = new ArrayList<>();

        public Bag(final String bagColor) {
            this.bagColor = bagColor;
        }

        public String getBagColor() {
            return bagColor;
        }

        public Map<Bag, Integer> getInnerBags() {
            return innerBags;
        }

        public List<Bag> getOuterBags() {
            return outerBags;
        }
        
        public String toString() {
            final StringBuilder result = new StringBuilder("Bag " + bagColor + " contains:\n");
            for (final Map.Entry<Bag, Integer> bagConfig : innerBags.entrySet()) {
                result.append(String.format("\t %s %s bag(s) \n", bagConfig.getValue(), bagConfig.getKey().bagColor));
            }
            result.append("\n");
            return result.toString();
        }

        public int countBags() {
            return 1 + innerBags.entrySet().stream()
                    .map(entry -> entry.getValue() * entry.getKey().countBags())
                    .reduce(0, Integer::sum);
        }
    }
}
