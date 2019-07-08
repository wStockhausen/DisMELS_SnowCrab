/*
 * Zooea.java
 */

package disMELS.IBMs.SnowCrab.Zooea;

import SnowCrabFunctions.AnnualMoltFunction;
import SnowCrabFunctions.FixedDurationFunction;
import SnowCrabFunctions.IntermoltIntegratorFunction;
import SnowCrabFunctions.MortalityFunction_OuelletAndSteMarie2017;
import com.vividsolutions.jts.geom.Coordinate;
import disMELS.IBMs.SnowCrab.AbstractPelagicStage;
import disMELS.IBMs.SnowCrab.EggMassExtruded.ExtrudedEggMassAttributes;
import disMELS.IBMs.SnowCrab.Megalopa.Megalopa;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import wts.models.DisMELS.IBMFunctions.Movement.DielVerticalMigration_FixedDepthRanges;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedDepthAndTempRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedDepthRange;
import wts.models.DisMELS.IBMFunctions.SwimmingBehavior.ConstantMovementRateFunction;
import wts.models.DisMELS.framework.*;
import wts.models.utilities.DateTimeFunctions;
import wts.roms.model.LagrangianParticle;

/**
 * This class represents the snow crab zooea stages.
 */
@ServiceProvider(service=LifeStageInterface.class)
public class Zooea extends AbstractPelagicStage {
    
        //Static fields    
            //  Static fields new to this class
    /* flag to do debug operations */
    public static boolean debugOps = false;
    /* Class for attributes */
    public static final String attributesClass = ZooeaAttributes.class.getName();
    /* Class for parameters */
    public static final String parametersClass = ZooeaParameters.class.getName();
    /* Class for feature type for point positions */
    public static final String pointFTClass = wts.models.DisMELS.framework.LHSPointFeatureType.class.getName();
//            wts.models.DisMELS.IBMs.Arrowtooth.EggStage.EggStagePointFT.class.getName();
    /* Classes for next LHS */
    public static final String[] nextLHSClasses = new String[]{Zooea.class.getName(),
                                                               Megalopa.class.getName()};
    /* Classes for spawned LHS */
    public static final String[] spawnedLHSClasses = new String[]{};
    
    //Instance fields
            //  Fields hiding ones from superclass
    /* life stage attributes object */
    protected ZooeaAttributes atts = null;
    /* life stage parameters object */
    protected ZooeaParameters params = null;
    
    //  Fields new to class
        //fields that reflect parameter values
    //--none
    
        //fields that reflect (new) attribute values
    //--none
    
    
    /** flag to print debugging info */
    public static boolean debug = false;
    /** logger for class */
    private static final Logger logger = Logger.getLogger(Zooea.class.getName());
    
    /**
     * Creates a new instance of Zooea.  
     *  This constructor should be used ONLY to obtain
     *  the class names of the associated classes.
     * DO NOT DELETE THIS CONSTRUCTOR!!
     */
    public Zooea() {
        super("");
        super.atts = atts;
        super.params = params;
    }
    
    /**
     * Creates a new instance of Zooea with the given typeName.
     * A new id number is calculated in the superclass and assigned to
     * the new instance's id, parentID, and origID. 
     * 
     * The attributes are vanilla.  Initial attribute values should be set,
     * then initialize() should be called to initialize all instance variables.
     * DO NOT DELETE THIS CONSTRUCTOR!!
     */
    public Zooea(String typeName) 
                throws InstantiationException, IllegalAccessException {
        super(typeName);
        atts = new ZooeaAttributes(typeName);
        atts.setValue(LifeStageAttributesInterface.PROP_id,id);
        atts.setValue(LifeStageAttributesInterface.PROP_parentID,id);
        atts.setValue(LifeStageAttributesInterface.PROP_origID,id);
        setAttributesFromSubClass(atts);  //set object in the superclass
        params = (ZooeaParameters) LHS_Factory.createParameters(typeName);
        setParameters(params);
    }

    /**
     * Creates a new instance of Zooea with type name and
 attribute values given by input String array.
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
    public Zooea createInstance(String[] strv) 
                        throws InstantiationException, IllegalAccessException {
        LifeStageAttributesInterface theAtts = LHS_Factory.createAttributes(strv);
        Zooea lhs = createInstance(theAtts);
        return lhs;
    }

    /**
     * Creates a new instance of Zooea with attributes (including type name) 
 corresponding to the input attributes instance.
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
    public Zooea createInstance(LifeStageAttributesInterface theAtts)
                        throws InstantiationException, IllegalAccessException {
        Zooea lhs = null;
        if (theAtts instanceof ZooeaAttributes) {
            lhs = new Zooea(theAtts.getTypeName());
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
    public ZooeaAttributes getAttributes() {
        return atts;
    }

    /**
     * Sets the values of the associated attributes object to those in the input
     * String[]. 
     * 
     * This does NOT change the typeNameof the LHS instance (or the 
 associated LHSAttributes instance) on which the method is called.
 Attribute values are set using ZooeaAttributes.setValues(String[]).
 
 Side effects:
  1. If th new id attribute is not "-1", then its value for id replaces the 
      current value for the lhs.
  2. If the new parentID attribute is "-1", then it is set to the value for id.
  3. If the new origID attribute is "-1", then it is set to the value for id.
  4. initialize() is called to initialize variables and convert position attributes.
     * 
     * @param strv - attribute values as String[]
     */
    @Override
    public void setAttributes(String[] strv) {
        long aid;
        atts.setValues(strv);
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
     *      1. updateVariables() is called to update instance variables.
     *      2. Instance field "id" is also updated.
     *      3. Instance field "moltindicator" is set to 0 if molt occurred
     * 
     * @param newAtts - should be instance of ExtrudedEggMassAttributes or ZooeaAttributes
     */
    @Override
    public void setAttributes(LifeStageAttributesInterface newAtts) {
        //copy attributes, regardless of life stage associated w/ newAtts
        for (String key: newAtts.getKeys()) atts.setValue(key,newAtts.getValue(key));
        //fill in missing attributes depending on class of newAtts
        if (newAtts instanceof ExtrudedEggMassAttributes) {
            //set moltIndicator to 0
            atts.setValue(ZooeaAttributes.PROP_moltindicator, 0.0);
        } else
        if (newAtts instanceof ZooeaAttributes) {
            //set moltIndicator to 0 ifmolt occurred 
            String key = ZooeaAttributes.PROP_moltindicator;
            double mi = ((ZooeaAttributes) newAtts).getValue(key,1.0);
            if (mi>=1.0) atts.setValue(key, 0.0);
        }
        id = atts.getValue(LifeStageAttributesInterface.PROP_id, id);
        updateVariables();
    }
    
    /**
     *  Sets the associated attributes object. 
     * 
     * Use this after creating an LHS instance as an "output" from 
     * another LHS that is functioning as an ordinary individual.
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

        //copy LagrangianParticle information
        this.setLagrangianParticle(oldLHS.getLagrangianParticle());
        //start track at last position of oldLHS track
        this.startTrack(oldLHS.getLastPosition(COORDINATE_TYPE_PROJECTED),COORDINATE_TYPE_PROJECTED);
        this.startTrack(oldLHS.getLastPosition(COORDINATE_TYPE_GEOGRAPHIC),COORDINATE_TYPE_GEOGRAPHIC);
        //update local variables to capture changes made here
        updateVariables();
    }
    
    /**
     *  Sets the associated attributes object. 
     * 
     * Use this after creating an LHS instance as an "output" from another LHS 
     * that is functioning as a super individual.
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
    public ZooeaParameters getParameters() {
        return params;
    }

    /**
     * Sets the parameters for the instance to the input.
     * @param newParams - should be instance of EggStageParameters
     */
    @Override
    public void setParameters(LifeStageParametersInterface newParams) {
        if (newParams instanceof ZooeaParameters) {
            params = (ZooeaParameters) newParams;
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
        fcnMoltTiming = params.getSelectedIBMFunctionForCategory(ZooeaParameters.FCAT_IntermoltDuration);
        if (!(fcnMoltTiming instanceof IntermoltIntegratorFunction||
               fcnMoltTiming instanceof AnnualMoltFunction||
               fcnMoltTiming instanceof FixedDurationFunction))
            throw new java.lang.UnsupportedOperationException("Intermolt duration function "+fcnMoltTiming.getFunctionName()+" is not supported for Megalopa.");
        
        fcnMort     = params.getSelectedIBMFunctionForCategory(ZooeaParameters.FCAT_Mortality);
        if (!(fcnMort instanceof MortalityFunction_OuelletAndSteMarie2017||
              fcnMort instanceof ConstantMortalityRate||
              fcnMort instanceof TemperatureDependentMortalityRate_Houde1989))
            throw new java.lang.UnsupportedOperationException("Mortality function "+fcnMort.getFunctionName()+" is not supported for Zooea1.");
        
        fcnVM       = params.getSelectedIBMFunctionForCategory(ZooeaParameters.FCAT_VerticalMovement);
        if (!(fcnVM instanceof VerticalMovement_FixedDepthRange||
              fcnVM instanceof VerticalMovement_FixedDepthAndTempRange||
              fcnVM instanceof DielVerticalMigration_FixedDepthRanges))
            throw new java.lang.UnsupportedOperationException("Vertical movement function "+fcnVM.getFunctionName()+" is not supported for Zooea1.");
        
        fcnVV       = params.getSelectedIBMFunctionForCategory(ZooeaParameters.FCAT_VerticalVelocity);
        if (!(fcnVV instanceof ConstantMovementRateFunction))
            throw new java.lang.UnsupportedOperationException("Vertical velocity function "+fcnVV.getFunctionName()+" is not supported for Zooea1.");
    }
    
    /*
     * Copy the values from the params map to the param variables.
     */
    private void setParameterValues() {
        isSuperIndividual = 
                params.getValue(ZooeaParameters.PARAM_isSuperIndividual,isSuperIndividual);
        horizRWP = 
                params.getValue(ZooeaParameters.PARAM_horizRWP,horizRWP);
        maxStageDuration = 
                params.getValue(ZooeaParameters.PARAM_maxStageDuration,maxStageDuration);
        randomizeTransitions = 
                params.getValue(ZooeaParameters.PARAM_randomizeTransitions,true);
    }
    
    /**
     *  Provides a copy of the object.  The attributes and parameters
     *  are cloned in the process, so the clone is independent of the
     *  original.
     */
    @Override
    public Object clone() {
        Zooea clone = null;
        try {
            clone = (Zooea) super.clone();
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
        List<LifeStageInterface> nLHSs;
        if (moltIndicator>=1.0) {
            if ((numTrans>0)||!isSuperIndividual){
                nLHSs = getMetamorphosedIndividuals();
                if (nLHSs!=null) output.addAll(nLHSs);
            }
        }
        return output;
    }

    /**
     * Creates a list of objects representing individuals in the succeeding life stage.
     * 
     * @return 
     */
    private List<LifeStageInterface> getMetamorphosedIndividuals() {
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
        return nLHSs;
    }

    /**
     * Initializes instance variables to attribute values (via updateVariables()), 
     * then determines initial position for the lagrangian particle tracker
     * and resets the track, sets horizType and vertType attributes to HORIZ_LL, VERT_H,
     * and finally calls updatePosition(), updateEnvVars(), and updateAttributes().
     */
    public void initialize() {
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
    public void step(double dt) throws ArrayIndexOutOfBoundsException {
        double[] pos = lp.getIJK();
        double[] uvw = calcUVW(pos,dt);//this also sets "attached" and may change pos[2] to 0
        //do lagrangian particle tracking
        lp.setU(uvw[0],lp.getN());
        lp.setV(uvw[1],lp.getN());
        lp.setW(uvw[2],lp.getN());
        //now do predictor step
        lp.doPredictorStep();
        //assume same daytime status, but recalc depth and revise W 
        pos = lp.getPredictedIJK();
        depth = -i3d.calcZfromK(pos[0],pos[1],pos[2]);
        if (debugOps) logger.info("Depth after predictor step = "+depth);
        //w = calcW(dt,lp.getNP1())+r; //set swimming rate for predicted position
        lp.setU(uvw[0],lp.getNP1());
        lp.setV(uvw[1],lp.getNP1());
        lp.setW(uvw[2],lp.getNP1());
        //now do corrector step
        lp.doCorrectorStep();
        pos = lp.getIJK();
        if (debugOps) logger.info("Depth after corrector step = "+(-i3d.calcZfromK(pos[0],pos[1],pos[2])));
        time = time+dt;
        
        updateAge(dt);
        updateMoltIndicator(dt);
        updateNum(dt);
        updatePosition(pos);
    
        updateEnvVars(pos);
        //check for exiting grid
        if (i3d.isAtGridEdge(pos,tolGridEdge)){
            alive=false;
            active=false;
            gridCellID=i3d.getGridCellID(pos, tolGridEdge);
            logger.info("Indiv "+id+" exited grid at ["+pos[0]+","+pos[1]+"]: "+gridCellID);
        }
        updateAttributes(); //update the attributes object w/ nmodified values
    }
    
    /**
     * Function to calculate vertical and horizontal movement rates.
     * 
     * @param dt time step (s) 
     * @return double[] with elements u, v, w
     */
    public double[] calcUVW(double[] pos, double dt) {
        //compute vertical velocity
        double w = 0;
        if (fcnVM instanceof VerticalMovement_FixedDepthRange) {    
            w = (Double) fcnVM.calculate(new double[]{dt,depth,bathym});
        } else     
        if (fcnVM instanceof VerticalMovement_FixedDepthAndTempRange) {    
            w = (Double) fcnVM.calculate(new double[]{dt,depth,bathym,temperature});
        } else     
        if (fcnVM instanceof wts.models.DisMELS.IBMFunctions.Movement.DielVerticalMigration_FixedDepthRanges) {
            if (fcnVV instanceof wts.models.DisMELS.IBMFunctions.SwimmingBehavior.ConstantMovementRateFunction) {
                /**
                * @param vars - double[]{dt}.
                * @return     - movement rate as a Double 
                */
                w = (Double) fcnVV.calculate(new double[]{dt});
            }
            /**
            * Compute time of local sunrise, sunset and solar noon (in minutes, UTC) 
            * for given lon, lat, and time (in Julian day-of-year).
            *@param lon : longitude of position (deg Greenwich, prime meridian)
            *@param lat : latitude of position (deg)
            *@param time : day-of-year (1-366, fractional part indicates time-of-day)
            *@return double[5] = [0] time of sunrise (min UTC from midnight)
            *                    [1] time of sunset (min UTC from midnight)
            *                    [2] time of solarnoon (min UTC from midnight)
            *                    [3] solar declination angle (deg)
            *                    [4] solar zenith angle (deg)
            * If sunrise/sunset=NaN then its either 24-hr day or night 
            * (if lat*declination>0, it's summer in the hemisphere, hence daytime). 
            * Alternatively, if the solar zenith angle > 90.833 deg, then it is night.
            */
            double[] ss = DateTimeFunctions.computeSunriseSunset(lon,lat,globalInfo.getCalendar().getYearDay());
            /**
            * @param vars - the inputs variables as a double[] array with elements
            *                  dt          - [0] - integration time step
            *                  depth       - [1] - current depth of individual
            *                  total depth - [2] - total depth at location
            *                  w           - [3] - active vertical swimming speed outside preferred depth range
            *                  lightLevel  - [4] - value >= 0 indicates daytime, otherwise night 
            * @return     - double[] with elements
            *              w        - individual active vertical movement velocity
            *              attached - flag indicating whether individual is attached to bottom(< 0) or not (>0)
            */
            double td = i3d.interpolateBathymetricDepth(lp.getIJK());
            double[] res = (double[]) fcnVM.calculate(new double[]{dt,depth,td,w,90.833-ss[4]});
            w = res[0];
        }
        
        //calculate horizontal movement
        double[] uv = {0.0,0.0};
        if ((horizRWP>0)&&(Math.abs(dt)>0)) {
            double r = Math.sqrt(horizRWP/Math.abs(dt));
            uv[0] += r*rng.computeNormalVariate(); //stochastic swimming rate
            uv[1] += r*rng.computeNormalVariate(); //stochastic swimming rate
            if (debugOps) logger.info("id: "+id+"; r, uv: "+r+", {"+uv[0]+", "+uv[1]+"}\n");
        }
        
        //return the result
        return new double[]{Math.signum(dt)*uv[0],Math.signum(dt)*uv[1],Math.signum(dt)*w};
    }

    /**
     * Update the number of individuals associated with this object.
     * 
     * @param dt - time step in seconds
     */
    private void updateNum(double dt) {
        double mortalityRate = 0.0D;//in unis of [days]^-1
        if (fcnMort instanceof MortalityFunction_OuelletAndSteMarie2017){
            /**
             * Calculate the instantaneous mortality rate.
             * 
             * @param o double[] with elements <pre>
             *      T   - temperature
             *      mnD - mean stage duration at T</pre>
             * 
             * @return double - instantaneous mortality rate (no survival if &lt 0).
             */
            mortalityRate = (Double)fcnMort.calculate(new double[]{temperature,meanStageDuration});
        } else 
        if (fcnMort instanceof ConstantMortalityRate){
            /**
             * @param vars - null
             * @return     - Double - the corresponding mortality rate (per day) 
             */
            mortalityRate = (Double)fcnMort.calculate(null);
        } else 
        if (fcnMort instanceof TemperatureDependentMortalityRate_Houde1989){
            /**
             * @param vars - Double - temperature (deg C)
             * @return     - Double - the corresponding mortality rate (per day) 
             */
            mortalityRate = (Double)fcnMort.calculate(temperature);//using temperature as covariate for mortality
        }
        if (mortalityRate<0){
            //no survival
            active=false;alive=false;number=0.0;
        } else {
            double totRate = mortalityRate;
            if (moltIndicator>=1.0) {
                if((numTrans<number)&&(numTrans>0.0)){
                double transRate = numTrans/number;
                double instTransRate = -Math.log(1-transRate);
                totRate += instTransRate;
                //apply mortality rate to previous number transitioning and
                //add in new transitioners
                numTrans = numTrans*Math.exp(-dt*mortalityRate/DAY_SECS)+
                        (instTransRate/totRate)*number*(1-Math.exp(-dt*totRate/DAY_SECS));
                } else if(numTrans==number){
                    number = number - numTrans;//TODO: = 0, right?? why the logic?
                }
            }
            number = number*Math.exp(-dt*totRate/DAY_SECS);
            if(number<0.01){ //TODO: replace this with parameter
                active=false;alive=false;number=number+numTrans;//TODO: ??
            }
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

    @Override
    public String getReport() {
        updateAttributes();//make sure attributes are up to date
        atts.setValue(LifeStageAttributesInterface.PROP_track, getTrackAsString(COORDINATE_TYPE_GEOGRAPHIC));//
        return atts.getCSV();
    }

    @Override
    public String getReportHeader() {
        return atts.getCSVHeaderShortNames();
    }
    
    /**
     * Updates attribute values defined for this class. 
     */
    @Override
    protected void updateAttributes() {
        //update superclass attributes
        super.updateAttributes();
        //update attributes new to class
        //--NONE!
    }

    /**
     * Updates local variables from the attributes.  
     */
    @Override
    protected void updateVariables() {
        //update superclass variables
        super.updateVariables();
        //update new variables
        //--NONE!
    }
}
