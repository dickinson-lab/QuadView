///////////////////////////////////////////////////////////////////////////////
//FILE:          SplitViewFrame.java
//PROJECT:       Micro-Manager
//SUBSYSTEM:     mmstudio
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nico Stuurman
//
// COPYRIGHT:    University of California, San Francisco, 2011, 2012
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.


/**
 * Created on Aug 28, 2011, 9:41:57 PM
 */
package QuadView;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;

import mmcorej.CMMCore;

import net.miginfocom.swing.MigLayout;

import org.micromanager.data.ProcessorConfigurator;
import org.micromanager.PropertyMap;
import org.micromanager.PropertyMaps;
import org.micromanager.Studio;
import org.micromanager.data.internal.PropertyKey;
import org.micromanager.display.ChannelDisplaySettings;
import org.micromanager.display.DataViewer;
import org.micromanager.display.DisplaySettings;
import org.micromanager.display.internal.DefaultDisplaySettings;
import org.micromanager.internal.utils.WindowPositioning;

// Imports for MMStudio internal packages
// Plugins should not access internal packages, to ensure modularity and
// maintainability. However, this plugin code is older than the current
// MMStudio API, so it still uses internal classes and interfaces. New code
// should not imitate this practice.

/**
 * Micro-Manager plugin that can split the acquired image top-down or left-right
 * and arrange the split images along the channel axis.
 *
 * @author nico, modified by Chris Weisiger
 */
public class QuadViewFrame extends JFrame implements ProcessorConfigurator {
   private static final int DEFAULT_WIN_X = 100;
   private static final int DEFAULT_WIN_Y = 100;
   private static final String KEEP_BLUE = "keep_blue";
   private static final String KEEP_GREEN = "keep_green";
   private static final String KEEP_RED = "keep_red";
   private static final String KEEP_FARRED = "keep_farRed";
   /* Old parameters from split-view plugin
   private static final String ORIENTATION = "Orientation";
   private static final String NUM_SPLITS = "numSplits";
   private static final String[] SPLIT_OPTIONS = new String[] {"Two", "Three",
      "Four", "Five"};
   public static final String LR = "lr";
   public static final String TB = "tb"; */ 

   private final Studio studio_;
   private final CMMCore core_;
   private boolean keep_blue_;
   private boolean keep_green_;
   private boolean keep_red_;
   private boolean keep_farRed_;
   private JCheckBox blueCheckBox_;
   private JCheckBox greenCheckBox_;
   private JCheckBox redCheckBox_;
   private JCheckBox farRedCheckBox_;

   /* Old variables from split-view
   private String orientation_;
   private int numSplits_;
   private JRadioButton lrRadio_;
   private JRadioButton tbRadio_; */

   public QuadViewFrame(PropertyMap settings, Studio studio) {
      studio_ = studio;
      core_ = studio_.getCMMCore();

      // Get any existing settings
      keep_blue_ = settings.getBoolean("keep_blue",
            studio_.profile().getSettings(QuadViewFrame.class).getBoolean(KEEP_BLUE, true));
      keep_green_ = settings.getBoolean("keep_green",
            studio_.profile().getSettings(QuadViewFrame.class).getBoolean(KEEP_GREEN, true));
      keep_red_ = settings.getBoolean("keep_red",
            studio_.profile().getSettings(QuadViewFrame.class).getBoolean(KEEP_RED, true));
      keep_farRed_ = settings.getBoolean("keep_farRed",
            studio_.profile().getSettings(QuadViewFrame.class).getBoolean(KEEP_FARRED, true));

      /* Old variables from split-view
      orientation_ = settings.getString("orientation",
            studio_.profile().getSettings(QuadViewFrame.class).getString(ORIENTATION, LR));
      numSplits_ = settings.getInteger("splits",
            studio_.profile().getSettings(QuadViewFrame.class).getInteger(NUM_SPLITS, 2)); */

      // Create dialog box and components
      initComponents();

      super.setIconImage(Toolkit.getDefaultToolkit().getImage(
              getClass().getResource("/org/micromanager/icons/microscope.gif")));
      super.setLocation(DEFAULT_WIN_X, DEFAULT_WIN_Y);
      WindowPositioning.setUpLocationMemory(this, this.getClass(), null);

      // Apply any existing settings
      blueCheckBox_.setSelected(keep_blue_);
      greenCheckBox_.setSelected(keep_green_);
      redCheckBox_.setSelected(keep_red_);
      farRedCheckBox_.setSelected(keep_farRed_);

      /* lrRadio_.setSelected(orientation_.equals(LR));
      tbRadio_.setSelected(orientation_.equals(TB)); */
   }

   @Override
   public PropertyMap getSettings() {
      PropertyMap.Builder builder =  PropertyMaps.builder();
      builder.putBoolean("keep_blue", keep_blue_);
      builder.putBoolean("keep_green", keep_green_);
      builder.putBoolean("keep_red", keep_red_);
      builder.putBoolean("keep_farRed", keep_farRed_);
      return builder.build();
   }

   @Override
   public void showGUI() {
      setVisible(true);
   }

   @Override
   public void cleanup() {
      dispose();
   }

   /** This method is called from within the constructor to
    * initialize the form.
    */
   @SuppressWarnings("unchecked")
   private void initComponents() {
      setTitle("QuadView");
      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

      blueCheckBox_ = new JCheckBox("Keep Blue Channel");
      greenCheckBox_ = new JCheckBox("Keep Green Channel");
      redCheckBox_ = new JCheckBox("Keep Red Channel");
      farRedCheckBox_ = new JCheckBox("Keep Far Red Channel");

      blueCheckBox_.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AbstractButton abstractButton = (AbstractButton) evt.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            updateSettings("blue", selected);
         }
      });

      greenCheckBox_.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AbstractButton abstractButton = (AbstractButton) evt.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            updateSettings("green", selected);
         }
      });

      redCheckBox_.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AbstractButton abstractButton = (AbstractButton) evt.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            updateSettings("red", selected);
         }
      });

      farRedCheckBox_.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            AbstractButton abstractButton = (AbstractButton) evt.getSource();
            boolean selected = abstractButton.getModel().isSelected();
            updateSettings("farRed", selected);
         }
      });

      setLayout(new MigLayout("flowx"));

      add(new Preview(), "align center, wrap");
      add(blueCheckBox_);
      add(redCheckBox_, "wrap");
      add(greenCheckBox_);
      add(farRedCheckBox_, "wrap");

      /*add(new JLabel(
               "<html>Note: if the image size does not evenly divide<br> " +
               "the number of splits, then some rows or columns<br>" +
               "from the source image will be discarded.</html>"),
            "span, wrap");*/
      pack();
   }

   private void updateSettings(String channel, boolean selected) {
      switch (channel) {
         case ("blue"): {
            keep_blue_ = selected;
            studio_.profile().getSettings(QuadViewFrame.class).putBoolean(
                KEEP_BLUE, selected);
            break;
         }
         case ("green"): {
            keep_green_ = selected;
            studio_.profile().getSettings(QuadViewFrame.class).putBoolean(
                KEEP_GREEN, selected);
            break;
         }
         case ("red"): {
            keep_red_ = selected;
            studio_.profile().getSettings(QuadViewFrame.class).putBoolean(
                KEEP_RED, selected);
            break;
         }
         case ("farRed"): {
            keep_farRed_ = selected;
            studio_.profile().getSettings(QuadViewFrame.class).putBoolean(
                KEEP_FARRED, selected);
            break;
         }
      }
      //Update display settings
      DisplaySettings dsTmp = DefaultDisplaySettings.restoreFromProfile(
                                studio_.profile(), PropertyKey.ACQUISITION_DISPLAY_SETTINGS.key());

      if (dsTmp == null) {
          dsTmp = DefaultDisplaySettings.getStandardSettings(
                    PropertyKey.ACQUISITION_DISPLAY_SETTINGS.key());
      }
      
      DisplaySettings.Builder settingsBuilder = dsTmp.copyBuilder();
      
      int counter = 0;
      if (keep_blue_) {
          ChannelDisplaySettings.Builder cds = studio_.displays().channelDisplaySettingsBuilder();
          settingsBuilder.channel(counter, cds.colorCyan().build());
          counter++;
      }
      if (keep_green_) {
          ChannelDisplaySettings.Builder cds = studio_.displays().channelDisplaySettingsBuilder();
          settingsBuilder.channel(counter, cds.colorGreen().build());
          counter++;
      }
      if (keep_red_) {
          ChannelDisplaySettings.Builder cds = studio_.displays().channelDisplaySettingsBuilder();
          settingsBuilder.channel(counter, cds.color(java.awt.Color.ORANGE).build() );
          counter++;
      }
      if (keep_farRed_) {
          ChannelDisplaySettings.Builder cds = studio_.displays().channelDisplaySettingsBuilder();
          settingsBuilder.channel(counter, cds.colorMagenta().build() );
          counter++;
      }
      
      if (counter == 1) {
          settingsBuilder.colorModeGrayscale();
      } else {
          settingsBuilder.colorModeComposite();  
      }   

      // Update active display
      DataViewer viewer = studio_.displays().getActiveDataViewer();
      if (viewer != null) {
        DisplaySettings oldSettings;
        DisplaySettings newSettings;
        do {
            oldSettings = viewer.getDisplaySettings();
            newSettings = settingsBuilder.build();
        } while (!viewer.compareAndSetDisplaySettings(oldSettings, newSettings));
       }

     // save display settings to profile
      DisplaySettings newSettings = settingsBuilder.build();
      ( (DefaultDisplaySettings) viewer.getDisplaySettings()).saveToProfile(
                    studio_.profile(), PropertyKey.ACQUISITION_DISPLAY_SETTINGS.key());
      ( (DefaultDisplaySettings) viewer.getDisplaySettings()).saveToProfile(
                    studio_.profile(), PropertyKey.SNAP_LIVE_DISPLAY_SETTINGS.key());

      studio_.data().notifyPipelineChanged();
      repaint();
   }

   private class Preview extends JPanel {
      /*public Preview() {
         isLeftRight_ = isLeftRight;
      }*/

      @Override
      public Dimension getMinimumSize() {
         return new Dimension(50, 50);
      }

      @Override
      public void paint(Graphics graphics) {
         Graphics2D g = (Graphics2D) graphics;
         g.setColor(Color.WHITE);
         g.fillRect(0, 0, 50, 50);
         g.setColor(Color.BLACK);
         // Draw a box around the outside
         int[] xPoints = new int[] {0, 50, 50, 0};
         int[] yPoints = new int[] {0, 0, 50, 50};
         g.drawPolygon(xPoints, yPoints, 4);
         // Fill un-selected boxes gray
         
         if (keep_blue_) {
            g.setColor(Color.WHITE);
         } else {
            g.setColor(Color.LIGHT_GRAY);
         }
         g.fillRect(0,0,25,25);
         if (keep_green_) {
            g.setColor(Color.WHITE);
         } else {
            g.setColor(Color.LIGHT_GRAY);
         }
         g.fillRect(0,25,25,25);
         if (keep_red_) {
            g.setColor(Color.WHITE);
         } else {
            g.setColor(Color.LIGHT_GRAY);
         }
         g.fillRect(25,0,25,25);
         if (keep_farRed_) {
            g.setColor(Color.WHITE);
         } else {
            g.setColor(Color.LIGHT_GRAY);
         }
         g.fillRect(25,25,25,25);
         // Draw dividers.
         g.setColor(Color.BLACK);
         g.drawLine(25, 0, 25, 50);
         g.drawLine(0, 25, 50, 25);
      }
   }
}