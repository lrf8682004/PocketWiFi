package simulator;

import plotdemo.plotter;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class Util {
	enum CellStatus{
		IDLE,
		TX, 
		TAIL
	}
	
	enum WiFiStatus{
		IDLE,
		SLEEP
	}
	
	private static  plotter thePlot = null;    //plotter���ʵ��  
	static{
		//��ʼ��plotter�Ķ���  
        try {
			thePlot = new plotter();
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	public static void drawLine(int value_x, int value_y, int value_x1, int value_y1 ){
		try {
			thePlot.drawLine(value_x,value_x1,value_y,value_y1);
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void drawPoint(int value_x, int value_y){
		 //����x��y��ֵ  
        int[] dims = {1, 1}; 
        MWNumericArray x = null;   //���xֵ������  
	    MWNumericArray y = null;    //���yֵ������  
        x = MWNumericArray.newInstance(dims,   
           MWClassID.DOUBLE, MWComplexity.REAL);  
        y = MWNumericArray.newInstance(dims,   
           MWClassID.DOUBLE, MWComplexity.REAL);  
        
	     //����  
        x.set(1, value_x);  
        y.set(1, value_y);  
    
        //��ͼ  
        try {
			thePlot.drawplot(x, y);
		} catch (MWException e) {
			MWArray.disposeArray(x);  
		    MWArray.disposeArray(y);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
