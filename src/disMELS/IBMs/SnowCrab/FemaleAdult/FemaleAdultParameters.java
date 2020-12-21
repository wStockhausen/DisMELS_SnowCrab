/*
 * FemaleAdultParameters.java
 *
 * Created on October 17, 2017.
 *
 */

package disMELS.IBMs.SnowCrab.FemaleAdult;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Growth.vonBertalanffyGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Miscellaneous.ConstantFunction;
import wts.models.DisMELS.IBMFunctions.Miscellaneous.LogisticFunction;
import wts.models.DisMELS.IBMFunctions.Miscellaneous.PowerLawFunction;
import wts.models.DisMELS.framework.AbstractLHSParameters;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameter;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterBoolean;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterDouble;
import wts.models.DisMELS.framework.LifeStageParametersInterface;
import SnowCrabFunctions.CrabBioenergeticsGrowthFunction;

/**
 * DisMELS class representing parameters for adolescent female snow crab.
 * 
 * This class uses the IBMParameters/IBMFunctions approach to specifying stage-specific parameters.
 * 
 * @author William Stockhausen
 */
@ServiceProvider(service=LifeStageParametersInterface.class)
public class FemaleAdultParameters extends AbstractLHSParameters {
    
    public static final long serialVersionUID = 1L;
    
    /** the number of IBMParameter objects defined in the class */
    public static final int numParams = 18;
    public static final String PARAM_isSuperIndividual     = "is a super-individual?";
    public static final String PARAM_horizRWP              = "horizontal random walk parameter [m^2]/[s]";
    public static final String PARAM_minStageDuration      = "min stage duration [d]";
    public static final String PARAM_maxStageDuration      = "max stage duration [d]";
    public static final String PARAM_minSizeAtTrans        = "min size at transition (cm)";
    public static final String PARAM_meanStageTransDelay   = "mean stage transition delay (d)";
    public static final String PARAM_randomizeTransitions  = "randomize stage transitions?";
    public static final String PARAM_firstDaySpawning      = "first day-of-year for spawning";
    public static final String PARAM_lengthSpawningSeason  = "length of spawning season (d)";
    public static final String PARAM_isBatchSpawner        = "is batch spawner?";
    public static final String PARAM_recoveryPeriod        = "recovery period after spawning (d)";
    public static final String PARAM_meanTimeToSpawn       = "mean time to spawn after recory (d)?";
    public static final String PARAM_randomizeSpawning     = "randomize spawning?";
    public static final String PARAM_percLostWeight        = "maximum percentage of weight lost [0-1]";
    public static final String PARAM_maxStarvTime          = "maximum amount of time crab can starve before dying (s)";
    public static final String PARAM_aLengthWeight         = "intercept of length-weight relationship";
    public static final String PARAM_bLengthWeight         = "slope of length-weight relationship";
    public static final String PARAM_walkSpeed             = "speed of crab walking along bottom [m^2]/[s]";
    
    /** the number of IBMFunction categories defined in the class */
    public static final int numFunctionCats = 3;
    public static final String FCAT_Growth         = "growth";
    public static final String FCAT_Mortality      = "mortality";
    public static final String FCAT_Fecundity      = "fecundity";
    
    private static final Logger logger = Logger.getLogger(FemaleAdultParameters.class.getName());
    
    /**
     * Creates a new instance of AdultStageParameters.
     */
    public FemaleAdultParameters() {
        super("",numParams,numFunctionCats);
        createMapToParameters();
        createMapToPotentialFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * Creates a new instance of AdultStageParameters
     */
    public FemaleAdultParameters(String typeName) {
        super(typeName,numParams,numFunctionCats);
        createMapToParameters();
        createMapToPotentialFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * This creates the basic parameters mapParams.
     */
    @Override
    protected final void createMapToParameters() {
        String key;
        key = PARAM_isSuperIndividual;     mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_horizRWP;              mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_minStageDuration;      mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_maxStageDuration;      mapParams.put(key,new IBMParameterDouble(key,key,365.0));
        key = PARAM_minSizeAtTrans;        mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_meanStageTransDelay;   mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_randomizeTransitions;  mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_firstDaySpawning;      mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_lengthSpawningSeason;  mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_isBatchSpawner;        mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_recoveryPeriod;        mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_meanTimeToSpawn;       mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_randomizeSpawning;     mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_percLostWeight;        mapParams.put(key, new IBMParameterDouble(key, key, 0.9385));
        key = PARAM_maxStarvTime;          mapParams.put(key, new IBMParameterDouble(key, key, 518400.0));
        key = PARAM_walkSpeed;             mapParams.put(key, new IBMParameterDouble(key, key, 0.1));
    }

    @Override
    protected final void createMapToPotentialFunctions() {
        //create the map from function categories to potential functions in each category
        String cat; 
        Map<String,IBMFunctionInterface> mapOfPotentialFunctions; 
        IBMFunctionInterface ifi;
        
        cat = FCAT_Growth;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new vonBertalanffyGrowthFunction(); mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new CrabBioenergeticsGrowthFunction(); mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Mortality;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new ConstantFunction();  //generic function, so change defaults
            ifi.setFunctionName("Constant mortality rate"); 
            ifi.setDescription("Constant mortality rate [1/day]"); 
            ifi.setParameterDescription(ConstantFunction.PARAM_constant,"Constant mortality rate [1/day]");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new PowerLawFunction(); //generic function, so change defaults
            ifi.setFunctionName("Size-specific mortality function");
            ifi.setDescription("size-specific mortality function [1/day]");
            ifi.setParameterDescription(PowerLawFunction.PARAM_stdVal,"mortality rate at standard size (z0) [1/day]");
            ifi.setParameterDescription(PowerLawFunction.PARAM_stdX,"standard size z0 [cm]");
            ifi.setParameterDescription(PowerLawFunction.PARAM_exponent,"exponent (<0 for decreasing function of size)");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Fecundity;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new ConstantFunction(); 
            ifi.setFunctionName("Constant fecundity");
            ifi.setDescription("constant fecundity");
            ifi.setParameterDescription(ConstantFunction.PARAM_constant,"constant fecundity");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new PowerLawFunction(); 
            ifi.setFunctionName("Power law fecundity");
            ifi.setDescription("power law fecundity");
            ifi.setParameterDescription(PowerLawFunction.PARAM_stdVal,"fecundity at standard size (z0)");
            ifi.setParameterDescription(PowerLawFunction.PARAM_stdX,"standard size z0 [cm]");
            ifi.setParameterDescription(PowerLawFunction.PARAM_exponent,"exponent (>0 for increasing function of size)");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);        
    }
    /**
     * Returns a deep copy of the instance.  Values are copied.  
     * Any listeners on 'this' are not(?) copied, so these need to be hooked up.
     * @return - the clone.
     */
    @Override
    public Object clone() {
        FemaleAdultParameters clone = null;
        try {
            clone = (FemaleAdultParameters) super.clone();
            for (String pKey: mapParams.keySet()) {
                clone.setValue(pKey,this.getValue(pKey));
            }
            for (String fcKey: mapOfPotentialFunctionsByCategory.keySet()) {
                Set<String> fKeys = this.getIBMFunctionKeysByCategory(fcKey);
                IBMFunctionInterface sfi = this.getSelectedIBMFunctionForCategory(fcKey);
                for (String fKey: fKeys){
                    IBMFunctionInterface tfi = this.getIBMFunction(fcKey, fKey);
                    IBMFunctionInterface cfi = clone.getIBMFunction(fcKey,fKey);
                    Set<String> pKeys = tfi.getParameterNames();
                    for (String pKey: pKeys) {
                        cfi.setParameterValue(pKey, tfi.getParameter(pKey).getValue());
                    }
                    if (sfi==tfi) clone.setSelectedIBMFunctionForCategory(fcKey, fKey);
                }
            }
            clone.propertySupport = new PropertyChangeSupport(clone);
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return clone;
    }

    /**
     *  Creates an instance of SimplePelagicLHSParameters.
     *
     *@param strv - array of values (as Strings) used to create the new instance. 
     *              This should be typeName followed by parameter value (as Strings)
     *              in the same order as the keys.
     */
    @Override
    public FemaleAdultParameters createInstance(final String[] strv) {
        int c = 0;
        FemaleAdultParameters params = new FemaleAdultParameters(strv[c++]);
        for (String key: mapParams.keySet()) params.setValueFromString(key,strv[c++]);
        return params;
    }
    
    private void setValueFromString(String key, String value) throws NumberFormatException {
        IBMParameter param = mapParams.get(key);
        param.parseValue(value);
        setValue(key,param.getValue());
    }

    /**
     * Returns a CSV string representation of the parameter values.
     * This method should be overriden by subclasses that add additional parameters, 
     * possibly calling super.getCSV() to get an initial csv string to which 
     * additional field values could be appended.
     * 
     *@return - CSV string parameter values
     */
    @Override
    public String getCSV() {
        String str = typeName;
        for (String key: mapParams.keySet()) str = str+cc+getIBMParameter(key).getValueAsString();
        return str;
    }
                
    /**
     * Returns the comma-delimited string corresponding to the parameters
     * to be used as a header for a csv file.  
     * This should be overriden by subclasses that add additional parameters, 
     * possibly calling super.getCSVHeader() to get an initial header string 
     * to which additional field names could be appended.
     * Use getCSV() to get the string of actual parameter values.
     *
     *@return - String of CSV header names
     */
    @Override
    public String getCSVHeader() {
        String str = "LHS type name";
        for (String key: mapParams.keySet()) str = str+cc+key;
        return str;
    }
}
