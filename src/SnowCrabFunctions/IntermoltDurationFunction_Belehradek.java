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
 * This class provides a method of determining the intermolt period using
 * Belehradek's function based on integrating the
 * temperature experienced by an individual crab over time.
 * <pre>
 * At constant temperature T, the intermolt duration D based on Belehradek's function is given by
 *      D = a/(T-b)^c                         (1)
 * Taking the inverse of (1) yields 
 *      {(T-b)^c}/a = 1/D                     (2)
 * If one regards both sides of (2) as functions of time, even though all terms are constant,
 * and integrates both sides from t=0 (start of stage) to t=D (when molting occurs), 
 * one finds that
 *      S{(T-b)^c}/a*dt = S{1/D)*dt = 1       (3)
 * where S represents the integral over the intermolt period. 
 * 
 * Equation 3 provides an algorithm for dealing with time-varying temperatures 
 * which at least agrees with Belehradek's function in the case of constant temperatures.
 * This algorithm uses a running integral of {(T-b)^c}/a for an individual within a 
 * life stage .
 * 
 * Citations:
 *      Oullete and Ste.Marie. 2018. ICES JMS 75(2):773-784.
 * </pre>
 * @author William Stockhausen
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class IntermoltDurationFunction_Belehradek extends AbstractIBMFunction implements IBMFunctionInterface  {
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Belehradek intemolt duration function";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent function to calculate intermolt period based on Belehradek's function.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
        "\n\t**************************************************************************"+
        "\n\t* This class provides a method of determining the intermolt period using"+
        "\n\t* Belehradek's function based on integrating the"+
        "\n\t* temperature experienced by an individual crab over time."+
        "\n\t* "+
        "\n\t* At constant temperature T, the intermolt duration D based on Belehradek's function is given by"+
        "\n\t*      D = a/(T-b)^c                         (1)"+
        "\n\t* Taking the inverse of (1) yields "+
        "\n\t*      {(T-b)^c}/a = 1/D                     (2)"+
        "\n\t* If one regards both sides of (2) as functions of time, even though all terms are constant,"+
        "\n\t* and integrates both sides from t=0 (start of stage) to t=D (when molting occurs), "+
        "\n\t* one finds that"+
        "\n\t*      S{(T-b)^c}/a*dt = S{1/D)*dt = 1       (3)"+
        "\n\t* where S represents the integral over the intermolt period. "+
        "\n\t* "+
        "\n\t* Equation 3 provides an algorithm for dealing with time-varying temperatures "+
        "\n\t* which at least agrees with Belehradek's function in the case of constant temperatures."+
        "\n\t* This algorithm uses a running integral of {(T-b)^c}/a for an individual within a "+
        "\n\t* ife stage. When the integral reaches 1, the molt to the next life stage occurs."+
        "\n\t* "+
        "\n\t* @author William Stockhausen"+
        "\n\t* "+
        "\n\t* Citations:"+
        "\n\t* Oullete and Ste.Marie. 2018. ICES JMS 75(2):773-784."+
        "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 3;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
    /** key for the 'thermal constant' */
    public static final String PARAM_a = "a";
    /** key for the 'threshold temperature constant' */
    public static final String PARAM_b = "b";
    /** key for the exponent */
    public static final String PARAM_c = "c";
    
    /** value of the 'thermal constant' */
    private double a = 0;
    /** value of the 'threshold temperature constant' */
    private double b = 0;
    /** value of the 'threshold temperature constant' */
    private double c = 0;
    
    public IntermoltDurationFunction_Belehradek(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_a; addParameter(key,Double.class,"the 'thermal constant'");
        key = PARAM_b; addParameter(key,Double.class,"the 'threshold temperature constant'");
        key = PARAM_c; addParameter(key,Double.class,"the exponent");
    }

    @Override
    public Object clone() {
        IntermoltDurationFunction_Belehradek clone = new IntermoltDurationFunction_Belehradek();
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
                case PARAM_a:
                    a = ((Double) value);
                    set = true;
                    break;
                case PARAM_b:
                    b = ((Double) value);
                    set = true;
                    break;
                case PARAM_c:
                    c = ((Double) value);
                    set = true;
                    break;
            }
        }
        return set;
    }
    
    /**
     * Calculate the intermolt increment at T, as well as the total intermolt 
     * duration at constant temperature T.
     * 
     * @param o Double: temperature T
     * 
     * @return double[] with elements<pre>
     *      intermolt increment (days)
     *      total intermolt duration at T (days)</pre>
     */
    @Override
    public Object calculate(Object o) {
        double inc = 0.0;
        double tot = Double.POSITIVE_INFINITY;
        double T = (Double) o;
        if (T>b) {
            inc = Math.pow(T-b,c)/a;
            tot = a/Math.pow(T-b,c);
        }
        double[] vals = new double[]{inc,tot};
        return vals;
    }
    
}
