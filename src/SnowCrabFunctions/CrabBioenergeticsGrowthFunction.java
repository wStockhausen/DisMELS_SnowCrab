/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SnowCrabFunctions;

import com.wtstockhausen.utils.RandomNumberGenerator;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import wts.models.DisMELS.framework.GlobalInfo;
import wts.models.DisMELS.framework.IBMFunctions.AbstractIBMFunction;
import wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface;
import wts.models.DisMELS.framework.IBMFunctions.IBMGrowthFunctionInterface;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
        
@ServiceProviders(value={
    @ServiceProvider(service=IBMGrowthFunctionInterface.class),
    @ServiceProvider(service=IBMFunctionInterface.class)}
)

public class CrabBioenergeticsGrowthFunction extends AbstractIBMFunction implements IBMGrowthFunctionInterface{
    /** function classification */
    public static final String DEFAULT_type = "Individual growth";
    /** user-friendly function name */
    public static final String DEFAULT_name = "Crab Bioenergetics Model";
    /** function description */
    public static final String DEFAULT_descr = "Crab Bioenergetics Model";
    /** full description */
    public static final String DEFAULT_fullDescr = 
            "\n\t**************************************************************************"+
            "\n\t* This class provides an implementation of a crab bioenergetics growth function."+
            "\n\t* "+
            "\n\t* Type: "+
            "\n\t*      Individual growth function"+
            "\n\t* Parameters (by key):"+
            "\n\t       pVal - Double vector - realized fraction of max consumption"+
            "\n\t"+
            "\n\t       aC  - Double - linear coefficient of weight-dependent max consumption"+
            "\n\t       bC  - Double - exponent coefficient of weight-dependent max consumption"+
            "\n\t       cmT - Double - max temperature at which consumption occurs"+
            "\n\t       coT - Double - temperature at which consumption is maximized"+
            "\n\t       c1c - Double - consumption coefficient"+
            "\n\t       "+
            "\n\t       ACT  - Double - respiration activity multiplier"+
            "\n\t       aR  - Double - linear coefficient of weight-dependent respiration"+
            "\n\t       bR  - Double - exponent coefficient of weight-dependent respiration"+
            "\n\t       rmT  - Double - max temperature at which respiration occurs"+
            "\n\t       roT  - Double - temperature at which respiration is maximized"+
            "\n\t       c1r  - Double - respiration coefficient"+
            "\n\t        "+
            "\n\t       FA   - Double - fraction of consumption lost to egestion"+
            "\n\t       aSDA  - Double - coefficient on weight of fraction of assimilated energy lost to SDA"+
            "\n\t       bSDA  - Double - exponent on weight of fraction of assimilated energy lost to SDA"+
            "\n\t       UA   - Double - weight-specific excretion coefficient"+
            "\n\t       ex   - Double - amount of energy lost to exuviae cost daily "+
            "\n\t"+
            "\n\t       sigRt  - Double - std. dev. in linear growth rate"+
            "\n\t* Variables:"+
            "\n\t*      vars - double[]{dt,w0,T}."+
            "\n\t*      dt - double - time interval   ([time])"+
            "\n\t*      instar - int - which instar the crab is in"+
            "\n\t*      w0 - double - weight at time t0 ([weight])"+
            "\n\t*      T  - double - temperature (deg C)"+
            "\n\t* Value:"+
            "\n\t*      w(dt) - Double - weight at time t+dt"+
            "\n\t* Calculation:"+
            "\n\t*     ;"+
            "\n\t* "+
            "\n\t* @author Christine Stawitz"+
            "\n\t*  Citations:"+
            "\n\t* Kitchell, J.F., Stewart, D.J., and D. Weininger. 1977. "+
            "\n\t* Applications of a Bioenergetics Model to Yellow Perch (Perca"+
            "\n\t* flavescens) and Walleye (Stizostedion vitreum vitreum)."+
            "\n\t* Journal of the Fisheries Research Board of Canada 34(10) 1922-1935."+
            "\n\t* Holsman, K.K., Armstrong, D.A., Beauchamp, D.A., and J.L. Ruesink."+
            "\n\t* 2003. The Necessity for Intertidal Foraging by Estuarine Populations"+
            "\n\t* of Subadult Dungeness Crab, Cancer magister: Evidence from a Bioenergetics Model."+
            "\n\t* Estuaries 26(4B): pp. 1155-1173."+
            "\n\t**************************************************************************";
    /** random number generator */
    protected static final RandomNumberGenerator rng = GlobalInfo.getInstance().getRandomNumberGenerator();
    /** number of settable parameters */
    public static final int numParams = 21;
    /** number of sub-functions */
    public static final int numSubFuncs = 1;

    /** key to set pVal parameter */
    public static final String PARAM_pVal = "pVal";
    /** key to set aC parameter */
    public static final String PARAM_aC = "aC";
    /** key to set bC parameter */
    public static final String PARAM_bC = "bC";
    /** key to set cmT parameter */
    public static final String PARAM_cmT = "cmT";
    /** key to set coT parameter */
    public static final String PARAM_coT = "coT";
    /** key to set c1c parameter */
    public static final String PARAM_c1c = "c1c";
    /** key to set aR parameter */
    public static final String PARAM_ACT = "ACT";
    /** key to set aR parameter */
    public static final String PARAM_aR = "aR";
    /** key to set bR parameter */
    public static final String PARAM_bR = "bR";
    /** key to set rmT parameter */
    public static final String PARAM_rmT = "rmT";
    /** key to set roT parameter */
    public static final String PARAM_roT = "roT";
    /** key to set c1r parameter */
    public static final String PARAM_c1r = "c1r";
    /** key to set FA parameter */
    public static final String PARAM_FA = "FA";
    /** key to set aSDA parameter */
    public static final String PARAM_aSDA = "aSDA";
    /** key to set bSDA parameter */
    public static final String PARAM_bSDA = "bSDA";
    /** key to set UA parameter */
    public static final String PARAM_UA = "UA";
    /** key to set sigRate parameter */
    public static final String PARAM_sigRt = "std. dev.";
       /** key to set ex parameter */
    public static final String PARAM_ex = "ex";

    
    public static final String PARAM_calPerGram = "calPerGram";
    
    public static final String PARAM_wRat = "wRat";
    
    public static final String PARAM_sex = "sex";
    
    /** value of pVal parameter */
    private static final double[][] pVal = new double[][]{
        {0.91, 0.85, 0.82, 0.86, 0.87, 0.89, 0.89, 0.92, 0.96, 0.99, 1.1, 1.18, 1.1},
        {0.89, 0.8, 0.77, 0.71, 0.75, 0.78, 0.79, 0.85, 0.95, 1.0, 1.17, 1.25, 1.3}
        //{1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1, 1.1},
        //{1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25, 1.25}
    };
    
    /** value of aC parameter */
    private double aC = 18.08;
    /** value of bC parameter */
    private double bC = 1.75;
    /** value of cmT parameter */
    private double cmT = 14;
    /** value of coT parameter */
    private double coT = 5;
    /** value of c1c parameter */
    private double c1c = 5.5;
    
    /** value of aR parameter */
    private double ACT = 1.46;
    /** value of aR parameter */
    private double aR = Math.exp(3.966);
    /** value of bR parameter */
    private double bR = 1.722;
    /** value of rmT parameter */
    private double rmT = 13.32;
    /** value of roT parameter */
    private double roT = 8.04;
    /** value of c1r parameter */
    private double c1r = 2.2;
    
    /** value of FA parameter */
    private double FA = .11;
    /** value of aSDA parameter */
    private double aSDA = 0.019;
        /** value of bSDA parameter */
    private double bSDA = .17;
    
    /** value of UA parameter */
    private double UA = .018;
    
    /** value of sigRate parameter */
    private double sigRt = 0;
    
    private double wRat = .136;
    private double calPerGram = 3900;
    
    private double sex = 0;
    
    /** constructor for class */
    public CrabBioenergeticsGrowthFunction(){
        super(numParams,numSubFuncs,DEFAULT_type,DEFAULT_name,DEFAULT_descr,DEFAULT_fullDescr);
        String key; 
        key = PARAM_pVal;addParameter(key,double[].class,"realized fraction of max consumption");
        
        key = PARAM_aC; addParameter(key,Double.class,"linear coefficient of weight-dependent max consumption");
        key = PARAM_bC; addParameter(key,Double.class,"exponent coefficient of weight-dependent max consumption");
        key = PARAM_cmT;addParameter(key,Double.class,"max temperature at which consumption occurs");
        key = PARAM_coT;addParameter(key,Double.class,"temperature at which consumption is maximized");
        key = PARAM_c1c;addParameter(key,Double.class,"consumption coefficient");
        
        key = PARAM_ACT;addParameter(key,Double.class,"respiration activity multiplier");
        key = PARAM_aR; addParameter(key,Double.class,"linear coefficient of weight-dependent respiration");
        key = PARAM_bR; addParameter(key,Double.class,"exponent coefficient of weight-dependent respiration");
        key = PARAM_rmT;addParameter(key,Double.class,"max temperature at which respiration occurs");
        key = PARAM_roT;addParameter(key,Double.class,"temperature at which respiration is maximized");
        key = PARAM_c1r;addParameter(key,Double.class,"respiration coefficient");
        
        key = PARAM_FA; addParameter(key,Double.class,"fraction of consumption lost to egestion");
        key = PARAM_aSDA; addParameter(key,Double.class,"coefficient of fraction of assimilated energy lost to SDA");
        key = PARAM_bSDA; addParameter(key,Double.class,"exponent of fraction of assimilated energy lost to SDA");
        key = PARAM_UA; addParameter(key,Double.class,"excretion fraction");
        key = PARAM_ex; addParameter(key, Double.class, "daily cost of exuviae");
        key = PARAM_sigRt;addParameter(key,Double.class,"std. dev. in linear growth rate");
        key = PARAM_wRat; addParameter(key, Double.class, "dry to wet weight ratio of crab");
        key = PARAM_calPerGram; addParameter(key, Double.class, "calories per gram of crab tissue");
        key = PARAM_sex; addParameter(key, Double.class, "sex of the crab");
    }
    
    @Override
    public CrabBioenergeticsGrowthFunction clone(){
        CrabBioenergeticsGrowthFunction clone = new CrabBioenergeticsGrowthFunction();
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
        //the following sets the value in the parameter map AND in the local variable
        if (super.setParameterValue(param, value)){
            switch (param) {
                case PARAM_aC:
                    aC = ((Double) value).doubleValue();
                    break;
                case PARAM_bC:
                    bC = ((Double) value).doubleValue();
                    break;
                case PARAM_cmT:
                    cmT = ((Double) value).doubleValue();
                    break;
                case PARAM_coT:
                    coT = ((Double) value).doubleValue();
                    break;
                case PARAM_c1c:
                    c1c = ((Double) value).doubleValue();
                    break;
                case PARAM_ACT:
                    ACT = ((Double) value).doubleValue();
                    break;
                case PARAM_aR:
                    aR = ((Double) value).doubleValue();
                    break;
                case PARAM_bR:
                    bC = ((Double) value).doubleValue();
                    break;
                case PARAM_rmT:
                    rmT = ((Double) value).doubleValue();
                    break;
                case PARAM_roT:
                    roT = ((Double) value).doubleValue();
                    break;
                case PARAM_c1r:
                    c1r = ((Double) value).doubleValue();
                    break;
                case PARAM_FA:
                    FA = ((Double) value).doubleValue();
                    break;
                case PARAM_aSDA:
                    aSDA = ((Double) value).doubleValue();
                    break;
                case PARAM_bSDA:
                    bSDA = ((Double) value).doubleValue();
                    break;
                case PARAM_UA:
                    UA = ((Double) value).doubleValue();
                    break;
                case PARAM_sigRt:
                    sigRt = ((Double) value).doubleValue();
                    break;
                case PARAM_calPerGram:
                    calPerGram = ((Double) value).doubleValue();
                    break;
                case PARAM_wRat:
                    wRat = ((Double) value).doubleValue();
                    break;
                case PARAM_sex:
                    sex = ((Double) value).doubleValue();
                    break;
            }
        }
        return false;
    }

    /**
     * Calculates the value of the function, given the current parameter params 
     * and the input variable.
     * 
     * @param vars - the inputs variables, {dt,w0,T}, as a double[].
     * @return     - the function value (w[dt]) as a Double 
     */
    @Override
    public Double calculate(Object vars) {
        double[] lvars = (double[]) vars;//cast object to required double[]
        int i = 0;
        int instar = Math.max((int) lvars[i++],1);
        double w0 = lvars[i++];
        double T   = lvars[i++];
        double ex = lvars[i++];
        double maxC = aC*Math.pow(w0,bC-1.0);//max consumption
        double p = pVal[(int) sex][instar-1];
        double c = maxC*p*calcF(T,cmT,coT,c1c);//realized weight-specific consumption
        double maxR = aR*Math.pow(w0,bR-1.0)*.00463*24.0;       //reference-level respiration
        double r = maxR*ACT*calcF(T,rmT,roT,c1r);
        double f = FA*c;     //weight-specific egestion
        double SDA = (24.0*aSDA*Math.exp(bSDA*T))/(1000.0);
        double s = (SDA*f);//temperature-specific loss due to specific dynamic action
        double m = r+s;       //weight-specific metabolic loss rate
        double e = UA*w0; //weight-specific excretion
        double w = f+e;       //weight-specific waste rate
        double g = (c-(m+w+ex))/(calPerGram*wRat*w0);   //weight-specific total daily growth rate / weight 
        if (sigRt>0) g += rng.computeNormalVariate()*sigRt; 
        return g;
    }

    private double calcF(double T, double Tm, double T0, double a){
        double v = (Tm-T)/(Tm-T0);
        double w = Math.log(a)*(Tm-T0);
        double y = Math.log(a)*(Tm-T0+2);
        double x = (Math.pow(w,2)*Math.pow(1+Math.pow((1+40/y),0.5),2))/400;
        double f = Math.pow(v,x)*Math.exp(x*(1-v));
        return f;
    }

}

