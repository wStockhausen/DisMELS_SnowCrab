/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnowCrabFunctions;

import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import wts.models.DisMELS.framework.IBMFunctions.AbstractIBMFunction;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;

/**
 * This class provides a function to calculate an end-of-stage, temperature-dependent 
 * survival probability for the zooea I, zooea II, and megalopal stages of snow crab,
 * based on Ouellet and Sainte-Marie (2017).
 * 
 * @author William Stockhausen
 * 
 * Citations:
 * Ouellet, P. and B. Sainte-Marie.2017. ICES JMS. doi:10.1093/icesjms/fsx169.
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class SurvivalFunction_OuelletAndSteMarie2017 extends AbstractIBMFunction implements IBMFunctionInterface {
    
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Temperature-based survival";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent function to calculate stage survival.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
        "\n\t**************************************************************************"+
        "\n\t* This class provides a function to calculate an end-of-stage, temperature-dependent"+
        "\n\t* survival probability for the zooea I, zooea II, and megalopal stages of snow crab,"+
        "\n\t* based on Ouellet and Sainte-Marie (2017)."+
        "\n\t* "+
        "\n\t* @author William Stockhausen"+
        "\n\t* "+
        "\n\t* Citations:"+
        "\n\t* Ouellet, P. and B. Sainte-Marie.2017. ICES JMS. doi:10.1093/icesjms/fsx169."+
        "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 2;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
    /** key for the survival at 10 deg C */
    public static final String PARAM_S10 = "S10: survival at 10C";
    /** survival at 10 deg C */
    private double S10 = 0;
    /** key for the intercept coefficient */
    public static final String PARAM_c0 = "c0: the intercept";
    /** value of the intercept coefficient */
    private double c0 = 0;
    /** key for the linear coefficient */
    public static final String PARAM_c1 = "c1: linear coefficient";
    /** value of the linear coefficient */
    private double c1 = 0;
    /** key for the quadratic coefficient */
    public static final String PARAM_c2 = "c2: quadratic coefficient";
    /** value of the quadratic coefficient */
    private double c2 = 0;
    /** key for the minimum temperature for survival */
    public static final String PARAM_minT = "min temperature";
    /** value of the minimum temperature */
    private double minT = 0;
    /** key for the minimum temperature for survival */
    public static final String PARAM_maxT = "max temperature";
     /** value of the maximum temperature */
    private double maxT = 0;
   
    
    public SurvivalFunction_OuelletAndSteMarie2017(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_c0; addParameter(key,Double.class,"c0: the intercept");
        key = PARAM_minT; addParameter(key,Double.class,"the 'threshold temperature constant'");
    }

    @Override
    public Object clone() {
        SurvivalFunction_OuelletAndSteMarie2017 clone = new SurvivalFunction_OuelletAndSteMarie2017();
        clone.setFunctionType(getFunctionType());
        clone.setFunctionName(getFunctionName());
        clone.setDescription(getDescription());
        clone.setFullDescription(getFullDescription());
        for (String key: getParameterNames()) clone.setParameterValue(key,getParameter(key).getValue());
//        for (String key: getSubfunctionNames()) clone.setSubfunction(key,(IBMFunctionInterface)getSubfunction(key).clone());
        return clone;
    }

    @Override
    public boolean setParameterValue(String param,Object value){
        //the following sets the value in the parameter map AND in the local variable
        boolean set = false;
        if (super.setParameterValue(param, value)){
            switch (param) {
                case PARAM_c0:
                    c0 = ((Double) value);
                    set = true;
                    break;
                case PARAM_minT:
                    minT = ((Double) value);
                    set = true;
                    break;
            }
        }
        return set;
    }
    
    @Override
    public Object calculate(Object o) {
        double S = 0.0;
        double T = (Double) o;
        if (T>minT) S = c0 + c1*T +c2*T*T; 
        return S;
    }
    
}
