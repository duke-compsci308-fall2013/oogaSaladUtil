package undo.test.mockclasses;

import undo.actions.undo.undoablecommands.AbstractUndoableCommand;


public class TestUndoableAction extends AbstractUndoableCommand {
    public static String testString;
    private String myPrevText = "";
    private String myText;

    public TestUndoableAction (String prevText, String curText) {
        myPrevText = prevText;
        myText = curText;
    }

    @Override
    public void undo () {
        testString = myPrevText;
    }

    @Override
    public void execute () {
        testString = myText;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((myPrevText == null) ? 0 : myPrevText.hashCode());
        result = prime * result + ((myText == null) ? 0 : myText.hashCode());
        return result;
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TestUndoableAction other = (TestUndoableAction) obj;
        if (myPrevText == null) {
            if (other.myPrevText != null) return false;
        }
        else if (!myPrevText.equals(other.myPrevText)) return false;
        if (myText == null) {
            if (other.myText != null) return false;
        }
        else if (!myText.equals(other.myText)) return false;
        return true;
    }

}
