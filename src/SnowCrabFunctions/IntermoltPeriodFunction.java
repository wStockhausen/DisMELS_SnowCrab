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
@ServiceProviders(value={
    @ServiceProvider(service=IBMFunctionInterface.class)}
)
/**
 *
 * @author christine.stawitz
 */
public class IntermoltPeriodFunction extends AbstractIBMFunction implements IBMFunctionInterface {
    /** function classification */
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Snow crab intermolt period function";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent function to calculate intermolt period for snow crab.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a temperature-dependent function to calculate"+
            "\n\t*       intermolt period for snow crab."+
            "\n\t* Type: "+
            "\n\t*      intermolt period function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      aK  - Double  - asymptote to calculate K"+
            "\n\t*      bK- Double - exponential coefficient to calculate K"+
            "\n\t*      aAlpha    - Double  - asymptote to calculate alpha"+
            "\n\t*      bAlpha - Double  - exponential coefficient to calculate alpha"+
            "\n\t* Variables:"+
            "\n\t*      vars - double[]{cW, T}."+
            "\n\t*      size - carapace width (mm)"+
            "\n\t*      T  - double - environmental temperature"+
            "\n\t* Value:"+
            "\n\t*      IM(T) - length of intermolt period at temperature T"+
            "\n\t* Calculation:"+
            "\n\t*      K = aK*(1-Math.exp(bK*size));"+
            "\n\t*      double alpha = aAlpha*(1-Math.exp(bAlpha*size))"+
            "\n\t*      D = (K/T-alpha);"+
            "\n\t* "+
            "\n\t* author: Christine Stawitz"+
            "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 4;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
        
    public static final String PARAM_aK = "aK";

    public static final String PARAM_bK = "bK";

    public static final String PARAM_aAlpha = "aAlpha";

    public static final String PARAM_bAlpha = "bAlpha";

    private double aK = 0;

    private double bK = 0;

    private double aAlpha = 0;

    private double bAlpha = 0;

    public IntermoltPeriodFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_aK;addParameter(key,Double.class,"coefficient of K parameter");
        key = PARAM_bK;addParameter(key,Double.class,"exponent of K parameter");
        key = PARAM_aAlpha;addParameter(key,Double.class,"coefficient of alpha parameter");
        key = PARAM_bAlpha;addParameter(key,Double.class,"exponent of alpha parameter");
    }
    
     @Override
    public IntermoltPeriodFunction clone(){
        IntermoltPeriodFunction clone = new IntermoltPeriodFunction();
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
                case PARAM_aK:
                    aK = ((Double) value).doubleValue();
                    break;
                case PARAM_bK:
                    bK = ((Double) value).doubleValue();
                    break;
                case PARAM_aAlpha:
                    aAlpha = ((Double) value).doubleValue();
                    break;
                case PARAM_bAlpha:
                    bAlpha = ((Double) value).doubleValue();
                    break;
            }
        }
        return false;
    }
    
    @Override
    public Double calculate(Object vars) {
        double[] lvars = (double[]) vars;//cast object to required double[]
        int i = 0;
        double cW = lvars[i++];
        double T   = 5.0;
        double K = aK*(1-Math.exp(bK*cW));
        double alpha = aAlpha*(1-Math.exp(bAlpha*cW));
        Double D = new Double(K/(T-alpha));
        return D;
    }
}
;