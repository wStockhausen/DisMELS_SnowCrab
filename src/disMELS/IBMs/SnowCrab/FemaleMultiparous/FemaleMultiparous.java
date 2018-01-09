/*
 * FemaleMultiparous.java
 */

package disMELS.IBMs.SnowCrab.FemaleMultiparous;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import disMELS.IBMs.SnowCrab.AbstractBenthicStage;
import disMELS.IBMs.SnowCrab.EggMassExtruded.ExtrudedEggMass;
import disMELS.IBMs.SnowCrab.EggMassExtruded.ExtrudedEggMassAttributes;
import disMELS.IBMs.SnowCrab.FemalePrimiparous.FemalePrimiparousAttributes;
import wts.models.DisMELS.framework.*;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.utilities.CalendarIF;
import wts.models.utilities.DateTimeFunctions;
import wts.roms.model.LagrangianParticle;


/**
 * DisMELS class representing multiparous female snow crab.
 * 
 * Primiparous female snow crab (females with their first egg clutch) become
 * multiparous after their egg clutch is hatched. Multiparous females typically
 * have larger egg clutches with a higher fraction of viable eggs than similarly-sized
 * primiparous females.
 * 
 * For the IBM, instances of the FemalePrimiparous class metamorphose into
 * instances of the FemaleMultiparous class after their egg clutch hatches.
 */
@ServiceProvider(service=LifeStageInterface.class)
public class FemaleMultiparous extends AbstractBenthicStage {
    
        //Static fields    
            //  Static fields new to this class
    /** flag to do debug operations */
    public static boolean debugOps = false;
    /** Class for attributes */
    public static final String attributesClass = FemaleMultiparousAttributes.class.getName();
    /** Class for parameters */
    public static final String parametersClass = FemaleMultiparousParameters.class.getName();
    /** Class for feature type for point positions */
    public static final String pointFTClass = wts.models.DisMELS.framework.LHSPointFeatureType.class.getName();
//            "wts.models.DisMELS.LHS.BenthicAdult.AdultStagePointFT";
    /** Classes for next LHS */
    public static final String[] nextLHSClasses = new String[]{FemaleMultiparous.class.getName()};
    /** Classes for spawned LHS */
    public static final String[] spawnedLHSClasses = new String[]{ExtrudedEggMass.class.getName()};
    
        //Instance fields
            //  Fields hiding ones from superclass
    /** life stage attributes */
    protected FemaleMultiparousAttributes atts = null;
    /* life stage parameters */
    protected FemaleMultiparousParameters params = null;
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
    /** stage transition rate */
    protected double stageTransRate;
    
    
        //fields that reflect (new) attribute values
    //none    
            //other fields
    /** number of individuals transitioning to next stage */
    private double numTrans;  
     /** day of year */
    private double dayOfYear;
   
    /** IBM function selected for mortality */
    private IBMFunctionInterface fcnMortality = null; 
    
    /** flag to print debugging info */
    public static boolean debug = false;
    /** logger for class */
    private static final Logger logger = Logger.getLogger(FemaleMultiparous.class.getName());
    
    /**
     * This constructor is provided only to facilitate the ServiceProvider functionality.
     * DO NOT USE IT!!
     */
    public FemaleMultiparous() {
        super("");
        super.atts = atts;
        super.params = params;
    }
    
    /**
     * Creates a new life stage instance with parameters based on the typeName and
     * "default" attributes.
     */
    public FemaleMultiparous(String typeName) 
                throws InstantiationException, IllegalAccessException {
        super(typeName);
        atts   = new FemaleMultiparousAttributes(typeName);
        atts.setValue(LifeStageAttributesInterface.PROP_id,id);
        atts.setValue(LifeStageAttributesInterface.PROP_parentID,id);
        atts.setValue(LifeStageAttributesInterface.PROP_origID,id);
        setAttributesFromSubClass(atts);  //set object in the superclass
        params = (FemaleMultiparousParameters) LHS_Factory.createParameters(typeName);
        setParametersFromSubClass(params);//set object in the superclass
        setParameters(params);
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
    public FemaleMultiparous createInstance(String[] strv) 
                        throws InstantiationException, IllegalAccessException {
        LifeStageAttributesInterface theAtts = LHS_Factory.createAttributes(strv);
        FemaleMultiparous lhs = createInstance(theAtts);
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
    public FemaleMultiparous createInstance(LifeStageAttributesInterface theAtts)
                        throws InstantiationException, IllegalAccessException {
        FemaleMultiparous lhs = null;
        if (theAtts instanceof FemaleMultiparousAttributes) {
            lhs = new FemaleMultiparous(theAtts.getTypeName());
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
    public FemaleMultiparousAttributes getAttributes() {
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
        if (newAtts instanceof FemaleMultiparousAttributes) {
            FemaleMultiparousAttributes spAtts = (FemaleMultiparousAttributes) newAtts;
            for (String key: atts.getKeys()) atts.setValue(key,spAtts.getValue(key));
        } else if(newAtts instanceof FemalePrimiparousAttributes){
            FemalePrimiparousAttributes spAtts = (FemalePrimiparousAttributes) newAtts;
            String key = FemalePrimiparousAttributes.PROP_size; atts.setValue(key, spAtts.getValue(key));
            key = FemalePrimiparousAttributes.PROP_weight; atts.setValue(key, spAtts.getValue(key));
            key = FemalePrimiparousAttributes.PROP_instar; atts.setValue(key, spAtts.getValue(key));
            key = FemalePrimiparousAttributes.PROP_age; atts.setValue(key, spAtts.getValue(key));
            key = FemalePrimiparousAttributes.PROP_shellcond; atts.setValue(key, spAtts.getValue(key));
            key = FemalePrimiparousAttributes.PROP_shellthick; atts.setValue(key, spAtts.getValue(key));   
        } else {
            //TODO: should throw an error here
            logger.info("AdultStage.setAttributes(): no match for attributes type");
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
        atts.setValue(FemaleMultiparousAttributes.PROP_size, size);
        atts.setValue(FemaleMultiparousAttributes.PROP_weight, weight);
        atts.setValue(FemaleMultiparousAttributes.PROP_instar, instar);
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
    public FemaleMultiparousParameters getParameters() {
        return params;
    }

    /**
     * Sets the parameters for the instance to a cloned version of the input.
     * @param newParams - should be instance of FemaleMultiparousParameters
     */
    @Override
    public void setParameters(LifeStageParametersInterface newParams) {
        if (newParams instanceof FemaleMultiparousParameters) {
            params = (FemaleMultiparousParameters) newParams;
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
        fcnMortality      = params.getSelectedIBMFunctionForCategory(FemaleMultiparousParameters.FCAT_Mortality);
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
        FemaleMultiparous clone = null;
        try {
            clone       = (FemaleMultiparous) super.clone();
            clone.setAttributes((FemaleMultiparousAttributes) atts.clone());
            clone.setParameters((FemaleMultiparousParameters) params.clone());
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
        if ((ageInStage+dtp>=minStageDuration)&&(size>=minSizeAtTrans)) {
            nLHSs = createMetamorphosedIndividual();
            if (nLHSs!=null) output.addAll(nLHSs);
        }
        return output;
    }

    private List<LifeStageInterface> createMetamorphosedIndividual() {
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

    //TODO: implement this!
    @Override
    public List<LifeStageInterface> getSpawnedIndividuals() {
        output.clear();
        return output;
    }
        
    /**
     * Initializes time-dependent and time-independent variables
     * @param time
     */
    private void initializeTimedependentVariables() {
        //temporarily set calendar time to variable time
        CalendarIF cal = globalInfo.getCalendar();
        long modTime = cal.getTimeOffset();
        cal.setTimeOffset((long) time);
        dayOfYear = cal.getYearDay();
        
        //reset calendar time to model time
        cal.setTimeOffset(modTime);
    }

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
            lp.setIJK(IJ[0],IJ[1],K);
            //reset track array
            track.clear();
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
            initializeTimedependentVariables();
        }
        updateVariables();//set instance variables to attribute values
    }
    
    @Override
    public void step(double dt) throws ArrayIndexOutOfBoundsException {
        //determine daytime/nighttime
        dayOfYear = globalInfo.getCalendar().getYearDay();
    }
    
    /**
     * Function to calculate horizontal swimming speeds.
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
        if (ageInStage>maxStageDuration) {
            alive = false;
            active = false;
        }
    }

    private void updateWeight(double dt) {
        //TODO: implement!
    }

    /**
     *
     * @param dt - time step in seconds
     */
    private void updateNum(double dt) {
        double mortalityRate = 0.0D;//in unis of [days]^-1
        if (fcnMortality instanceof ConstantMortalityRate){
            /**
             * @param vars - null
             * @return     - Double - the corresponding mortality rate (per day) 
             */
            mortalityRate = (Double)fcnMortality.calculate(null);
        } else 
        if (fcnMortality instanceof TemperatureDependentMortalityRate_Houde1989){
            /**
             * @param vars - Double - temperature (deg C)
             * @return     - Double - the corresponding mortality rate (per day) 
             */
            mortalityRate = (Double)fcnMortality.calculate(temperature);//using temperature as covariate for mortality
        }
        double totRate = mortalityRate;
        if ((ageInStage>=minStageDuration)) {
            totRate += stageTransRate;
            //apply mortality rate to previous number transitioning and
            //add in new transitioners
            numTrans = numTrans*Math.exp(-dt*mortalityRate/DAY_SECS)+
                    (stageTransRate/totRate)*number*(1-Math.exp(-dt*totRate/DAY_SECS));
        }
        number = number*Math.exp(-dt*totRate/DAY_SECS);
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
        //TODO: add pH
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
//        //no new attributes, but would look like:
//        atts.setValue(MaleImmatureAttributes.PROP_size,size);
    }

    /**
     * Updates local variables from the attributes.  
     */
    @Override
    protected void updateVariables() {
        //update superclass variables
        super.updateVariables();
        //update new variables
//        //no new variables, but would look like:
//        size        = atts.getValue(FemaleImmatureAttributes.PROP_size,size);
    }
}
