/*
 * MATLAB Compiler: 4.18.1 (R2013a)
 * Date: Wed Nov 18 16:11:48 2015
 * Arguments: "-B" "macro_default" "-W" "java:plotdemo,Class1" "-T" "link:lib" "-d" 
 * "F:\\eclipse-java-luna-SR1a-win32\\workspace\\simulator\\plotdemo\\src" "-w" 
 * "enable:specified_file_mismatch" "-w" "enable:repeated_file" "-w" 
 * "enable:switch_ignored" "-w" "enable:missing_lib_sentinel" "-w" "enable:demo_license" 
 * "-v" 
 * "class{Class1:F:\\MATLAB\\R2013a\\toolbox\\javabuilder\\Examples\\PlotExample\\PlotDemoComp\\drawplot.m}" 
 */

package plotdemo;

import com.mathworks.toolbox.javabuilder.*;
import com.mathworks.toolbox.javabuilder.internal.*;
import java.util.*;

/**
 * The <code>Class1</code> class provides a Java interface to the M-functions
 * from the files:
 * <pre>
 *  F:\\MATLAB\\R2013a\\toolbox\\javabuilder\\Examples\\PlotExample\\PlotDemoComp\\drawplot.m
 * </pre>
 * The {@link #dispose} method <b>must</b> be called on a <code>Class1</code> instance 
 * when it is no longer needed to ensure that native resources allocated by this class 
 * are properly freed.
 * @version 0.0
 */
public class Class1 extends MWComponentInstance<Class1>
{
    /**
     * Tracks all instances of this class to ensure their dispose method is
     * called on shutdown.
     */
    private static final Set<Disposable> sInstances = new HashSet<Disposable>();

    /**
     * Maintains information used in calling the <code>drawplot</code> M-function.
     */
    private static final MWFunctionSignature sDrawplotSignature =
        new MWFunctionSignature(/* max outputs = */ 0,
                                /* has varargout = */ false,
                                /* function name = */ "drawplot",
                                /* max inputs = */ 2,
                                /* has varargin = */ false);

    /**
     * Shared initialization implementation - private
     */
    private Class1 (final MWMCR mcr) throws MWException
    {
        super(mcr);
        // add this to sInstances
        synchronized(Class1.class) {
            sInstances.add(this);
        }
    }

    /**
     * Constructs a new instance of the <code>Class1</code> class.
     */
    public Class1() throws MWException
    {
        this(PlotdemoMCRFactory.newInstance());
    }
    
    private static MWComponentOptions getPathToComponentOptions(String path)
    {
        MWComponentOptions options = new MWComponentOptions(new MWCtfExtractLocation(path),
                                                            new MWCtfDirectorySource(path));
        return options;
    }
    
    /**
     * @deprecated Please use the constructor {@link #Class1(MWComponentOptions componentOptions)}.
     * The <code>com.mathworks.toolbox.javabuilder.MWComponentOptions</code> class provides API to set the
     * path to the component.
     * @param pathToComponent Path to component directory.
     */
    public Class1(String pathToComponent) throws MWException
    {
        this(PlotdemoMCRFactory.newInstance(getPathToComponentOptions(pathToComponent)));
    }
    
    /**
     * Constructs a new instance of the <code>Class1</code> class. Use this constructor 
     * to specify the options required to instantiate this component.  The options will 
     * be specific to the instance of this component being created.
     * @param componentOptions Options specific to the component.
     */
    public Class1(MWComponentOptions componentOptions) throws MWException
    {
        this(PlotdemoMCRFactory.newInstance(componentOptions));
    }
    
    /** Frees native resources associated with this object */
    public void dispose()
    {
        try {
            super.dispose();
        } finally {
            synchronized(Class1.class) {
                sInstances.remove(this);
            }
        }
    }
  
    /**
     * Invokes the first m-function specified by MCC, with any arguments given on
     * the command line, and prints the result.
     */
    public static void main (String[] args)
    {
        try {
            MWMCR mcr = PlotdemoMCRFactory.newInstance();
            mcr.runMain( sDrawplotSignature, args);
            mcr.dispose();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * Calls dispose method for each outstanding instance of this class.
     */
    public static void disposeAllInstances()
    {
        synchronized(Class1.class) {
            for (Disposable i : sInstances) i.dispose();
            sInstances.clear();
        }
    }

    /**
     * Provides the interface for calling the <code>drawplot</code> M-function 
     * where the first input, an instance of List, receives the output of the M-function and
     * the second input, also an instance of List, provides the input to the M-function.
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * %DRAWPLOT Plot a curve from x and y arrays.
     * %   DRAWPLOT(X,Y) Plots a curve from x and y arrays in a MATLAB
     * %   figure window.  This file is used as an example
     * %   for the MATLAB Builder JA product.
     * </pre>
     * </p>
     * @param lhs List in which to return outputs. Number of outputs (nargout) is
     * determined by allocated size of this List. Outputs are returned as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>.
     * Each output array should be freed by calling its <code>dispose()</code>
     * method.
     *
     * @param rhs List containing inputs. Number of inputs (nargin) is determined
     * by the allocated size of this List. Input arguments may be passed as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or
     * as arrays of any supported Java type. Arguments passed as Java types are
     * converted to MATLAB arrays according to default conversion rules.
     * @throws MWException An error has occurred during the function call.
     */
    public void drawplot(List lhs, List rhs) throws MWException
    {
        fMCR.invoke(lhs, rhs, sDrawplotSignature);
    }

    /**
     * Provides the interface for calling the <code>drawplot</code> M-function 
     * where the first input, an Object array, receives the output of the M-function and
     * the second input, also an Object array, provides the input to the M-function.
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * %DRAWPLOT Plot a curve from x and y arrays.
     * %   DRAWPLOT(X,Y) Plots a curve from x and y arrays in a MATLAB
     * %   figure window.  This file is used as an example
     * %   for the MATLAB Builder JA product.
     * </pre>
     * </p>
     * @param lhs array in which to return outputs. Number of outputs (nargout)
     * is determined by allocated size of this array. Outputs are returned as
     * sub-classes of <code>com.mathworks.toolbox.javabuilder.MWArray</code>.
     * Each output array should be freed by calling its <code>dispose()</code>
     * method.
     *
     * @param rhs array containing inputs. Number of inputs (nargin) is
     * determined by the allocated size of this array. Input arguments may be
     * passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     * @throws MWException An error has occurred during the function call.
     */
    public void drawplot(Object[] lhs, Object[] rhs) throws MWException
    {
        fMCR.invoke(Arrays.asList(lhs), Arrays.asList(rhs), sDrawplotSignature);
    }

    /**
     * Provides the standard interface for calling the <code>drawplot</code>
     * M-function with 2 input arguments.
     * Input arguments may be passed as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>, or as arrays of
     * any supported Java type. Arguments passed as Java types are converted to
     * MATLAB arrays according to default conversion rules.
     *
     * <p>M-documentation as provided by the author of the M function:
     * <pre>
     * %DRAWPLOT Plot a curve from x and y arrays.
     * %   DRAWPLOT(X,Y) Plots a curve from x and y arrays in a MATLAB
     * %   figure window.  This file is used as an example
     * %   for the MATLAB Builder JA product.
     * </pre>
     * </p>
     * @param rhs The inputs to the M function.
     * @return Array of length nargout containing the function outputs. Outputs
     * are returned as sub-classes of
     * <code>com.mathworks.toolbox.javabuilder.MWArray</code>. Each output array
     * should be freed by calling its <code>dispose()</code> method.
     * @throws MWException An error has occurred during the function call.
     */
    public Object[] drawplot(Object... rhs) throws MWException
    {
        Object[] lhs = new Object[0];
        fMCR.invoke(Arrays.asList(lhs), 
                    MWMCR.getRhsCompat(rhs, sDrawplotSignature), 
                    sDrawplotSignature);
        return lhs;
    }
}
