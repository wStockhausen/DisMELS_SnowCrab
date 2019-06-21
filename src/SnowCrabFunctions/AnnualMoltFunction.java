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
 * This function provides an implementation for an annual molt cycle, 
 * with the potential for skip molting.
 * 
 * @param 
 * @author WilliamStockhausen
 */
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
public class AnnualMoltFunction  extends AbstractIBMFunction implements IBMFunctionInterface  {
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Annual molt cycle";
    /** function description */
    public static final String DEFAULT_descr = "Annual molt cycle";
    /** full description */
    public static final String DEFAULT_fullDescr = 
        "\n\t**************************************************************************"+
        "\n\t* This function provides an implementation for an annual molt cycle,"+
        "\n\t* with the potential for skip molting."+
        "\n\t* "+
        "\n\t* "+
        "\n\t* @author William Stockhausen"+
        "\n\t* "+
        "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 2;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
    /** first day of molting */
    public static final String PARAM_firstDay = "firstDay";
    /** key for the 'threshold temperature constant' */
    public static final String PARAM_interval = "interval";
    /** day of peak molting */
    public static final String PARAM_peakDay = "peakDay";
    /** width of molting peak */
    public static final String PARAM_width = "width";
    /** flag to randomize molting */
    public static final String PARAM_random = "random";
    /**probability of skip molting */
    public static final String PARAM_skip = "skip";
    
    /** first day of molting */
    private double firstDay = 0.0;
    /** time interval during which molting may occur */
    private double interval = 1.0;
    /** day of peak molting */
    private double peakDay = 0.5;
    /** width of molting peak */
    private double width = 1.0;
    /** flag to randomize molting */
    private boolean random = false;
    /**probability of skip molting */
    private double skip = 0.0;
    
    /** */
    private boolean calcConsts = false;
    private NormalDistribution norm = null;
    private double phiL = 0.0;
    private double phiU = 1.0;
    
    public AnnualMoltFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_firstDay; addParameter(key,Double.class,"first day for molting (DOY)");
        key = PARAM_interval; addParameter(key,Double.class,"molting interval (days)");
        key = PARAM_peakDay;  addParameter(key,Double.class,"peak day of molting (DOY)");
        key = PARAM_width;    addParameter(key,Double.class,"width of molting peak (DOY)");
        key = PARAM_random;   addParameter(key,Double.class,"randomize molt timing?");
        key = PARAM_skip;     addParameter(key,Double.class,"probability of skip molting");
    }
    
    @Override
    public Object clone() {
        AnnualMoltFunction clone = new AnnualMoltFunction();
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
                case PARAM_firstDay:
                    firstDay = ((Double) value);
                    calcConsts = true;
                    set = true;
                    break;
                case PARAM_interval:
                    interval = ((Double) value);
                    calcConsts = true;
                    set = true;
                    break;
                case PARAM_peakDay:
                    peakDay = ((Double) value);
                    calcConsts = true;
                    set = true;
                    break;
                case PARAM_width:
                    width = ((Double) value);
                    calcConsts = true;
                    set = true;
                    break;
                case PARAM_random:
                    random = ((Boolean) value);
                    calcConsts = true;
                    set = true;
                    break;
                case PARAM_skip:
                    skip = ((Double) value);
                    set = true;
                    break;
            }
        }
        return set;
    }
    
    /**
     * Calculates the day of year on which molting will occur, 
     * unless molting is skipped.
     * 
     * @param o - not used (can be null)
     * 
     * @return Double - day of year on which molting will occur, or -1 for a skip molt.
     * 
     */
    @Override
    public Object calculate(Object o) {
        double doy = peakDay;
        if (skip>0.0){
            RandomNumberGenerator rng = GlobalInfo.getInstance().getRandomNumberGenerator();
            double rnd = rng.computeUniformVariate(0.0, 1.0);
            if (rnd<=skip) return -1.0;//molt skipped
            if (random){
                rnd = rng.computeUniformVariate(0.0, 1.0);
                if (calcConsts){
                    norm = new NormalDistribution(peakDay, width);
                    phiL = norm.cumulativeProbability(firstDay);
                    phiU = norm.cumulativeProbability(firstDay+interval);
                    calcConsts= false;//don't need to re-calculate these unless parameters change
                }
                rnd = phiL+rnd*(phiU-phiL);
                doy = norm.inverseCumulativeProbability(rnd);
            }
        }
        return doy;
    }
    
}
