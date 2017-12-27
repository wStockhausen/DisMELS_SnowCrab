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
public class ExCostFunction extends AbstractIBMFunction implements IBMFunctionInterface{
     /** function classification */
    public static final String DEFAULT_type = "generic";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Snow crab function to calculate exuviae cost";
    /** function description */
    public static final String DEFAULT_descr = "Function to calculate the cost of developing the shell.";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a function to calculate"+
            "\n\t*       the cost of developing a new soft shell."+
            "\n\t* Type: "+
            "\n\t*      molt increment function"+
            "\n\t* Parameters (by key):"+
            "\n\t*      a  - Double  - intercept of ex cost"+
            "\n\t*      b- Double - exponent of ex cost"+
            "\n\t*      mat - Boolean - true if crab is mature"+
            "\n\t* Variables:"+
            "\n\t*      vars - double[]{size}."+
            "\n\t*      size - carapace width of next instar (mm)"+
            "\n\t* Value:"+
            "\n\t*      ex - cost of next carapace shell"+
            "\n\t* Calculation:"+
            "\n\t*     if(mat){\n" +
            "\n\t*            ex = new Double(a*Math.pow(size,b));\n" +
            "\n\t*        } else{\n" +
            "\n\t*            ex = new Double(a+b*size);\n" +
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


    public ExCostFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_a;addParameter(key,Double.class,"intercept of exuviae cost");
        key = PARAM_b;addParameter(key,Double.class,"exponent of exuviae cost");
        key = PARAM_mat;addParameter(key,Boolean.class,"if crab is mature");
    }
    
     @Override
    public ExCostFunction clone(){
        ExCostFunction clone = new ExCostFunction();
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
        double lvars = (Double) vars;//cast object to required double[]
        double size = lvars;
        Double exuviae;
        if(mat){
            exuviae = new Double(a*Math.pow(size,b));
        } else{
            exuviae = new Double(a+b*size);
        }  
        return exuviae;
    }
    
}
