
import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Encoder {

    public static void encode() throws IOException {
        String s = "aba ab cabbb";
        char[] file = s.toCharArray();
        int[] frequencyTable = new int[1 << 8];

        for (char c : file) {
            frequencyTable[c]++;
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (int i = 0; i < frequencyTable.length; ++i) {
            if (frequencyTable[i] > 0) {
                pq.add(new Node((char) i, frequencyTable[i], null, null));
            } else if (i == 1 << 8 - 1) {
                pq.add(new Node((char) i, 1, null, null));
            }
        }

        while (pq.size() > 1) {
            Node node1 = pq.poll();
            Node node2 = pq.poll();
            Node node3 = new Node('\0', node1.frequency + node2.frequency, node1, node2);
            pq.add(node3);
        }

        Node huffmanTree = pq.poll();
        String[] encodingMap = new String[1 << 8];
        dfs(huffmanTree, encodingMap, "");

        StringBuilder encoded = new StringBuilder();
        for (char c : file) {
            encoded.append(encodingMap[c]);
        }

        StringBuilder header = new StringBuilder();
        dfs(huffmanTree, header);
        System.out.println(encoded);
        System.out.println(header);


    }

    private static void dfs(Node tree, String[] encodingMap, String path) {
        if (tree == null) return;

        if (tree.left == null && tree.right == null) encodingMap[tree.character] = path;
        dfs(tree.left, encodingMap, path + "0");
        dfs(tree.right, encodingMap, path + "1");
    }

    private static void dfs(Node tree, StringBuilder header) {
        if (tree == null) return;

        if (tree.character != '\0') {
            header.append(1);
            String binaryRepresentation = Integer.toBinaryString(tree.character);
            header.append(binaryRepresentation);
        } else {
            header.append(0);
        }

        dfs(tree.left, header);
        dfs(tree.right, header);
    }

    public static void writeToFile(String encoded, String file) {

    }

    public static void readFromFile(File file) throws IOException {

    }

    private static char[] filetoString(File file) {
        return null;
    }
}
