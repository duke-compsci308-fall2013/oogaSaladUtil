package undo.actions.undo;

import undo.actions.IAction;

/**
 * Interface to encapsulate actions that can be done and undone.
 * 
 * @author Patrick Schutz
 *
 */
public interface IUndoableAction extends IAction {

    /**
     * Defines action to be taken when executed.
     * Should be the same as execute()
     */
    public abstract void redo ();

    /**
     * Defines action needed to undo previously taken action.
     * Should be the inverse of execute()/redo().
     */
    public abstract void undo ();

}
