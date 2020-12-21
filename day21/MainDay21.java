package adventofcode.day21;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MainDay21 {

    public static void main(String[] args) throws IOException {
        final String inputPath =
//                "src/adventofcode/day21/input_tiny.txt";
//                "src/adventofcode/day21/input_small.txt";
                "src/adventofcode/day21/input.txt";

        // Container for all the different food products
        final List<Food> foods = new ArrayList<>();
        // Container for all the different ingredients
        final Set<String> ingredients = new HashSet<>();

        // Parse the input and fill the food and ingredient container
        Files.lines(Paths.get(inputPath))
                .forEach(foodDescription -> {
                    final int allergeneIndex = foodDescription.indexOf('(');
                    final List<String> ingredientsForFood = Arrays.stream(foodDescription.substring(0, allergeneIndex - 1).split(" "))
                            .peek(ingredients::add)
                            .collect(Collectors.toList());
                    final List<String> allergenes = Arrays.stream(foodDescription.substring(allergeneIndex + 10, foodDescription.length() - 1).split(", "))
                            .collect(Collectors.toList());
                    foods.add(new Food(ingredientsForFood, allergenes));
                });

        // Solve the "which food product has allergene information"-problem
        final Map<String, List<String>> solution = solve(foods);
        // Extract the set of ingredients that have allergene information
        final Set<String> ingredientsWithAllergenes =
                solution.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        // Extract the set of ingredients that have no allergene information
        final Set<String> ingredientsWithoutAllergenes =
                new HashSet<>(ingredients) {{ removeAll(ingredientsWithAllergenes); }};

        part1(foods, ingredientsWithoutAllergenes);
        part2(solution);
    }

    private static void part2(final Map<String, List<String>> solution) {
        final String result = solution.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> entry.getValue().get(0))
                .collect(Collectors.joining(","));
        System.out.println("Here it is: " + result);
    }

    private static void part1(final List<Food> foods, final Set<String> ingredients) {
        final AtomicInteger sum = new AtomicInteger(0);
        foods.forEach(food -> {
            ingredients.forEach(ingredient -> {
                if (food.getIngredients().contains(ingredient)) {
                    sum.incrementAndGet();
                }
            });
        });
        System.out.printf("Safe ingredients appear %d times.\n", sum.get());
    }

    private static Map<String, List<String>> solve(final List<Food> foods) {
        final Map<String, List<String>> ingredientsByAllergene = new HashMap<>();

        for (final Food food : foods) {
            for (final String allergene : food.getAllergenes()) {
                if (ingredientsByAllergene.containsKey(allergene)) {
                    ingredientsByAllergene.get(allergene).retainAll(food.getIngredients());
                    cleanupIfSingleIngredientLeft(allergene, ingredientsByAllergene);
                } else {
                    ingredientsByAllergene.put(allergene, new ArrayList<>(food.getIngredients()));
                }
            }
        }

        return ingredientsByAllergene;
    }

    private static void cleanupIfSingleIngredientLeft(final String allergene, final Map<String, List<String>> ingredientsByAllergene) {
        if (ingredientsByAllergene.get(allergene).size() == 1) {
            final String ingredient = ingredientsByAllergene.get(allergene).get(0);
            ingredientsByAllergene.entrySet().stream().filter(entry -> !entry.getKey().equals(allergene)).forEach(entry -> {
                if (entry.getValue().contains(ingredient)) {
                    entry.getValue().remove(ingredient);
                    cleanupIfSingleIngredientLeft(entry.getKey(), ingredientsByAllergene);
                }
            });
        }
    }

    public static final class Food {
        private final List<String> ingredients;
        private final List<String> allergenes;

        public Food(final List<String> ingredients, final List<String> allergenes) {
            this.ingredients = ingredients;
            this.allergenes = allergenes;
        }

        public List<String> getIngredients() {
            return ingredients;
        }

        public List<String> getAllergenes() {
            return allergenes;
        }
    }
}
