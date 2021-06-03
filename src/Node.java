public class Node implements Comparable<Node> {

    public char character;
    public int frequency;
    public Node left, right;

    public Node(char character, int frequency, Node left, Node right) {
        this.character = character;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(Node o) {
        return frequency - o.frequency;
    }
}
