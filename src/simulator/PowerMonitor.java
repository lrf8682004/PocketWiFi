package simulator;

public class PowerMonitor {

	private final double mDCHPowerMW = 732.826 / 1000.0;
	private final double mFACHPowerMW = 388.880 / 1000.0;
	private final double mDCHTimeSecond = 3.287;
	private final double mFACHTimeSecond = 4.024;
	
	private int mStartTime;
	private Util.CellStatus m_CellularStatus;
	public double CellTXEnergy, CellTAILEnergy;
	
	
	private double mStartTAILTime, mStartTXTime;
	private int mCurrentRSSI;
	
	private SignalGenerator mSignalGenerator;
	
	private static PowerMonitor mPowerMonitor = null;
	public static PowerMonitor getInstance(int startTime){
		if(mPowerMonitor == null){
			mPowerMonitor = new PowerMonitor(startTime);
		}
		return mPowerMonitor;
	}
	private PowerMonitor(int startTime){
		mStartTime = startTime;
		mWiFiIDLEStart = startTime;
		m_CellularStatus = Util.CellStatus.IDLE;	
		mSignalGenerator = SignalGenerator.getInstance();
	}
	
	/**
	 * 
	 * @param SignalStrength
	 * @return mW
	 * -50dBm 220mW
	 * -70dBm 720mW
	 * -90dBm 1220mW
	 */
	private static double TXPower(int SignalStrength){
		return (-25 * SignalStrength - 1030) /1000.0;
	}
	
	/**
	 * Callback
	 * @param status
	 * @param timeToSwitch milliSecond
	 */
	public void onChangeStatus(Util.CellStatus status, double timeToSwitch){
		/* X-> TAIL*/
		if(m_CellularStatus != Util.CellStatus.TAIL && status == Util.CellStatus.TAIL){
			mStartTAILTime = timeToSwitch;
		}
		/* TAIL->X*/
		if(m_CellularStatus == Util.CellStatus.TAIL && status != Util.CellStatus.TAIL){
			
			double timeInTAILSecond =  (timeToSwitch -mStartTAILTime) / 1000.0;
			if(timeInTAILSecond> mDCHTimeSecond){
				CellTAILEnergy += mDCHTimeSecond * mDCHPowerMW;
				CellTAILEnergy +=  ( timeInTAILSecond - mDCHTimeSecond) * mFACHPowerMW;
			}else{
				CellTAILEnergy += timeInTAILSecond * mDCHPowerMW;
			}
		}
		
		/* X-> TX*/
		if(m_CellularStatus != Util.CellStatus.TX && status == Util.CellStatus.TX){
			mStartTXTime = timeToSwitch;
			mCurrentRSSI = mSignalGenerator.GetCurrentSiganlStrength((int)timeToSwitch);
		}
		
		/* TX-> X*/
		if(m_CellularStatus == Util.CellStatus.TX && status != Util.CellStatus.TX){
			CellTXEnergy +=  (timeToSwitch -mStartTXTime) * TXPower(mCurrentRSSI) / 1000.0;
		}
		
		m_CellularStatus = status;
	}
	
	/**
	 * Callback
	 * @param timeToSwitch
	 */
	public void onChangeRSSI(double timeToSwitch){
		if(m_CellularStatus == Util.CellStatus.TX){
			CellTXEnergy +=  (timeToSwitch -mStartTXTime)* TXPower(mCurrentRSSI) / 1000.0;
			mCurrentRSSI = mSignalGenerator.GetCurrentSiganlStrength((int)timeToSwitch);
			mStartTXTime = timeToSwitch;
		}
	}
	
	
	/**
	 *  WiFi Power module
	 */
	public double WiFiEnergy =0;
	private Util.WiFiStatus  m_WiFiStatus = Util.WiFiStatus.IDLE; 
	private double mWiFiIDLEStart;
	public void onWiFiStatusChange(Util.WiFiStatus status, double timeToSwitch){
		if(m_WiFiStatus != Util.WiFiStatus.IDLE && status == Util.WiFiStatus.IDLE){
			mWiFiIDLEStart = timeToSwitch;
		}
		if(m_WiFiStatus != Util.WiFiStatus.SLEEP && status == Util.WiFiStatus.SLEEP){
			//wifi Idle power 270mw 
			WiFiEnergy+= (timeToSwitch- mWiFiIDLEStart) * 0.27 / 1000.0  ;
		}
		m_WiFiStatus = status;
	}
}
