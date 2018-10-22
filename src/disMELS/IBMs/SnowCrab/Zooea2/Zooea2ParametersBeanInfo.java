/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package disMELS.IBMs.SnowCrab.Zooea2;

import java.beans.*;

/**
 *
 * @author WilliamStockhausen
 */
public class Zooea2ParametersBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
        // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_CSV = 0;
    private static final int PROPERTY_CSVHeader = 1;
    private static final int PROPERTY_IBMFunctionCategories = 2;
    private static final int PROPERTY_IBMParameterKeys = 3;
    private static final int PROPERTY_keys = 4;
    private static final int PROPERTY_typeName = 5;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[6];
    
        try {
            properties[PROPERTY_CSV] = new PropertyDescriptor ( "CSV", disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "getCSV", null ); // NOI18N
            properties[PROPERTY_CSVHeader] = new PropertyDescriptor ( "CSVHeader", disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "getCSVHeader", null ); // NOI18N
            properties[PROPERTY_IBMFunctionCategories] = new PropertyDescriptor ( "IBMFunctionCategories", disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "getIBMFunctionCategories", null ); // NOI18N
            properties[PROPERTY_IBMParameterKeys] = new PropertyDescriptor ( "IBMParameterKeys", disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "getIBMParameterKeys", null ); // NOI18N
            properties[PROPERTY_keys] = new PropertyDescriptor ( "keys", disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "getKeys", null ); // NOI18N
            properties[PROPERTY_typeName] = new PropertyDescriptor ( "typeName", disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "getTypeName", "setTypeName" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
        // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_propertyChangeListener = 0;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];
    
        try {
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events
        // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_clone0 = 0;
    private static final int METHOD_createInstance1 = 1;
    private static final int METHOD_getIBMFunction2 = 2;
    private static final int METHOD_getIBMFunctionKeysByCategory3 = 3;
    private static final int METHOD_getIBMParameter4 = 4;
    private static final int METHOD_getSelectedIBMFunctionForCategory5 = 5;
    private static final int METHOD_getValue6 = 6;
    private static final int METHOD_getValue7 = 7;
    private static final int METHOD_getValue8 = 8;
    private static final int METHOD_getValue9 = 9;
    private static final int METHOD_getValue10 = 10;
    private static final int METHOD_getValue11 = 11;
    private static final int METHOD_getValue12 = 12;
    private static final int METHOD_getValue13 = 13;
    private static final int METHOD_getValue14 = 14;
    private static final int METHOD_getValue15 = 15;
    private static final int METHOD_setIBMFunction16 = 16;
    private static final int METHOD_setSelectedIBMFunctionForCategory17 = 17;
    private static final int METHOD_setValue18 = 18;
    private static final int METHOD_setValue19 = 19;
    private static final int METHOD_setValue20 = 20;
    private static final int METHOD_setValue21 = 21;
    private static final int METHOD_setValue22 = 22;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[23];
    
        try {
            methods[METHOD_clone0] = new MethodDescriptor(disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class.getMethod("clone", new Class[] {})); // NOI18N
            methods[METHOD_clone0].setDisplayName ( "" );
            methods[METHOD_createInstance1] = new MethodDescriptor(disMELS.IBMs.SnowCrab.Zooea2.Zooea2Parameters.class.getMethod("createInstance", new Class[] {java.lang.String[].class})); // NOI18N
            methods[METHOD_createInstance1].setDisplayName ( "" );
            methods[METHOD_getIBMFunction2] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getIBMFunction", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_getIBMFunction2].setDisplayName ( "" );
            methods[METHOD_getIBMFunctionKeysByCategory3] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getIBMFunctionKeysByCategory", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getIBMFunctionKeysByCategory3].setDisplayName ( "" );
            methods[METHOD_getIBMParameter4] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getIBMParameter", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getIBMParameter4].setDisplayName ( "" );
            methods[METHOD_getSelectedIBMFunctionForCategory5] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getSelectedIBMFunctionForCategory", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getSelectedIBMFunctionForCategory5].setDisplayName ( "" );
            methods[METHOD_getValue6] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, java.lang.Boolean.class})); // NOI18N
            methods[METHOD_getValue6].setDisplayName ( "" );
            methods[METHOD_getValue7] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, java.lang.Double.class})); // NOI18N
            methods[METHOD_getValue7].setDisplayName ( "" );
            methods[METHOD_getValue8] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, java.lang.Integer.class})); // NOI18N
            methods[METHOD_getValue8].setDisplayName ( "" );
            methods[METHOD_getValue9] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, java.lang.Long.class})); // NOI18N
            methods[METHOD_getValue9].setDisplayName ( "" );
            methods[METHOD_getValue10] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, boolean.class})); // NOI18N
            methods[METHOD_getValue10].setDisplayName ( "" );
            methods[METHOD_getValue11] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, double.class})); // NOI18N
            methods[METHOD_getValue11].setDisplayName ( "" );
            methods[METHOD_getValue12] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_getValue12].setDisplayName ( "" );
            methods[METHOD_getValue13] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, long.class})); // NOI18N
            methods[METHOD_getValue13].setDisplayName ( "" );
            methods[METHOD_getValue14] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_getValue14].setDisplayName ( "" );
            methods[METHOD_getValue15] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("getValue", new Class[] {java.lang.String.class})); // NOI18N
            methods[METHOD_getValue15].setDisplayName ( "" );
            methods[METHOD_setIBMFunction16] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setIBMFunction", new Class[] {java.lang.String.class, java.lang.String.class, wts.models.DisMELS.framework.IBMFunctions.IBMFunctionInterface.class})); // NOI18N
            methods[METHOD_setIBMFunction16].setDisplayName ( "" );
            methods[METHOD_setSelectedIBMFunctionForCategory17] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setSelectedIBMFunctionForCategory", new Class[] {java.lang.String.class, java.lang.String.class})); // NOI18N
            methods[METHOD_setSelectedIBMFunctionForCategory17].setDisplayName ( "" );
            methods[METHOD_setValue18] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setValue", new Class[] {java.lang.String.class, java.lang.Object.class})); // NOI18N
            methods[METHOD_setValue18].setDisplayName ( "" );
            methods[METHOD_setValue19] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setValue", new Class[] {java.lang.String.class, double.class})); // NOI18N
            methods[METHOD_setValue19].setDisplayName ( "" );
            methods[METHOD_setValue20] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setValue", new Class[] {java.lang.String.class, float.class})); // NOI18N
            methods[METHOD_setValue20].setDisplayName ( "" );
            methods[METHOD_setValue21] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setValue", new Class[] {java.lang.String.class, int.class})); // NOI18N
            methods[METHOD_setValue21].setDisplayName ( "" );
            methods[METHOD_setValue22] = new MethodDescriptor(wts.models.DisMELS.framework.AbstractLHSParameters.class.getMethod("setValue", new Class[] {java.lang.String.class, long.class})); // NOI18N
            methods[METHOD_setValue22].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
        // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons

    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


//GEN-FIRST:Superclass
    // Here you can add code for customizing the Superclass BeanInfo.

//GEN-LAST:Superclass
    /**
     * Gets the bean's <code>BeanDescriptor</code>s.
     *
     * @return BeanDescriptor describing the editable properties of this bean.
     * May return null if the information should be obtained by automatic
     * analysis.
     */
    @Override
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }

    /**
     * Gets the bean's <code>PropertyDescriptor</code>s.
     *
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by this bean. May return null if the information
     * should be obtained by automatic analysis.
     * <p>
     * If a property is indexed, then its entry in the result array will belong
     * to the IndexedPropertyDescriptor subclass of PropertyDescriptor. A client
     * of getPropertyDescriptors can use "instanceof" to check if a given
     * PropertyDescriptor is an IndexedPropertyDescriptor.
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }

    /**
     * Gets the bean's <code>EventSetDescriptor</code>s.
     *
     * @return An array of EventSetDescriptors describing the kinds of events
     * fired by this bean. May return null if the information should be obtained
     * by automatic analysis.
     */
    @Override
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }

    /**
     * Gets the bean's <code>MethodDescriptor</code>s.
     *
     * @return An array of MethodDescriptors describing the methods implemented
     * by this bean. May return null if the information should be obtained by
     * automatic analysis.
     */
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }

    /**
     * A bean may have a "default" property that is the property that will
     * mostly commonly be initially chosen for update by human's who are
     * customizing the bean.
     *
     * @return Index of default property in the PropertyDescriptor array
     * returned by getPropertyDescriptors.
     * <P>
     * Returns -1 if there is no default property.
     */
    @Override
    public int getDefaultPropertyIndex() {
        return defaultPropertyIndex;
    }

    /**
     * A bean may have a "default" event that is the event that will mostly
     * commonly be used by human's when using the bean.
     *
     * @return Index of default event in the EventSetDescriptor array returned
     * by getEventSetDescriptors.
     * <P>
     * Returns -1 if there is no default event.
     */
    @Override
    public int getDefaultEventIndex() {
        return defaultEventIndex;
    }

    /**
     * This method returns an image object that can be used to represent the
     * bean in toolboxes, toolbars, etc. Icon images will typically be GIFs, but
     * may in future include other formats.
     * <p>
     * Beans aren't required to provide icons and may return null from this
     * method.
     * <p>
     * There are four possible flavors of icons (16x16 color, 32x32 color, 16x16
     * mono, 32x32 mono). If a bean choses to only support a single icon we
     * recommend supporting 16x16 color.
     * <p>
     * We recommend that icons have a "transparent" background so they can be
     * rendered onto an existing background.
     *
     * @param iconKind The kind of icon requested. This should be one of the
     * constant values ICON_COLOR_16x16, ICON_COLOR_32x32, ICON_MONO_16x16, or
     * ICON_MONO_32x32.
     * @return An image object representing the requested icon. May return null
     * if no suitable icon is available.
     */
    @Override
    public java.awt.Image getIcon(int iconKind) {
        switch (iconKind) {
            case ICON_COLOR_16x16:
                if (iconNameC16 == null) {
                    return null;
                } else {
                    if (iconColor16 == null) {
                        iconColor16 = loadImage(iconNameC16);
                    }
                    return iconColor16;
                }
            case ICON_COLOR_32x32:
                if (iconNameC32 == null) {
                    return null;
                } else {
                    if (iconColor32 == null) {
                        iconColor32 = loadImage(iconNameC32);
                    }
                    return iconColor32;
                }
            case ICON_MONO_16x16:
                if (iconNameM16 == null) {
                    return null;
                } else {
                    if (iconMono16 == null) {
                        iconMono16 = loadImage(iconNameM16);
                    }
                    return iconMono16;
                }
            case ICON_MONO_32x32:
                if (iconNameM32 == null) {
                    return null;
                } else {
                    if (iconMono32 == null) {
                        iconMono32 = loadImage(iconNameM32);
                    }
                    return iconMono32;
                }
            default:
                return null;
        }
    }
    
}
