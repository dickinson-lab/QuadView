///////////////////////////////////////////////////////////////////////////////
//FILE:          SplitViewProcessor.java
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



package QuadView;

import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.micromanager.data.Coords;
import org.micromanager.data.Image;
import org.micromanager.data.Processor;
import org.micromanager.data.ProcessorContext;
import org.micromanager.data.SummaryMetadata;

import org.micromanager.Studio;
import org.micromanager.display.DataViewer;

/**
 * DataProcessor that splits images as instructed in SplitViewFrame
 *
 * @author nico, heavily updated by Chris Weisiger
 */
public class QuadViewProcessor implements Processor {

   private final Studio studio_;
   //private String orientation_ = QuadViewFrame.LR;
   private final boolean keep_blue_;
   private final boolean keep_green_;
   private final boolean keep_red_;
   private final boolean keep_farRed_;
   private final ArrayList<String> channelSuffixes_ = new ArrayList<String>(Arrays.asList(
                                                                new String[] {"Blue", "Green", "Red", "FarRed"}));

   public QuadViewProcessor(Studio studio, boolean keep_blue, boolean keep_green, boolean keep_red, boolean keep_farRed) {
      studio_ = studio;
      keep_blue_ = keep_blue;
      keep_green_ = keep_green;
      keep_red_ = keep_red;
      keep_farRed_ = keep_farRed;
   }

   @Override
   public SummaryMetadata processSummaryMetadata(SummaryMetadata summary) {
      // Update channel names in summary metadata.
      List<String> chNames = summary.getChannelNameList();
      if (chNames == null || chNames.isEmpty()) {
         // Can't do anything as we don't know how many names there'll be.
         return summary;
      }
      
      // Need to fix channel naming in this section
      String[] newNames = new String[chNames.size() * 4];
      for (int i = 0; i < chNames.size(); ++i) {
         String base = summary.getSafeChannelName(i);
         for (int j = 0; j < 4; ++j) {
            newNames[i * 4 + j] = base + channelSuffixes_.get(j);
         }
      }
      return summary.copyBuilder().channelNames(newNames).build();
   }

   @Override
   public void processImage(Image image, ProcessorContext context) {
      ImageProcessor proc = studio_.data().ij().createProcessor(image);

      int width = image.getWidth();
      int height = image.getHeight();
      int xStep = width/2;
      int yStep = height/2;
      width /= 2;
      height /= 2;
      /* if (orientation_.equals(QuadViewFrame.TB)) {
         height /= numSplits_;
         yStep = height;
      }
      else {
         width /= numSplits_;
         xStep = width;
      } */
      
      // For now, only single-channel images are supported.  This could be improved by embedding the code below in a loop 
      //int channelIndex = image.getCoords().getChannel();

      // Grab selected images
      int channelCounter = 0;
      if (keep_blue_) {
         proc.setRoi(0, 0, width, height);
         Coords coords = image.getCoords().copy()
            .channel(channelCounter).build();
         Image output = studio_.data().createImage(proc.crop().getPixels(),
               width, height, image.getBytesPerPixel(),
               image.getNumComponents(), coords, image.getMetadata());
         context.outputImage(output);
         
         channelCounter++;
      }
      if (keep_green_) {
         proc.setRoi(0, yStep, width, height);
         Coords coords = image.getCoords().copy()
            .channel(channelCounter).build();
         Image output = studio_.data().createImage(proc.crop().getPixels(),
               width, height, image.getBytesPerPixel(),
               image.getNumComponents(), coords, image.getMetadata());
         context.outputImage(output);
         channelCounter++;
      }
      if (keep_red_) {
         proc.setRoi(xStep, 0, width, height);
         Coords coords = image.getCoords().copy()
            .channel(channelCounter).build();
         Image output = studio_.data().createImage(proc.crop().getPixels(),
               width, height, image.getBytesPerPixel(),
               image.getNumComponents(), coords, image.getMetadata());
         context.outputImage(output);
         channelCounter++;
      }
      if (keep_farRed_) {
         proc.setRoi(xStep, yStep, width, height);
         Coords coords = image.getCoords().copy()
            .channel(channelCounter).build();
         Image output = studio_.data().createImage(proc.crop().getPixels(),
               width, height, image.getBytesPerPixel(),
               image.getNumComponents(), coords, image.getMetadata());
         context.outputImage(output);
         channelCounter++;
      }

      
   }
}