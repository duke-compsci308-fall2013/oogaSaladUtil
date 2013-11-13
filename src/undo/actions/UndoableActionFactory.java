package undo.actions;

import oogasalad.util.reflection.Reflection;
import undo.actions.undo.IUndoableAction;


/**
 * Factory for generating undoable actions (IUndoableAction).
 * 
 * @author Patrick Schutz
 */
public class UndoableActionFactory {

    /**
     * Method to build a new instance of IUndoableAction, the exact type of which
     * is determined by the caller.
     * 
     * @param type Type of undoable action to return. Should be full path to type, including
     *        packages. (ie "undo.actions.UndoableAction")
     * @return An instance of IUndoableAction representing the specified type.
     * @throws RuntimeException if the undoable action can't be instantiated or
     *         argument not of type IUndoableAction.
     */
    public static IUndoableAction generateUndoableAction (String type, Object ... args) {
        return (IUndoableAction) Reflection.createInstance(type, args);
    }
}
