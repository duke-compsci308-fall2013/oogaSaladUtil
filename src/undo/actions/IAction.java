package undo.actions;

/**
 * Interface to define an action to be executed.
 * Part of command pattern.
 * 
 * @author Patrick Schutz
 */
public interface IAction {

    /**
     * Method to execute a given instance of IAction.
     */
    public abstract void execute ();
}
