package undo.actions;

import java.util.Stack;
import undo.actions.undo.IUndoRedoManager;
import undo.actions.undo.IUndoableAction;


/**
 * Implementation of IUndoRedoManager that keeps track of undoable and
 * redoable actions using Stacks.See IUndoRedoManager for Javadoc
 * comments pertaining to overridden methods.
 * 
 * @author Patrick Schutz
 * 
 */
public class UndoRedoManager implements IUndoRedoManager {

    private Stack<IUndoableAction> myRedoableActions;
    private Stack<IUndoableAction> myUndoableActions;

    public UndoRedoManager () {
        myRedoableActions = new Stack<IUndoableAction>();
        myUndoableActions = new Stack<IUndoableAction>();
    }

    @Override
    public void redo () {
        myRedoableActions.peek().redo();
        myUndoableActions.push(myRedoableActions.pop());
    }

    @Override
    public void addAndExecute (String actionName, Object ... args) {
        IUndoableAction action = UndoableActionFactory.generateUndoableAction(actionName, args);
        action.execute();
        myUndoableActions.push(action);
    }

    @Override
    public void undo () {
        myUndoableActions.peek().undo();
        myRedoableActions.push(myUndoableActions.pop());
    }

}
