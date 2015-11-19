package simulator;

public class SignalGenerator {

	public static SignalGenerator mSignalGenerator = null;
	public static int mStartTimeMilliSecond = 0;
	public static SignalGenerator getInstance(){
		if(mSignalGenerator == null){
			mSignalGenerator = new SignalGenerator();
		}
		return mSignalGenerator;
	}
	public int GetCurrentSiganlStrength(int timeMilliSecond){
		int minute = (timeMilliSecond - mStartTimeMilliSecond) / (60 * 1000);
		/*switch(minute % 4){
		case 0 :return -60; 
		case 1:return -80; 
		case 2 :return -90; 
		case 3 :return -100; 
		}
		return -90;*/ 
		int rssi  = 0;
		
		int phase = (timeMilliSecond - mStartTimeMilliSecond)  % (60 * 1000);
		if(phase <= 30*1000){
			rssi  = (phase * 5 )/3000 -100;
		}else{
		rssi=  - (phase * 5) / 3000;
		}
		
		return rssi;
	}
}
