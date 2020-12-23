package adventofcode.day23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MainDay23 {

    private static final int MOVES = 100;

    private static final List<Integer> input_small = new ArrayList<>(Arrays.asList(3, 8, 9, 1, 2, 5, 4, 6, 7));
    private static final List<Integer> input = Arrays.asList(5, 9, 8, 1, 6, 2, 7, 3, 4);

    public static void main(String[] args) {
        final List<Integer> inputToUse =
//                input_small;
                input;
        part1(inputToUse);
        part2(inputToUse);
    }

    private static void part2(final List<Integer> input) {
        final Map<Integer, Node> nodesByLabel = new HashMap<>();
        final CircleList circleList = new CircleList();

        input.forEach(i -> nodesByLabel.put(i, circleList.add(i)));

        // Add up to one million cups
        IntStream.range(input.stream().mapToInt(i -> i).max().orElseThrow() + 1, 1_000_000 + 1)
                .forEach(i -> nodesByLabel.put(i, circleList.add(i)));

        printPart2Solution(play(10_000_000, circleList, nodesByLabel));
    }

    private static void printPart2Solution(final Node nodeOne) {
        final int nextCup = nodeOne.getNext().getValue();
        final int nextCupAfterNext = nodeOne.getNext().getNext().getValue();
        System.out.printf("Result of part 2: %d * %d = %d", nextCup, nextCupAfterNext, Integer.toUnsignedLong(nextCup) * Integer.toUnsignedLong(nextCupAfterNext));
    }

    private static void part1(final List<Integer> input) {
        final Map<Integer, Node> nodesByLabel = new HashMap<>();
        final CircleList circleList = new CircleList();

        input.forEach(i -> nodesByLabel.put(i, circleList.add(i)));

        printPart1Solution(play(100, circleList, nodesByLabel));
    }

    private static void printPart1Solution(final Node nodeOne) {
        final StringBuilder s = new StringBuilder();
        Node printNode = nodeOne.getNext();
        while (printNode != nodeOne) {
            s.append(printNode.getValue());
            printNode = printNode.getNext();
        }
        System.out.println("Result of part 1: " + s.toString());
    }

    private static Node play(final int rounds, final CircleList circleList, final Map<Integer, Node> nodesByLabel) {
        Node currentNode = circleList.getBegin();
        for (int i = 0; i < rounds; i++) {
            final Node destination = findDestinationCup(currentNode, nodesByLabel, circleList);
            circleList.move(destination, currentNode, 3);
            currentNode = currentNode.getNext();
        }

        return nodesByLabel.get(1);
    }

    private static Node findDestinationCup(final Node originNode, final Map<Integer, Node> nodesByLabel, final CircleList circleList) {
        boolean stillSearching = true;
        final Node firstPickupNode = originNode.getNext();
        // First guess.
        Node destinationNode = nodesByLabel.get(originNode.getValue() - 1);
        while (stillSearching) {
            if (destinationNode == null) {
                destinationNode = circleList.getLargest();
            } else if (firstPickupNode.equalsInRange(destinationNode, 3)) {
                destinationNode = nodesByLabel.get(destinationNode.getValue() - 1);;
            } else {
                stillSearching = false;
            }
        }
        return destinationNode;
    }

    private static final class CircleList {
        private Node end;
        private Node largest;

        public CircleList() {
            end = null;
            largest = null;
        }

        public Node add(final int value) {
            final Node newNode = new Node(value);
            final Node previousEnd = end;

            end = newNode;
            if (previousEnd == null) {
                end.setNext(newNode);
            } else {
                newNode.setNext(previousEnd.getNext());
                previousEnd.setNext(newNode);
            }

            if (newNode.isLargerThan(largest)) {
                largest = newNode;
            }

            return newNode;
        }

        public void move(final Node from, final Node currentNode, final int chainLength) {
            final Node firstNodeInChain = currentNode.getNext();
            final Node temp = from.getNext();

            from.setNext(firstNodeInChain);

            Node lastNodeToMove = firstNodeInChain;
            for (int i = 0; i < chainLength - 1; i++) {
                lastNodeToMove = lastNodeToMove.getNext();
            }
            currentNode.setNext(lastNodeToMove.getNext());
            lastNodeToMove.setNext(temp);
        }

        public Node getBegin() {
            return end.getNext();
        }

        public Node getLargest() {
            return largest;
        }

        public void print() {
            final Node firstNode = end.getNext();
            Node currentNode = firstNode;
            do {
                System.out.print(" " + currentNode.getValue());
                currentNode = currentNode.getNext();
            } while (currentNode != firstNode);
            System.out.println();
        }
    }

    private static final class Node {
        private final int value;
        private Node next;

        public Node(final int value) {
            this.value = value;
        }

        public void setNext(final Node next) {
            this.next = next;
        }

        public Node getNext() {
            return next;
        }

        public int getValue() {
            return value;
        }

        public boolean equals(final Node other) {
            return other.value == value;
        }

        public boolean equalsInRange(final Node other, final int range) {
            if (range == 1) {
                return equals(other);
            } else {
                return equals(other) || next.equalsInRange(other, range - 1);
            }
        }

        public boolean isLargerThan(final Node other) {
            if (other == null) {
                return true;
            }
            return this.value > other.getValue();
        }
    }
}
