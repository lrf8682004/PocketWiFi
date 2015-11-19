package test;

import plotdemo.plotter;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  MWNumericArray x = null;   //存放x值的数组  
	      MWNumericArray y = null;    //存放y值的数组  
	      
	      MWNumericArray x1 = null;   //存放x值的数组  
	      MWNumericArray y1 = null;    //存放y值的数组  
	      
	      plotter thePlot = null;    //plotter类的实例  
	      int n = 20;                //作图点数  
	  
	      try  
	      {  
	        //分配x、y的值  
	         int[] dims = {1, n};  
	         x = MWNumericArray.newInstance(dims,   
	            MWClassID.DOUBLE, MWComplexity.REAL);  
	         y = MWNumericArray.newInstance(dims,   
	            MWClassID.DOUBLE, MWComplexity.REAL);  
	  
	         x1 = MWNumericArray.newInstance(dims,   
	 	            MWClassID.DOUBLE, MWComplexity.REAL);  
	 	     y1 = MWNumericArray.newInstance(dims,   
	 	            MWClassID.DOUBLE, MWComplexity.REAL); 
	         
	 	     //定义  y = x^2  
	         for (int i = 1; i <= n; i++)  
	         {  
	            x.set(i, i);  
	            y.set(i, i*i);  
	            x1.set(i, i);  
	            y1.set(i, i*i + 100);  
	         }  
	  
	         //初始化plotter的对象  
	         thePlot = new plotter();  
	  
	         //作图  
	         //thePlot.drawplot(x1, y1);  
	         //thePlot.drawplot(x, y);  
	         thePlot.drawLine(0,1000,0,1000);
	         thePlot.waitForFigures();    
	      }  
	  
	      catch (Exception e)  
	      {  
	         System.out.println("Exception: " + e.toString());  
	      }  
	  
	      finally  
	      {  
	         //释放本地资源  
	         MWArray.disposeArray(x);  
	         MWArray.disposeArray(y);  
	         if (thePlot != null)  
	            thePlot.dispose();  
	      }  
	   }  
	

}
