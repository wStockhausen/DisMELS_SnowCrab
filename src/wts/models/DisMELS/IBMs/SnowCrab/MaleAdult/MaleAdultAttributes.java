/*
 * MaleAdultAttributes.java
 *
 * Created on October 17, 2017.
 *
 */

package wts.models.DisMELS.IBMs.SnowCrab.MaleAdult;

import java.util.*;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;
import wts.models.DisMELS.framework.AbstractLHSAttributes2;
import wts.models.DisMELS.framework.IBMAttributes.IBMAttribute;
import wts.models.DisMELS.framework.IBMAttributes.IBMAttributeDouble;

/**
 * DisMELS class representing attributes for snow crab adults.
 * 
 * @author William Stockhausen
 */
@ServiceProvider(service=wts.models.DisMELS.framework.LifeStageAttributesInterface.class)
public class MaleAdultAttributes extends AbstractLHSAttributes2 {
    
    /** Number of attributes defined by this class (including typeName) */
    public static final int numNewAttributes = 5;
    
    public static final String PROP_size        = "size (mm CW)";
    public static final String PROP_weight      = "weight (g)";
    public static final String PROP_gonadStage  = "gonad stage";
    public static final String PROP_temperature = "temperature deg C";
    public static final String PROP_salinity    = "salinity";
    
    protected static final Set<String> newKeys = new LinkedHashSet<>((int)(2*numNewAttributes));
    protected static final Set<String> allKeys = new LinkedHashSet<>((int)(2*(numAttributes+numNewAttributes)));
    protected static final Map<String,IBMAttribute> mapAllAttributes = new HashMap<>((int)(2*(numAttributes+numNewAttributes)));
    protected static final String[] aKeys      = new String[numAttributes+numNewAttributes-1];//does not include typeName
    protected static final Class[]  classes    = new Class[numAttributes+numNewAttributes];
    protected static final String[] shortNames = new String[numAttributes+numNewAttributes];
   
    private static final Logger logger = Logger.getLogger(MaleAdultAttributes.class.getName());
    
    /**
     * This constructor is provided only to facilitate the ServiceProvider functionality.
     * DO NOT USE IT!!
     */
    public MaleAdultAttributes(){
        super("NULL");
        finishInstantiation();
    }
    
    /**
     * Creates a new attributes instance with type name 'typeName'.
     */
    public MaleAdultAttributes(String typeName) {
        super(typeName);
        finishInstantiation();
    }
    
    /**
     * Returns a deep copy of the instance.  Values are copied.  
     * Any listeners on 'this' are not(?) copied, so these need to be hooked up.
     * @return - the clone.
     */
    @Override
    public Object clone() {
        MaleAdultAttributes clone = new MaleAdultAttributes(typeName);
        for (String key: allKeys) clone.setValue(key,this.getValue(key));
        return clone;
    }

    /**
     * Returns a new instance constructed from the values of the string[].
     * The first value in the string vector must be the type name.
     * Values are set internally by calling setValues(strv) on the new instance.
     * @param strv - vector of values (as Strings) 
     * @return - the new instance
     */
    @Override
    public MaleAdultAttributes createInstance(final String[] strv) {
        MaleAdultAttributes atts = new MaleAdultAttributes(strv[0]);//this sets atts.typeName
        atts.setValues(strv);
        return atts;
    }
    
    private void finishInstantiation(){
        if (newKeys.isEmpty()){
            //set static field information
            mapAllAttributes.putAll(AbstractLHSAttributes2.mapAttributes);//add from superclass
            String key;
            key = PROP_size;       newKeys.add(key); mapAllAttributes.put(key,new IBMAttributeDouble(key,"size"));
            key = PROP_weight;     newKeys.add(key); mapAllAttributes.put(key,new IBMAttributeDouble(key,"weight"));
            key = PROP_gonadStage; newKeys.add(key); mapAllAttributes.put(key,new IBMAttributeDouble(key,"gonadStage"));
            key = PROP_temperature;newKeys.add(key); mapAllAttributes.put(key,new IBMAttributeDouble(key,"temperature"));
            key = PROP_salinity;   newKeys.add(key); mapAllAttributes.put(key,new IBMAttributeDouble(key,"salinity"));
            allKeys.addAll(AbstractLHSAttributes2.keys);//add from superclass
            allKeys.addAll(newKeys);//add from this class
            Iterator<String> it = allKeys.iterator();
            int j = 0; it.next();//skip typeName
            while (it.hasNext()) aKeys[j++] = it.next();
        }
        //set instance information
        Map<String,Object> tmpMapValues = new HashMap<>((int)(2*(numNewAttributes+numAttributes)));
        tmpMapValues.putAll(mapValues);//copy from super
        tmpMapValues.put(PROP_size,       new Double(0));
        tmpMapValues.put(PROP_weight,     new Double(0));
        tmpMapValues.put(PROP_gonadStage, new Double(0));
        tmpMapValues.put(PROP_temperature,new Double(-1));
        tmpMapValues.put(PROP_salinity,   new Double(-1));
        mapValues = tmpMapValues;//assign to super
    }

    /**
     * Returns the attribute values as an ArrayList (including typeName).
     * 
     * @return 
     */
    @Override
    public ArrayList getArrayList() {
        ArrayList a = super.getArrayList();
        for (String key: newKeys) a.add(getValue(key));
        return a;
    }

    /**
     * Returns the attributes values (not including typeName) as an Object[].
     * 
     * @return 
     */
    @Override
    public Object[] getAttributes() {
        Object[] atts = new Object[numNewAttributes+AbstractLHSAttributes2.numAttributes-1];
        int j = 0;
        Iterator<String> it = allKeys.iterator();
        it.next();//skip PROP_typeName
        while (it.hasNext()) atts[j++] = getValue(it.next()); 
        return atts;
    }
    
    /**
     * Returns a CSV string representation of the attribute values.
     * 
     *@return - CSV string attribute values
     */
    @Override
    public String getCSV() {
        String str = super.getCSV();
        Iterator<String> it = newKeys.iterator();
        while (it.hasNext()) str = str+cc+getValueAsString(it.next());
        return str;
    }
                
    /**
     * Returns the comma-delimited string corresponding to the attributes
     * to be used as a header for a csv file.  
     * Use getCSV() to get the string of actual attribute values.
     *
     *@return - String of CSV header names
     */
    @Override
    public String getCSVHeader() {
        Iterator<String> it = allKeys.iterator();
        String str = it.next();//typeName
        while (it.hasNext()) str = str+cc+it.next();
        return str;
    }
                
    /**
     * Returns the comma-delimited string corresponding to the attributes
     * to be used as a header for a csv file.  
     *
     *@return - String of CSV header names (short style)
     */
    @Override
    public String getCSVHeaderShortNames() {
        Iterator<String> it = allKeys.iterator();
        String str = mapAllAttributes.get(it.next()).shortName;//this is "typeName"
        while (it.hasNext()) str = str+cc+mapAllAttributes.get(it.next()).shortName;
        return str;
    }

    /**
     * Returns Class types for all attributes (including typeName) as a Class[]
     * in the order the allKeys are defined.
     * 
     * @return 
     */
    @Override
    public Class[] getClasses() {
        if (classes[0]==null){
            int j = 0;
            for (String key: allKeys){
                classes[j++] = mapAllAttributes.get(key).getValueClass();
            }
        }
        return classes;
    }

    /**
     * Returns keys for all attributes excluding typeName as a String[]
     * in the order the keys are defined.
     * 
     * @return 
     */
    @Override
    public String[] getKeys() {        
        return aKeys;
    }

    /**
     * Returns short names for all attributes (including typeName) as a String[]
     * in the order the allKeys are defined.
     * 
     * @return 
     */
    @Override
    public String[] getShortNames() {
        if (shortNames[0]==null){
            int j = 0;
            for (String key: allKeys){
                shortNames[j++] = mapAllAttributes.get(key).shortName;
            }
        }
        return shortNames;
    }
    
    /**
     * Sets attribute values to those of input String[].
     * @param strv - String[] of attribute values.
     */
    @Override
    public void setValues(final String[] strv) {
        super.setValues(strv);//set the standard attribute values
        //set the values of the new attributes
        int j = AbstractLHSAttributes2.numAttributes;
        try {
            for (String key: newKeys) setValueFromString(key,strv[j++]);
        } catch (java.lang.IndexOutOfBoundsException ex) {
            //@TODO: should throw an exception here that identifies the problem
            String[] aKeys = new String[MaleAdultAttributes.allKeys.size()];
            aKeys = MaleAdultAttributes.allKeys.toArray(aKeys);
                String str = "Missing attribute value for "+aKeys[j-1]+".\n"+
                             "Prior values are ";
                for (int i=0;i<(j);i++) str = str+strv[i]+" ";
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        str,
                        "Error setting attribute values:",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                throw ex;
        } catch (java.lang.NumberFormatException ex) {
            String[] aKeys = new String[MaleAdultAttributes.allKeys.size()];
            aKeys = MaleAdultAttributes.allKeys.toArray(aKeys);
            String str = "Bad attribute value for "+aKeys[j-2]+".\n"+
                         "Value was '"+strv[j-1]+"'.\n"+
                         "Entry was '";
            try {
                for (int i=0;i<(strv.length-1);i++) {
                    if ((strv[i]!=null)&&(!strv[i].isEmpty())) {
                        str = str+strv[i]+", ";
                    } else {
                        str = str+"<missing_value>, ";
                    }
                }
                if ((strv[strv.length-1]!=null)&&(!strv[strv.length-1].isEmpty())) {
                    str = str+strv[strv.length-1]+"'.";
                } else {
                    str = str+"<missing_value>'.";
                }
            }  catch (java.lang.IndexOutOfBoundsException ex1) {
                //do nothing
            }
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    str,
                    "Error setting attribute values:",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            throw ex;
        }
    }
    
    @Override
    public String getValueAsString(String key){
        Object val = getValue(key);
        IBMAttribute att = mapAllAttributes.get(key);
        att.setValue(val);
        String str = att.getValueAsString();
        return str;
    }
    
    @Override
    public void setValueFromString(String key, String value) throws NumberFormatException {
        if (!key.equals(PROP_typeName)){
            IBMAttribute att = mapAllAttributes.get(key);
            att.parseValue(value);
            setValue(key,att.getValue());
        }
    }
}
