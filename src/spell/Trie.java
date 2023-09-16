package spell;

public class Trie implements ITrie {
    private int count;
    private int nodeCount;
    private INode root;

    public Trie() {
        count = 0;
        nodeCount = 1;
        root = new Node();
    }
    @Override
    public void add(String word) {
        StringBuilder newWord = new StringBuilder(word);
        INode currNode = this.root;
        while (newWord.length() > 0) {
            char currLetter = newWord.charAt(0);
            if (currNode.getChildren()[currLetter - 'a'] == null) {
                currNode.getChildren()[currLetter - 'a'] = new Node();
                nodeCount++;
            }
            currNode = currNode.getChildren()[currLetter - 'a'];
            newWord.deleteCharAt(0);
        }
        if (currNode.getValue() == 0){
            count++;
        }
        currNode.incrementValue();
    }

    @Override
    public INode find(String word) {
        StringBuilder newWord = new StringBuilder(word);
        INode currNode = this.root;
        while (!newWord.isEmpty()) {
            char currLetter = newWord.charAt(0);
            if (currNode.getChildren()[currLetter - 'a'] == null) {
                return null;
            }
            currNode = currNode.getChildren()[currLetter - 'a'];
            newWord.deleteCharAt(0);
        }
        if (currNode.getValue() > 0) {
            return currNode;
        }
        return null;
    }

    @Override
    public int getWordCount() {
        return count;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public String toString() {
        StringBuilder word = new StringBuilder();
        StringBuilder string = new StringBuilder();

        toStringHelper(root, word, string);

        return string.toString();
    }

    private void toStringHelper(INode currNode, StringBuilder word, StringBuilder string) {
        if (currNode.getValue() > 0) {
            string.append(word.toString());
            string.append('\n');
        }

        for (int nodes=0; nodes<26; nodes++) {
            if (currNode.getChildren()[nodes]!=null) {
                char c = (char) (nodes + 'a');
                word.append(c);
                toStringHelper(currNode.getChildren()[nodes], word, string);
                word.deleteCharAt(word.length()-1);
            }
        }
    }




//    @Override
//    public String toString() {
//        StringBuilder word = new StringBuilder();
//        StringBuilder words = new StringBuilder();
//
//        toStringHelper(root, word, words);
//
//        return words.toString();
//    }
//
//    private void toStringHelper(INode node, StringBuilder word, StringBuilder words) {
//        if (node.getValue() > 0) {
//            words.append(word.toString());
//            words.append('\n');
//        }
//
//        INode[] children = node.getChildren();
//
//        for(int i=0; i<children.length;i++) {
//            if(children[i] != null) {
//                char c = (char) (i + 'a');
//                word.append(c);
//                toStringHelper(children[i], word, words);
//                word.deleteCharAt(word.length()-1);
//            }
//        }
//    }

    @Override
    public int hashCode() {
        int node = 0;
        for (node=0; node < 26; node++) {
            if (this.root.getChildren()[node] != null) {
                return (this.getWordCount() * this.getNodeCount() * node);
            }
        }
        return (this.getWordCount() * this.getNodeCount() * node);
    }

    @Override
    public boolean equals(Object object) {
        if ((((Trie) object).getNodeCount() != this.getNodeCount())) {
            return false;
        }
        if (((Trie) object).getWordCount() != this.getWordCount()) {
            return false;
        }

        return nodesEqual(((Trie) object).root, this.root);
    }

    private boolean nodesEqual(INode nodeOne, INode nodeTwo) {
        if (nodeOne == null && nodeTwo == null) {
            return true;
        }
        if (nodeOne == null || nodeTwo == null) {
            return false;
        }
        if (nodeOne.getValue() != nodeTwo.getValue()) {
            return false;
        }
        int equalNodes = 0;
        for (int node=0; node < 26; node++) {
            if (nodesEqual(nodeOne.getChildren()[node], nodeTwo.getChildren()[node])) {
                equalNodes++;
            }
            if (equalNodes == 26) {
                return true;
            }
        }
        return false;
    }
}


// given a character int index = character - 'a';