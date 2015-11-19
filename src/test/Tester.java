package test;

import plotdemo.plotter;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  MWNumericArray x = null;   //���xֵ������  
	      MWNumericArray y = null;    //���yֵ������  
	      
	      MWNumericArray x1 = null;   //���xֵ������  
	      MWNumericArray y1 = null;    //���yֵ������  
	      
	      plotter thePlot = null;    //plotter���ʵ��  
	      int n = 20;                //��ͼ����  
	  
	      try  
	      {  
	        //����x��y��ֵ  
	         int[] dims = {1, n};  
	         x = MWNumericArray.newInstance(dims,   
	            MWClassID.DOUBLE, MWComplexity.REAL);  
	         y = MWNumericArray.newInstance(dims,   
	            MWClassID.DOUBLE, MWComplexity.REAL);  
	  
	         x1 = MWNumericArray.newInstance(dims,   
	 	            MWClassID.DOUBLE, MWComplexity.REAL);  
	 	     y1 = MWNumericArray.newInstance(dims,   
	 	            MWClassID.DOUBLE, MWComplexity.REAL); 
	         
	 	     //����  y = x^2  
	         for (int i = 1; i <= n; i++)  
	         {  
	            x.set(i, i);  
	            y.set(i, i*i);  
	            x1.set(i, i);  
	            y1.set(i, i*i + 100);  
	         }  
	  
	         //��ʼ��plotter�Ķ���  
	         thePlot = new plotter();  
	  
	         //��ͼ  
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
	         //�ͷű�����Դ  
	         MWArray.disposeArray(x);  
	         MWArray.disposeArray(y);  
	         if (thePlot != null)  
	            thePlot.dispose();  
	      }  
	   }  
	

}
