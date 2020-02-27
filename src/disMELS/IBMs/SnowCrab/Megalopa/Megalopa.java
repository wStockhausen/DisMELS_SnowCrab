/*
 * Megalopa.java
 */

package disMELS.IBMs.SnowCrab.Megalopa;

import SnowCrabFunctions.AnnualMoltFunction;
import SnowCrabFunctions.FixedDurationFunction;
import SnowCrabFunctions.IntermoltDurationFunction_Belehradek;
import SnowCrabFunctions.IntermoltIntegratorFunction;
import SnowCrabFunctions.MortalityFunction_OuelletAndSteMarie2017;
import com.vividsolutions.jts.geom.Coordinate;
import disMELS.IBMs.SnowCrab.AbstractBenthicStageAttributes;
import disMELS.IBMs.SnowCrab.AbstractPelagicStage;
import disMELS.IBMs.SnowCrab.ImmatureCrab.ImmatureCrab;
import disMELS.IBMs.SnowCrab.ImmatureCrab.ImmatureFemale;
import disMELS.IBMs.SnowCrab.ImmatureCrab.ImmatureMale;
import disMELS.IBMs.SnowCrab.Zooea.Zooea;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import wts.models.DisMELS.IBMFunctions.Movement.DielVerticalMigration_FixedDepthRanges;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedDepthRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedOffBottomAndTempRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedOffBottomRange;
import wts.models.DisMELS.IBMFunctions.SwimmingBehavior.ConstantMovementRateFunction;
import wts.models.DisMELS.framework.*;
import wts.roms.model.LagrangianParticle;

/**
 * This class represents the snow crab megalopal stage.
 */
@ServiceProvider(service=LifeStageInterface.class)
public class Megalopa extends AbstractPelagicStage {
    
        //Static fields    
            //  Static fields new to this class
    /* flag to do debug operations */
    public static boolean debugOps = false;
    /** flag to print debugging info */
    public static boolean debug = false;
    /* Class for attributes */
    public static final String attributesClass = MegalopaAttributes.class.getName();
    /* Class for parameters */
    public static final String parametersClass = MegalopaParameters.class.getName();
    /* Class for feature type for point positions */
    public static final String pointFTClass = wts.models.DisMELS.framework.LHSPointFeatureType.class.getName();
//            wts.models.DisMELS.IBMs.Arrowtooth.EggStage.EggStagePointFT.class.getName();
    /* Classes for next LHS */
    public static final String[] nextLHSClasses = new String[]{Megalopa.class.getName(),
                                                               ImmatureFemale.class.getName(),
                                                               ImmatureMale.class.getName()};
    /* Classes for spawned LHS */
    public static final String[] spawnedLHSClasses = new String[]{};
    
    //Instance fields
            //  Fields hiding ones from superclass
    /* life stage atrbutes object */
    protected MegalopaAttributes atts = null;
    /* life stage parameters object */
    protected MegalopaParameters params = null;
    
    //Fields new to class
        //fields that reflect parameter values
     /** minimum settlement depth */
    protected double minSettlementDepth;
    /** maximum settlement depth */
    protected double maxSettlementDepth;
   
        //fields that reflect (new) attribute values
    //--none
    
        //other fields
    //--none
        
    /** logger for class */
    private static final Logger logger = Logger.getLogger(Megalopa.class.getName());
    
    /**
     * Creates a new instance of Megalopa.  
     *  This constructor should be used ONLY to obtain
     *  the class names of the associated classes.
     * DO NOT DELETE THIS CONSTRUCTOR!!
     */
    public Megalopa() {
        super("");
        super.atts = atts;
        super.params = params;
    }
    
    /**
     * Creates a new instance of Megalopa with the given typeName.
     * A new id number is calculated in the superclass and assigned to
     * the new instance's id, parentID, and origID. 
     * 
     * The attributes are vanilla.  Initial attribute values should be set,
     * then initialize() should be called to initialize all instance variables.
     * DO NOT DELETE THIS CONSTRUCTOR!!
     */
    public Megalopa(String typeName) 
                throws InstantiationException, IllegalAccessException {
        super(typeName);
        atts = new MegalopaAttributes(typeName);
        atts.setValue(LifeStageAttributesInterface.PROP_id,id);
        atts.setValue(LifeStageAttributesInterface.PROP_parentID,id);
        atts.setValue(LifeStageAttributesInterface.PROP_origID,id);
        setAttributesFromSubClass(atts);  //set object in the superclass
        params = (MegalopaParameters) LHS_Factory.createParameters(typeName);
        setParameters(params);//calls setParametersFromSubClass(...)
    }

    /**
     * Creates a new instance of Megalopa with type name and
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
    public Megalopa createInstance(String[] strv) 
                        throws InstantiationException, IllegalAccessException {
        LifeStageAttributesInterface theAtts = LHS_Factory.createAttributes(strv);
        Megalopa lhs = createInstance(theAtts);
        return lhs;
    }

    /**
     * Creates a new instance of this LHS with attributes (including type name) 
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
    public Megalopa createInstance(LifeStageAttributesInterface theAtts)
                        throws InstantiationException, IllegalAccessException {
        Megalopa lhs = null;
        if (theAtts instanceof MegalopaAttributes) {
            lhs = new Megalopa(theAtts.getTypeName());
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
        }
        if (lhs!=null) lhs.initialize();//initialize instance variables
        return lhs;
    }

    /**
     *  Returns the associated attributes.  
     */
    @Override
    public MegalopaAttributes getAttributes() {
        return atts;
    }

    /**
     * Sets the values of the associated attributes object to those in the input
     * String[]. This does NOT change the typeNameof the LHS instance (or the 
     * associated LHSAttributes instance) on which the method is called.
     * Attribute values are set using SimpleBenthicLHSAttributes.setValues(String[]).
     * Side effects:
     *  1. If th new id attribute is not "-1", then its value for id replaces the 
     *      current value for the lhs.
     *  2. If the new parentID attribute is "-1", then it is set to the value for id.
     *  3. If the new origID attribute is "-1", then it is set to the value for id.
     *  4. initialize() is called to initialize variables and convert position
     *   attributes.
     * /
     * @param strv - attribute values as String[]
     */
    @Override
    public void setAttributes(String[] strv) {
        long aid;
        atts.setValues(strv);
        initialN = atts.getValue(MegalopaAttributes.PROP_number, initialN);
        aid = atts.getValue(LifeStageAttributesInterface.PROP_id, id);
        if (aid==-1) {
            //change atts id to lhs id
            atts.setValue(LifeStageAttributesInterface.PROP_id, id);
        } else {
            //change lhs id to atts id
            id = aid;
        }
        aid = atts.getValue(LifeStageAttributesInterface.PROP_parentID, id);
        if (aid==-1) {
            atts.setValue(LifeStageAttributesInterface.PROP_parentID, id);
        }
        aid = atts.getValue(LifeStageAttributesInterface.PROP_origID, id);
        if (aid==-1) {
            atts.setValue(LifeStageAttributesInterface.PROP_origID, id);
        }
        initialize();//initialize instance variables
    }

    /**
     * Sets the attributes for the instance by copying values from the input.
     * 
     * This does NOT change the typeName of the LHS instance (or the associated 
     * LHSAttributes instance) on which the method is called.
     * Note that ALL attributes are copied, so id, parentID, and origID are copied
     * as well. 
     * 
     *  Side effects:
     *      1. Instance field "moltIndicator" is set to 0 if molt occurred (moltIndicator>=maxMoltIndicator)
     *           or transition is from Zooea stage, but is carried over otherwise.
     *      2. Instance field "id" is also updated.
     *      3. updateVariables() is called to update instance variables.
     * 
     * @param newAtts - should be instance of ZooeaAttributes or MegalopaAttributes
     */
    @Override
    public void setAttributes(LifeStageAttributesInterface newAtts) {
        //copy attributes, regardless of life stage associated w/ newAtts
        for (String key: newAtts.getKeys()) atts.setValue(key,newAtts.getValue(key));
        id = atts.getValue(LifeStageAttributesInterface.PROP_id, id);
        updateVariables();
    }
    
    /**
     *  Sets the associated attributes object. Use this after creating an LHS instance
     * as an "output" from another LHS that is functioning as an ordinary individual.
     */
    @Override
    public void setInfoFromIndividual(LifeStageInterface oldLHS){
        /** 
         * Since this is a single individual making a transition, we need to:
         *  1) copy the attributes from the old LHS (id's should remain as for old LHS)
         *  2) set age in stage = 0
         *  3) set active and alive to true
         *  5) copy the Lagrangian Particle from the old LHS
         *  6) start a new track from the current position for the oldLHS
         *  7) update local variables
         */
        LifeStageAttributesInterface oldAtts = oldLHS.getAttributes();            
        setAttributes(oldAtts);
        
        //reset some attributes
        atts.setValue(LifeStageAttributesInterface.PROP_ageInStage, 0.0);//reset age in stage
        atts.setValue(LifeStageAttributesInterface.PROP_active,true);    //set active to true
        atts.setValue(LifeStageAttributesInterface.PROP_alive,true);     //set alive to true
        id = atts.getID(); //reset id for current LHS to one from old LHS

        //check some other attributes
        if (oldLHS instanceof Zooea) {
            //set the initialN based on previous initialN
            initialN = ((Zooea) oldLHS).initialN;
            //set moltIndicator to 0, as molt just occurred
            atts.setValue(MegalopaAttributes.PROP_moltindicator, 0.0);
        } else
        if (oldLHS instanceof Megalopa) {
            //set the initialN based on previous initialN
            initialN = ((Megalopa) oldLHS).initialN;
            //if oldLHS is Megalopa, then moltIndicator >=1.0 
            //indicates the stage is competent to settle once it
            //reaches suitable benthic nursery habitat. 
            //Thus, no need to reset it to 0.
//            String key = MegalopaAttributes.PROP_moltindicator;
//            double mi = ((MegalopaAttributes) oldAtts).getValue(key,1.0);//moltIndicator value from oldLHS
//            if (mi>=1.0) atts.setValue(key, 0.0);//molt occurred, so reset. 
            //otherwise old value copied over, as desired
        }
        
        //copy LagrangianParticle information
        this.setLagrangianParticle(oldLHS.getLagrangianParticle());
        //start track at last position of oldLHS track
        this.startTrack(oldLHS.getLastPosition(COORDINATE_TYPE_PROJECTED),COORDINATE_TYPE_PROJECTED);
        this.startTrack(oldLHS.getLastPosition(COORDINATE_TYPE_GEOGRAPHIC),COORDINATE_TYPE_GEOGRAPHIC);
        //update local variables to capture changes made here
        updateVariables();
    }
    
    /**
     *  Sets the associated attributes object. Use this after creating an LHS instance
     * as an "output" from another LHS that is functioning as a super individual.
     */
    @Override
    public void setInfoFromSuperIndividual(LifeStageInterface oldLHS, double numTrans) {
        /** 
         * Since the old LHS instance is a super individual, only a part 
         * (numTrans) of it transitioned to the current LHS. Thus, we need to:
         *          1) copy most attribute values from old stage
         *          2) make sure id for this LHS is retained, not changed
         *          3) assign old LHS id to this LHS as parentID
         *          4) copy old LHS origID to this LHS origID
         *          5) set number in this LHS to numTrans
         *          6) reset age in stage to 0
         *          7) set active and alive to true
         *          9) copy the Lagrangian Particle from the old LHS
         *         10) start a new track from the current position for the oldLHS
         *         11) update local variables to match attributes
         */
        //copy some variables that should not change
        long idc = id;
        
        //copy the old attribute values
        LifeStageAttributesInterface oldAtts = oldLHS.getAttributes();            
        setAttributes(oldAtts);
        
        //reset some attributes and variables
        id = idc;
        atts.setValue(LifeStageAttributesInterface.PROP_id,idc);//reset id to one for current LHS
        atts.setValue(LifeStageAttributesInterface.PROP_parentID,
                      oldAtts.getValue(LifeStageAttributesInterface.PROP_id));//copy old id to parentID
        atts.setValue(LifeStageAttributesInterface.PROP_number, numTrans);//set number to numTrans
        atts.setValue(LifeStageAttributesInterface.PROP_ageInStage, 0.0); //reset age in stage
        atts.setValue(LifeStageAttributesInterface.PROP_active,true);     //set active to true
        atts.setValue(LifeStageAttributesInterface.PROP_alive,true);      //set alive to true
            
        //check some other attributes
        if (oldLHS instanceof Zooea) {
            //set the initialN based on previous initialN
            initialN = ((Zooea) oldLHS).initialN;
            //set moltIndicator to 0, as molt just occurred
            atts.setValue(MegalopaAttributes.PROP_moltindicator, 0.0);
        } else
        if (oldLHS instanceof Megalopa) {
            //set the initialN based on previous initialN
            initialN = ((Megalopa) oldLHS).initialN;
            //if oldLHS is Megalopa, then moltIndicator >=1.0 
            //indicates the stage is competent to settle once it
            //reaches suitable benthic nursery habitat. 
            //Thus, no need to reset it to 0.
//            String key = MegalopaAttributes.PROP_moltindicator;
//            double mi = ((MegalopaAttributes) oldAtts).getValue(key,1.0);//moltIndicator value from oldLHS
//            if (mi>=1.0) atts.setValue(key, 0.0);//molt occurred, so reset. 
            //otherwise old value copied over, as desired
        }
        
        //copy LagrangianParticle information
        this.setLagrangianParticle(oldLHS.getLagrangianParticle());
        //start track at last position of oldLHS track
        this.startTrack(oldLHS.getLastPosition(COORDINATE_TYPE_PROJECTED),COORDINATE_TYPE_PROJECTED);
        this.startTrack(oldLHS.getLastPosition(COORDINATE_TYPE_GEOGRAPHIC),COORDINATE_TYPE_GEOGRAPHIC);
        //update local variables to capture changes made here
        updateVariables();
    }

    /**
     *  Returns the associated parameters.  
     */
    @Override
    public MegalopaParameters getParameters() {
        return params;
    }

    /**
     * Sets the parameters for the instance to the input.
     * @param newParams - should be instance of EggStageParameters
     */
    @Override
    public void setParameters(LifeStageParametersInterface newParams) {
        if (newParams instanceof MegalopaParameters) {
            params = (MegalopaParameters) newParams;
            setParametersFromSubClass(params);
            setParameterValues();
            setParameterFunctions();
        } else {
            //TODO: throw some error
        }
    }
        
    /**
     * Sets the IBM functions from the parameters object
     */
    private void setParameterFunctions(){
        fcnMoltTiming = params.getSelectedIBMFunctionForCategory(MegalopaParameters.FCAT_IntermoltDuration);
        if (!(fcnMoltTiming instanceof IntermoltIntegratorFunction||
              fcnMoltTiming instanceof IntermoltDurationFunction_Belehradek||
              fcnMoltTiming instanceof AnnualMoltFunction||
              fcnMoltTiming instanceof FixedDurationFunction))
            throw new java.lang.UnsupportedOperationException("Intermolt duration function "+fcnMoltTiming.getFunctionName()+" is not supported for Megalopa.");
        
        fcnMort     = params.getSelectedIBMFunctionForCategory(MegalopaParameters.FCAT_Mortality);
        if (!(fcnMort instanceof MortalityFunction_OuelletAndSteMarie2017||
              fcnMort instanceof ConstantMortalityRate||
              fcnMort instanceof TemperatureDependentMortalityRate_Houde1989))
            throw new java.lang.UnsupportedOperationException("Mortality function "+fcnMort.getFunctionName()+" is not supported for Megalopa.");
        
        fcnVM       = params.getSelectedIBMFunctionForCategory(MegalopaParameters.FCAT_VerticalMovement);
        if (!(fcnVM instanceof VerticalMovement_FixedOffBottomRange||
              fcnVM instanceof VerticalMovement_FixedOffBottomAndTempRange||
              fcnVM instanceof VerticalMovement_FixedDepthRange||
              fcnVM instanceof DielVerticalMigration_FixedDepthRanges))
            throw new java.lang.UnsupportedOperationException("Vertical movement function "+fcnVM.getFunctionName()+" is not supported for Megalopa.");
        
        fcnVV       = params.getSelectedIBMFunctionForCategory(MegalopaParameters.FCAT_VerticalVelocity);
        if (!(fcnVV instanceof ConstantMovementRateFunction))
            throw new java.lang.UnsupportedOperationException("Vertical velocity function "+fcnVV.getFunctionName()+" is not supported for Megalopa.");
    }
    
    /*
     * Copy the values from the params map to the param variables.
     */
    private void setParameterValues() {
        isSuperIndividual = 
                params.getValue(MegalopaParameters.PARAM_isSuperIndividual,isSuperIndividual);
        maxDecrease = 
                params.getValue(MegalopaParameters.PARAM_maxDecrease,maxDecrease);
        horizRWP = 
                params.getValue(MegalopaParameters.PARAM_horizRWP,horizRWP);
        maxMoltIndicator = 
                params.getValue(MegalopaParameters.PARAM_maxMoltIndicator,maxMoltIndicator);
        maxStageDuration = 
                params.getValue(MegalopaParameters.PARAM_maxStageDuration,maxStageDuration);
        stageTransRate = 
                params.getValue(MegalopaParameters.PARAM_stageTransRate,stageTransRate);
        randomizeTransitions = 
                params.getValue(MegalopaParameters.PARAM_randomizeTransitions,true);
        minSettlementDepth = 
                params.getValue(MegalopaParameters.PARAM_minSettlementDepth,minSettlementDepth);
        maxSettlementDepth = 
                params.getValue(MegalopaParameters.PARAM_maxSettlementDepth,maxSettlementDepth);
    }
    
    /**
     *  Provides a copy of the object.  The attributes and parameters
     *  are cloned in the process, so the clone is independent of the
     *  original.
     */
    @Override
    public Object clone() {
        Megalopa clone = null;
        try {
            clone = (Megalopa) super.clone();
            clone.setAttributes(atts);//this clones atts
            clone.setParameters(params);//this clones params
            clone.lp      = (LagrangianParticle) lp.clone();
            clone.track   = (ArrayList<Coordinate>) track.clone();
            clone.trackLL = (ArrayList<Coordinate>) trackLL.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return clone;
        
    }

    /**
     * 
     * @param dt - time step in seconds
     * @return
     */
    @Override
    public List<LifeStageInterface> getMetamorphosedIndividuals(double dt) {
        double dtp = 0.25*(dt/DAY_SECS);//use 1/4 timestep (converted from sec to d)
        output.clear();
        List<LifeStageInterface> nLHSs=null;
        if (moltIndicator>=maxMoltIndicator) {
            //transition to next stage
            //--if maxSettlementDepth<0, then next stage should be Megalopa also and settlement does not occur
            //--if minSettlementDepth<=bathym<=maxSettlementDepth, settlement occurs and next stage should be ImmatureCrab
            if ((maxSettlementDepth<0)||
                    (minSettlementDepth<=bathym)&&(bathym<=maxSettlementDepth)) {
                nLHSs = createMetamorphosedIndividuals();
                if (nLHSs!=null) output.addAll(nLHSs);
            }
        }
        return output;
    }

    private List<LifeStageInterface> createMetamorphosedIndividuals() {
        List<LifeStageInterface> nLHSs = null;
        try {
            //create LHS with "next" stage
            if (isSuperIndividual) {
                /** 
                 * Since this is LHS instance is a super individual, only a part 
                 * (numTrans) of it transitions to the next LHS. Thus, we need to:
                 *          1) create new LHS instance
                 *          2. assign new id to new instance
                 *          3) assign current LHS id to new LHS as parentID
                 *          4) copy current LHS origID to new LHS origID
                 *          5) set number in new LHS to numTrans for current LHS
                 *          6) reset numTrans in current LHS
                 */
                nLHSs = LHS_Factory.createNextLHSsFromSuperIndividual(typeName,this,numTrans);
                numTrans = 0.0;//reset numTrans to zero
            } else {
                /**  
                 * Since this is a single individual making a transition, we should
                 * "kill" the current LHS.  Also, the various IDs should remain
                 * the same in the new LHS since it's the same individual. Thus, 
                 * we need to:
                 *          1) create new LHS instance
                 *          2. assign current LHS id to new LHS id
                 *          3) assign current LHS parentID to new LHS parentID
                 *          4) copy current LHS origID to new LHS origID
                 *          5) kill current LHS
                 */
                nLHSs = LHS_Factory.createNextLHSsFromIndividual(typeName,this);
                alive  = false; //allow only 1 transition, so kill this stage
                active = false; //set stage inactive, also
            }
        } catch (IllegalAccessException | InstantiationException ex) {
            ex.printStackTrace();
        }
        if (nLHSs!=null){
            for (LifeStageInterface nLHS : nLHSs){
//                if (nLHS instanceof Megalopa){
//                    logger.info("createMetamorphosedIndividuals: transition to next Megalopa stage.");
//                    MegalopaAttributes atts = (MegalopaAttributes) nLHS.getAttributes();
//                    atts.setValue(LifeStageAttributesInterface.PROP_number,number);
//                    atts.setValue(AbstractPelagicStageAttributes.PROP_moltindicator, moltIndicator);//moltIndicator is continuous
//                    //update attributes on nLHS (updates id, parentID as well as other attributes)
//                    nLHS.setAttributes(atts);//instar incremented in setAttributes(atts)
//                } else
                if (nLHS instanceof ImmatureCrab){
                    //apply sex ratio 
                    logger.info("createMetamorphosedIndividuals: transition to immature crab stage.");
                    double xr = (Double)params.getValue(MegalopaParameters.PARAM_sexRatio);
                    //apply sex ratio
                    double n = (Double) nLHS.getAttributes().getValue(LifeStageAttributesInterface.PROP_number);
                    double nr = n;
                    if (nLHS instanceof ImmatureFemale){
                        nr = (1.0-xr)*n;
                        logger.info("createMetamorphosedIndividuals: immature female n: "+nr+". orig = "+n);
                    } else if (nLHS instanceof ImmatureMale){
                        nr = xr*n;
                        logger.info("createMetamorphosedIndividuals: immature male n:   "+nr+". orig = "+n);
                    }
                    LifeStageAttributesInterface atts = nLHS.getAttributes();
                    atts.setValue(LifeStageAttributesInterface.PROP_number,nr);
                    atts.setValue(AbstractBenthicStageAttributes.PROP_instar, 0);//reset to 0, will be incremented
                    if (!isSuperIndividual){
                        //generate new id and copy old id to parentID (already done if isSuperIndividual is true)
                        long pID = atts.getID();
                        atts.setValue(LifeStageAttributesInterface.PROP_id,LHS_Factory.getNewID());
                        atts.setValue(LifeStageAttributesInterface.PROP_parentID, pID);
                    }
                    //update attributes on nLHS (updates id, parentID as well as other attributes)
                    nLHS.setAttributes(atts);//instar incremented in setAttributes(atts)
                }
            }
        }
        return nLHSs;
    }

    /**
     * Initializes instance variables to attribute values (via updateVariables()), 
     * then determines initial position for the lagrangian particle tracker
     * and resets the track,
     * sets horizType and vertType attributes to HORIZ_LL, VERT_H,
     * and finally calls updatePosition(), updateEnvVars(), and updateAttributes().
     */
    public void initialize() {
//        atts.setValue(SimplePelagicLHSAttributes.PARAM_id,id);//TODO: should do this beforehand!!
        updateVariables();//set instance variables to attribute values
        int hType,vType;
        hType=vType=-1;
        double xPos, yPos, zPos;
        xPos=yPos=zPos=0;
        hType      = atts.getValue(LifeStageAttributesInterface.PROP_horizType,hType);
        vType      = atts.getValue(LifeStageAttributesInterface.PROP_vertType,vType);
        xPos       = atts.getValue(LifeStageAttributesInterface.PROP_horizPos1,xPos);
        yPos       = atts.getValue(LifeStageAttributesInterface.PROP_horizPos2,yPos);
        zPos       = atts.getValue(LifeStageAttributesInterface.PROP_vertPos,zPos);
        time       = startTime;
        numTrans   = 0.0; //set numTrans to zero
        if (debug) {
            logger.info("\n---------------Setting initial position------------");
            logger.info(hType+cc+vType+cc+startTime+cc+xPos+cc+yPos+cc+zPos);
        }
        if (i3d!=null) {
            double[] IJ = new double[] {xPos,yPos};
            if (debug) wts.roms.model.Grid2DUtilities.debug = true;
            if (hType==Types.HORIZ_XY) {
                IJ = i3d.getGrid().computeIJfromXY(xPos,yPos);
            } else if (hType==Types.HORIZ_LL) {
//                if (xPos<0) xPos=xPos+360;
                IJ = i3d.getGrid().computeIJfromLL(yPos,xPos);
            }
            if (debug) wts.roms.model.Grid2DUtilities.debug = false;
            double z = i3d.interpolateBathymetricDepth(IJ);
            if (debug) logger.info("Bathymetric depth = "+z);
            double ssh = i3d.interpolateSSH(IJ);

            double K = 0;  //set K = 0 (at bottom) as default
            if (vType==Types.VERT_K) {
                if (zPos<0) {K = 0;} else
                if (zPos>i3d.getGrid().getN()) {K = i3d.getGrid().getN();} else
                K = zPos;
            } else if (vType==Types.VERT_Z) {//depths negative
                if (zPos<-z) {K = 0;} else                     //at bottom
                if (zPos>ssh) {K = i3d.getGrid().getN();} else //at surface
                K = i3d.calcKfromZ(IJ[0],IJ[1],zPos);          //at requested depth
            } else if (vType==Types.VERT_H) {//depths positive
                if (zPos>z) {K = 0;} else                       //at bottom
                if (zPos<-ssh) {K = i3d.getGrid().getN();} else //at surface
                K = i3d.calcKfromZ(IJ[0],IJ[1],-zPos);          //at requested depth
            } else if (vType==Types.VERT_DH) {//distance off bottom
                if (zPos<0) {K = 0;} else                        //at bottom
                if (zPos>z+ssh) {K = i3d.getGrid().getN();} else //at surface
                K = i3d.calcKfromZ(IJ[0],IJ[1],-(z-zPos));       //at requested distance off bottom
            }
            lp.setIJK(IJ[0],IJ[1],K);
            //reset track array
            track.clear();
            trackLL.clear();
            //set horizType to lat/lon and vertType to depth
            atts.setValue(LifeStageAttributesInterface.PROP_horizType,Types.HORIZ_LL);
            atts.setValue(LifeStageAttributesInterface.PROP_vertType,Types.VERT_H);
            //interpolate initial position and environmental variables
            double[] pos = lp.getIJK();
            updatePosition(pos);
            if (debug) {
                logger.info("pos = ["+lon+", "+lat+"]");
                logger.info("bathymetric depth = "+bathym);
                logger.info("depth = "+depth);
                logger.info("-------Finished setting initial position------------");
            }
            updateEnvVars(pos);
            updateAttributes(); 
        }
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

}
