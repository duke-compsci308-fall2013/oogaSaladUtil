package undo.actions.undo;

/**
 * Interface that defines methods for undoing/redoing the a user's (or program's)
 * most recent actions. 
 * 
 * @author Patrick Schutz
 */
public interface IUndoRedoManager {

    /**
     * Undo last done action.
     */
    public abstract void undo ();

    /**
     * Redo last undone action.
     */
    public abstract void redo ();

    /**
     * Add undoable action to be added to IUndoRedoManager and provide ability to undo and
     * redo this action. Execute this undoable action at time of adding.
     * 
     * @param actionName Name of class that implements IUndoableAction to instantiate via reflection
     * @param args Arguments for class being added.
     */
    public abstract void addAndExecute (String actionName,Object... args);

}
