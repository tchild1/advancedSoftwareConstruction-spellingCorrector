package spell;

import java.util.Dictionary;

public class Node implements INode {

    int count;
    INode[] childNodes = new Node[26];


    /**
     * Constructor
     */
    public Node() {
        count = 0;
    }
    @Override
    public int getValue() {
        return count;
    }

    @Override
    public void incrementValue() {
        count = count + 1;
    }

    @Override
    public INode[] getChildren() {
        return childNodes;
    }
}
