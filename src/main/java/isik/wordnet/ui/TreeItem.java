package isik.wordnet.ui;

public class TreeItem {
    private static double lastAssignedID = 0;

    private String representative;
    private String wordNetID;
    private double assignedID;

    TreeItem(String representative, String wordNetID) {
        this.representative = representative;
        this.wordNetID = wordNetID;
        this.assignedID = lastAssignedID + 0.0001;
        lastAssignedID = this.assignedID;
    }

    public String getRepresentative() {
        return representative;
    }

    public String getWordNetID() {
        return wordNetID;
    }

    @Override
    public boolean equals(Object t) {
        if (t instanceof TreeItem) {
            return ((TreeItem) t).assignedID == this.assignedID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (10000 * 17 + this.assignedID);
    }

    @Override
    public String toString() {
        return this.representative + "-" + this.wordNetID;
    }
}
