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
 * Reaumur's Law from heat summation theory based on integrating the
 * temperature experienced by an individual crab over time.
 * <pre>
 * At constant temperature T, the intermolt duration D based on Reaumur's Law is given by
 *      D = a/(T-b)             (1)
 * Taking the inverse of (1) yields 
 *      (T-b)/a = 1/D           (2)
 * If one regards both sides of (2) as functions of time, even though all terms are constant,
 * and integrates both sides from t=0 (start of stage) to t=D (when molting occurs), 
 * one finds that
 *      S{(T-b)/a}*dt = 1       (3)
 * where S represents the integral over the intermolt period. 
 * 
 * Equation 3 provides an algorithm for dealing with time-varying temperatures 
 * which at least agrees with Reaumur's Law in the case of constant temperatures.
 * This algorithm uses a running integral of (T-b)/a for an individual within a 
 * pelagic life stage (zooea 1, zooea 2 or megalops) or benthic instar (I through 
 * instar VII). When the integral reaches 1, the molt to the next life stage occurs.
 * 
 * Citations:
 * Yamada et al. 2014. J. Shellfish Res. 33(1): 19-24.
 * Yamada et al. 2015. J. Crust. Biol. 35(2): 140-148.
 * </pre>
 * @author William Stockhausen
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class IntermoltIntegratorFunction extends AbstractIBMFunction implements IBMFunctionInterface  {
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Intermolt duration integrator";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent function to calculate intermolt period based on Reaumur's Law.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
        "\n\t**************************************************************************"+
        "\n\t* This class provides a method of determining the intermolt period using"+
        "\n\t* Reaumur's Law from heat summation theory based on integrating the"+
        "\n\t* temperature experienced by an individual crab over time."+
        "\n\t* "+
        "\n\t* At constant temperature T, the intermolt duration D based on Reaumur's Law is given by"+
        "\n\t*      D = a/(T-b)             (1)"+
        "\n\t* Taking the inverse of (1) yields "+
        "\n\t*      (T-b)/a = 1/D           (2)"+
        "\n\t* If one regards both sides of (2) as functions of time, even though all terms are constant,"+
        "\n\t* and integrates both sides from t=0 (start of stage) to t=D (when molting occurs), "+
        "\n\t* one finds that"+
        "\n\t*      S{(T-b)/a}*dt = 1       (3)"+
        "\n\t* where S represents the integral over the intermolt period. "+
        "\n\t* "+
        "\n\t* Equation 3 provides an algorithm for dealing with time-varying temperatures "+
        "\n\t* which at least agrees with Reaumur's Law in the case of constant temperatures."+
        "\n\t* This algorithm uses a running integral of (T-b)/a for an individual within a "+
        "\n\t* pelagic life stage (zooea 1, zooea 2 or megalops) or benthic instar (I through "+
        "\n\t* instar VII). When the integral reaches 1, the molt to the next life stage occurs."+
        "\n\t* "+
        "\n\t* @author William Stockhausen"+
        "\n\t* "+
        "\n\t* Citations:"+
        "\n\t* Yamada et al. 2014. J. Shellfish Res. 33(1): 19-24."+
        "\n\t* Yamada et al. 2015. J. Crust. Biol. 35(2): 140-148."+
        "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 2;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
    /** key for the 'thermal constant' */
    public static final String PARAM_a = "a";
    /** key for the 'threshold temperature constant' */
    public static final String PARAM_b = "b";
    
    /** value of the 'thermal constant' */
    private double a = 0;
    /** value of the 'threshold temperature constant' */
    private double b = 0;
    
    public IntermoltIntegratorFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_a; addParameter(key,Double.class,"the 'thermal constant'");
        key = PARAM_b; addParameter(key,Double.class,"the 'threshold temperature constant'");
    }

    @Override
    public Object clone() {
        IntermoltIntegratorFunction clone = new IntermoltIntegratorFunction();
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
            inc = (T-b)/a;
            tot = a/(T-b);
        }
        double[] vals = new double[]{inc,tot};
        return vals;
    }
    
}
