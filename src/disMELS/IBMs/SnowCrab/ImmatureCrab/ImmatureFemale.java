/*
 * ImmatureFemale.java
 */

package disMELS.IBMs.SnowCrab.ImmatureCrab;

import disMELS.IBMs.SnowCrab.FemaleAdolescent.FemaleAdolescent;
import disMELS.IBMs.SnowCrab.ImmatureCrab.ImmatureCrabAttributes;
import disMELS.IBMs.SnowCrab.ImmatureCrab.ImmatureCrabParameters;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import disMELS.IBMs.SnowCrab.ImmatureCrab.ImmatureCrab;
import wts.models.DisMELS.framework.*;

/**
 * DisMELS class representing immature female snow crab, i.e. females that have
 * not yet developed sex organs.
 * 
 * ImmatureFemale inherits almost all its functionality from ImmatureCrab.
 * 
 * @author William Stockhausen
 */
@ServiceProvider(service=LifeStageInterface.class)
public class ImmatureFemale extends ImmatureCrab {
    
        //Static fields    
            //  Static fields new to this class
    /** flag to print debugging info */
    public static boolean debug = false;
    /** flag to do debug operations */
    public static boolean debugOps = false;
    /** Class for attributes */
    public static final String attributesClass = ImmatureCrabAttributes.class.getName();
    /** Class for parameters */
    public static final String parametersClass = ImmatureCrabParameters.class.getName();
    /** Class for feature type for point positions */
    public static final String pointFTClass = wts.models.DisMELS.framework.LHSPointFeatureType.class.getName();
    /** Classes for next LHS */
    public static final String[] nextLHSClasses = new String[]{ImmatureFemale.class.getName(),
                                                               FemaleAdolescent.class.getName()};
    /** Classes for spawned LHS */
    public static final String[] spawnedLHSClasses = new String[]{};
    
    //Instance fields
        //  Fields hiding ones from superclass
    //--none
    
    //  Fields new to class
        //fields that reflect parameter values
    //--none
        //fields that reflect (new) attribute values
    //--none
    
            //other fields    
    /** logger for class */
    private static final Logger logger = Logger.getLogger(ImmatureFemale.class.getName());
    
    /**
     * Creates a new instance of the ImmatureFemale class.  
     *  This constructor should be used ONLY to obtain
     *  the class names of the associated classes.
     * DO NOT DELETE THIS CONSTRUCTOR!!
     */
    public ImmatureFemale() {
        super();
    }
    
    /**
     * Creates a new life stage instance with the given typeName.
     * A new id number is calculated in the superclass and assigned to
     * the new instance's id, parentID, and origID. 
     * 
     * The attributes are vanilla.  Initial attribute values should be set,
     * then initialize() should be called to initialize all instance variables.
     * DO NOT DELETE THIS CONSTRUCTOR!!
     */
    public ImmatureFemale(String typeName) 
                throws InstantiationException, IllegalAccessException {
        super(typeName);
    }

    /**
     * Creates a new instance of this life stage with type name and
     * attribute values given by input String array.
     * 
     * Side effects:
     *  1. Calls createInstance(LifeStageAttributesInterface), with associated effects,
     *  based on creating an attributes instance from the string array.
     * /
     * @param strv - attributes as string array
     * @return - instance of LHS
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @Override
    public ImmatureFemale createInstance(String[] strv) 
                        throws InstantiationException, IllegalAccessException {
        LifeStageAttributesInterface theAtts = LHS_Factory.createAttributes(strv);
        ImmatureFemale lhs = createInstance(theAtts);
        return lhs;
    }

    /**
     * Creates a new instance of this life stage with attributes (including type name) 
     * corresponding to the input attributes instance.
     * 
     * Side effects:
     *  1. If theAtts id attribute is "-1", then a new (unique) id value is created 
     *  for the new LHS instance.
     *  2. If theAtts parentID attribute is "-1", then it is set to the value for id.
     *  3. If theAtts origID attribute is "-1", then it is set to the value for id.
     *  4. initialize() is called to initialize variables and convert position
     *   attributes.
     * /
     * @param theAtts - attributes instance
     * @return - instance of LHS
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @Override
    public ImmatureFemale createInstance(LifeStageAttributesInterface theAtts)
                        throws InstantiationException, IllegalAccessException {
        ImmatureFemale lhs = null;
        if (theAtts instanceof ImmatureCrabAttributes) {
            lhs = new ImmatureFemale(theAtts.getTypeName());
            long newID = lhs.id;//save id of new instance
            lhs.setAttributes(theAtts);
            if (lhs.atts.getID()==-1) {
                //constructing new individual, so reset id values to those of new
                lhs.id = newID;
                lhs.atts.setValue(LifeStageAttributesInterface.PROP_id,newID);
            }
            newID = (Long) lhs.atts.getValue(LifeStageAttributesInterface.PROP_parentID);
            if (newID==-1) {
                lhs.atts.setValue(LifeStageAttributesInterface.PROP_parentID,newID);
            }
            newID = (Long) lhs.atts.getValue(LifeStageAttributesInterface.PROP_origID);
            if (newID==-1) {
                lhs.atts.setValue(LifeStageAttributesInterface.PROP_origID,newID);
            }
        } else {
            //throw error?
        }
        if (lhs!=null) lhs.initialize();//initialize instance variables
        return lhs;
    }

    /**
     *  Provides a copy of the object.  The attributes and parameters
     *  are cloned in the process, so the clone is independent of the
     *  original.
     */
    @Override
    public Object clone() {
        ImmatureFemale clone = (ImmatureFemale) super.clone();
        return clone;        
    }

    @Override
    public String getAttributesClassName() {
        return attributesClass;
    }

    @Override
    public String getParametersClassName() {
        return parametersClass;
    }

    @Override
    public String[] getNextLHSClassNames() {
        return nextLHSClasses;
    }

    @Override
    public String getPointFeatureTypeClassName() {
        return pointFTClass;
    }

    @Override
    public String[] getSpawnedLHSClassNames() {
        return spawnedLHSClasses;
    }

    @Override
    public List<LifeStageInterface> getSpawnedIndividuals() {
        output.clear();
        return output;
    }

}
