/*
 * MaleAdolescentParameters.java
 *
 * Created on October 17, 2017.
 *
 */

package wts.models.DisMELS.IBMs.SnowCrab.MaleAdolescent;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    public static final int numParams = 13;
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
    
    /** the number of IBMFunction categories defined in the class */
    public static final int numFunctionCats = 4;
    public static final String FCAT_Growth         = "growth";
    public static final String FCAT_Mortality      = "mortality";
    public static final String FCAT_Maturity       = "maturity";
    public static final String FCAT_Fecundity      = "fecundity";
    
    /** The 'keys' used to store the ibm functions */
    protected static final Set<String> setOfFunctionCategories = new LinkedHashSet<>(2*numFunctionCats);
    /** The 'keys' used to store the ibm parameters */
    protected static final Set<String> setOfParamKeys = new LinkedHashSet<>(2*numParams);
    
    private static final Logger logger = Logger.getLogger(MaleAdolescentParameters.class.getName());
    
    /** Utility field used by bound properties.  */
    private transient PropertyChangeSupport propertySupport;
    
    /**
     * Creates a new instance of AdultStageParameters.
     */
    public MaleAdolescentParameters() {
        super("",numParams,numFunctionCats);
        createMapToValues();
        createMapToSelectedFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * Creates a new instance of AdultStageParameters
     */
    public MaleAdolescentParameters(String typeName) {
        super(typeName,numParams,numFunctionCats);
        createMapToValues();
        createMapToSelectedFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * This creates the basic parameters mapParams.
     */
    @Override
    protected final void createMapToValues() {
        String key;
        key = PARAM_isSuperIndividual;    setOfParamKeys.add(key); mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_horizRWP;             setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_minStageDuration;     setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_maxStageDuration;     setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,365.0));
        key = PARAM_minSizeAtTrans;       setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_meanStageTransDelay;  setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_randomizeTransitions; setOfParamKeys.add(key); mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_firstDaySpawning;     setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_lengthSpawningSeason; setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_isBatchSpawner;       setOfParamKeys.add(key); mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_recoveryPeriod;       setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_meanTimeToSpawn;      setOfParamKeys.add(key); mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_randomizeSpawning;    setOfParamKeys.add(key); mapParams.put(key,new IBMParameterBoolean(key,key,false));
    }

    @Override
    protected final void createMapToSelectedFunctions() {
        //create the set of function category keys for this class
        setOfFunctionCategories.add(FCAT_Growth);
        setOfFunctionCategories.add(FCAT_Mortality);
        setOfFunctionCategories.add(FCAT_Maturity);
        setOfFunctionCategories.add(FCAT_Fecundity);
        
        //create the map from function categories to potential functions in each category
        String cat; Map<String,IBMFunctionInterface> mapOfPotentialFunctions; IBMFunctionInterface ifi;
        cat = FCAT_Growth;  
        mapOfPotentialFunctions = new LinkedHashMap<>(2); mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new vonBertalanffyGrowthFunction(); mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Mortality;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
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
        
        cat = FCAT_Maturity;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new ConstantFunction(); 
            ifi.setFunctionName("Constant fraction mature");
            ifi.setDescription("constant fraction mature");
            ifi.setParameterDescription(ConstantFunction.PARAM_constant,"constant fraction mature");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new LogisticFunction(); 
            ifi.setFunctionName("Logistic maturity function");
            ifi.setDescription("Logistic (ize-specific) maturity function");
            ifi.setParameterDescription(LogisticFunction.PARAM_x50,"size at 50% maturity (cm)");
            ifi.setParameterDescription(LogisticFunction.PARAM_slope,"slope at 50% maturity (1/cm)");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Fecundity;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
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
     * Returns the IBMFunctionInterface object corresponding to the 
     * given category and function key. 
     * 
     * As a DEFAULT IMPLEMENTATION, this method throws an UnsupportedOperationException 
     * 
     * This method SHOULD BE OVERRIDDEN by subclasses that use IBMFunctions.
     * 
     * @param cat  - usage category 
     * @param name - function name
     * @return   - the model function
     */
    @Override
    public IBMFunctionInterface getIBMFunction(String cat, String key){
        return mapOfPotentialFunctionsByCategory.get(cat).get(key);    
    }

    @Override
    public Set<String> getIBMFunctionCategories(){
        return mapOfPotentialFunctionsByCategory.keySet();
    }
    
    @Override
    public Set<String> getIBMFunctionNamesByCategory(String cat){
        return mapOfPotentialFunctionsByCategory.get(cat).keySet();
    }
    
    @Override
   public void selectIBMFunctionForCategory(String cat, String key){
        IBMFunctionInterface ifi = mapOfPotentialFunctionsByCategory.get(cat).get(key);
        mapOfSelectedFunctionsByCategory.put(cat,ifi);
    }

    @Override
    public Set<String> getIBMParameterNames() {
        return setOfParamKeys;
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
            for (String pKey: setOfParamKeys) {
                clone.setValue(pKey,this.getValue(pKey));
            }
            for (String fcKey: setOfFunctionCategories) {
                Set<String> fKeys = this.getIBMFunctionNamesByCategory(fcKey);
                IBMFunctionInterface sfi = this.getSelectedIBMFunctionForCategory(fcKey);
                for (String fKey: fKeys){
                    IBMFunctionInterface tfi = this.getIBMFunction(fcKey, fKey);
                    IBMFunctionInterface cfi = clone.getIBMFunction(fcKey,fKey);
                    Set<String> pKeys = tfi.getParameterNames();
                    for (String pKey: pKeys) {
                        cfi.setParameterValue(pKey, tfi.getParameter(pKey).getValue());
                    }
                    if (sfi==tfi) clone.selectIBMFunctionForCategory(fcKey, fKey);
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
        for (String key: setOfParamKeys) params.setValueFromString(key,strv[c++]);
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
        for (String key: setOfParamKeys) str = str+cc+getIBMParameter(key).getValueAsString();
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
        for (String key: setOfParamKeys) str = str+cc+key;
        return str;
    }

    /**
     * Gets the parameter keys.
     * 
     * @return - keys as String array.
     */
    @Override
    public String[] getKeys(){
        String[] strv = new String[setOfParamKeys.size()];
        return setOfParamKeys.toArray(strv);
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
