package undo.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import undo.actions.UndoRedoManager;
import undo.actions.undo.IUndoRedoManager;
import undo.test.mockclasses.TestUndoableAction;


public class TestUndoRedoManager {

    String myTestString; // string set by UndoableCommand for testing
    IUndoRedoManager myManager;

    @Before
    public void setUp () throws Exception {
        myManager = new UndoRedoManager();
    }

    @Test
    public void testAddingUndoableAction () {
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", "Hello", "Goodbye");
        assertEquals("Goodbye", TestUndoableAction.testString);
    }

    @Test
    public void testUndoing () {
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", "Hello", "Goodbye");
        myManager.undo();
        assertEquals("Hello", TestUndoableAction.testString);
    }

    @Test
    public void testRedoing () {
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", "Hello", "Goodbye");
        myManager.undo();
        myManager.redo();
        assertEquals("Goodbye", TestUndoableAction.testString);
    }

    @Test
    public void testUndoingWithMultipleObjects () {
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", "", "Hello");
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", TestUndoableAction.testString, "Goodbye2");
        myManager.undo();

        assertEquals("Hello", TestUndoableAction.testString);

        myManager.undo();

        assertEquals("", TestUndoableAction.testString);

    }

    @Test
    public void testRedoingWithMultipleObjects () {
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", "Hello", "Goodbye");
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", TestUndoableAction.testString, "Goodbye2");
        myManager.undo();
        myManager.redo();

        assertEquals("Goodbye2", TestUndoableAction.testString);
    }

    @Test
    public void testRedoingandUndoingWithMultipleObjects () {
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", "Hello", "Goodbye");
        myManager.addAndExecute("undo.test.mockclasses.TestUndoableAction", TestUndoableAction.testString, "Goodbye2");
        myManager.undo();
        myManager.undo();
        myManager.redo();

        assertEquals("Goodbye", TestUndoableAction.testString);

    }
}
