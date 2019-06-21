/*
 * AbstractBenthicStage.java
 */

package disMELS.IBMs.SnowCrab;

import com.vividsolutions.jts.geom.Coordinate;
import com.wtstockhausen.utils.RandomNumberGenerator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import wts.models.DisMELS.framework.GlobalInfo;
import wts.models.DisMELS.framework.LHS_Factory;
import wts.models.DisMELS.framework.LifeStageAttributesInterface;
import wts.models.DisMELS.framework.LifeStageInterface;
import wts.models.DisMELS.framework.LifeStageParametersInterface;
import wts.roms.gis.AlbersNAD83;
import wts.roms.model.Interpolator3D;
import wts.roms.model.LagrangianParticle;

/**
 * Abstract class for snow crab benthic life stages.
 */
public abstract class AbstractBenthicStage implements LifeStageInterface {
    
    //Static fields
    /** boolean flag indicating no advection by currents (= true) */
    public static final boolean noAdvection = true;
    /** ROMS 3d interpolator object */
    protected static Interpolator3D i3d;
    /** Random Number Generator */
    protected static RandomNumberGenerator rng = null;
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
    protected AbstractBenthicStageAttributes atts = null;
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
    
    //fields that reflect attribute values
    /** flag indicating whether individual has been "activated" */
    protected boolean active=false;
    /** flag indicating whether individual is alive */
    protected boolean alive=true;
    /** ocean_time at which indiviudal/this was created */
    protected double  startTime=0;
    /** current ocean_time */
    protected double  time=0;
    /** current latitude, in decimal degrees */
    protected double  lat=0;
    /** current longitude, in decimal degrees */
    protected double  lon=0;
    /** current depth, in meters */
    protected double  depth=0;
    /** current bathymetric depth, in meters */
    protected double  bathym=0;
    /** current ROMS grid cell, as a string */
    protected String  gridCellID="";
    /** total age, in days */
    protected double  age=0;
    /** age in life stage, in days */
    protected double  ageInStage=0;
    /** number of individuals represented */
    protected double  number=0;
    
    /** instar */
    protected int instar = 1;
    /** age in instar, in days */
    protected double ageInInstar=0;
    /** molt indicator */
    protected double moltIndicator=0;
    /** size (mm carapace width) */
    protected double size = 0;
    /** weight (g) */
    protected double weight = 0;
    /** shell condition */
    protected double shellcond = 0;
    /** shell thickness */
    protected double shellthick = 0;
    /** in situ temperature (deg C) */
    protected double temperature = 0;
    /** in situ salinity */
    protected double salinity = 0;
    /** in situ pH */
    protected double ph = 0; 
    
    
    /**
     * Partially instantiates an instance of a class inheriting from AbstractBenthicStage
     * 
     * Importantly, this sets "NoAdvection" to TRUE for the associated LagrangianParticle object.
     */
    protected AbstractBenthicStage(String typeName) {
        this.typeName = typeName;
        id = LHS_Factory.getNewID();
        lp.setNoAdvection(noAdvection);
        if (rng==null) rng = GlobalInfo.getInstance().getRandomNumberGenerator();
        if (i3d==null) i3d = GlobalInfo.getInstance().getInterpolator3D();
    }
    
    /**
     * Subclasses can use this method to make sure the attributes object for
     * this superclass refers to the one from their class.
     * 
     * @param subAtts - the attributes object for the subclass
     */
    protected final void setAttributesFromSubClass(AbstractBenthicStageAttributes subAtts){
        atts = subAtts;
    }
    
    /**
     * Subclasses can use this method to make sure the attributes object from
     * this superclass refers to the one from their class.
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
     * Gets the LagrangianParticle instance associated with this object.
     * 
     * @return 
     */
    @Override
    public LagrangianParticle getLagrangianParticle() {
        return lp;
    }
    
    /**
     * Sets the lagrangian particle representation for the instance to a clone  
     * of newLP. noAdvection is set to true on the cloned LP to ensure consistency
     * with motion of benthic life stages.
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
    public abstract String getReport();

     /**
     * Returns the report header for the implementing class as a CSV formatted string.
     *
     * @return - the header names as a csv-formatted String
     */
    @Override
   public abstract String getReportHeader();
    
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
     * If newAtts inherits from AbstractBenthicStageAttributes, then additional attributes are copied.
     * 
     *  NOTE: This changes the this object's current Instance field "id" to the one from newAtts.
     * 
     * This DOES NOT call updateVariables() to update the variables with values from the attributes object.
     * Implementing subclasses should call super.updateVariables() to do so.
     * 
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
        //track?
        
        key = LifeStageAttributesInterface.PROP_active;     atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_alive;      atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_age;        atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_ageInStage; atts.setValue(key,newAtts.getValue(key));
        key = LifeStageAttributesInterface.PROP_number;     atts.setValue(key,newAtts.getValue(key));
        
        if (newAtts instanceof AbstractBenthicStageAttributes){
            key = AbstractBenthicStageAttributes.PROP_instar;        atts.setValue(key,newAtts.getValue(key));
            key = AbstractBenthicStageAttributes.PROP_ageInInstar;   atts.setValue(key,newAtts.getValue(key));
            key = AbstractBenthicStageAttributes.PROP_moltindicator; atts.setValue(key,newAtts.getValue(key));
            key = AbstractBenthicStageAttributes.PROP_size;          atts.setValue(key,newAtts.getValue(key));
            key = AbstractBenthicStageAttributes.PROP_weight;        atts.setValue(key,newAtts.getValue(key));
            key = AbstractBenthicStageAttributes.PROP_shellcond;     atts.setValue(key,newAtts.getValue(key));
            key = AbstractBenthicStageAttributes.PROP_shellthick;    atts.setValue(key,newAtts.getValue(key));
        }
        
        id = atts.getValue(LifeStageAttributesInterface.PROP_id, id);
    }
    
    /**
     * Updates attribute values defined for this abstract class. Subclasses should
     * override this method, possibly calling super.updateAttributes().
     */
    protected void updateAttributes() {
        //note that the following do not need to be updated
        //  id, parentID, origID, startTime, horizType, vertType
        //id
        //parentID
        //origID
        //startTime
        atts.setValue(AbstractBenthicStageAttributes.PROP_time,time);
        //horizType
        //vertType
        atts.setValue(AbstractBenthicStageAttributes.PROP_horizPos1,lon);
        atts.setValue(AbstractBenthicStageAttributes.PROP_horizPos2,lat);
        atts.setValue(AbstractBenthicStageAttributes.PROP_vertPos,depth);
        atts.setValue(AbstractBenthicStageAttributes.PROP_bathym,bathym);
        atts.setValue(AbstractBenthicStageAttributes.PROP_gridCellID,gridCellID);
        //track ??
        atts.setValue(AbstractBenthicStageAttributes.PROP_active,active);
        atts.setValue(AbstractBenthicStageAttributes.PROP_alive,alive);
        atts.setValue(AbstractBenthicStageAttributes.PROP_age,age);
        atts.setValue(AbstractBenthicStageAttributes.PROP_ageInStage,ageInStage);
        atts.setValue(AbstractBenthicStageAttributes.PROP_number,number);
        
        atts.setValue(AbstractBenthicStageAttributes.PROP_instar,instar);
        atts.setValue(AbstractBenthicStageAttributes.PROP_ageInInstar,ageInInstar);
        atts.setValue(AbstractBenthicStageAttributes.PROP_moltindicator,moltIndicator);
        atts.setValue(AbstractBenthicStageAttributes.PROP_size,size);
        atts.setValue(AbstractBenthicStageAttributes.PROP_weight,weight);
        atts.setValue(AbstractBenthicStageAttributes.PROP_shellcond,shellcond);
        atts.setValue(AbstractBenthicStageAttributes.PROP_shellthick,shellthick);
        atts.setValue(AbstractBenthicStageAttributes.PROP_salinity,salinity);
        atts.setValue(AbstractBenthicStageAttributes.PROP_temperature,temperature);
        atts.setValue(AbstractBenthicStageAttributes.PROP_ph,ph);
    }

    /**
     * Updates local variables from the attributes.  
     * The following are NOT updated here:
     *  id, parentID, origID, hType, vType
     */
    protected void updateVariables() {
        //id
        //parentID
        //origID
        startTime  = atts.getValue(AbstractBenthicStageAttributes.PROP_startTime,startTime);
        time       = atts.getValue(AbstractBenthicStageAttributes.PROP_time,time);
        //horizType
        //vertType
        lon        = atts.getValue(AbstractBenthicStageAttributes.PROP_horizPos1,lon);
        lat        = atts.getValue(AbstractBenthicStageAttributes.PROP_horizPos2,lat);
        depth      = atts.getValue(AbstractBenthicStageAttributes.PROP_vertPos,depth);
        bathym     = atts.getValue(AbstractBenthicStageAttributes.PROP_bathym,bathym);
        gridCellID = atts.getValue(AbstractBenthicStageAttributes.PROP_gridCellID,gridCellID);
        //track
        active     = atts.getValue(AbstractBenthicStageAttributes.PROP_active,active);
        alive      = atts.getValue(AbstractBenthicStageAttributes.PROP_alive,alive);
        age        = atts.getValue(AbstractBenthicStageAttributes.PROP_age,age);
        ageInStage = atts.getValue(AbstractBenthicStageAttributes.PROP_ageInStage,ageInStage);
        number     = atts.getValue(AbstractBenthicStageAttributes.PROP_number,number);
        
        instar        = atts.getValue(AbstractBenthicStageAttributes.PROP_instar,instar);
        ageInInstar   = atts.getValue(AbstractBenthicStageAttributes.PROP_ageInInstar,ageInInstar);
        moltIndicator = atts.getValue(AbstractBenthicStageAttributes.PROP_moltindicator,moltIndicator);
        size          = atts.getValue(AbstractBenthicStageAttributes.PROP_size,size);
        weight        = atts.getValue(AbstractBenthicStageAttributes.PROP_weight,weight);
        shellcond     = atts.getValue(AbstractBenthicStageAttributes.PROP_shellcond,shellcond);
        shellthick    = atts.getValue(AbstractBenthicStageAttributes.PROP_shellthick,shellthick);
        salinity      = atts.getValue(AbstractBenthicStageAttributes.PROP_salinity,salinity);
        temperature   = atts.getValue(AbstractBenthicStageAttributes.PROP_temperature,temperature);
        ph            = atts.getValue(AbstractBenthicStageAttributes.PROP_ph,ph);
    }
    
}
