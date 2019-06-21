/*
 * AbstractPelagicStageAttributes.java
 */

package disMELS.IBMs.SnowCrab;

import java.beans.PropertyChangeSupport;
import java.util.*;
import wts.models.DisMELS.framework.IBMAttributes.*;
import wts.models.DisMELS.framework.LifeStageAttributesInterface;
import wts.models.DisMELS.framework.Types;

/**
 * This class defines a default set of attributes for
 * pelagic life stages in the DisMELS IBM for snow crab.
 */
public abstract class AbstractPelagicStageAttributes implements LifeStageAttributesInterface {
    
    // attributes in addition to those from LifeStageAttributesInterface
    /** the number of new attributes defined by this class */
    public static final int PROP_NumNewAtts = 5;
    /** the property key for the individual's molt indicator */
    public static final String PROP_moltindicator = "molt indicator";
    /** the property key for the shell thickness for the individual */
    public static final String PROP_shellthick  = "shell thickness";
    /** the property key for the in situ temperature at the individual's location */
    public static final String PROP_temperature = "temperature";
    /** the property key for the in situ salinity at the individual's location */
    public static final String PROP_salinity    = "salinity";
    /** the property key for the in situ pH at the individual's location */
    public static final String PROP_ph          = "pH";
    
    /** Number of attributes defined by this class (including typeName) */
    public static final int numAttributes = LifeStageAttributesInterface.PROP_NumAtts+PROP_NumNewAtts;
    
    protected static final Set<String> keys = new LinkedHashSet<>(32);
    
    /** map to IBMAttributes defined in this class */
    protected static final Map<String,IBMAttribute> mapAttributes = new HashMap<>(32);
    
    /* LHS type name assigned to instance*/
    protected String typeName;
    /* map of values for attributes defined in the this class (subclasses should add their attribute values to it) */
    protected Map<String,Object> mapValues;
    
    /**
     * Utility field used by bound properties.
     */
    protected PropertyChangeSupport propertySupport;

    /** 
     * Assigns the life stage type name to the constructed subclass instance and adds
     * the attribute keys and information defined in this class to the static Set "keys" 
     * and the static Map mapAttributes.
     * 
     * Subclasses should call this constructor with a valid life stsage type name from
     * all constructors to set the type name.  They should then 
     * 
     *@param typeName - the type name as a String.
     */
    protected AbstractPelagicStageAttributes(String typeName) {
        this.typeName = typeName;
        propertySupport =  new PropertyChangeSupport(this);
        if (mapAttributes.isEmpty()){
            //assign static-level attributes information for this class
            String key;
            key = PROP_typeName;   keys.add(key); mapAttributes.put(key,new IBMAttributeString(key,"typeName"));
            key = PROP_id;         keys.add(key); mapAttributes.put(key,new IBMAttributeLong(key,"id"));
            key = PROP_parentID;   keys.add(key); mapAttributes.put(key,new IBMAttributeLong(key,"parentID"));
            key = PROP_origID;     keys.add(key); mapAttributes.put(key,new IBMAttributeLong(key,"origID"));
            key = PROP_startTime;  keys.add(key); mapAttributes.put(key,new IBMAttributeROMSDate(key,"startTime"));
            key = PROP_time;       keys.add(key); mapAttributes.put(key,new IBMAttributeROMSDate(key,"time"));
            key = PROP_horizType;  keys.add(key); mapAttributes.put(key,new IBMAttributeInteger(key,"horizType"));
            key = PROP_vertType;   keys.add(key); mapAttributes.put(key,new IBMAttributeInteger(key,"vertType"));
            key = PROP_horizPos1;  keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"horizPos1"));
            key = PROP_horizPos2;  keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"horizPos2"));
            key = PROP_vertPos;    keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"vertPos"));
            key = PROP_bathym;     keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"bathym"));
            key = PROP_gridCellID; keys.add(key); mapAttributes.put(key,new IBMAttributeString(key,"gridCellID"));
            key = PROP_track;      keys.add(key); mapAttributes.put(key,new IBMAttributeString(key,"track"));
            key = PROP_active;     keys.add(key); mapAttributes.put(key,new IBMAttributeBoolean(key,"active"));
            key = PROP_alive;      keys.add(key); mapAttributes.put(key,new IBMAttributeBoolean(key,"alive"));
            key = PROP_age;        keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"age"));
            key = PROP_ageInStage; keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"ageInStage"));
            key = PROP_number;     keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"number"));
            
            key = PROP_moltindicator; keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"molt indicator"));
            key = PROP_shellthick;    keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"shellthickness"));
            key = PROP_temperature;   keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"temperature"));
            key = PROP_salinity;      keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"salinity"));
            key = PROP_ph;            keys.add(key); mapAttributes.put(key,new IBMAttributeDouble(key,"pH"));
        }
        //assign instance-level attributes values for this class
        mapValues = new HashMap<>(2*numAttributes);
        mapValues.put(PROP_id,        new Long(-1));
        mapValues.put(PROP_parentID,  new Long(-1));
        mapValues.put(PROP_origID,    new Long(-1));
        mapValues.put(PROP_startTime, new Double(0));
        mapValues.put(PROP_time,      new Double(0));
        mapValues.put(PROP_horizType, new Integer(0));
        mapValues.put(PROP_vertType,  new Integer(0));
        mapValues.put(PROP_horizPos1, new Double(0));
        mapValues.put(PROP_horizPos2, new Double(0));
        mapValues.put(PROP_vertPos,   new Double(0));
        mapValues.put(PROP_bathym,    new Double(0));
        mapValues.put(PROP_gridCellID,"");
        mapValues.put(PROP_track,     "");
        mapValues.put(PROP_active,    false);
        mapValues.put(PROP_alive,     true);
        mapValues.put(PROP_age,       new Double(0));
        mapValues.put(PROP_ageInStage,new Double(0));
        mapValues.put(PROP_number,    new Double(1));
        
        mapValues.put(PROP_moltindicator,new Double(0));
        mapValues.put(PROP_shellthick,   new Double(-1));
        mapValues.put(PROP_temperature,  new Double(-1));
        mapValues.put(PROP_salinity,     new Double(-1));
        mapValues.put(PROP_ph,           new Double(0));
    }

    @Override
    public abstract LifeStageAttributesInterface createInstance(final String[] strv);

    /**
     *  This method should be overridden by extending classes.
     */
    @Override
    public abstract Object clone() throws CloneNotSupportedException;
    
    /**
     *  Returns attribute values as an Object array.
     *  Subclasses should order the values in the array
     *  in the same order as in the keys array.
     */
    @Override
    public abstract Object[] getAttributes();

    /**
     * Gets the parameter keys.
     * This method is abstract to force dispatch of method to overriding method
     * in subclasses.
     * 
     * @return - keys as String array.
     */
    @Override
    public abstract String[] getKeys();
    
    @Override
    public abstract Class[] getClasses();

    @Override
    public abstract String[] getShortNames();
    
    /**
     * NOTE: This method should be overridden in subclasses, with the subclass method
     * possibly calling super.setValues(strv) to set the values for this class.
     * 
     * The order of the String[] should be:
     *  typeName [this is not set]
     *  id
     *  parentID
     *  origID
     *  startTime
     *  time
     *  horizType
     *  vertType
     *  horizPos1
     *  horizPos2
     *  vertPos
     *  bathym
     *  gridCellID
     *  track
     *  active
     *  alive
     *  age
     *  ageInStage
     *  number
     * 
     *  molt indicator
     *  shell thickness
     *  temperature
     *  salinity
     *  ph
     * 
     * @param strv - String[] of attribute values.
     */
    @Override
    public void setValues(final String[] strv){
        int j = 1;
        try {
            Iterator<String> it = keys.iterator();
            it.next();//skip typeName
            while (it.hasNext()) setValueFromString(it.next(),strv[j++]);
        } catch (java.lang.IndexOutOfBoundsException ex) {
            //@TODO: should throw an exception here that identifies the problem
            String[] aKeys = new String[keys.size()];
            aKeys = keys.toArray(aKeys);
                String str = "Missing attribute value for "+aKeys[j]+".\n"+
                             "Prior values are ";
                for (int i=0;i<(j);i++) str = str+strv[i]+" ";
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        str,
                        "Error setting attribute values:",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                throw ex;
        } catch (java.lang.NumberFormatException ex) {
            String[] aKeys = new String[keys.size()];
            aKeys = keys.toArray(aKeys);
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

    /**
     * Gets type name and attribute values as an ArrayList.  Subclasses should 
     * override this method. The overriding method can call super.getArrayList()
     * to return an ArrayList of the correct size and with the values filled in
     * for the attributes defined in this class.
     * 
     * @return - the array list.
     */
    @Override
    public ArrayList getArrayList() {
        ArrayList a = new ArrayList(mapValues.size());
        a.add(typeName);
        Iterator<String> it = keys.iterator();
        it.next();//skip PROP_typeName
        while (it.hasNext()) a.add(getValue(it.next()));
        return a;
    }
    
    /**
     * Sets the value for the mapValues object indicated by the key.
     * This overrides the superclass method to provide property change support.
     * 
     * @param key   - String giving key name
     * @param value - Object to be set as value
     */
    @Override
    public void setValue(String key, Object value) {
        if (mapValues.containsKey(key)) {
            Object old = mapValues.get(key);
            Object val = mapValues.put(key,value);
            propertySupport.firePropertyChange(key,old,val);
        }
    }

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertySupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertySupport.removePropertyChangeListener(l);
    }
    
    @Override
    public boolean isActive() {
        return getValue(PROP_active,b);
    }
    
    /**
     * Sets "active" value.
     * 
     * @param b - boolean indicating whether associated LHS is active or not
     */
    @Override
    public void setActive(boolean b) {
        setValue(PROP_active,b);
    }

    @Override
    public boolean isAlive() {
        return getValue(PROP_alive,b);
    }

    /**
     * Sets "alive" value.
     * 
     * @param b - boolean indicating whether associated LHS is alive or not
     */
    @Override
    public void setAlive(boolean b) {
        setValue(PROP_alive,b);
    }

    @Override
    public long getID() {
        return getValue(PROP_id,l);
    }

    @Override
    public double getStartTime() {
        return getValue(PROP_startTime,d);
    }

    /**
     * Sets start time for LHS.
     * 
     * @param t - new start time (in seconds)
     */
    @Override
    public void setStartTime(double t) {
        setValue(PROP_startTime,t);
    }
    
    /**
     * Returns a CSV string representation of the attribute values.
     * This method should be overriden by subclasses that add additional attributes, 
     * possibly calling super.getCSV() to get an initial csv string to which 
     * additional field values could be appended.
     * 
     *@return - CSV string attribute values
     */
    @Override
    public String getCSV() {
        String str = typeName;
        Iterator<String> it = keys.iterator();
        it.next();//skip typeName
        while (it.hasNext()) {
            String key = it.next();
            str = str+cc+getValueAsString(key);
        }
        return str;
    }
                
    /**
     * Returns the comma-delimited string corresponding to the attributes
     * to be used as a header for a csv file.  
     * This should be overriden by subclasses that add additional attributes, 
     * possibly calling super.getCSVHeader() to get an initial header string 
     * to which additional field names could be appended.
     * Use getCSV() to get the string of actual attribute values.
     *
     *@return - String of CSV header names
     */
    @Override
    public String getCSVHeader() {
        Iterator<String> it = keys.iterator();
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
        Iterator<String> it = keys.iterator();
        String str = mapAttributes.get(it.next()).shortName;//this is "typeName"
        while (it.hasNext())  str = str+cc+mapAttributes.get(it.next()).shortName;
        return str;
    }
    
    /**
     * Gets the LHS type name assigned to the instance.
     *
     * @return - the LHS type name as a String.
     */
    @Override
    public String getTypeName() {return typeName;}
                
    /**
     * Returns the position (x,y,z) or (lon,lat,z) as a 3-element double[].  If the
     * horizType is Lat/Lon (Types.HORIZ_LL) the returned coordinates are
     * relative to NAD83 (Greenwich PM, range -180 to 180).
     * 
     * @return - double[] {x,y,z} or {lon,lat,z} or {I,J,K}
     */
    @Override
    public double[] getGeometry() {
        int vertType=0;
        double x=0,y=0,z=0;
        vertType = getValue(PROP_vertType,vertType);
        x = getValue(PROP_horizPos1,x);
        y = getValue(PROP_horizPos2,y);
        z = getValue(PROP_vertPos,z);
        if (vertType==Types.VERT_H) z = -z;
        return new double[]{x,y,z};
    }
                
    /**
     * Sets the horizontal position from a double[].  The
     * coordinates should be NAD83, if lon/lat.
     * @param pt - {x,y} or {lon,lat}. 
     */
    @Override
    public void setGeometry(double[] pt) {
        mapValues.put(PROP_horizPos1, pt[0]);
        mapValues.put(PROP_horizPos2, pt[1]);
    }

    
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//      The following are implemented to extend LifeStageDataInterface
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    @Override
    public Boolean getValue(String key, Boolean b) {
        Boolean v = null;
        try {
            v = (Boolean) mapValues.get(key);
        } catch (ClassCastException exc) {}
        return v;
    }

    @Override
    public Double getValue(String key, Double d) {
        Double v = null;
        try {
            v = (Double) mapValues.get(key);
        } catch (ClassCastException exc) {}
        return v;
    }

    @Override
    public Integer getValue(String key, Integer i) {
        Integer v = null;
        try {
            v = (Integer) mapValues.get(key);
        } catch (ClassCastException exc) {}
        return v;
    }

    @Override
    public Long getValue(String key, Long l) {
        Long v = null;
        try {
            v = (Long) mapValues.get(key);
        } catch (ClassCastException exc) {}
        return v;
    }

    @Override
    public String getValue(String key, String s) {
        String v = null;
        try {
            v = (String) mapValues.get(key);
        } catch (ClassCastException exc) {}
        return v;
    }

    @Override
    public boolean getValue(String key, boolean b) throws ClassCastException {
        boolean v = ((Boolean) mapValues.get(key));
        return v;
    }

    @Override
    public double getValue(String key, double d) throws ClassCastException {
        double v = ((Double) mapValues.get(key));
        return v;
    }

    @Override
    public int getValue(String key, int i) throws ClassCastException {
        int v = ((Integer) mapValues.get(key));
        return v;
    }

    @Override
    public long getValue(String key, long l) throws ClassCastException {
        long v = ((Long) mapValues.get(key));
        return v;
    }

    @Override
    public Object getValue(String key) {
        return mapValues.get(key);
    }
//    
//    @Override
//    public void setValue(String key, boolean value) {
//        Boolean v = value;
//        setValue(key, v);
//    }
    
    @Override
    public void setValue(String key, double value) {
        setValue(key,new Double(value));
    }
    
    @Override
    public void setValue(String key, float value) {
        setValue(key,new Float(value));
    }
    
    @Override
    public void setValue(String key, int value) {
        setValue(key,new Integer(value));
    }
    
    @Override
    public void setValue(String key, long value) {
        setValue(key,new Long(value));
    }
    
    public String getValueAsString(String key){
        Object val = getValue(key);
        IBMAttribute att = mapAttributes.get(key);
        att.setValue(val);
        String str = att.getValueAsString();
        return str;
    }
    
    public void setValueFromString(String key, String value) throws NumberFormatException {
        if (!key.equals(PROP_typeName)){
            IBMAttribute att = mapAttributes.get(key);
            att.parseValue(value);
            setValue(key,att.getValue());
        }
    }
}
