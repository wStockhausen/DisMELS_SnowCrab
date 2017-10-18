/*
 * AbstractBenthicStageAttributesCustomizer.java
 *
 * Created on Octobre 17, 2017
 */

package wts.models.DisMELS.IBMs.SnowCrab;

import wts.models.DisMELS.framework.LifeStageAttributesInterface;
import wts.models.DisMELS.framework.Types;
import wts.models.DisMELS.gui.AttributesCustomizer;

/**
 * @author William Stockhausen
 */
public class AbstractBenthicStageAttributesCustomizer extends AttributesCustomizer {

    private AbstractBenthicStageAttributes attributes = null;
    
    /**
     * Creates new customizer AbstractArrowtoothAttributesCustomizer
     */
    public AbstractBenthicStageAttributesCustomizer() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jpTime = new javax.swing.JPanel();
        jtfTime = new javax.swing.JTextField();
        jpHoriz = new javax.swing.JPanel();
        jcbHorizType = new javax.swing.JComboBox();
        jtfX = new javax.swing.JTextField();
        jtfY = new javax.swing.JTextField();
        lblX = new javax.swing.JLabel();
        lblY = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jpVert = new javax.swing.JPanel();
        jtfZ = new javax.swing.JTextField();
        jcbVertType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        lblZ = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtfAge = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtfAgeInStage = new javax.swing.JTextField();
        jtfNumber = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtfSize = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtfWeight = new javax.swing.JTextField();
        jtfInstar = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jtfShellCond = new javax.swing.JTextField();
        jtfShellThick = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        jpTime.setBorder(javax.swing.BorderFactory.createTitledBorder("Start time"));

        jtfTime.setText("0.0");
        jtfTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfTimeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpTimeLayout = new org.jdesktop.layout.GroupLayout(jpTime);
        jpTime.setLayout(jpTimeLayout);
        jpTimeLayout.setHorizontalGroup(
            jpTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpTimeLayout.createSequentialGroup()
                .addContainerGap()
                .add(jtfTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 277, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpTimeLayout.setVerticalGroup(
            jpTimeLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jtfTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jpHoriz.setBorder(javax.swing.BorderFactory.createTitledBorder("Horizontal position"));

        jcbHorizType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Grid IJ", "Grid XY", "Lat/Lon" }));
        jcbHorizType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbHorizTypeActionPerformed(evt);
            }
        });

        jtfX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfXActionPerformed(evt);
            }
        });

        jtfY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfYActionPerformed(evt);
            }
        });

        lblX.setText("Lon");
        lblX.setMaximumSize(new java.awt.Dimension(30, 14));
        lblX.setPreferredSize(new java.awt.Dimension(30, 14));

        lblY.setText("Lat");

        jLabel3.setText("Type");

        org.jdesktop.layout.GroupLayout jpHorizLayout = new org.jdesktop.layout.GroupLayout(jpHoriz);
        jpHoriz.setLayout(jpHorizLayout);
        jpHorizLayout.setHorizontalGroup(
            jpHorizLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpHorizLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jpHorizLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblX, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblY, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpHorizLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpHorizLayout.createSequentialGroup()
                        .add(jtfX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel3))
                    .add(jpHorizLayout.createSequentialGroup()
                        .add(jtfY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jcbHorizType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        jpHorizLayout.setVerticalGroup(
            jpHorizLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpHorizLayout.createSequentialGroup()
                .add(jpHorizLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(lblX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpHorizLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblY)
                    .add(jtfY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jcbHorizType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpVert.setBorder(javax.swing.BorderFactory.createTitledBorder("Vertical position"));

        jtfZ.setText("0.");
        jtfZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfZActionPerformed(evt);
            }
        });

        jcbVertType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Grid K", "Z (< 0)", "Depth (>0)", "Dist. above bottom" }));
        jcbVertType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbVertTypeActionPerformed(evt);
            }
        });

        jLabel1.setText("Type");

        lblZ.setText("Depth");

        org.jdesktop.layout.GroupLayout jpVertLayout = new org.jdesktop.layout.GroupLayout(jpVert);
        jpVert.setLayout(jpVertLayout);
        jpVertLayout.setHorizontalGroup(
            jpVertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpVertLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jpVertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jpVertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jcbVertType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(27, 27, 27))
        );
        jpVertLayout.setVerticalGroup(
            jpVertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpVertLayout.createSequentialGroup()
                .add(jpVertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(lblZ))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpVertLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jcbVertType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Standard attributes"));

        jLabel2.setText("age");

        jtfAge.setText("0.0");
        jtfAge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfAgeActionPerformed(evt);
            }
        });

        jLabel4.setText("age in stage");

        jtfAgeInStage.setText("0.0");
        jtfAgeInStage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfAgeInStageActionPerformed(evt);
            }
        });

        jtfNumber.setText("0.0");
        jtfNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfNumberActionPerformed(evt);
            }
        });

        jLabel6.setText("number");

        jtfSize.setText("1.0");
        jtfSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfSizeActionPerformed(evt);
            }
        });

        jLabel5.setText("size (mm CW)");

        jLabel7.setText("weight (g)");

        jtfWeight.setText("0.0");
        jtfWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfWeightActionPerformed(evt);
            }
        });

        jtfInstar.setText("0");
        jtfInstar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfInstarActionPerformed(evt);
            }
        });

        jLabel8.setText("instar");

        jLabel9.setText("shell condition");

        jLabel10.setText("shell thickness");

        jtfShellCond.setText("0.0");
        jtfShellCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfShellCondActionPerformed(evt);
            }
        });

        jtfShellThick.setText("1.0");
        jtfShellThick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfShellThickActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jtfAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2)
                            .add(jLabel8))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jtfAgeInStage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel5)
                            .add(jLabel4))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6)
                            .add(jtfNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel7)))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jtfInstar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel10)
                            .add(jtfSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfWeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jtfShellCond, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jtfShellThick, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfAgeInStage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfAge, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jLabel7)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfInstar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfSize, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfWeight, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jtfShellCond, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jtfShellThick, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jpHoriz, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jpVert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 307, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jpTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jpTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpHoriz, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jpVert, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jtfNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfNumberActionPerformed
        Double n = new Double(jtfNumber.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_number,n);
    }//GEN-LAST:event_jtfNumberActionPerformed

    private void jtfAgeInStageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfAgeInStageActionPerformed
        Double n = new Double(jtfAgeInStage.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_ageInStage,n);
    }//GEN-LAST:event_jtfAgeInStageActionPerformed

    private void jtfAgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfAgeActionPerformed
        Double n = new Double(jtfAge.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_age,n);
    }//GEN-LAST:event_jtfAgeActionPerformed

    private void jtfZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfZActionPerformed
        Double n = new Double(jtfZ.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_vertPos,n);
    }//GEN-LAST:event_jtfZActionPerformed

    private void jtfYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfYActionPerformed
        Double n = new Double(jtfY.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_horizPos2,n);
    }//GEN-LAST:event_jtfYActionPerformed

    private void jtfXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfXActionPerformed
        Double n = new Double(jtfX.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_horizPos1,n);
    }//GEN-LAST:event_jtfXActionPerformed

    private void jcbHorizTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbHorizTypeActionPerformed
        int idx = jcbHorizType.getSelectedIndex();
        attributes.setValue(AbstractBenthicStageAttributes.PROP_horizType,new Integer(idx));
        switch (idx) {
            case 0:
                lblX.setText("I");
                lblY.setText("J");
                break;
            case 1:
                lblX.setText("X");
                lblY.setText("Y");
                break;
            case 2:
                lblX.setText("Lon");
                lblY.setText("Lat");
                break;
        }       
    }//GEN-LAST:event_jcbHorizTypeActionPerformed

    private void jtfTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfTimeActionPerformed
        Double n = new Double(jtfTime.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_startTime,n);
    }//GEN-LAST:event_jtfTimeActionPerformed

    private void jcbVertTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbVertTypeActionPerformed
        int idx = jcbVertType.getSelectedIndex();
        attributes.setValue(AbstractBenthicStageAttributes.PROP_vertType,new Integer(idx));
        switch (idx) {
            case Types.VERT_K:
                lblZ.setText("K");
                break;
            case Types.VERT_Z:
                lblZ.setText("Z");
                break;
            case Types.VERT_H:
                lblZ.setText("Depth");
                break;
            case Types.VERT_DH:
                lblZ.setText("Distance off bottom");
                break;
        }
    }//GEN-LAST:event_jcbVertTypeActionPerformed

    private void jtfSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfSizeActionPerformed
        Double n = new Double(jtfSize.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_size,n);
    }//GEN-LAST:event_jtfSizeActionPerformed

    private void jtfWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfWeightActionPerformed
        Double n = new Double(jtfWeight.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_weight,n);
    }//GEN-LAST:event_jtfWeightActionPerformed

    private void jtfShellThickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfShellThickActionPerformed
        Double n = new Double(jtfShellThick.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_shellthick,n);
    }//GEN-LAST:event_jtfShellThickActionPerformed

    private void jtfShellCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfShellCondActionPerformed
        Double n = new Double(jtfShellCond.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_shellcond,n);
    }//GEN-LAST:event_jtfShellCondActionPerformed

    private void jtfInstarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfInstarActionPerformed
        Integer n = new Integer(jtfInstar.getText());
        attributes.setValue(AbstractBenthicStageAttributes.PROP_instar,n);
    }//GEN-LAST:event_jtfInstarActionPerformed

    @Override
    public void setObject(Object bean) {
        if (bean instanceof AbstractBenthicStageAttributes) {
            setAttributes((AbstractBenthicStageAttributes) bean);
        }
    }
    
    @Override
    public AbstractBenthicStageAttributes getAttributes() {
        return attributes;
    }
    
    @Override
    public void setAttributes(LifeStageAttributesInterface newAtts) {
        if (newAtts instanceof AbstractBenthicStageAttributes) {
            attributes = (AbstractBenthicStageAttributes) newAtts;
            Boolean b = null;
            Double  d = null;
            Integer i = null;
            jcbHorizType.setSelectedIndex(attributes.getValue(AbstractBenthicStageAttributes.PROP_horizType,i));
            jcbVertType.setSelectedIndex(attributes.getValue(AbstractBenthicStageAttributes.PROP_vertType,i));
            jtfTime.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_startTime,d).toString());
            jtfX.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_horizPos1,d).toString());
            jtfY.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_horizPos2,d).toString());
            jtfZ.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_vertPos,d).toString());
            jtfAge.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_age,d).toString());
            jtfAgeInStage.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_ageInStage,d).toString());
            jtfNumber.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_number,d).toString());
            jtfInstar.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_instar,i).toString());
            jtfSize.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_size,d).toString());
            jtfWeight.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_weight,d).toString());
            jtfShellCond.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_shellcond,d).toString());
            jtfShellThick.setText(attributes.getValue(AbstractBenthicStageAttributes.PROP_shellthick,d).toString());
        }
    }
    
    @Override
    public void showHorizPos(boolean b) {
        jpHoriz.setVisible(b);
        revalidate();
    }
    
    @Override
    public void showVertPos(boolean b) {
        jpVert.setVisible(b);
        revalidate();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JComboBox jcbHorizType;
    private javax.swing.JComboBox jcbVertType;
    private javax.swing.JPanel jpHoriz;
    private javax.swing.JPanel jpTime;
    private javax.swing.JPanel jpVert;
    private javax.swing.JTextField jtfAge;
    private javax.swing.JTextField jtfAgeInStage;
    private javax.swing.JTextField jtfInstar;
    private javax.swing.JTextField jtfNumber;
    private javax.swing.JTextField jtfShellCond;
    private javax.swing.JTextField jtfShellThick;
    private javax.swing.JTextField jtfSize;
    private javax.swing.JTextField jtfTime;
    private javax.swing.JTextField jtfWeight;
    private javax.swing.JTextField jtfX;
    private javax.swing.JTextField jtfY;
    private javax.swing.JTextField jtfZ;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblY;
    private javax.swing.JLabel lblZ;
    // End of variables declaration//GEN-END:variables
    
}