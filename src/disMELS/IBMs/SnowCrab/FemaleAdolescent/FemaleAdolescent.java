/*
 * FemaleAdolescent.java
 */

package disMELS.IBMs.SnowCrab.FemaleAdolescent;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import disMELS.IBMs.SnowCrab.AbstractBenthicStage;
import disMELS.IBMs.SnowCrab.FemaleImmature.FemaleImmatureAttributes;
import disMELS.IBMs.SnowCrab.FemaleAdult.FemaleAdult;
import wts.models.DisMELS.framework.*;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import static wts.models.DisMELS.framework.LifeStageInterface.DAY_SECS;
import wts.roms.model.LagrangianParticle;


/**
 * DisMELS class representing attributes for adolescent female snow crab. 
 * Adolescent females are capable of developing an internal egg "clutch" and, upon mating, 
 * terminally molt to the primiparous female life stage--at which point they
 * extrude the egg mass onto their abdomen, where it develops over the next year 
 * prior to hatching.
 */
@ServiceProvider(service=LifeStageInterface.class)
public class FemaleAdolescent extends AbstractBenthicStage {
    
        //Static fields    
            //  Static fields new to this class
    /* flag to do debug operations */
    public static boolean debugOps = false;
    /* Class for attributes */
    public static final String attributesClass = FemaleAdolescentAttributes.class.getName();
    /* Class for parameters */
    public static final String parametersClass = FemaleAdolescentParameters.class.getName();
    /* Class for feature type for point positions */
    public static final String pointFTClass = wts.models.DisMELS.framework.LHSPointFeatureType.class.getName();
//            "wts.models.DisMELS.LHS.BenthicAdult.AdultStagePointFT";
    /* Classes for next LHS */
    public static final String[] nextLHSClasses = new String[]{FemaleAdult.class.getName()};
    /* Classes for spawned LHS */
    public static final String[] spawnedLHSClasses = new String[]{};
    
        //Instance fields
            //  Fields hiding ones from superclass
    /** life stage attributes object */
    protected FemaleAdolescentAttributes atts = null;
    /** life stage parameters object */
    protected FemaleAdolescentParameters params = null;
            //  Fields new to class
            //fields that reflect parameter values
    /** flag indicating instance is a super-individual */
    protected boolean isSuperIndividual;
    /** horizontal random walk parameter */
    protected double horizRWP;
    /** minimum stage duration before metamorphosis to next stage */
    protected double minStageDuration;
    /** maximum stage duration (followed by death) */
    protected double maxStageDuration;
    /** minimum size (cm) before metamorphosis to next stage can occur */
    protected double minSizeAtTrans;
    /** mean delay before metamorphosis to next stage occurs (d) */
    protected double meanStageTransDelay;
    /** flag to use stochastic transitions */
    protected boolean randomizeTransitions;

        //fields that reflect (new) attribute values
    //none
    
            //other fields
    /** number of individuals transitioning to next stage */
    private double numTrans;  
     /** day of year */
    private double dayOfYear;
    private double starvationMort;
    private double exTot;
    private boolean molted;
   
    /** IBM function selected for mortality */
    private IBMFunctionInterface fcnMort = null; 
    private IBMFunctionInterface fcnGrowth = null;
    private IBMFunctionInterface fcnExCost = null;
    private IBMFunctionInterface fcnMoltTime = null;
    private IBMFunctionInterface fcnMolt = null;
    private IBMFunctionInterface fcnMaturity = null;
    
    /** flag to print debugging info */
    public static boolean debug = false;
    /** logger for class */
    private static final Logger logger = Logger.getLogger(FemaleAdolescent.class.getName());
    
    /**
     * This constructor is provided only to facilitate the ServiceProvider functionality.
     * DO NOT USE IT!!
     */
    public FemaleAdolescent() {
        super("");
        super.atts = atts;
        super.params = params;
    }
    
    /**
     * Creates a new life stage instance with parameters based on the typeName and
     * "default" attributes.
     */
    public FemaleAdolescent(String typeName) 
                throws InstantiationException, IllegalAccessException {
        super(typeName);
        atts   = new FemaleAdolescentAttributes(typeName);
        atts.setValue(LifeStageAttributesInterface.PROP_id,id);
        atts.setValue(LifeStageAttributesInterface.PROP_parentID,id);
        atts.setValue(LifeStageAttributesInterface.PROP_origID,id);
        setAttributesFromSubClass(atts);  //set object in the superclass
        params = (FemaleAdolescentParameters) LHS_Factory.createParameters(typeName);
        setParameters(params);
    }

    /**
     * Creates a new instance of the life stage with type name and
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
    public FemaleAdolescent createInstance(String[] strv) 
                        throws InstantiationException, IllegalAccessException {
        LifeStageAttributesInterface theAtts = LHS_Factory.createAttributes(strv);
        FemaleAdolescent lhs = createInstance(theAtts);
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
    public FemaleAdolescent createInstance(LifeStageAttributesInterface theAtts)
                        throws InstantiationException, IllegalAccessException {
        FemaleAdolescent lhs = null;
        if (theAtts instanceof FemaleAdolescentAttributes) {
            lhs = new FemaleAdolescent(theAtts.getTypeName());
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
        lhs.initialize();//initialize instance variables
        return lhs;
    }

    /**
     *  Returns the associated attributes.  
     */
    @Override
    public FemaleAdolescentAttributes getAttributes() {
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
     * This does NOT change the typeNameof the LHS instance (or the associated 
     * LHSAttributes instance) on which the method is called.
     * Note that ALL attributes are copied, so id, parentID, and origID are copied
     * as well. 
     *  As a side effect, updateVariables() is called to update instance variables.
     *  LHS instance variable "id" is changed to the id value of newAtts.
     * @param newAtts - should be instance of SimplePelagicLHSAttributes or
     *                  SimpleBenthicLHSAttributes.
     */
    @Override
    public void setAttributes(LifeStageAttributesInterface newAtts) {
        if (newAtts instanceof FemaleAdolescentAttributes) {
            FemaleAdolescentAttributes spAtts = (FemaleAdolescentAttributes) newAtts;
            for (String key: atts.getKeys()) atts.setValue(key,spAtts.getValue(key));
        }  else if(newAtts instanceof FemaleImmatureAttributes) {
            FemaleImmatureAttributes spAtts = (FemaleImmatureAttributes) newAtts;
            for (String key: atts.getKeys()) atts.setValue(key,spAtts.getValue(key));
        }else {
            //TODO: should throw an error here
            logger.info("FemaleAdolescentAttributes.setAttributes(): no match for attributes type");
        }
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
         *  4) set attached to true
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
         *          8) set attached to true
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
        atts.setValue(LifeStageAttributesInterface.PROP_parentID,oldAtts.getValue(LifeStageAttributesInterface.PROP_id));//copy old id to parentID
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
    public FemaleAdolescentParameters getParameters() {
        return params;
    }

    /**
     * Sets the parameters for the instance to a cloned version of the input.
     * @param newParams - should be instance of FemaleAdolescentParameters
     */
    @Override
    public void setParameters(LifeStageParametersInterface newParams) {
        if (newParams instanceof FemaleAdolescentParameters) {
            params = (FemaleAdolescentParameters) newParams;
            setParametersFromSubClass(params);
            setParameterValues();
            setIBMFunctions();
        } else {
            //TODO: throw some error
        }
    }
    
    /**
     * Sets the IBM functions from the parameters object
     */
    private void setIBMFunctions(){
        fcnMort = params.getSelectedIBMFunctionForCategory(FemaleAdolescentParameters.FCAT_Mortality);
        fcnGrowth = params.getSelectedIBMFunctionForCategory(FemaleAdolescentParameters.FCAT_Growth);
        fcnMaturity = params.getSelectedIBMFunctionForCategory(FemaleAdolescentParameters.FCAT_Maturity);
        fcnExCost = params.getSelectedIBMFunctionForCategory(FemaleAdolescentParameters.FCAT_ExCost);  
        fcnMolt = params.getSelectedIBMFunctionForCategory(FemaleAdolescentParameters.FCAT_Molt);
        fcnMoltTime = params.getSelectedIBMFunctionForCategory(FemaleAdolescentParameters.FCAT_MoltTiming);
    }
    
    /*
     * Copy the values from the params map to the param variables.
     */
    private void setParameterValues() {
        isSuperIndividual = 
                params.getValue(params.PARAM_isSuperIndividual,true);
        horizRWP = 
                params.getValue(params.PARAM_horizRWP,horizRWP);
        minStageDuration = 
                params.getValue(params.PARAM_minStageDuration,minStageDuration);
        maxStageDuration = 
                params.getValue(params.PARAM_maxStageDuration,maxStageDuration);
        minSizeAtTrans = 
                params.getValue(params.PARAM_minSizeAtTrans,minSizeAtTrans);
        meanStageTransDelay = 
                params.getValue(params.PARAM_meanStageTransDelay,meanStageTransDelay);
        randomizeTransitions = 
                params.getValue(params.PARAM_randomizeTransitions,randomizeTransitions);
    }
    
    /**
     *  Provides a copy of the object.  The attributes and parameters
     *  are cloned in the process, so the clone is independent of the
     *  original.
     */
    @Override
    public Object clone() {
        FemaleAdolescent clone = null;
        try {
            clone       = (FemaleAdolescent) super.clone();
            clone.setAttributes((FemaleAdolescentAttributes) atts.clone());
            clone.setParameters((FemaleAdolescentParameters) params.clone());
            clone.lp    = (LagrangianParticle) lp.clone();
            clone.track = (ArrayList<Coordinate>) track.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return clone;        
    }

    @Override
    public List<LifeStageInterface> getMetamorphosedIndividuals(double dt) {
        double dtp = 0.25*(dt/DAY_SECS);//use 1/4 timestep (converted from sec to d)
        output.clear();
        List<LifeStageInterface> nLHSs=null;
        if ((ageInStage+dtp>=minStageDuration)) {
            if(numTrans>0){
            nLHSs = createNextLHSs();
            if (nLHSs!=null) output.addAll(nLHSs);
        }
        }
        return output;
    }

    private List<LifeStageInterface> createNextLHSs() {
        List<LifeStageInterface> nLHSs = null;
        try {
            //create LHS with "output" stage
            if (isSuperIndividual) {
                /** 
                 * Since this LHS instance is a super individual, only a part 
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
                starvationMort = 0.0;
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

    @Override
    public List<LifeStageInterface> getSpawnedIndividuals() {
        output.clear();
        return output;
    }
    
    /**
     * Initializes instance variables to attribute values (via updateVariables()), 
     * then determines initial position for the lagrangian particle tracker
     * and resets the track,
     * sets horizType and vertType attributes to HORIZ_LL, VERT_H,
     * and finally calls updatePosition(), updateEnvVars(), and updateAttributes().
     */
    public void initialize() {
//        atts.setValue(atts.PROP_id,id);
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
        starvationMort = 0.0;
        molted = false;
        exTot = 0.0;
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
            double K = 0;  //benthic adult starts out on bottom
            double z = i3d.interpolateBathymetricDepth(IJ);
            if (debug) logger.info("Bathymetric depth = "+z);
            double ssh = i3d.interpolateSSH(IJ);

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
                logger.info("depth = "+depth);
                logger.info("-------Finished setting initial position------------");
            }
            interpolateEnvVars(pos);
            updateAttributes(); 
        }
    }
    
    @Override
    public void step(double dt) throws ArrayIndexOutOfBoundsException {
        //determine daytime/nighttime
        dayOfYear = globalInfo.getCalendar().getYearDay();
        starvationMort = 0.0;
        numTrans = 0.0;
        molted = false;
        exTot = 0.0;
//        isDaytime = DateTimeFunctions.isDaylight(lon,lat,dayOfYear);
        //movement here
        //TODO: revise so no advection by currents!
        double[] pos;
            double[] uv = calcUV(dt);
            lp.setU(uv[0],lp.getN());
            lp.setV(uv[1],lp.getN());
            lp.doPredictorStep();
            //assume same daytime status, but recalc depth and revise W 
//            pos = lp.getPredictedIJK();
//            depth = -i3d.calcZfromK(pos[0],pos[1],pos[2]);
//            if (debug) logger.info("Depth after predictor step = "+depth);
            //w = calcW(dt,lp.getNP1())+r; //set swimming rate for predicted position
            lp.setU(uv[0],lp.getNP1());
            lp.setV(uv[1],lp.getNP1());
            //now do corrector step
            lp.doCorrectorStep();
            pos = lp.getIJK();
        time = time+dt;
        updateAge(dt);
        updateSize(dt);
        updateWeight(dt);
        updateNum(dt);
        updatePosition(pos);
        interpolateEnvVars(pos);
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
     * Function to calculate horizontal walking speeds.
     * 
     * @param dt - time step
     * @return   - double[]{u,v}
     */
    public double[] calcUV(double dt) {
        double[] uv = {0.0,0.0};
        if (horizRWP>0) {
            double r = Math.sqrt(horizRWP/Math.abs(dt));
            uv[0] += r*rng.computeNormalVariate(); //stochastic swimming rate
            uv[1] += r*rng.computeNormalVariate(); //stochastic swimming rate
            if (debugOps) logger.info("uv: "+r+"; "+uv[0]+", "+uv[1]+"\n");
        }
        uv[0] = Math.signum(dt)*uv[0];
        uv[1] = Math.signum(dt)*uv[1];
        return uv;
    }
    
    private void updateAge(double dt) {
        age        = age+dt/DAY_SECS;
        ageInStage = ageInStage+dt/DAY_SECS;
        ageInInstar = ageInInstar+dt/DAY_SECS;
        if (ageInStage>maxStageDuration) {
            alive = false;
            active = false;
        }
    }

    /**
     *
     * @param dt - time step in seconds
     */
    private void updateSize(double dt) {        
        double D;
        if(instar<8){
            D = (Double) fcnMoltTime.calculate(new double[]{size, temperature});
        } else{
            D = 365.0;
        }
        exTot = (Double) fcnExCost.calculate(size);
        if((ageInInstar+dt/DAY_SECS)>D){
            boolean mat = (Boolean) fcnMaturity.calculate(new double[]{size,temperature});
            size = (Double) fcnMolt.calculate(size);
            instar += 1;
            ageInInstar = 0.0;
            molted = true;
          if(mat){
                numTrans += number;
            }
        }
    }

    /**
     * Updates weight.
     * 
     * @param dt - time step in seconds
     */
    private void updateWeight(double dt) {
        double D = (Double) fcnMoltTime.calculate(new double[]{size, temperature});
        double exPerDay = exTot/D;
        fcnGrowth.setParameterValue("sex", 1.0);
        double growthRate = (Double) fcnGrowth.calculate(new double[]{instar, weight, temperature, exPerDay});
        if(growthRate>0){
            weight = weight*Math.exp(Math.log(1.0+((dt/DAY_SECS)*growthRate)));
        } else{
            double totRate = Math.max(-1.0,growthRate/weight);
            starvationMort = -Math.log(-(.0099+totRate))*(dt/DAY_SECS);
        } 
        if(molted){
            weight = weight - exTot;
            molted=false;
        }
    }

    /**
     *
     * @param dt - time step in seconds
     */
    private void updateNum(double dt) {
        double mortalityRate = 0.0D;//in unis of [days]^-1
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
        double totRate = mortalityRate + starvationMort;
        if ((ageInStage>=minStageDuration)&&numTrans>0) {
            if(number!=numTrans){
            double matRate = numTrans/number;
            double instMatRate = -Math.log(1-matRate);
            totRate += instMatRate;
            //apply mortality rate to previous number transitioning and
            //add in new transitioners
            
                    numTrans = numTrans*Math.exp(-dt*mortalityRate/DAY_SECS)+
                    (instMatRate/totRate)*number*(1-Math.exp(-dt*totRate/DAY_SECS));
            } else{
                number = number-numTrans;
        }
        }
        number = number*Math.exp(-dt*totRate/DAY_SECS);
        if(number<0.01){
            active=false;alive=false;number=number+numTrans;
        }
    }

    private void updatePosition(double[] pos) {
        depth = -i3d.calcZfromK(pos[0],pos[1],pos[2]);
        lat   = i3d.interpolateLat(pos);
        lon   = i3d.interpolateLon(pos);
        gridCellID = ""+Math.round(pos[0])+"_"+Math.round(pos[1]);
        updateTrack();
    }
    
    private void interpolateEnvVars(double[] pos) {
        temperature = i3d.interpolateTemperature(pos);
        salinity    = i3d.interpolateSalinity(pos);
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(double newTime) {
        startTime = newTime;
        time      = startTime;
        atts.setValue(LifeStageAttributesInterface.PROP_startTime,startTime);
        atts.setValue(LifeStageAttributesInterface.PROP_time,time);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean b) {
        active = b;
        atts.setActive(b);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void setAlive(boolean b) {
        alive = b;
        atts.setAlive(b);
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
    public boolean isSuperIndividual() {
        return isSuperIndividual;
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
     * Updates attribute values defined for this abstract class. 
     */
    @Override
    protected void updateAttributes() {
        //update superclass attributes
        super.updateAttributes();
        //update new attributes
        atts.setValue(FemaleAdolescentAttributes.PROP_size,size);
        atts.setValue(FemaleAdolescentAttributes.PROP_weight,weight);
        atts.setValue(FemaleAdolescentAttributes.PROP_ageInInstar,ageInInstar);
        atts.setValue(FemaleAdolescentAttributes.PROP_instar,instar);
        atts.setValue(FemaleAdolescentAttributes.PROP_salinity,salinity);
        atts.setValue(FemaleAdolescentAttributes.PROP_temperature,temperature);
        atts.setValue(FemaleAdolescentAttributes.PROP_ph,ph);
        atts.setValue(FemaleAdolescentAttributes.PROP_number, number);
    }

    /**
     * Updates local variables from the attributes.  
     */
    @Override
    protected void updateVariables() {
        //update superclass variables
        super.updateVariables();
        //update new variables
       size        = atts.getValue(FemaleAdolescentAttributes.PROP_size,size);
       weight      = atts.getValue(FemaleAdolescentAttributes.PROP_weight, weight);
       ageInInstar = atts.getValue(FemaleAdolescentAttributes.PROP_ageInInstar, ageInInstar);
       instar      = atts.getValue(FemaleAdolescentAttributes.PROP_instar, instar);
       salinity    = atts.getValue(FemaleAdolescentAttributes.PROP_salinity,salinity);
       temperature = atts.getValue(FemaleAdolescentAttributes.PROP_temperature,temperature);
       ph        = atts.getValue(FemaleAdolescentAttributes.PROP_ph,ph);
       number    = atts.getValue(FemaleAdolescentAttributes.PROP_number, number);
    }
}
