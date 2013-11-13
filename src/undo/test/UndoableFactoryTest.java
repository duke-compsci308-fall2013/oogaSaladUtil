package undo.test;

import static org.junit.Assert.*;
import org.junit.Test;
import undo.actions.UndoableActionFactory;
import undo.actions.undo.IUndoableAction;
import undo.test.mockclasses.TestUndoableAction;


public class UndoableFactoryTest {
    String testString;

     

    @Test
    public void testMakingUndoableAction () {
        IUndoableAction command = 
                UndoableActionFactory.generateUndoableAction("undo.test.mockclasses.TestUndoableAction", "Undone", "Execute");
        assertEquals(new TestUndoableAction("Undone", "Execute"),command);
    }
}
