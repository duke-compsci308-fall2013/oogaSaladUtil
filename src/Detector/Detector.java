package gameEngine.model;

import gameEngine.constant.GameEngineConstant;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import jgame.JGObject;
import jgame.JGRectangle;
import jgame.impl.JGEngineInterface;


/**
 * In each game, we have lots of detection to decide
 * if an enemy or a tower is in range etc.  This class is reused by me teammates a lot.
 * This class hides the bad design of JGEngine in detection from other
 * part of code. In JGEngine, it returns a non-parameterized Vector.
 * Vector will be drepracated soon. So we change it to List(Collection would be better considering other team wants to use)
 * It follows Adapter Design Pattern. If JGame updates or people decide to use another library, detector is still there.
 * It is a universal function in a game so it is desirable to make this adapter.
 * Also while using JGEngine to directly detect JGObject, the user may mess up
 * becasue they may want to detect a certain type of JGObject but gives 
 * the wrong collision_id or name.This class creates a bond to the parameter T
 * and the .class: 
 * For eg. Detector<Enemy> d= new Detector<Enemy>(eng, Enemy.class)
 * The type in the bracket has to be consistent to the .class. Otherwise, 
 * it would be a compile error. It is the main purpose of this generics plus
 * reflection method. It forces the user to do the right thing. 
 * 
 * @author Jiaran
 * 
 */
/**
 * 
 *
 * @param <T>
 */
public class Detector<T extends JGObject> {
    private JGEngineInterface myEng;
    // Because of Erasure, we need to use reflection to help complete the function
    private Class<T> myType;

    public Detector (JGEngineInterface eng, Class<T> type) {
        myEng = eng;
        myType = type;
    }

   
    /**
     * This will return a certain type of subclasses of JGObject. It hides 
     * all the bad design in JGame. Change the return value from a non-parameterized 
     * Vector to a nice List.
     * @param centerx : x coordinate of the place to perform the detection
     * @param centery: y coordinate of the place to perform the detection
     * @param range: the range to detect
     * @return List of a type of JGObject 
     */
    @SuppressWarnings("unchecked")
    public List<T> getTargetsInRange (int centerx, int centery, int range) {
        Vector<JGObject> v =
                getObjects(centerx, centery, range, GameEngineConstant.query(myType), null);
        List<T> result = new ArrayList<T>();
        for (int i = 0; i < v.size(); i++) {
            result.add((T) v.get(i));
        }
        return result;
    }
    
   

   
    @SuppressWarnings("unchecked")
    private Vector<JGObject> getObjects (int centerx, int centery, int range, int cid, String prefix) {
        JGRectangle rect = new JGRectangle(centerx - range, centery - range, 2 * range, 2 * range);
        return myEng.getObjects(prefix, cid, false, rect);
    }

}
