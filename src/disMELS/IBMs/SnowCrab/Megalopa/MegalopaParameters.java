/*
 * MegalopaParameters.java
 *
 * Created on March 20, 2012
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package disMELS.IBMs.SnowCrab.Megalopa;

import SnowCrabFunctions.AnnualMoltFunction;
import SnowCrabFunctions.FixedDurationFunction;
import SnowCrabFunctions.IntermoltDurationFunction_Belehradek;
import SnowCrabFunctions.IntermoltIntegratorFunction;
import SnowCrabFunctions.MortalityFunction_OuelletAndSteMarie2017;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.IBMFunctions.Mortality.ConstantMortalityRate;
import wts.models.DisMELS.IBMFunctions.Mortality.TemperatureDependentMortalityRate_Houde1989;
import wts.models.DisMELS.IBMFunctions.Movement.DielVerticalMigration_FixedDepthRanges;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedOffBottomAndTempRange;
import wts.models.DisMELS.IBMFunctions.Movement.VerticalMovement_FixedOffBottomRange;
import wts.models.DisMELS.IBMFunctions.SwimmingBehavior.ConstantMovementRateFunction;
import wts.models.DisMELS.framework.AbstractLHSParameters;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameter;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterBoolean;
import wts.models.DisMELS.framework.IBMFunctions.IBMParameterDouble;
import wts.models.DisMELS.framework.LifeStageParametersInterface;

/**
 * DisMELS class representing parameters for snow crab megalopae.
 * 
 * This class uses the IBMParameters/IBMFunctions approach to specifying stage-specific parameters.
 * 
 *  Potential intermolt duration functions          (FCAT_MoltTiming)
 *      IntermoltIntegratorFunction()
 *      AnnualMoltFunction()
 *      FixedDuration()
 *  Potential mortality functions                   (FCAT_Mortality)
 *      ConstantMortalityRate()
 *      TemperatureDependentMortalityRate_Houde1989()
 *  Potential vertical movement functions           (FCAT_VerticalMovement)
 *      VerticalMovement_FixedOffBottomRange
 *      VerticalMovement_FixedOffBottomAndTempRange()
 *      DielVerticalMigration_FixedDepthRanges()
 *  Potential vertical velocity functions           (FCAT_VerticalVelocity)
 *      ConstantMovementRateFunction()
 * 
 * @author William Stockhausen
 */
@ServiceProvider(service=LifeStageParametersInterface.class)
public class MegalopaParameters extends AbstractLHSParameters {
    
    public static final long serialVersionUID = 1L;
    
    /** the number of IBMParameter objects defined in the class */
    public static final int numParams = 9;
    public static final String PARAM_isSuperIndividual      = "is a super-individual?";
    public static final String PARAM_maxDecrease            = "min allowed N/N0";
    public static final String PARAM_horizRWP               = "horizontal random walk parameter [m^2]/[s]";
    public static final String PARAM_maxStageDuration       = "max stage duration [d]";
    public static final String PARAM_minSettlementDepth     = "min settlement depth (m)";
    public static final String PARAM_maxSettlementDepth     = "max settlement depth (m)";
    public static final String PARAM_sexRatio               = "fraction male abundance at 1st benthic instar";
    public static final String PARAM_stageTransRate         = "stage transition rate (1/d)";
    public static final String PARAM_randomizeTransitions   = "randomize transitions?";
    
    
    /** the number of IBMFunction categories defined in the class */
    public static final int numFunctionCats = 4;
    public static final String FCAT_IntermoltDuration = "intermolt duration";
    public static final String FCAT_Mortality         = "mortality";
    public static final String FCAT_VerticalMovement  = "vertical movement";
    public static final String FCAT_VerticalVelocity  = "vertical velocity";
    
    private static final Logger logger = Logger.getLogger(MegalopaParameters.class.getName());
    
    /**
     * Creates a new instance of MeagalopaParameters.
     */
    public MegalopaParameters() {
        super("",numParams,numFunctionCats);
        createMapToParameters();
        createMapToPotentialFunctions();
        propertySupport =  new PropertyChangeSupport(this);
    }
    
    /**
     * Creates a new instance of MegalopaParameters
     */
    public MegalopaParameters(String typeName) {
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
        key = PARAM_maxDecrease;          mapParams.put(key,new IBMParameterDouble(key,key,  0.0));
        key = PARAM_horizRWP;             mapParams.put(key,new IBMParameterDouble(key,key,  0.0));
        key = PARAM_maxStageDuration;     mapParams.put(key,new IBMParameterDouble(key,key,365.0));
        key = PARAM_minSettlementDepth;   mapParams.put(key,new IBMParameterDouble(key,key,  0.0));
        key = PARAM_maxSettlementDepth;   mapParams.put(key,new IBMParameterDouble(key,key, 50.0));
        key = PARAM_sexRatio;             mapParams.put(key,new IBMParameterDouble(key,key,  0.5));
        key = PARAM_stageTransRate;             mapParams.put(key,new IBMParameterDouble(key,key,  0.0));
        key = PARAM_randomizeTransitions; mapParams.put(key,new IBMParameterBoolean(key,key,false));
    }

    @Override
    protected final void createMapToPotentialFunctions() {
        //create the map from function categories to potential functions in each category
        String cat; 
        Map<String,IBMFunctionInterface> mapOfPotentialFunctions; 
        IBMFunctionInterface ifi;
        
        cat = FCAT_IntermoltDuration;  
        mapOfPotentialFunctions = new LinkedHashMap<>(8); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new IntermoltIntegratorFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new IntermoltDurationFunction_Belehradek();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new AnnualMoltFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new FixedDurationFunction();
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_Mortality;  
        mapOfPotentialFunctions = new LinkedHashMap<>(6); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new MortalityFunction_OuelletAndSteMarie2017(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new ConstantMortalityRate(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new TemperatureDependentMortalityRate_Houde1989(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_VerticalMovement;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
        ifi = new VerticalMovement_FixedOffBottomRange(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new VerticalMovement_FixedOffBottomAndTempRange(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        ifi = new DielVerticalMigration_FixedDepthRanges(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);
        
        cat = FCAT_VerticalVelocity;  
        mapOfPotentialFunctions = new LinkedHashMap<>(4); 
        mapOfPotentialFunctionsByCategory.put(cat,mapOfPotentialFunctions);
         ifi = new ConstantMovementRateFunction(); 
            mapOfPotentialFunctions.put(ifi.getFunctionName(),ifi);           
    }
    
    /**
     * Returns a deep copy of the instance.  Values are copied.  
     * Any listeners on 'this' are not(?) copied, so these need to be hooked up.
     * @return - the clone.
     */
    @Override
    public Object clone() {
        MegalopaParameters clone = null;
        try {
            clone = (MegalopaParameters) super.clone();
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
     *  Creates an instance of MegalopaParameters.
     *
     *@param strv - array of values (as Strings) used to create the new instance. 
     *              This should be typeName followed by parameter value (as Strings)
     *              in the same order as the keys.
     */
    @Override
    public MegalopaParameters createInstance(final String[] strv) {
        int c = 0;
        MegalopaParameters params = new MegalopaParameters(strv[c++]);
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
