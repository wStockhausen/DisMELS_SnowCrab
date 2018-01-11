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
public class IntermoltLarvaFunction  extends AbstractIBMFunction implements IBMFunctionInterface {
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Snow crab larvae intermolt period function";
    /** function description */
    public static final String DEFAULT_descr = "Temperature-dependent function to calculate intermolt period for snow crab larvae.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a temperature-dependent function to calculate"+
            "\n\t*       intermolt period for snow crab."+
            "\n\t* Type: "+
            "\n\t*      intermolt period function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      a  - Double  - asymptote to calculate K"+
            "\n\t*      b- Double - exponential coefficient to calculate K"+
            "\n\t* Variables:"+
            "\n\t*      T  - double - environmental temperature"+
            "\n\t* Value:"+
            "\n\t*      IM(T) - length of intermolt period at temperature T"+
            "\n\t* Calculation:"+
            "\n\t*      D = (a/T-b);"+
            "\n\t* "+
            "\n\t* author: Christine Stawitz"+
            "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 2;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
        
    public static final String PARAM_a = "a";

    public static final String PARAM_b = "b";

    private double a = 0;

    private double b = 0;

    public IntermoltLarvaFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_a;addParameter(key,Double.class,"numerator of intermolt period");
        key = PARAM_b;addParameter(key,Double.class,"Intercept of intermolt period denominator");
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
                case PARAM_a:
                    a = ((Double) value).doubleValue();
                    break;
                case PARAM_b:
                    b = ((Double) value).doubleValue();
                    break;
            }
        }
        return false;
    }
    
    @Override
    public Double calculate(Object vars) {
        double lvars = (double) vars;//cast object to required double[]
        double T   = lvars;
        T = 5.0;
        Double D = new Double(a/(T-b));
        return D;
    }
}
;