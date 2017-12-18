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
    @ServiceProvider(service=IBMFunctionInterface.class)})
        
/**
 *
 * @author christine.stawitz
 */
public class MoltIncrementFunction extends AbstractIBMFunction implements IBMFunctionInterface {
     /** function classification */
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Snow crab molt increment function";
    /** function description */
    public static final String DEFAULT_descr = "Function to calculate size at next instar for snow crab.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a function to calculate"+
            "\n\t*       size at next instar for snow crab."+
            "\n\t* Type: "+
            "\n\t*      molt increment function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      a  - Double  - intercept of molt increment"+
            "\n\t*      b- Double - exponent of molt increment"+
            "\n\t*      mat - Boolean - true if crab is mature"+
            "\n\t* Variables:"+
            "\n\t*      vars - double[]{cW, T}."+
            "\n\t*      size - carapace width (mm)"+
            "\n\t* Value:"+
            "\n\t*      size(T) - next carapace width crab will molt to "+
            "\n\t* Calculation:"+
            "\n\t*     if(mat){\n" +
            "\n\t*            new_size = new Double(a*Math.pow(size,b));\n" +
            "\n\t*        } else{\n" +
            "\n\t*            new_size = new Double(a+b*size);\n" +
            "\n\t*        }  "+
            "\n\t* "+
            "\n\t* author: Christine Stawitz"+
            "\n\t**************************************************************************";
    /** number of settable parameters */
    public static final int numParams = 3;
    /** number of sub-functions */
    public static final int numSubFuncs = 0;
        
    public static final String PARAM_a = "a";

    public static final String PARAM_b = "b";

    public static final String PARAM_mat = "mat";


    private double a = 0;

    private double b = 0;

    private boolean mat = false;


    public MoltIncrementFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_a;addParameter(key,Double.class,"intercept of molt increment");
        key = PARAM_b;addParameter(key,Double.class,"exponent of molt increment");
        key = PARAM_mat;addParameter(key,Boolean.class,"if crab is mature");
    }
    
     @Override
    public MoltIncrementFunction clone(){
        MoltIncrementFunction clone = new MoltIncrementFunction();
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
                case PARAM_mat:
                    mat = ((Boolean) value).booleanValue();
                    break;
            }
        }
        return false;
    }
    
    @Override
    public Double calculate(Object vars) {
        double[] lvars = (double[]) vars;//cast object to required double[]
        int i = 0;
        double size = lvars[i++];
        Double new_size;
        if(mat){
            new_size = new Double(a*Math.pow(size,b));
        } else{
            new_size = new Double(a+b*size);
        }  
        return new_size;
    }
}
