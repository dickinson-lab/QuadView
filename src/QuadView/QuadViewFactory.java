///////////////////////////////////////////////////////////////////////////////
//FILE:          QuadViewProcessor.java
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

import org.micromanager.data.Processor;
import org.micromanager.data.ProcessorFactory;
import org.micromanager.PropertyMap;
import org.micromanager.Studio;

public class QuadViewFactory implements ProcessorFactory {
   private final Studio studio_;
   private final boolean keep_blue_;
   private final boolean keep_green_;
   private final boolean keep_red_;
   private final boolean keep_farRed_;
   public QuadViewFactory(Studio studio, PropertyMap settings) {
      studio_ = studio;
      keep_blue_ = settings.getBoolean("keep_blue", true);
      keep_green_ = settings.getBoolean("keep_green", true);
      keep_red_ = settings.getBoolean("keep_red", true);
      keep_farRed_ = settings.getBoolean("keep_farRed", true);
   }

   @Override
   public Processor createProcessor() {
      return new QuadViewProcessor(studio_, keep_blue_, keep_green_, keep_red_, keep_farRed_);
   }
}