/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package disMELS.IBMs.SnowCrab.EggMassExtruded;

import com.wtstockhausen.utils.RandomNumberGenerator;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import wts.models.DisMELS.framework.GlobalInfo;
import wts.models.DisMELS.framework.IBMFunctions.AbstractIBMFunction;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.IBMFunctions.IBMGrowthFunctionInterface;

/**
 * This class provides an implementation of a linear growth function.
 * Type: 
 *      Individual growth function
 * Parameters (by key):
 *      tCoeff  - Double  - temperature coefficient
 *      useNomT - Boolean - flag to use nominal temperature (instead of in situ T)
 *      nomT    - Double  - nominal temperature to use
 *      sigRate - Double - std. deviation in random component to development ([1/[time])
 * Variables:
 *      vars - double[]{dt,z0}.
 *      dt - double - time interval   (hours)
 *      s0 - double - egg stage at time t0 ([dev. stage 1-19])
 *      T  - double - environmental temperature (deg C)
 * Value:
 *      s(dt) - Double - egg stage at time t+dt
 * Calculation:
 *      eps   = N(0,sigRate) [random draw from a normal distribution)
 *      rate  = exp(r0[s0]+tCoeff*T+eps);
 *      z(dt) = z0 + dt*rate;
 *      whwere r0[] is an array of ln-scale development rates at T=0 C.
 * 
 * @author William.Stockhausen
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class EggDevelopmentFunction extends AbstractIBMFunction implements IBMGrowthFunctionInterface {
    
    /** function classification */
    public static final String DEFAULT_type = "Egg development";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Arrowtooth flounder egg development function";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent egg development function for arrowtooth flounder with additive random noise";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a temperature-dependent egg development function for"+
            "\n\t*      arrowtooth flounder with additive random noise."+
            "\n\t* Type: "+
            "\n\t*      egg development function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      tCoeff  - Double  - temperature coefficient"+
            "\n\t*      useNomT - Boolean - flag to use nominal temperature (instead of in situ T)"+
            "\n\t*      nomT    - Double  - nominal temperature to use"+
            "\n\t*      sigRate - Double  - std. deviation in random component to development"+
            "\n\t* Variables:"+
            "\n\t*      vars - double[]{dt,s0,T}."+
            "\n\t*      dt - double - time interval   ([time])"+
            "\n\t*      s0 - double - stage at time t0 ([size])"+
            "\n\t*      T  - double - environmental temperature"+
            "\n\t* Value:"+
            "\n\t*      s(dt) - Double - stage at time t+dt"+
            "\n\t* Calculation:"+
            "\n\t*      eps   = N(0,sigRate) [random draw from a normal distribution)"+
            "\n\t*      rate  = exp(r0[s0]+tCoeff*T+eps)"+
            "\n\t*      s(dt) = z0 + dt*rate+eps;"+
            "\n\t*      whwere r0[] is an array of ln-scale development rates at T=0 C."+
            "\n\t* "+
            "\n\t* author: William.Stockhausen"+
            "\n\t**************************************************************************";
    /** random number generator */
    protected static final RandomNumberGenerator rng = GlobalInfo.getInstance().getRandomNumberGenerator();

    /** number of settable parameters */
    public static final int numParams = 4;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;

    /** key to set temperature coefficient */
    public static final String PARAM_tCoeff = "temperature coefficient";
    /** key to set flag to use nominal temperature instead of in situ T */
    public static final String PARAM_useNomT = "use nominal temperature?";
    /** key to set nominal temperature */
    public static final String PARAM_nomT = "nominal temperature (deg C)";
    /** key to set standard deviation parameter */
    public static final String PARAM_stdvRate = "std. dev. of rate";
    
    /** value of rate parameter */
    private double tCoeff = 0.2153114;//value from analysis of Blood et al. 2007 results
    /** value of flag to use nominal temperature */
    private boolean useNomT = false;
    /** value of nominal temperature */
    private double nomT = 3;
    /** value of standard deviation parameter */
    private double stdvRate = 0;
    
    /** ln(development rate) for stages 1-19 at T = 0 C. */
    private double[] r0 = new double[]{-3.052159, 
                                        -2.141430, 
                                        -1.607833, 
                                        -1.729589, 
                                        -2.484353, 
                                        -3.511702, 
                                        -4.297922, 
                                        -4.500519, 
                                        -4.204786, 
                                        -3.852646, 
                                        -3.831174,
                                        -4.111935, 
                                        -4.354460, 
                                        -4.355601, 
                                        -4.278690, 
                                        -4.385344, 
                                        -4.670129, 
                                        -4.909279, 
                                        -5.007842};
    
    /** constructor for class */
    public EggDevelopmentFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_tCoeff;  addParameter(key,Double.class, "temperature coefficient for development rate");
        setParameterValue(key, tCoeff);
        key = PARAM_useNomT; addParameter(key,Boolean.class, "use nominal temperature?");
        setParameterValue(key, useNomT);
        key = PARAM_nomT;    addParameter(key,Double.class, "nominal temperature (deg C)");
        setParameterValue(key, nomT);
        key = PARAM_stdvRate;addParameter(key,Double.class, "std. dev. in ln-scale development rate");
        setParameterValue(key, stdvRate);
    }
    
    @Override
    public EggDevelopmentFunction clone(){
        EggDevelopmentFunction clone = new EggDevelopmentFunction();
        clone.setFunctionType(getFunctionType());
        clone.setFunctionName(getFunctionName());
        clone.setDescription(getDescription());
        clone.setFullDescription(getFullDescription());
        for (String key: getParameterNames()) clone.setParameterValue(key,getParameter(key).getValue());
//        for (String key: getSubfunctionNames()) clone.setSubfunction(key,(IBMFunctionInterface)getSubfunction(key).clone());
        return clone;
    }
    
    /**
     * Sets the parameter value corresponding to the key associated with param.
     * 
     * @param param - the parameter key (name)
     * @param value - its value
     * @return 
     */
    @Override
    public boolean setParameterValue(String param,Object value){
        if (super.setParameterValue(param, value)){
            switch (param) {
                case PARAM_tCoeff:
                    tCoeff    = ((Double) value).doubleValue();
                    break;
                case PARAM_useNomT:
                    useNomT    = ((Boolean) value).booleanValue();
                    break;
                case PARAM_nomT:
                    nomT    = ((Double) value).doubleValue();
                    break;
                case PARAM_stdvRate:
                    stdvRate = ((Double) value).doubleValue();
                    break;
            }
        }
        return false;
    }

    /**
     * Calculates the value of the function, given the current parameter params 
     * and the input variable.
     * 
     * @param vars - the inputs variables, dt s0 and T as a double[].
     *      dt - time step in hours
     *      s0 - development stage at t
     *      T  - temperature
     * @return     - s[dt], the development stage at t+dt, as a Double 
     */
    @Override
    public Double calculate(Object vars) {
        double[] lvars = (double[]) vars;//cast object to required double[]
        int i = 0;
        double dt = lvars[i++];
        double s0 = lvars[i++];
        double T = nomT;
        if (!useNomT) T = lvars[i++];
        double rnd = 0; 
        if (stdvRate>0) rnd = rng.computeNormalVariate(); 
        double rate = Math.exp(r0[((int)s0)-1]+tCoeff*T+rnd*stdvRate);
        s0 += rate*dt;
        return s0;
    }
}
