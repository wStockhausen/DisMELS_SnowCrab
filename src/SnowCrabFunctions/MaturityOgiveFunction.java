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
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)})
/**
 *
 * @author christine.stawitz
 */
public class MaturityOgiveFunction extends AbstractIBMFunction implements IBMFunctionInterface{
     /** function classification */
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Snow crab maturity ogive function";
    /** function description */
    public static final String DEFAULT_descr = "Function to calculate whether or not a crab matures.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a function to calculate"+
            "\n\t*       if a crab of a given size will molt to maturity."+
            "\n\t* Type: "+
            "\n\t*      maturity ogive function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      aCW  - Double  - intercept of molt increment"+
            "\n\t*      bCW- Double - exponent of molt increment"+
            "\n\t*      aQ1 - slope of temp-maturity relationship for Q1"+
            "\n\t*      bQ1 - slope of temp-maturity relationship for Q1"+
            "\n\t*      aQ99 - slope of temp-maturity relationship for Q99"+
            "\n\t*      bQ99 - slope of temp-maturity relationship for Q99"+
            "\n\t* Variables:"+
            "\n\t*      vars - double[]{size, T}."+
            "\n\t*      size - carapace width (mm)"+
            "\n\t*      T - temperature (in degrees Celsius)"+
            "\n\t* Value:"+
            "\n\t*      success - Boolean does crab molt to maturity?"+
            "\n\t* Calculation:"+
            "\n\t* double q1 = aQ1*Math.log(T+2.0)+bQ1;"+
            "\n\t* double q99 = aQ99*Math.log(T+2.0)+bQ99;"+
            "\n\t* NormalDistribution stdNorm = new NormalDistribution();"+
            "\n\t* double phi1 = stdNorm.cumulativeProbability(.01);"+
            "\n\t* double phi99 = stdNorm.cumulativeProbability(.99);"+
            "\n\t* double sigmaT = ((q99-q1)/(phi99-phi1))/bCW;"+
            "\n\t* double muT = ((q1*phi99-q99*phi1)/(phi99-phi1)-aCW)/bCW;"+
            "\n\t* NormalDistribution postMoltDist = new NormalDistribution(muT, sigmaT);"+
            "\n\t* double probMature = postMoltDist.cumulativeProbability(size);"+
            "\n\t* BinomialDistribution pMat = new BinomialDistribution(1,probMature);"+
            "\n\t* int p = pMat.sample();"+
            "\n\t* Boolean success = false;"+
            "\n\t* if(p==1){"+
            "\n\t*    success=true;"+
            "\n\t* }"+
            "\n\t* return success; "+
            "\n\t* author: Christine Stawitz"+
            "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 6;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
        
    public static final String PARAM_aCW = "aCW";

    public static final String PARAM_bCW = "bCW";

    public static final String PARAM_aQ1 = "aQ1";
    public static final String PARAM_bQ1 = "bQ1";
        
    public static final String PARAM_aQ99 = "aQ99";
    public static final String PARAM_bQ99 = "bQ99";

    private double aCW = 0;

    private double bCW = 0;

    private double aQ1 = 0;
    
    private double bQ1 = 0;
    
    private double aQ99 = 0;
    
    private double bQ99 = 0;


    public MaturityOgiveFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_aCW;addParameter(key,Double.class,"intercept of molt increment");
        key = PARAM_bCW;addParameter(key,Double.class,"exponent of molt increment");
        key = PARAM_aQ1;addParameter(key,Boolean.class,"slope of temperature maturity relationship for .01 quantile");
        key = PARAM_bQ1;addParameter(key,Boolean.class,"intercept of temp-maturity relationship for .01 quantile");
        key = PARAM_aQ99;addParameter(key,Boolean.class,"slope of temperature maturity relationship for .99 quantile");
        key = PARAM_bQ99;addParameter(key,Boolean.class,"intercept of temperature maturity relationship for .99 quantile");
    }
    
     @Override
    public MaturityOgiveFunction clone(){
        MaturityOgiveFunction clone = new MaturityOgiveFunction();
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
        if (super.setParameterValue(param, value)){
            switch (param) {
                case PARAM_aCW:
                    aCW = ((Double) value).doubleValue();
                    break;
                case PARAM_bCW:
                    bCW = ((Double) value).doubleValue();
                    break;
                case PARAM_aQ1:
                    aQ1 = ((Double) value).doubleValue();
                    break;
                case PARAM_bQ1:
                    bQ1 = ((Double) value).doubleValue();
                    break;
                case PARAM_aQ99:
                    aQ99 = ((Double) value).doubleValue();
                    break;
                case PARAM_bQ99:
                    bQ99 = ((Double) value).doubleValue();
            }
        }
        return false;
    }
    
    @Override
    public Boolean calculate(Object vars) {
        double[] lvars = (double[]) vars;//cast object to required double[]
        int i = 0;
        double size = lvars[i++];
        double T = lvars[i++];
        double q1 = aQ1*Math.log(T+2.0)+bQ1;
        double q99 = aQ99*Math.log(T+2.0)+bQ99;
        NormalDistribution stdNorm = new NormalDistribution();
        double phi1 = stdNorm.inverseCumulativeProbability(.01);
        double phi99 = stdNorm.inverseCumulativeProbability(.99);
        double sigmaT = ((q99-q1)/(phi99-phi1))/bCW;
        double muT = (((q1*phi99-q99*phi1)/(phi99-phi1))-aCW)/bCW;
        NormalDistribution postMoltDist = new NormalDistribution(muT, sigmaT);
        double probMature = postMoltDist.cumulativeProbability(size);
        BinomialDistribution pMat = new BinomialDistribution(1,probMature);
        int p = pMat.sample();
        Boolean success = false;
        if(p==1){
            success=true;
        }
        return success;
    }
}
