/*
 * ExtrudedEggMassParameters.java
 */

package disMELS.IBMs.SnowCrab.EggMassExtruded;

import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Miscellaneous.ConstantFunction;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import wts.models.DisMELS.framework.AbstractLHSParameters;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterBoolean;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterDouble;
import wts.models.DisMELS.framework.LifeStageParametersInterface;

/**
 * DisMELS class encapsulating parameters for an extruded clutch of eggs on the abdomen of a post-adolescent
 * female snow crab.
 * 
 * When the egg mass develops, it hatches into 1st-stage zooea.
 * 
 * This class uses the IBMParameters/IBMFunctions approach to specifying stage-specific parameters.
 */
@ServiceProvider(service=LifeStageParametersInterface.class)
public class ExtrudedEggMassParameters extends AbstractLHSParameters {
    
    public static final long serialVersionUID = 1L;
    
    /** the number of IBMParameter objects defined in the class */
    public static final int numParams = 7;
    public static final String PARAM_isSuperIndividual      = "is a super-individual?";
    public static final String PARAM_minStageDuration       = "min stage duration [d]";
    public static final String PARAM_maxStageDuration       = "max stage duration [d]";
    public static final String PARAM_minDevStage            = "min development stage before metamorphosis";
    public static final String PARAM_maxDevStage            = "max development stage before death";
    public static final String PARAM_randomizeTransitions   = "randomize transitions?";
    public static final String PARAM_initialDevStage        = "initial development stage [1-19]";
    
    
    /** the number of IBMFunction categories defined in the class */
    public static final int numFunctionCats = 2;
    public static final String FCAT_Development      = "stage development";
    public static final String FCAT_Mortality        = "mortality";
    
    private static final Logger logger = Logger.getLogger(ExtrudedEggMassParameters.class.getName());
    
    /**
     * Creates a new instance of EggStageParameters.
     */
    public ExtrudedEggMassParameters() {
        super("",numParams,numFunctionCats);
        createMapToParameters();
        createMapToPotentialFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * Creates a new instance of EggStageParameters
     */
    public ExtrudedEggMassParameters(String typeName) {
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
        key = PARAM_minStageDuration;      mapParams.put(key,new IBMParameterDouble(key,key,0.0));
        key = PARAM_maxStageDuration;      mapParams.put(key,new IBMParameterDouble(key,key,365.0));
        key = PARAM_minDevStage;           mapParams.put(key,new IBMParameterDouble(key,key,19.0));
        key = PARAM_maxDevStage;           mapParams.put(key,new IBMParameterDouble(key,key,20.0));
        key = PARAM_randomizeTransitions;  mapParams.put(key,new IBMParameterBoolean(key,key,false));
        key = PARAM_initialDevStage;       mapParams.put(key,new IBMParameterDouble(key,key,1.0));
    }

    @Override
    protected final void createMapToPotentialFunctions() {
        //create the map from function categories to potential functions in each category
        String cat; 
        Map<String,IBMFunctionInterface> mapOfPotentialFunctions; 
        IBMFunctionInterface ifi;
        
        cat = FCAT_Development;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new EggDevelopmentFunction(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ConstantFunction();  //generic function, so change defaults
            ifi.setFunctionName("Constant development rate"); 
            ifi.setDescription("Constant development rate [stage/day]"); 
            ifi.setParameterDescription(ConstantFunction.PARAM_constant,"Constant development rate [stage/day]");
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Mortality;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new ConstantMortalityRate(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new TemperatureDependentMortalityRate_Houde1989(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
    }
    
    /**
     * Returns a deep copy of the instance.  Values are copied.  
     * Any listeners on 'this' are not(?) copied, so these need to be hooked up.
     * @return - the clone.
     */
    @Override
    public Object clone() {
        ExtrudedEggMassParameters clone = null;
        try {
            clone = (ExtrudedEggMassParameters) super.clone();
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
     *  Creates an instance of this class.
     *
     *@param strv - array of values (as Strings) used to create the new instance. 
     *              This should be typeName followed by parameter value (as Strings)
     *              in the same order as the keys.
     */
    @Override
    public ExtrudedEggMassParameters createInstance(final String[] strv) {
        throw new UnsupportedOperationException();
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
