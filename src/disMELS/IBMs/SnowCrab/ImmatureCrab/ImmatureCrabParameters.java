/*
 * ImmatureCrabParameters.java
 *
 * Created on September 26, 2017.
 *
 */

package disMELS.IBMs.SnowCrab.ImmatureCrab;

import SnowCrabFunctions.CrabBioenergeticsGrowthFunction;
import SnowCrabFunctions.MoltIncrementFunction;
import SnowCrabFunctions.ExCostFunction;
import SnowCrabFunctions.IntermoltIntegratorFunction;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Growth.ExponentialGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Growth.LinearGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Growth.vonBertalanffyGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import wts.models.DisMELS.IBMFunctions.SwimmingBehavior.ConstantMovementRateFunction;
import wts.models.DisMELS.IBMFunctions.SwimmingBehavior.PowerLawSwimmingSpeedFunction;
import wts.models.DisMELS.framework.AbstractLHSParameters;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameter;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterBoolean;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterDouble;
import wts.models.DisMELS.framework.LifeStageParametersInterface;
/**
 * DisMELS class representing parameters for Bering Sea snow crab
 * 
 * This class uses the IBMParameters/IBMFunctions approach to specifying stage-specific parameters.
 * 
 * @author William Stockhausen
 */
@ServiceProvider(service=LifeStageParametersInterface.class)
public class ImmatureCrabParameters extends AbstractLHSParameters {
    
    public static final long serialVersionUID = 1L;
    
    /** the number of IBMParameter objects defined in the class */
    public static final int numParams = 11;
    public static final String PARAM_isSuperIndividual      = "is a super-individual?";
    public static final String PARAM_horizRWP               = "horizontal random walk parameter [m^2]/[s]";
    public static final String PARAM_minStageDuration       = "min stage duration [d]";
    public static final String PARAM_maxStageDuration       = "max stage duration [d]";
    public static final String PARAM_minSize                = "min size before metamorphosis (mm)";
    public static final String PARAM_minWeight              = "min weight before metamorphosis (g)";
    public static final String PARAM_randomizeTransitions   = "randomize transitions?";
    public static final String PARAM_initialSize            = "initial size in stage (mm)";
    public static final String PARAM_initialWeight          = "initial weight in stage (g)";
    public static final String PARAM_sCost                  = "number of days of exuviae cost";
    public static final String PARAM_maxStarvTime           = "maximum time a crab can starve [s]";
    public static final String PARAM_percLostWeight         = "maximum percentage of weight that can be lost";
    public static final String PARAM_aLengthWeight          = "intercept of length-weight relationship";
    public static final String PARAM_bLengthWeight          = "slope of length-weight relationship";
    public static final String PARAM_confInt                = "mean width of length-weight confidence interval";
    
    
    /** the number of IBMFunction categories defined in the class */
    public static final int numFunctionCats = 6;
    public static final String FCAT_MoltIncrement     = "molt increment";
    public static final String FCAT_IntermoltDuration = "intermolt duration";
    public static final String FCAT_Growth            = "growth in weight";
    public static final String FCAT_ExuviaeCost       = "exuviae cost";
    public static final String FCAT_Mortality         = "mortality";
    public static final String FCAT_Movement          = "movement";
    
    private static final Logger logger = Logger.getLogger(ImmatureCrabParameters.class.getName());
    
    /**
     * Creates a new instance of EggStageParameters.
     */
    public ImmatureCrabParameters() {
        super("",numParams,numFunctionCats);
        createMapToParameters();
        createMapToPotentialFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * Creates a new instance of EggStageParameters
     */
    public ImmatureCrabParameters(String typeName) {
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
        key = PARAM_isSuperIndividual;    mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_horizRWP;             mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_initialSize;          mapParams.put(key,new IBMParameterDouble(key,key,4.4));
        key = PARAM_initialWeight;        mapParams.put(key,new IBMParameterDouble(key,key,0.01));
        key = PARAM_maxStageDuration;     mapParams.put(key,new IBMParameterDouble(key,key,365.0));
        key = PARAM_minWeight;            mapParams.put(key,new IBMParameterDouble(key,key,0.001));
        key = PARAM_randomizeTransitions; mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_sCost;                mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_maxStarvTime;         mapParams.put(key, new IBMParameterDouble(key, key, 514800));
        key = PARAM_percLostWeight;       mapParams.put(key, new IBMParameterDouble(key, key, 0.9385));
        key = PARAM_aLengthWeight;        mapParams.put(key, new IBMParameterDouble(key, key, .00005));
        key = PARAM_bLengthWeight;        mapParams.put(key, new IBMParameterDouble(key, key, 2.903));
        key = PARAM_confInt;              mapParams.put(key, new IBMParameterDouble(key, key, .104));
    }

    @Override
    protected final void createMapToPotentialFunctions() {
        //create the map from function categories to potential functions in each category
        String cat; 
        Map<String,IBMFunctionInterface> mapOfPotentialFunctions; 
        IBMFunctionInterface ifi;
        
       cat = FCAT_MoltIncrement;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
       ifi = new MoltIncrementFunction();
               mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
               
       cat = FCAT_IntermoltDuration;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new IntermoltIntegratorFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
               
        cat = FCAT_Growth;  
        mapOfPotentialFunctions = new LinkedHashMap<>(10); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new CrabBioenergeticsGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new LinearGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ExponentialGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new vonBertalanffyGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Mortality;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new ConstantMortalityRate(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new TemperatureDependentMortalityRate_Houde1989(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Movement;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new PowerLawSwimmingSpeedFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ConstantMovementRateFunction(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
            
       cat = FCAT_ExuviaeCost;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
       ifi = new ExCostFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
    }
    
    /**
     * Returns a deep copy of the instance.  Values are copied.  
     * Any listeners on 'this' are not(?) copied, so these need to be hooked up.
     * @return - the clone.
     */
    @Override
    public Object clone() {
        ImmatureCrabParameters clone = null;
        try {
            clone = (ImmatureCrabParameters) super.clone();
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
     *  Creates an instance of ImmatureCrabParameters.
     *
     *@param strv - array of values (as Strings) used to create the new instance. 
     *              This should be typeName followed by parameter value (as Strings)
     *              in the same order as the keys.
     */
    @Override
    public ImmatureCrabParameters createInstance(final String[] strv) {
        int c = 0;
        ImmatureCrabParameters params = new ImmatureCrabParameters(strv[c++]);
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
     * This should be overridden by subclasses that add additional parameters, 
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
