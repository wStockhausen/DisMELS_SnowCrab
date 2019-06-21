/*
 * MaleAdolescentParameters.java
 *
 * Created on October 17, 2017.
 *
 */

package disMELS.IBMs.SnowCrab.MaleAdolescent;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Growth.ExponentialGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Growth.LinearGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Growth.vonBertalanffyGrowthFunction;
import wts.models.DisMELS.IBMFunctions.Miscellaneous.ConstantFunction;
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
import SnowCrabFunctions.CrabBioenergeticsGrowthFunction;
import SnowCrabFunctions.ExCostFunction;
import SnowCrabFunctions.IntermoltIntegratorFunction;
import SnowCrabFunctions.MoltIncrementFunction;
import SnowCrabFunctions.MaturityOgiveFunction;

/**
 * DisMELS class representing parameters for adolescent male snow crab.
 * 
 * This class uses the IBMParameters/IBMFunctions approach to specifying stage-specific parameters.
 * 
 * @author William Stockhausen
 */
@ServiceProvider(service=LifeStageParametersInterface.class)
public class MaleAdolescentParameters extends AbstractLHSParameters {
    
    public static final long serialVersionUID = 1L;
    
    /** the number of IBMParameter objects defined in the class */
    public static final int numParams = 12;
    public static final String PARAM_isSuperIndividual     = "is a super-individual?";
    public static final String PARAM_horizRWP              = "horizontal random walk parameter [m^2]/[s]";
    public static final String PARAM_minStageDuration      = "min stage duration [d]";
    public static final String PARAM_maxStageDuration      = "max stage duration [d]";
    public static final String PARAM_minSizeAtTrans        = "min size at transition (cm)";
    public static final String PARAM_meanStageTransDelay   = "mean stage transition delay (d)";
    public static final String PARAM_randomizeTransitions  = "randomize stage transitions?";
    public static final String PARAM_percLostWeight        = "maximum percentage of body weight that can be lost";
    public static final String PARAM_maxStarvTime          = "maximum time crab can starve before dying [s]";
    public static final String PARAM_aLengthWeight          = "intercept of length-weight relationship";
    public static final String PARAM_bLengthWeight          = "slope of length-weight relationship";
    public static final String PARAM_confInt                = "mean width of length-weight confidence interval";
    
    /** the number of IBMFunction categories defined in the class */
    public static final int numFunctionCats = 7;
    public static final String FCAT_Growth             = "growth";
    public static final String FCAT_Mortality          = "mortality";
    public static final String FCAT_SwimmingSpeed      = "swimming speed";
    public static final String FCAT_Molt                = "molt increment";
    public static final String FCAT_MoltTiming      = "intermolt period";
    public static final String FCAT_ExCost = "exuviae cost";
    public static final String FCAT_Maturity = "maturity";
    
    private static final Logger logger = Logger.getLogger(MaleAdolescentParameters.class.getName());
    
    /**
     * Creates a new instance of AdultStageParameters.
     */
    public MaleAdolescentParameters() {
        super("",numParams,numFunctionCats);
        createMapToParameters();
        createMapToPotentialFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * Creates a new instance of AdultStageParameters
     */
    public MaleAdolescentParameters(String typeName) {
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
        key = PARAM_minStageDuration;     mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_maxStageDuration;     mapParams.put(key,new IBMParameterDouble(key,key,365.0));
        key = PARAM_minSizeAtTrans;       mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_meanStageTransDelay;  mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_randomizeTransitions; mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_maxStarvTime;         mapParams.put(key, new IBMParameterDouble(key, key, 518400));
        key = PARAM_percLostWeight;       mapParams.put(key, new IBMParameterDouble(key, key, 0.9385));
        key = PARAM_aLengthWeight;        mapParams.put(key, new IBMParameterDouble(key, key, 0.00005));
        key = PARAM_bLengthWeight;        mapParams.put(key, new IBMParameterDouble(key, key, 2.903));
        key = PARAM_confInt;              mapParams.put(key, new IBMParameterDouble(key, key, 0.127));
    }

    @Override
    protected final void createMapToPotentialFunctions() {
        //create the map from function categories to potential functions in each category
        String cat; 
        Map<String,IBMFunctionInterface> mapOfPotentialFunctions; 
        IBMFunctionInterface ifi;
        
        cat = FCAT_Growth;  
        mapOfPotentialFunctions = new LinkedHashMap<>(10); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new vonBertalanffyGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ExponentialGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new LinearGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new CrabBioenergeticsGrowthFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ConstantFunction();  //generic function, so change defaults
            ifi.setFunctionName("Constant growth rate"); 
            ifi.setDescription("Constant growth rate [mm/day]"); 
            ifi.setParameterDescription(ConstantFunction.PARAM_constant,"Constant growth rate [mm/day]");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Mortality;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new ConstantMortalityRate(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new TemperatureDependentMortalityRate_Houde1989(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_SwimmingSpeed;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new PowerLawSwimmingSpeedFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ConstantMovementRateFunction(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
            
       cat = FCAT_Molt;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
       ifi = new MoltIncrementFunction();
               mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
               
       cat = FCAT_MoltTiming;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
       ifi = new IntermoltIntegratorFunction();
               mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
               
       cat = FCAT_ExCost;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
       ifi = new ExCostFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
            
       cat = FCAT_Maturity;
       mapOfPotentialFunctions = new LinkedHashMap<>(2); 
       mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
       ifi = new MaturityOgiveFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
    }
    
    /**
     * Returns a deep copy of the instance.  Values are copied.  
     * Any listeners on 'this' are not(?) copied, so these need to be hooked up.
     * @return - the clone.
     */
    @Override
    public Object clone() {
        MaleAdolescentParameters clone = null;
        try {
            clone = (MaleAdolescentParameters) super.clone();
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
    public MaleAdolescentParameters createInstance(final String[] strv) {
        int c = 0;
        MaleAdolescentParameters params = new MaleAdolescentParameters(strv[c++]);
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

    /**
     * Gets the parameter keys.
     * 
     * @return - keys as String array.
     */
    @Override
    public String[] getKeys(){
        String[] strv = new String[mapParams.keySet().size()];
        return mapParams.keySet().toArray(strv);
    }

    /**
     * Sets parameter value identified by the key and fires a property change.
     * @param key   - key identifying attribute to be set
     * @param value - value to set
     */
    @Override
    public void setValue(String key, Object value) {
        if (mapParams.containsKey(key)) {
            IBMParameter p = mapParams.get(key);
            Object old = p.getValue();
            p.setValue(value);
            propertySupport.firePropertyChange(key,old,value);
        }
    }

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }
}
