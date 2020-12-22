package adventofcode.day22;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainDay22 {

    public static void main(String[] args) throws IOException {
        final String inputPath =
//                "src/adventofcode/day22/input_tiny.txt";
//                "src/adventofcode/day22/input_small.txt";
                "src/adventofcode/day22/input.txt";

        final List<PlayerDeck> playerDecks = loadPlayerDecks(inputPath);
        part1(playerDecks);
        playerDecks.forEach(PlayerDeck::reset);
        part2(playerDecks);
    }

    private static List<PlayerDeck> loadPlayerDecks(final String inputPath) throws IOException {
        // Parse the input and fill the food and ingredient container
        return Arrays.stream(Files.readString(Paths.get(inputPath), StandardCharsets.UTF_8)
                .split("\n\n"))
                .map(input -> {
                    final String[] playerDeck = input.split("\n");
                    final String playerNumber = playerDeck[0].substring(0, playerDeck[0].length() - 1);
                    final Queue<Integer> cards = new LinkedList<>();
                    for (int i = 1; i < playerDeck.length; i++) {
                        cards.add(Integer.parseInt(playerDeck[i]));
                    }
                    return new PlayerDeck(playerNumber, cards);
                })
                .collect(Collectors.toList());
    }

    private static void part2(final List<PlayerDeck> playerDecks) {
        final long begin = System.currentTimeMillis();
        final PlayerDeck recursiveCombatWinner = playRecursiveCombat(playerDecks, new AtomicInteger(1), new HashMap<>());
        System.out.printf("Part 2 solved in %d ms.\n", System.currentTimeMillis() - begin);
//        System.out.println("\n\n== Post-game results==");
//        playerDecks.forEach(playerDeck -> {
//            final String deck = playerDeck.printCards();
//            System.out.printf("%s's deck: %s\n", playerDeck.getPlayerId(), deck);
//        });

        System.out.printf("Result of part 2: %d.\n", calculateScore(recursiveCombatWinner));
    }

    private static boolean doesAPlayerHaveARecurringDeckInCurrentRound(final List<PlayerDeck> playerDecks,
                                                                       final Map<String, List<String>> playerDecksForCurrentRound) {
        for (final PlayerDeck playerDeck : playerDecks) {
            playerDecksForCurrentRound.putIfAbsent(playerDeck.getPlayerId(), new ArrayList<>());
            if (playerDecksForCurrentRound.get(playerDeck.getPlayerId()).contains(playerDeck.printCards())) {
                return true;
            }
        }
        return false;
    }

    private static PlayerDeck playRecursiveCombat(final List<PlayerDeck> playerDecks, final AtomicInteger gameCounter,
                                                  final Map<String, PlayerDeck> memory) {
        final int thisGame = gameCounter.get();
//        System.out.printf("== Game %d ==\n", gameCounter.get());
        int round = 1;
        final Map<String, List<String>> playerDecksForCurrentRound = new HashMap<>();

        while (moreThanOnePlayerInTheRunning(playerDecks)) {
//            System.out.printf("\n-- Round %d (Game %d) --\n", round, gameCounter.get());
            if (doesAPlayerHaveARecurringDeckInCurrentRound(playerDecks, playerDecksForCurrentRound)) {
                return playerDecks.get(0);
            } else {
                playerDecks.forEach(playerDeck -> {
                    final String deck = playerDeck.printCards();
//                    System.out.printf("%s's deck: %s\n", playerDeck.getPlayerId(), deck);
                    playerDecksForCurrentRound.putIfAbsent(playerDeck.getPlayerId(), new ArrayList<>());
                    playerDecksForCurrentRound.get(playerDeck.getPlayerId()).add(deck);
                });
                final int[] topCardsOfEachDeck = getTopCardOfEachDeck(playerDecks);
                if (doAllPlayersHaveAtLeastTheAmountOfCardsAsTheCardValueDrawn(topCardsOfEachDeck, playerDecks)) {
//                    System.out.println("Playing a sub-gameCounter to determine the winner...\n");

                    final String memoryEntry = createMemory(playerDecks);
                    final PlayerDeck winnerOfSubGame;
                    gameCounter.incrementAndGet();
                    if (memory.containsKey(memoryEntry)) {
                        winnerOfSubGame = memory.get(memoryEntry);
                    } else {
                        winnerOfSubGame =
                                playRecursiveCombat(
                                        IntStream.range(0, playerDecks.size())
                                        .mapToObj(i -> playerDecks.get(i).copy(topCardsOfEachDeck[i]))
                                                .collect(Collectors.toList()),
                                        gameCounter,
                                        memory)
                                        .getParentPlayerDeck();
                        memory.put(memoryEntry, winnerOfSubGame);
                    }
//                    System.out.printf("...anyway, back to gameCounter %d.\n", gameCounter.get());
                    final int indexOfWinningPlayer = playerDecks.indexOf(playerDecks.stream()
                            .filter(playerDeck -> playerDeck.getPlayerId().equals(winnerOfSubGame.playerId))
                            .findFirst()
                            .orElseThrow());
                    winnerOfSubGame.getCards().add(topCardsOfEachDeck[indexOfWinningPlayer]);
                    IntStream.range(0, topCardsOfEachDeck.length)
                            .filter(i -> i != indexOfWinningPlayer)
                            .forEach(i -> winnerOfSubGame.getCards().add(topCardsOfEachDeck[i]));
//                    System.out.printf("%s wins round %d of gameCounter %d!\n", winnerOfSubGame.getPlayerId(), round, gameCounter.get());
                } else {
                    final int winnerIndex = getIndexOfLargest(topCardsOfEachDeck);
                    final PlayerDeck roundWinner = playerDecks.get(winnerIndex);
                    Arrays.sort(topCardsOfEachDeck);
                    for (int i = topCardsOfEachDeck.length - 1; i >= 0; i--) {
                        playerDecks.get(winnerIndex).getCards().add(topCardsOfEachDeck[i]);
                    }
//                    System.out.printf("%s wins round %d of gameCounter %d!\n", roundWinner.getPlayerId(), round, gameCounter.get());
                }
            }
            round++;
        }

        final PlayerDeck gameWinner = playerDecks.stream()
                .filter(playerDeck -> !playerDeck.getCards().isEmpty())
                .findFirst()
                .orElseThrow();
//        System.out.printf("The winner of gameCounter %d is %s!\n\n", gameCounter.get(), gameWinner.getPlayerId());
        return gameWinner;
    }

    private static String createMemory(final List<PlayerDeck> playerDecks) {
        return playerDecks.stream()
                .map(playerDeck -> playerDeck.getPlayerId() + "(" + playerDeck.printCards() + ")")
                .collect(Collectors.joining("} {", "{", "{"));
    }

    private static int[] getTopCardOfEachDeck(final List<PlayerDeck> playerDecks) {
        return playerDecks.stream()
//                .peek(playerDeck -> System.out.printf("%s plays: %d\n", playerDeck.getPlayerId(), playerDeck.getCards().peek()))
                .mapToInt(playerDeck -> playerDeck.getCards().remove())
                .toArray();
    }

    private static boolean doAllPlayersHaveAtLeastTheAmountOfCardsAsTheCardValueDrawn(final int[] drawn, final List<PlayerDeck> playerDecks) {
        return IntStream.range(0, drawn.length)
                .allMatch(i -> playerDecks.get(i).getCards().size() >= drawn[i]);
    }

    private static void part1(final List<PlayerDeck> playerDecks) {
        final long begin = System.currentTimeMillis();
        final PlayerDeck playerDeckOfWinner = playCombat(playerDecks);
        System.out.printf("Part 1 solved in %d ms.\n", System.currentTimeMillis() - begin);
//        System.out.printf("%s has won.\n", playerDeckOfWinner.playerId);
//        System.out.printf("Deck of winner: %s\n", playerDeckOfWinner.printCards());
        System.out.printf("Result of part 1: %d.\n", calculateScore(playerDeckOfWinner));
    }

    private static int calculateScore(final PlayerDeck playerDeck) {
        final List<Integer> cards = new ArrayList<>(playerDeck.getCards());
        return IntStream.range(0, playerDeck.getCards().size())
                .map(i -> (cards.size() - i) * cards.get(i))
                .sum();
    }

    private static PlayerDeck playCombat(final List<PlayerDeck> playerDecks) {
        final Map<String, List<String>> deckHistory = new HashMap<>();
        while (moreThanOnePlayerInTheRunning(playerDecks)) {
            if (doesAPlayerHaveARecurringDeckInCurrentRound(playerDecks, deckHistory)) {
                System.out.println("Infinite loop, declaring player 1 as winner!");
                return playerDecks.get(0);
            }
            playerDecks.forEach(playerDeck -> {
                deckHistory.putIfAbsent(playerDeck.getPlayerId(), new ArrayList<>());
                deckHistory.get(playerDeck.getPlayerId()).add(playerDeck.printCards());
            });
            final int[] topCards = getTopCardOfEachDeck(playerDecks);
            final int winnerIndex = getIndexOfLargest(topCards);
            Arrays.sort(topCards);
            for (int i = topCards.length - 1; i >= 0; i--) {
                playerDecks.get(winnerIndex).getCards().add(topCards[i]);
            }
        }
        return playerDecks.stream().filter(playerDeck -> !playerDeck.getCards().isEmpty()).findFirst().orElseThrow();
    }

    private static boolean moreThanOnePlayerInTheRunning(final List<PlayerDeck> playerDecks) {
        return playerDecks.stream().filter(playerDeck -> !playerDeck.getCards().isEmpty()).count() > 1;
    }

    private static int getIndexOfLargest(final int[] cards) {
        return IntStream.range(1, cards.length)
                .reduce(0, (current, next) -> (cards[current] < cards[next]) ? next : current);
    }

    private static final class PlayerDeck {
        private String playerId;

        private final Queue<Integer> originalCards;
        private Queue<Integer> cards;
        private PlayerDeck parentPlayerDeck;

        public PlayerDeck(final String playerId, final Queue<Integer> cards) {
            this.playerId = playerId;
            this.originalCards = new LinkedList<>(cards);
            this.cards = new LinkedList<>(cards);
        }

        public PlayerDeck(final String playerId, final Queue<Integer> cards, final PlayerDeck parentPlayerDeck) {
            this(playerId, cards);
            this.parentPlayerDeck = parentPlayerDeck;
        }

        public String getPlayerId() {
            return playerId;
        }

        public Queue<Integer> getCards() {
            return cards;
        }

        public void reset() {
            this.cards = new LinkedList<>(originalCards);
        }

        public String printCards() {
            return getCards().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
        }

        public PlayerDeck copy(int amountOfCards) {
            final LinkedList<Integer> subList = this.cards.stream().limit(amountOfCards).collect(Collectors.toCollection(LinkedList::new));
            return new PlayerDeck(this.playerId, subList, this);
        }

        public PlayerDeck getParentPlayerDeck() {
            return parentPlayerDeck;
        }
    }
}
