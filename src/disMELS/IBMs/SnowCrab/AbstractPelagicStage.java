/*
 * AbstractPelagicStage.java
 */

package disMELS.IBMs.SnowCrab;

import SnowCrabFunctions.AnnualMoltFunction;
import SnowCrabFunctions.FixedDurationFunction;
import SnowCrabFunctions.IntermoltDurationFunction_Belehradek;
import SnowCrabFunctions.IntermoltIntegratorFunction;
import SnowCrabFunctions.MortalityFunction_OuelletAndSteMarie2017;
import com.vividsolutions.jts.geom.Coordinate;
import com.wtstockhausen.utils.RandomNumberGenerator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedDepthAndTempRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedDepthRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedOffBottomAndTempRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedOffBottomRange;
import wts.models.DisMELS.framework.GlobalInfo;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.LHS_Factory;
import wts.models.DisMELS.framework.LifeStageAttributesInterface;
import wts.models.DisMELS.framework.LifeStageInterface;
import static wts.models.DisMELS.framework.LifeStageInterface.DAY_SECS;
import wts.models.DisMELS.framework.LifeStageParametersInterface;
import wts.models.utilities.DateTimeFunctions;
import wts.roms.gis.AlbersNAD83;
import wts.roms.model.Interpolator3D;
import wts.roms.model.LagrangianParticle;

/**
 * Abstract class for snow crab pelagic life stages.
 */
public abstract class AbstractPelagicStage implements LifeStageInterface {
    
    //Static fields
    /** boolean flag indicating no advection by currents (= false) */
    public static final boolean noAdvection = false;
    /** ROMS 3d interpolator object */
    public static Interpolator3D i3d; //can't be final, must be public!
    /** Random Number Generator */
    protected static final RandomNumberGenerator rng = GlobalInfo.getInstance().getRandomNumberGenerator();
    /** tolerance to edge of model grid */
    protected static double tolGridEdge = 0.5;
    
    /** comma for output strings */
    protected static final String cc = ",";
    /** the decimal format for output */
    protected static final DecimalFormat decFormat = new DecimalFormat("#.#####");
    
    /** the DisMELS globalInfo object */
    protected static final GlobalInfo globalInfo = GlobalInfo.getInstance();

    /** flag to write track info to output file */
    protected static boolean writeTracksFlag = false;
    
    /**
     * Sets the flag to retain full track information (true) or only
     * beginning and ending locations (false) for track output.
     * 
     * @param b - value to set
     */
    public static void setWriteTracksFlagForClass(boolean b){
        writeTracksFlag = b;
    }
    
    /**
     * Gets the flag to retain full track information (true) or only
     * beginning and ending locations (false) for track output.
     * 
     * @return true or false
     */
    public static boolean getWriteTracksFlagForClass(){
        return writeTracksFlag;
    }
    
    //Instance fields
    /** the LHS attributes */
    protected AbstractPelagicStageAttributes atts = null;
    /** the LHS parameters */
    protected LifeStageParametersInterface params = null;
    
    /** the LagrangianParticle */
    protected LagrangianParticle lp = new LagrangianParticle();;
    /** the spatial track in projected coordinates for the LHS */
    protected ArrayList<Coordinate> track = new ArrayList<>();
    /** the spatial track in geographic coordinates for the LHS */
    protected ArrayList<Coordinate> trackLL = new ArrayList<>();
    /** array list for output LHS instances */
    protected ArrayList<LifeStageInterface> output = new ArrayList<>();
    /** the type of LHS instance */
    protected String typeName = null;
    /** id for this instance*/
    protected long id = 0;
    
    /** fields that reflect parameter values */
    /** flag indicating instance is a super-individual */
    protected boolean isSuperIndividual;
    /** horizontal random walk parameter */
    protected double horizRWP;
    /** maximum stage duration (followed by death) */
    protected double maxStageDuration;
    /** stage transition rate */
    protected double stageTransRate;
    /** flag to use stochastic transitions */
    protected boolean randomizeTransitions;
    
    //fields that reflect attribute values
    /** flag indicating whether individual has been "activated" */
    protected boolean active=false;
    /** flag indicating whether individual is alive */
    protected boolean alive=true;
    /** ocean_time at which individual/this was created */
    protected double  startTime=0;
    /** current ocean_time */
    protected double  time=0;
    /** current latitude, in decimal degrees */
    protected double  lat=0;
    /** current longitude, in decimal degrees */
    protected double  lon=0;
    /** current depth, in meters */
    protected double  depth=0;
    /** bathymetric depth at individual's location, in meters */
    protected double  bathym=0;
    /** current ROMS grid cell, as a string */
    protected String  gridCellID="";
    /** total age, in days */
    protected double  age=0;
    /** age in life stage, in days */
    protected double  ageInStage=0;
    /** number of individuals represented */
    protected double  number=0;
    
    /** molt indicator */
    protected double moltIndicator = 0;
    /** shell thickness */
    protected double shellthick = 0;
    /** in situ temperature (deg C) */
    protected double temperature = 0;
    /** in situ salinity */
    protected double salinity = 0;
    /** in situ pH */
    protected double ph = 0; 
    
    //other fields
    /** number of individuals transitioning to next stage */
    protected double numTrans;  
    /** mean stage duration (days) at in situ temperature */
    protected double meanStageDuration;
    
    //IBM Functions
    /** IBM function selected for intermolt time */
    protected IBMFunctionInterface fcnMoltTiming = null; 
    /** IBM function selected for mortality */
    protected IBMFunctionInterface fcnMort = null; 
    /** IBM function selected for vertical movement */
    protected IBMFunctionInterface fcnVM = null; 
    /** IBM function selected for vertical velocity */
    protected IBMFunctionInterface fcnVV = null; 
    
    //private members (not inherited by subclasses)
    /* flag to log debugging information */
    private boolean debugOps = false;
    /* logger */
    private Logger logger  = Logger.getLogger(this.getClass().getName());
    
    /**
     * Partially instantiates an instance of a class inheriting from AbstractPelagicStage
     * 
     * Importantly, this sets "NoAdvection" to FALSE for the associated LagrangianParticle object.
     */
    protected AbstractPelagicStage(String typeName) {
        this.typeName = typeName;
        id = LHS_Factory.getNewID();
        lp.setNoAdvection(noAdvection);
        if (i3d==null) i3d = GlobalInfo.getInstance().getInterpolator3D();
    }
    
    /**
     * Subclasses can use this method to make sure the attributes object for
     * this superclass refers to the one from their class.
     * 
     * @param subAtts - the attributes object for the subclass
     */
    protected final void setAttributesFromSubClass(AbstractPelagicStageAttributes subAtts){
        atts = subAtts;
    }
    
    /**
     * Subclasses can use this method to make sure the parameters object from
     * this superclass refers to the one from the subclass.
     * 
     * @param subParams - the parameters object from the subclass 
     */
    protected final void setParametersFromSubClass(LifeStageParametersInterface subParams){
        params = subParams;
    }
    
    /**
     *  Returns the instance id.
     * 
     * @return - the ID
     */
    @Override
    public final long getID() {
        return id;
    }
    
    /**
     * Gets the LagrangianParticle instance associted with this object.
     * 
     * @return 
     */
    @Override
    public LagrangianParticle getLagrangianParticle() {
        return lp;
    }
    
    /**
     * Sets the lagrangian particle representation for the instance to a clone  
     * of newLP. noAdvection is set to FALSE on the cloned LP to ensure consistency
     * with motion of pelagic life stages.
     * 
     * @param newLP - LagrangianParticle instance to be cloned.
     */
    @Override
    public void setLagrangianParticle(LagrangianParticle newLP) {
        lp = (LagrangianParticle) newLP.clone();
        lp.setNoAdvection(noAdvection);
    }
    
    /**
     * Gets the last projected position in the track of the instance.
     * 
     * @param coordType - type of coordinates requested (e.g. grid or lat/lon)
     * @return - last proejcted position as Coordinate
     */
    @Override
    public Coordinate getLastPosition(int coordType) {
        if (coordType==COORDINATE_TYPE_PROJECTED)  return track.get(track.size()-1); else
        if (coordType==COORDINATE_TYPE_GEOGRAPHIC) return trackLL.get(trackLL.size()-1);
        return null;
    }
    
    /**
     * Gets the instance's track.
     * 
     * @param coordType - type of coordinates requested (e.g. grid or lat/lon)
     * @return - track as Coordinate[], or null
     */
    @Override
    public Coordinate[] getTrack(int coordType) {
        Coordinate[] cv = new Coordinate[1];
        if (coordType==COORDINATE_TYPE_PROJECTED)  return track.toArray(cv); else
        if (coordType==COORDINATE_TYPE_GEOGRAPHIC) return trackLL.toArray(cv);
        return null;
    }

    /**
     * Clears the existing track and restarts it with the new initial position.
     * 
     * @param initPos - new starting position.
     * @param coordType - type of coordinates requested (e.g. grid or lat/lon)
     */
    @Override
    public void startTrack(Coordinate initPos, int coordType) {
        if (coordType==COORDINATE_TYPE_PROJECTED) {
            track.clear();
            track.add(initPos);
        } else
        if (coordType==COORDINATE_TYPE_GEOGRAPHIC) {
            trackLL.clear();
            trackLL.add(initPos);
        }    
    }

    /**
     * Returns the track information in string format.
     * 
     * @param coordType - type of coordinates requested (e.g. grid or lat/lon)
     * @return 
     */
    @Override
    public String getTrackAsString(int coordType){
        StringBuffer strb = new StringBuffer();
        if (coordType==COORDINATE_TYPE_PROJECTED) {
            for (Coordinate c : track){
                strb.append(decFormat.format(c.x));strb.append(":");
                strb.append(decFormat.format(c.y));strb.append(":");
                strb.append(decFormat.format(c.z));strb.append(";");
            }
        } else
        if (coordType==COORDINATE_TYPE_GEOGRAPHIC) {
            for (Coordinate c : trackLL){
                strb.append(decFormat.format(c.x));strb.append(":");
                strb.append(decFormat.format(c.y));strb.append(":");
                strb.append(decFormat.format(c.z));strb.append(";");
            }
        }
        return strb.toString();
    }

    /**
     *  Returns the LHS type for the instance.
     */
    @Override
    public String getTypeName() {return typeName;}
    
//  Methods inherited from LifeHistoryStageIF  
    /**
     *  This method should be overridden by extending classes.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Returns the report for the implementing class as a CSV formatted string.
     *
     * @return - the attributes and track as a csv-formatted String
     */
    @Override
    public String getReport() {
        updateAttributes();//make sure attributes are up to date
        atts.setValue(LifeStageAttributesInterface.PROP_track, getTrackAsString(COORDINATE_TYPE_GEOGRAPHIC));//
        return atts.getCSV();
    }

     /**
     * Returns the report header for the implementing class as a CSV formatted string.
     *
     * @return - the header names as a csv-formatted String
     */
    @Override
    public String getReportHeader() {
        return atts.getCSVHeaderShortNames();
    }
    
   /**
    * Sets the tolerance used to determine whether an individual is "at" the
    * edge of the grid.
    * 
    * @param newTol 
    */
    @Override
    public void setGridEdgeTolerance(double newTol){
        tolGridEdge = newTol;
    }
    
    /** Gets value of flag to write complete track information (if true) */
    @Override
    public boolean getWriteTracksFlag(){
        return writeTracksFlag;
    }
    
    /** Sets flag to write complete track information (if true) */
    @Override
    public void setWriteTracksFlag(boolean b){
        writeTracksFlag = b;
    }
    
    /**
     * Updates both types of tracks (projected and geographic).
     */    
    protected void updateTrack() {
        double[] dest = AlbersNAD83.transformGtoP(new double[]{lon,lat});
        Coordinate c = new Coordinate(dest[0],dest[1]);
        if (writeTracksFlag){
            //add current location to end of tracks
            track.add(c);
            trackLL.add(new Coordinate(lon,lat,-depth));
        } else {
            switch (track.size()) {
                case 0:
                    //add current location to tracks as 1st coordinate
                    track.add(c);
                    trackLL.add(new Coordinate(lon,lat,-depth));
                    break;
                case 1:
                    //add current location to tracks as 2nd coordinate
                    track.add(c);
                    trackLL.add(new Coordinate(lon,lat,-depth));
                    break;
                default:
                    //replace 2nd coordinate with current location
                    track.set(1, c);
                    trackLL.set(1,new Coordinate(lon,lat,-depth));
                    break;
            }
        }
    }

    /**
     * Sets the attributes for the instance by copying values from the input.
     * This does NOT change the typeNameof the LHS instance (or the associated 
     * LHSAttributes instance) on which the method is called.
     * 
     * Note that id, parentID, and origID are copied as well as other LifeStageAttributesInterface attributes.
     * 
     * If newAtts inherits from AbstractPelagicStageAttributes, then additional attributes are copied.
     * 
     *  As a side effect, updateVariables() is called to update instance variables.
     *  Instance field "id" is also updated.
     * @param newAtts - instance of LifeStageAttributesInterface
     */
    @Override
    public void setAttributes(LifeStageAttributesInterface newAtts) {
        String key;
        key = LifeStageAttributesInterface.PROP_id;         atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_parentID;   atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_origID;     atts.setValue(key,newAtts.getValue(key));
        
        key = LifeStageAttributesInterface.PROP_startTime;  atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_time;       atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_horizType;  atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_vertType;   atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_horizPos1;  atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_horizPos2;  atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_vertPos;    atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_bathym;     atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_gridCellID; atts.setValue(key,newAtts.getValue(key));
        //track
        key = LifeStageAttributesInterface.PROP_active;     atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_alive;      atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_age;        atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_ageInStage; atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_number;     atts.setValue(key,newAtts.getValue(key));
        
        if (newAtts instanceof AbstractPelagicStageAttributes){
            key = AbstractPelagicStageAttributes.PROP_moltindicator; atts.setValue(key,newAtts.getValue(key));
            key = AbstractPelagicStageAttributes.PROP_shellthick;    atts.setValue(key,newAtts.getValue(key));
        }
        
        id = atts.getValue(LifeStageAttributesInterface.PROP_id, id);
        updateVariables();
    }
    
    /**
     * Updates attribute values defined for this abstract class. Subclasses should
     * override this method, possibly calling super.updateAttributes().
     */
    protected void updateAttributes() {
        //note that the following do not need to be updated
        //  id, parentID, origID, startTime, horizType, vertType
        //id, 
        //parentID, 
        //origID, 
        //startTime
        atts.setValue(AbstractPelagicStageAttributes.PROP_time,time);
        //horizType, 
        //vertType
        atts.setValue(AbstractPelagicStageAttributes.PROP_horizPos1,lon);
        atts.setValue(AbstractPelagicStageAttributes.PROP_horizPos2,lat);
        atts.setValue(AbstractPelagicStageAttributes.PROP_vertPos,depth);
        atts.setValue(AbstractBenthicStageAttributes.PROP_bathym,bathym);
        atts.setValue(AbstractPelagicStageAttributes.PROP_gridCellID,gridCellID);
        //track
        atts.setValue(AbstractPelagicStageAttributes.PROP_active,active);
        atts.setValue(AbstractPelagicStageAttributes.PROP_alive,alive);
        atts.setValue(AbstractPelagicStageAttributes.PROP_age,age);
        atts.setValue(AbstractPelagicStageAttributes.PROP_ageInStage,ageInStage);
        atts.setValue(AbstractPelagicStageAttributes.PROP_number,number);
        
        atts.setValue(AbstractPelagicStageAttributes.PROP_moltindicator,moltIndicator);
        atts.setValue(AbstractPelagicStageAttributes.PROP_shellthick,shellthick);
        atts.setValue(AbstractPelagicStageAttributes.PROP_salinity,salinity);
        atts.setValue(AbstractPelagicStageAttributes.PROP_temperature,temperature);
        atts.setValue(AbstractPelagicStageAttributes.PROP_ph,ph);
    }

    /**
     * Updates local variables from the attributes.  
     * The following are NOT updated here:
     *  id, parentID, origID, hType, vType
     */
    protected void updateVariables() {
        startTime  = atts.getValue(AbstractPelagicStageAttributes.PROP_startTime,startTime);
        time       = atts.getValue(AbstractPelagicStageAttributes.PROP_time,time);
        lon        = atts.getValue(AbstractPelagicStageAttributes.PROP_horizPos1,lon);
        lat        = atts.getValue(AbstractPelagicStageAttributes.PROP_horizPos2,lat);
        depth      = atts.getValue(AbstractPelagicStageAttributes.PROP_vertPos,depth);
        gridCellID = atts.getValue(AbstractPelagicStageAttributes.PROP_gridCellID,gridCellID);
        active     = atts.getValue(AbstractPelagicStageAttributes.PROP_active,active);        
        alive      = atts.getValue(AbstractPelagicStageAttributes.PROP_alive,alive);
        age        = atts.getValue(AbstractPelagicStageAttributes.PROP_age,age);
        ageInStage = atts.getValue(AbstractPelagicStageAttributes.PROP_ageInStage,ageInStage);
        number     = atts.getValue(AbstractPelagicStageAttributes.PROP_number,number);
        
        moltIndicator = atts.getValue(AbstractPelagicStageAttributes.PROP_moltindicator,moltIndicator);
        shellthick    = atts.getValue(AbstractPelagicStageAttributes.PROP_shellthick,shellthick);
        salinity      = atts.getValue(AbstractPelagicStageAttributes.PROP_salinity,salinity);
        temperature   = atts.getValue(AbstractPelagicStageAttributes.PROP_temperature,temperature);
        ph            = atts.getValue(AbstractPelagicStageAttributes.PROP_ph,ph);
    }
    
    @Override
    public boolean isSuperIndividual() {
        return isSuperIndividual;
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
    public List<LifeStageInterface> getSpawnedIndividuals() {
        output.clear();
        return output;
    }

    /**
     * Provides a 
     * @param dt
     * @throws ArrayIndexOutOfBoundsException 
     */
    @Override
    public void step(double dt) throws ArrayIndexOutOfBoundsException {
        if (i3d==null) i3d = GlobalInfo.getInstance().getInterpolator3D();
        stepPosition(dt);
        double[] pos = pos = lp.getIJK();
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
     * 
     * @param dt
     * @throws ArrayIndexOutOfBoundsException 
     */
    protected void stepPosition(double dt) throws ArrayIndexOutOfBoundsException {
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
        if (debugOps) {
            pos = lp.getIJK();
            logger.info("Depth after corrector step = "+(-i3d.calcZfromK(pos[0],pos[1],pos[2])));
        }
    }
    
    /**
     * Function to calculate vertical and horizontal movement rates.
     * 
     * @param dt time step (s) 
     * @return double[] with elements u, v, w
     */
    protected double[] calcUVW(double[] pos, double dt) {
        //compute vertical velocity
        double w = 0;
        if (fcnVV instanceof wts.models.DisMELS.IBMFunctions.SwimmingBehavior.ConstantMovementRateFunction) {
            //calculate nominal vertical swimming speed
            /**
            * @param vars - double[]{dt}.
            * @return     - movement rate as a Double 
            */
            w = (Double) fcnVV.calculate(new double[]{dt});
        }
        //add in other considerations
        if (fcnVM instanceof VerticalMovement_FixedDepthRange) {    
            double ssh = i3d.interpolateSSH(pos);
            w = (Double) fcnVM.calculate(new double[]{dt,depth-ssh,bathym,w});
        } else     
        if (fcnVM instanceof VerticalMovement_FixedDepthAndTempRange) {    
            double ssh = i3d.interpolateSSH(pos);
            w = (Double) fcnVM.calculate(new double[]{dt,depth-ssh,bathym,temperature,w});
        } else     
        if (fcnVM instanceof VerticalMovement_FixedOffBottomRange) {    
            double ssh = i3d.interpolateSSH(pos);
            w = (Double) fcnVM.calculate(new double[]{dt,depth-ssh,bathym,w});
        } else     
        if (fcnVM instanceof VerticalMovement_FixedOffBottomAndTempRange) {    
            double ssh = i3d.interpolateSSH(pos);
            w = (Double) fcnVM.calculate(new double[]{dt,depth-ssh,bathym,temperature,w});
        } else     
        if (fcnVM instanceof wts.models.DisMELS.IBMFunctions.Movement.DielVerticalMigration_FixedDepthRanges) {
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
     * Update age-related variables.
     * <pre>
     * This changes the values of the variables
     *      <code>age</code> 
     *      <code>ageInStage</code>
     * </pre>
     * @param dt - time step in seconds
     */
    protected void updateAge(double dt) {
        age        = age+dt/DAY_SECS;
        ageInStage = ageInStage+dt/DAY_SECS;
        if (ageInStage>maxStageDuration) {
            alive = false;
            active = false;
        }
    }

    /**
     * Update the molt indicator variables.
     * <pre>
     * This changes the values of the variables
     *      <code>moltIndicator</code> 
     *      <code>meanStageDuration</code> (in days)
     * </pre>
     * @param dt - time step in seconds
     */
    protected void updateMoltIndicator(double dt) throws ArithmeticException {
        if (fcnMoltTiming instanceof IntermoltIntegratorFunction) {
            double[] res = (double[]) fcnMoltTiming.calculate(temperature);
            moltIndicator    += dt/DAY_SECS*res[0];
            meanStageDuration = res[1];
            if (Double.isNaN(moltIndicator)|Double.isInfinite(moltIndicator)){
                String msg = "NaN or Inf detected in updateMoltIndicator\n"
                           + "for "+typeName+" "+id+". IntermoltIntegratorFunction parameter values are\n"
                           + "\ta = "+fcnMoltTiming.getParameter(IntermoltIntegratorFunction.PARAM_a).getValueAsString()+"\n"
                           + "\tb = "+fcnMoltTiming.getParameter(IntermoltIntegratorFunction.PARAM_b).getValueAsString()+"\n";
                throw(new ArithmeticException(msg));
            }
        } else if (fcnMoltTiming instanceof IntermoltDurationFunction_Belehradek){
            double[] res = (double[]) fcnMoltTiming.calculate(temperature);
            moltIndicator    += dt/DAY_SECS*res[0];
            meanStageDuration = res[1];
            if (Double.isNaN(moltIndicator)|Double.isInfinite(moltIndicator)){
                String msg = "NaN or Inf detected in updateMoltIndicator\n"
                           + "for "+typeName+" "+id+". IntermoltDurationFunction_Belehradek parameter values are\n"
                           + "\ta = "+fcnMoltTiming.getParameter(IntermoltDurationFunction_Belehradek.PARAM_a).getValueAsString()+"\n"
                           + "\tb = "+fcnMoltTiming.getParameter(IntermoltDurationFunction_Belehradek.PARAM_b).getValueAsString()+"\n"
                           + "\tc = "+fcnMoltTiming.getParameter(IntermoltDurationFunction_Belehradek.PARAM_c).getValueAsString()+"\n";
                throw(new ArithmeticException(msg));
            }
        } else if (fcnMoltTiming instanceof AnnualMoltFunction){
            //TODO: the following logic is incorrect!
            if ((Double)fcnMoltTiming.calculate(false)<globalInfo.getCalendar().getYearDay())
                moltIndicator = 1.0;
            else
                moltIndicator = 0.0;
            String msg = "Logic for "+fcnMoltTiming.getClass().getSimpleName()+"\n"
                        +"is WRONG!! updateMoltIndicator for "+typeName+".";
            throw(new ArithmeticException(msg));
        } else if (fcnMoltTiming instanceof FixedDurationFunction){
            if (ageInStage>=(Double)fcnMoltTiming.calculate(false))
                moltIndicator = 1.0;
            else
                moltIndicator = 0.0;
        } else {
            String msg = "Logic for "+fcnMoltTiming.getClass().getSimpleName()+"\n"
                        +"is missing from updateMoltIndicator for "+typeName+".";
            throw(new ArithmeticException(msg));
        }
    }

    /**
     * Update the number of individuals associated with this object.
     * 
     * @param dt - time step in seconds
     */
    protected void updateNum(double dt) {
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
                totRate += stageTransRate;
                //apply mortality rate to previous number transitioning and
                //add in new transitioners
                numTrans = numTrans*Math.exp(-dt*mortalityRate/DAY_SECS)+
                        (stageTransRate/totRate)*number*(1-Math.exp(-dt*totRate/DAY_SECS));
            }
            number = number*Math.exp(-dt*totRate/DAY_SECS);
            if(number<0.01){ //TODO: replace this with parameter
                active=false;alive=false;number=number+numTrans;//TODO: ??
            }
        }
    }
    
    /**
     * Updates position-related information.
     * 
     * <pre>
     * This changes the following variables:
         * <code>bathym<\code>, bathymetric depth (m)
         * <code>depth<\code>, total depth (m)
         * <code>lat<\code>
         * <code>lon<\code>
         * <code>gridCellID<\code>
         * <code>track<\code>
     * </pre>
     * 
     * @param pos double[] in ROMS grid coordinates
     */
    protected void updatePosition(double[] pos) {
        bathym     = i3d.interpolateBathymetricDepth(pos);
        depth      = -i3d.calcZfromK(pos[0],pos[1],pos[2]);
        lat        = i3d.interpolateLat(pos);
        lon        = i3d.interpolateLon(pos);
        gridCellID = ""+Math.round(pos[0])+"_"+Math.round(pos[1]);
        updateTrack();
    }
    
    /**
     * Updates environmental variables based on input position.
     * 
     * <pre>
     * This changes the following variables:
         * <code>temperature<\code>
         * <code>salinity<\code>
     * </pre>
     * 
     * @param pos double[] in ROMS grid coordinates
     */
    protected void updateEnvVars(double[] pos) {
        temperature = i3d.interpolateTemperature(pos);
        salinity    = i3d.interpolateSalinity(pos);
    }

}
