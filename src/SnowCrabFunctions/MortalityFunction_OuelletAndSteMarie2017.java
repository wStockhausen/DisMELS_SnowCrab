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
 * This class provides a function to calculate a temperature-dependent 
 * mortality rate for the zooeal and megalopal stages of snow crab
 * based on Ouellet and Sainte-Marie (2017).
 * 
 * <pre>
 * Citations:
 * Ouellet, P. and B. Sainte-Marie.2017. ICES JMS. doi:10.1093/icesjms/fsx169.
 * </pre>
 * @author William Stockhausen
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class MortalityFunction_OuelletAndSteMarie2017 extends AbstractIBMFunction implements IBMFunctionInterface {
    
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Temperature-based mortality rate (Oullet & Ste.-Marie 2017)";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent function to calculate a mortality rate.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
        "\n\t**************************************************************************"+
        "\n\t* This class provides a function to calculate a temperature-dependent"+
        "\n\t* mortality rate for the zooeal and megalopal stages of snow crab,"+
        "\n\t* based on Ouellet and Sainte-Marie (2017)."+
        "\n\t* "+
        "\n\t* @author William Stockhausen"+
        "\n\t* "+
        "\n\t* Citations:"+
        "\n\t* Ouellet, P. and B. Sainte-Marie.2017. ICES JMS. doi:10.1093/icesjms/fsx169."+
        "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 7;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
    
    /** key for the stage survival at reference temperature */
    public static final String PARAM_refTempS = "stage survival at reference temp";
    /** stage survival at reference temperature */
    private double sAtRefTemp = 1.0;
    /** key for the reference temperature */
    public static final String PARAM_refTemp = "reference temp (deg C)";
    /** reference temperature */
    private double refTemp = 0;
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
    public static final String PARAM_minT = "min survival temperature (deg C)";
    /** value of the minimum temperature */
    private double minT = 0;
    /** key for the minimum temperature for survival */
    public static final String PARAM_maxT = "max survival temperature (deg C)";
     /** value of the maximum temperature */
    private double maxT = 0;
   
    /** survival index at reference temperature */
    private double refSI = -1.0;
    
    public MortalityFunction_OuelletAndSteMarie2017(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_refTempS; addParameter(key,Double.class,PARAM_refTempS);
        key = PARAM_refTemp;  addParameter(key,Double.class,PARAM_refTemp);
        key = PARAM_c0;       addParameter(key,Double.class,PARAM_c0);
        key = PARAM_c1;       addParameter(key,Double.class,PARAM_c1);
        key = PARAM_c2;       addParameter(key,Double.class,PARAM_c2);
        key = PARAM_minT;     addParameter(key,Double.class,PARAM_minT);
        key = PARAM_maxT;     addParameter(key,Double.class,PARAM_maxT);
    }

    @Override
    public Object clone() {
        MortalityFunction_OuelletAndSteMarie2017 clone = new MortalityFunction_OuelletAndSteMarie2017();
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
                case PARAM_refTempS:
                    sAtRefTemp = ((Double) value);
                    set = true;
                    break;
                case PARAM_refTemp:
                    refTemp = ((Double) value);
                    set = true;
                    break;
                case PARAM_c0:
                    c0 = ((Double) value);
                    refSI = -1.0;//flag to recalculate refSI
                    set = true;
                    break;
                case PARAM_c1:
                    c1 = ((Double) value);
                    refSI = -1.0;//flag to recalculate refSI
                    set = true;
                    break;
                case PARAM_c2:
                    c2 = ((Double) value);
                    refSI = -1.0;//flag to recalculate refSI
                    set = true;
                    break;
                case PARAM_minT:
                    minT = ((Double) value);
                    set = true;
                    break;
                case PARAM_maxT:
                    maxT = ((Double) value);
                    set = true;
                    break;
            }
        }
        return set;
    }
    
    /**
     * Calculate the instantaneous mortality rate.
     * 
     * @param o double[] with elements <pre>
     *      T   - temperature
     *      mnD - mean stage duration at T</pre>
     * 
     * @return double - instantaneous mortality rate (no survival if &lt 0) 
     * <pre>
     * Note: units of the mortality rate are the inverse of those for <code>mnD</code>.
     * M &lt 0 implies no survival. 
     * </pre>
     */
    @Override
    public Object calculate(Object o) {
        double[] vals = (double[]) o; int k = 0;
        double T   = vals[k++];//current temperature
        double mnD = vals[k++];//mean intermolt duration at T
        double M = -1.0;//flag for  no survival
        if ((T>minT)&&(T<maxT)) {
            if (refSI<0) refSI = (c0 + c1*refTemp +c2*refTemp*refTemp);
            //intermolt survival at T 
            double stageS = sAtRefTemp*(c0 + c1*T +c2*T*T)/refSI;
            //equivalent instantaneous mortality rate
            if (stageS>0.0) M = -(1.0/mnD)*Math.log(stageS);
        } 
        return M;
    }
    
}
