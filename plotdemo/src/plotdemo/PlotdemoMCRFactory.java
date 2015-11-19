/*
 * MATLAB Compiler: 4.18.1 (R2013a)
 * Date: Wed Nov 18 22:19:24 2015
 * Arguments: "-B" "macro_default" "-W" "java:plotdemo,plotter" "-T" "link:lib" "-d" 
 * "F:\\eclipse-java-luna-SR1a-win32\\workspace\\simulator\\plotdemo\\src" "-w" 
 * "enable:specified_file_mismatch" "-w" "enable:repeated_file" "-w" 
 * "enable:switch_ignored" "-w" "enable:missing_lib_sentinel" "-w" "enable:demo_license" 
 * "-v" 
 * "class{plotter:F:\\MATLAB\\workspace\\drawLine.m,F:\\MATLAB\\workspace\\drawplot.m}" 
 */

package plotdemo;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;

/**
 * <i>INTERNAL USE ONLY</i>
 */
public class PlotdemoMCRFactory
{
   
    
    /** Component's uuid */
    private static final String sComponentId = "plotdemo_C1E0A2696E4E75F5FC115181A6531D0F";
    
    /** Component name */
    private static final String sComponentName = "plotdemo";
    
   
    /** Pointer to default component options */
    private static final MWComponentOptions sDefaultComponentOptions = 
        new MWComponentOptions(
            MWCtfExtractLocation.EXTRACT_TO_CACHE, 
            new MWCtfClassLoaderSource(PlotdemoMCRFactory.class)
        );
    
    
    private PlotdemoMCRFactory()
    {
        // Never called.
    }
    
    public static MWMCR newInstance(MWComponentOptions componentOptions) throws MWException
    {
        if (null == componentOptions.getCtfSource()) {
            componentOptions = new MWComponentOptions(componentOptions);
            componentOptions.setCtfSource(sDefaultComponentOptions.getCtfSource());
        }
        return MWMCR.newInstance(
            componentOptions, 
            PlotdemoMCRFactory.class, 
            sComponentName, 
            sComponentId,
            new int[]{8,1,0}
        );
    }
    
    public static MWMCR newInstance() throws MWException
    {
        return newInstance(sDefaultComponentOptions);
    }
}
