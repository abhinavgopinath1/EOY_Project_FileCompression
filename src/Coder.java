
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Coder {

    private File file;
    private JLabel jLabel;
    private JProgressBar progressBar;

    public Coder(File file){

        this.file = file;


    }


    public void encode() throws IOException {
        String mime = Files.probeContentType(file.toPath());
        if (!mime.equals("text/plain")) {
            System.out.println("ERROR: Invalid File Type");
            return;
        }

        char[] fileArr = filetoString(file.getAbsolutePath());
        int[] frequencyTable = new int[1 << 8];

        for (char c : fileArr) {
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
        for (char c : fileArr) {
            encoded.append(encodingMap[c]);
        }

        StringBuilder header = new StringBuilder();
        for (int i = 0; i < frequencyTable.length; ++i) {
            header.append(Long.toBinaryString(Integer.toUnsignedLong(frequencyTable[i]) | 0x100000000L ).substring(1));
        }

        String compressed = header.toString() + encoded.toString();
        writeToFile(compressed, file.getName()+".cmp");


    }

    private void dfs(Node tree, String[] encodingMap, String path) {
        if (tree == null) return;

        if (tree.left == null && tree.right == null) encodingMap[tree.character] = path;
        dfs(tree.left, encodingMap, path + "0");
        dfs(tree.right, encodingMap, path + "1");
    }

    public void writeToFile(String encoded, String file) {
        BitSet bitset = new BitSet(encoded.length());
        for (int i = 0; i < encoded.length(); ++i) {
            if (encoded.charAt(i) == '1') bitset.set(i);
        }
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
            os.writeObject(bitset);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String readFromFile() {



        if (file.getName().length() < 4 || !file.getName().endsWith(".cmp")) return null;

        BitSet bitset = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
            bitset = (BitSet) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder bits = new StringBuilder();

        for (int i = 0; i < bitset.length(); ++i) {
            if (bitset.get(i)) bits.append("1");
            else bits.append("0");
        }
        return bits.toString();
    }

    public void decompress() throws IOException {
        String encoded = readFromFile();

        if (encoded == null) {
            System.out.println("ERROR: Invalid File Type");
            return;
        }

        int[] frequencyTable = new int[1 << 8];
        int index = 0;
        for (int i = 0; i < 1 << 8; ++i) {
            String freq = encoded.substring(index, index+32);
            index += 32;
            int n = 0, pow = 1;
            for (int j = freq.length()-1; j >= 0; --j) {
                n += (freq.charAt(j) == '1' ? 1 : 0) * pow;
                pow *= 2;
            }
            frequencyTable[i] = n;
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

        String path = "";
        String decode = "";
        Node node = huffmanTree;
        for (; index < encoded.length(); ++index) {
            if (node.left == null && node.right == null) {
                decode += node.character;
                node = huffmanTree;
                path = "";
            }

            path += encoded.charAt(index);

            if (encoded.charAt(index) == '0') {
                node = node.left;
            } else if (encoded.charAt(index) == '1') {
                node = node.right;
            }

            if (index == encoded.length()-1 && node.left == null && node.right == null) {
                decode += node.character;
                node = huffmanTree;
                path = "";
            }
        }

        FileWriter fw = new FileWriter(file.getName().substring(0, file.getName().length()-4)+".dp");
        fw.write(decode);
        fw.close();
    }

    private static char[] filetoString(String file) throws IOException {
        String s = Files.readString(Path.of(file));
        return s.toCharArray();
    }
}
