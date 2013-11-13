package undo.actions.undo.undoablecommands;

import undo.actions.undo.IUndoableAction;


/**
 * Template class for UndoableActions.
 * 
 * @author Patrick Schutz
 */
public abstract class AbstractUndoableCommand implements IUndoableAction {

    /**
     * Method to redo previously undone action. Consists of merely re-executing the action.
     * Other methods must still be implemented. Dependencies must also be added via 
     * constructor.
     */
    public void redo () {
        execute();
    }
}
