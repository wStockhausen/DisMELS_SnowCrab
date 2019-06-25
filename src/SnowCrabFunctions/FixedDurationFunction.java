/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnowCrabFunctions;

import com.wtstockhausen.utils.RandomNumberGenerator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import wts.models.DisMELS.framework.GlobalInfo;
import wts.models.DisMELS.framework.IBMFunctions.AbstractIBMFunction;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;

/**
 * This function provides an implementation for a fixed duration molt cycle.
 * 
 * The returned intermolt duration should be compared to the age-in=stage
 * or age-in-instar to determine whether or not molting should occur.
 * 
 * @author William Stockhausen
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class FixedDurationFunction  extends AbstractIBMFunction implements IBMFunctionInterface  {
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Annual molt cycle";
    /** function description */
    public static final String DEFAULT_descr = "Annual molt cycle";
    /** full description */
    public static final String DEFAULT_fullDescr = 
        "\n\t**************************************************************************"+
        "\n\t* This function provides an implementation for a fixed duration molt cycle."+
        "\n\t* The returned intermolt duration should be compared to the age-in=stage"+
        "\n\t* or age-in-instar to determine whether or not molting should occur."+
        "\n\t* "+
        "\n\t* @author William Stockhausen"+
        "\n\t* "+
        "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 6;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
    /** key for the mean intermolt duration */
    public static final String PARAM_mean = "mean duration (days)";
    /** key for the standard deviation in intermolt duration */
    public static final String PARAM_stdev = "standard deviation (days)";
    
    /** mean intermolt duration */
    protected double mean = 0.0;
    /** std dev in intermolt duration */
    protected double stddev = 0.0;
    
    /** intermolt duration (days) */
    protected double intermoltDuration = 0.0;
    /** flag to calculate intermolt duration */
    protected boolean calcMoltDuration = false;
    /** class defining a normal distribution */
    protected NormalDistribution norm = null;
    /** cdf of normal distribution at T=0 */
    protected double phiL = 0.0;
    /** flag to calculate the intermolt duration */
    boolean calcDur = true;
    
    public FixedDurationFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_mean;  addParameter(key,Double.class,"mean intermolt duration (days)");
        key = PARAM_stdev; addParameter(key,Double.class,"std. dev. (days)");
    }
    
    @Override
    public Object clone() {
        FixedDurationFunction clone = new FixedDurationFunction();
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
                case PARAM_mean:
                    mean = ((Double) value);
                    break;
                case PARAM_stdev:
                    stddev = ((Double) value);
                    break;
            }
            calcDur = true;
            set = true;
        }
        return set;
    }
    
    /**
     * Returns the intermolt duration, in days.
     * 
     * If the input value "o" is true OR the first time this function is called, 
     * the intermolt duration is calculated based on the (possibly randomized) parameter values and
     * subsequently returned. If "o" is false, a previously-calculated value
     * will be returned. 
     * 
     * @param o - boolean to calculate the intermolt duration
     * 
     * @return Double - the intermolt duration, in days.
     * 
     */
    @Override
    public Object calculate(Object o) {
        if ((Boolean) o||calcDur){
            calcDur = false;//don't need to doo this automatically unless the parameters change
            intermoltDuration = mean;
            if ((stddev>0.0)||calcDur){
                RandomNumberGenerator rng = GlobalInfo.getInstance().getRandomNumberGenerator();
                double rnd = rng.computeUniformVariate(0.0, 1.0);
                norm = new NormalDistribution(mean, stddev);
                phiL = norm.cumulativeProbability(0.0);
                rnd  = phiL+rnd*(1.0-phiL);
                intermoltDuration = norm.inverseCumulativeProbability(rnd);
            }
        }
        return intermoltDuration;
    }
    
}
