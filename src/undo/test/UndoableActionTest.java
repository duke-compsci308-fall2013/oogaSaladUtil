package undo.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import undo.actions.UndoableActionFactory;
import undo.actions.undo.IUndoableAction;
import undo.test.mockclasses.TestUndoableAction;

public class UndoableActionTest {
    private IUndoableAction myAction;
    @Before
    public void setUp () throws Exception {
        myAction=UndoableActionFactory.generateUndoableAction("undo.test.mockclasses.TestUndoableAction", "Undone", "Done");
    }

    @Test
    public void testExecute () {
        myAction.execute();
        assertEquals("Done",TestUndoableAction.testString);
    }
    
    @Test
    public void testUndo(){
        myAction.undo();
        assertEquals("Undone",TestUndoableAction.testString);
    }
    
    @Test
    public void testRedo(){
        myAction.redo();
        assertEquals("Done",TestUndoableAction.testString);
    }

}
