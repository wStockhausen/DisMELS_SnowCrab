/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnowCrabFunctions;

import static cern.jet.stat.Probability.normal;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import wts.models.DisMELS.framework.IBMFunctions.AbstractIBMFunction;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;

/**
 *
 * @author William Stockhausen
 */
@ServiceProviders(value={@ServiceProvider(service=IBMFunctionInterface.class)})
public class PostMoltSizeBrokenStickFunction extends AbstractIBMFunction implements IBMFunctionInterface {
    
     /** function classification */
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "broken stick function for post-molt size";
    /** function description */
    public static final String DEFAULT_descr = "Function to calculate post-molt size given pre-molt size using broken stick function.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a function to calculate"+
            "\n\t*       size at next instar for snow crab."+
            "\n\t* Type: "+
            "\n\t*      molt increment function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      useSingle - boolean - flag to use only lower limb"+
            "\n\t*      aLL - Double - intercept for lower limb (mm CW)"+
            "\n\t*      bLL - Double - slope for lower limb"+
            "\n\t*      aUL - Double - intercept of upper limb (mm CW)"+
            "\n\t*      bUL - Double - slope for upper limb"+
            "\n\t*      cp  - Double - change point (mm CW)"+
            "\n\t*      cpw - Double - change point width (mm CW)"+
            "\n\t* Variables:"+
            "\n\t*      vars - Double: carapace width (mm)"+
            "\n\t* Value:"+
            "\n\t*      Double - post-molt CW "+
            "\n\t* Calculation:"+
            "\n\t* "+
            "\n\t* author: William Stockhausen"+
            "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 6;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
        
    public static final String PARAM_useLL = "use only lower limb";
    
    public static final String PARAM_aLL = "lower limb intercept";

    public static final String PARAM_bLL = "lower limb slope";
    
    public static final String PARAM_aUL = "upper limb intercept";

    public static final String PARAM_bUL = "upper limb slope";

    public static final String PARAM_cp  = "change point (mm CW)";

    public static final String PARAM_cpw = "change point width (mm CW)";
    
    private boolean useLL = false;
    
    private double aLL = 0;
    private double bLL = 0;
    private double aUL = 0;
    private double bUL = 0;
    private double cp  = 0;
    private double cpw = 0;
    
    public PostMoltSizeBrokenStickFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_useLL;addParameter(key,Double.class,"use only lower limb");
        key = PARAM_aLL;addParameter(key,Double.class,"lower limb intercept");
        key = PARAM_bLL;addParameter(key,Double.class,"lower limb slope");
        key = PARAM_aUL;addParameter(key,Double.class,"upper limb intercept");
        key = PARAM_bUL;addParameter(key,Double.class,"upper limb slope");
        key = PARAM_cp;addParameter(key,Double.class,"change point");
        key = PARAM_cpw;addParameter(key,Double.class,"change point width");
    }
    
     @Override
    public PostMoltSizeBrokenStickFunction clone(){
        PostMoltSizeBrokenStickFunction clone = new PostMoltSizeBrokenStickFunction();
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
                case PARAM_aLL:
                    aLL = ((Double) value).doubleValue();
                    break;
                case PARAM_bLL:
                    bLL = ((Double) value).doubleValue();
                    break;
                case PARAM_aUL:
                    aUL = ((Double) value).doubleValue();
                    break;
                case PARAM_bUL:
                    bUL = ((Double) value).doubleValue();
                    break;
            }
        }
        return false;
    }
    
    @Override
    public Double calculate(Object vars) {
        double z = (Double) vars;//cast object to required double pre-molt size
        double llZ = aLL+bLL*z;
        double pmZ = z;
        if (!useLL){
            double ulZ = aUL+bUL*z;
            double phi = cern.jet.stat.Probability.normalInverse((z-cp)/cpw);
            pmZ = phi*llZ + (1.0-phi)*ulZ;
        }
        return pmZ;
    }
}
