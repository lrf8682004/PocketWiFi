package simulator;

import java.io.IOException;

import simulator.PacketGenerator.ArrivePacketInform;
import simulator.main.CellStatus;

public class Scheduler {

	private static PowerMonitor mPowerMonitor;
	private static SignalGenerator mSignalGenerator;
	private static PacketGenerator mPacketGenerator ;
	private static int currentTimeMilliSecond =0; 
	private static int delayTimer = -1;
	private static boolean mDelayAlgorithm = true;
	public static void main(String[] args) throws IOException {
		
		//Get PacketGenerator
		mPacketGenerator = PacketGenerator.getInstance("F:/eclipse-java-luna-SR1a-win32/workspace/simulator/data/indoor_filtered.csv");
		currentTimeMilliSecond = mPacketGenerator.GetStartTimeMillionSecond();
		startIDLE = currentTimeMilliSecond;
		int intervalMillionSecond = 100;
				
		//Get SignalGenerator
		mSignalGenerator = SignalGenerator.getInstance();
		SignalGenerator.mStartTimeMilliSecond = currentTimeMilliSecond;
		
		//Get PowerMonitor
		mPowerMonitor = PowerMonitor.getInstance(currentTimeMilliSecond);
		
		//Enable delay algorithm
		mDelayAlgorithm = true;
		
		ArrivePacketInform arrivePacketInform;
		int total=0;
		int currentRSSI = mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond);
		
		do{
			//Generate Packet from file
			arrivePacketInform = mPacketGenerator.GeneratePacket(currentTimeMilliSecond,intervalMillionSecond);
			//System.out.println(" " + currentTimeMilliSecond +  "============receive " + arrivePacketInform.mNumofBytes + "packets, offset " + arrivePacketInform.offsetMilliSecond + "ms");
			
			
			//Monitor Cellular RSSI change
			if( currentRSSI  != mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond)){
				currentRSSI =  mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond);
				mPowerMonitor.onChangeRSSI(currentTimeMilliSecond);

				Util.drawPoint(currentTimeMilliSecond - mPacketGenerator.GetStartTimeMillionSecond(), currentRSSI);
			}
		
			m_WiFiQueueByte += arrivePacketInform.mNumofBytes;
			
			if(mDelayAlgorithm == true){
				//DelayAlgorithmNaive(arrivePacketInform.mNumofBytes);
				//DelayAlgorithmCheckCellQueue();
			   // DelayAlogrithmCellStatus();
			    DelayAlogrithmHighSpeedRailway();
			}else{
		    	
		    	if(currentRSSI >= -70){
					numberofPacketInStrong += m_WiFiQueueByte;
				}else{
					numberofPacketInWeak += m_WiFiQueueByte;
				}
		    	if(m_WiFiQueueByte > 0)System.out.println(m_WiFiQueueByte);
		    	CellStateMachineOrigin(m_WiFiQueueByte* 1024,arrivePacketInform.offsetMilliSecond);
		    	m_WiFiQueueByte =0;
		    	mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
		    }
			//CellStatusMachine
			//CellStateMachineOrigin(arrivePacketInform.mNumofBytes * 1024,arrivePacketInform.offsetMilliSecond);
		    
			
			//update time
			currentTimeMilliSecond += intervalMillionSecond;
			total += arrivePacketInform.mNumofBytes;
			
			//System.out.println(" m_CellularQueueByte " + m_CellularQueueByte + " m_CellularStatus " +  m_CellularStatus );
		}while(-1 != arrivePacketInform.mNumofBytes || m_CellularQueueByte !=0 || m_CellularStatus != Util.CellStatus.IDLE);
		
		mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond + 100);
		
		/*System.out.println("TOTAL " + total); 
		System.out.println("total time : "+ (mTxTime + mTailTime +  mIdleTime)/1000.0 + "s");
		System.out.println("total tail time : "+ mTailTime/1000.0 + "s");
		System.out.println("total TX time : "+ mTxTime/1000.0 + "s");
		System.out.println("total IDLE time : "+ mIdleTime/1000.0 + "s");
*/		System.out.println("Energy: " + (mPowerMonitor.CellTXEnergy + mPowerMonitor.CellTAILEnergy +  mPowerMonitor.WiFiEnergy));
		System.out.println("total TX Energy " +  mPowerMonitor.CellTXEnergy); 
		System.out.println("total TAIL Energy " +  mPowerMonitor.CellTAILEnergy); 
		System.out.println("total WIFI Energy " +  mPowerMonitor.WiFiEnergy);
		System.out.println("TX in strong:" +  numberofPacketInStrong);
		System.out.println("TX in Weak:" +  numberofPacketInWeak);
		System.out.println("Total TX:" +  (numberofPacketInStrong + numberofPacketInWeak ));
	}

	private static void DelayAlgorithmNaive(int numofPacket){
		 if(delayTimer < 0) delayTimer = 0;
		  if(delayTimer != 0){
				delayTimer--;
				//CellStatusMachine
				CellStateMachineOrigin(0,0);
			}else if(m_WiFiQueueByte  == 0 ){
				delayTimer= 1200;    //delayTimer * 100ms   10-> 1s
				CellStateMachineOrigin(0,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond + 100);
			}else{
				//CellStatusMachine
				CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
				m_WiFiQueueByte = 0;
			}	
	}
	
	private static void DelayAlgorithmCheckCellQueue(){
		   
		int currentRSSI = mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond);
		int currentByteRate = ByteRate(currentRSSI) ;
		if(m_CellularQueueByte >=  currentByteRate / 10){
				CellStateMachineOrigin(0,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond );
		}else{
				//CellStatusMachine
				CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
				m_WiFiQueueByte = 0;
		}	
	}
	
	private static int numberofPacketInStrong = 0,numberofPacketInWeak =0 ;
	
	private static int step = 0;
	private static void DelayAlogrithmCellStatus(){
		
		//step 1  Situation in which WiFi must sleep
		int currentRSSI = mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond);
		int currentByteRate = ByteRate(currentRSSI);
		if(m_CellularQueueByte >=  currentByteRate / 10 ){
				CellStateMachineOrigin(0,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond );
				return;
		}
	
		
		//step three  Situation in which WiFi must stay awake
		if(m_CellularStatus == Util.CellStatus.TX){
			
			if(currentRSSI >= -70){
				numberofPacketInStrong += m_WiFiQueueByte;
			}else{
				numberofPacketInWeak += m_WiFiQueueByte;
			}
			
			CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
			mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
			m_WiFiQueueByte = 0;
			return;
		}
		if(m_CellularStatus == Util.CellStatus.TAIL){
		
			if(m_CellularInactiveTimer / 7311.0 >= 0.38 ){
				
				if(currentRSSI >= -70){
					numberofPacketInStrong += m_WiFiQueueByte;
				}else{
					numberofPacketInWeak += m_WiFiQueueByte;
				}
				CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
				m_WiFiQueueByte = 0;

				return;
		    }
		}
	
		
		//step four 
		if(currentRSSI >= -70){
			
			step = (step + 1) % 600;
			if(step == 0 ){
			numberofPacketInStrong += m_WiFiQueueByte;
			if( m_WiFiQueueByte > 0 )System.out.println("TX in good "+ m_WiFiQueueByte +" K" );
			CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
			mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
			m_WiFiQueueByte = 0;
			delayTimer = -1; //no delay in good signal strength
			}else{
				CellStateMachineOrigin(0,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond);
			}
			return;	
		}else{ //signal is weak, so delay
			
			if(delayTimer > 0){  //in pervious delay
				CellStateMachineOrigin(0,0);
				delayTimer--;
				return;
			}else if(delayTimer == 0 ){ //reach deadline 
				
				numberofPacketInWeak += m_WiFiQueueByte;
			
				CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
				mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
				m_WiFiQueueByte = 0;
				delayTimer = -1; //delete the timer
				return;
			}else if(delayTimer == -1){ //start a new timer
				 delayTimer =  60*4*10;
				 CellStateMachineOrigin(0,0);
				 mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond+100 );
				 return;
			}
			
		}   
    }
	
	
	private static void DelayAlogrithmHighSpeedRailway(){
		
		int currentRSSI = mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond);
		//step four 
		if(currentRSSI > -60){
			if(m_WiFiQueueByte != 0 ){
				System.out.println("Bytes: " +m_WiFiQueueByte);
			}
			numberofPacketInStrong += m_WiFiQueueByte;
			CellStateMachineOrigin(m_WiFiQueueByte* 1024,0);
			mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.IDLE, currentTimeMilliSecond);
			m_WiFiQueueByte = 0;
			
		}else{
			System.out.print("-");
			mPowerMonitor.onWiFiStatusChange(Util.WiFiStatus.SLEEP, currentTimeMilliSecond );
		}
		
}
	
	private static int m_CellularQueueByte = 0, m_WiFiQueueByte = 0;
	private static Util.CellStatus m_CellularStatus = Util.CellStatus.IDLE;
	private static int m_CellularInactiveTimer = 0;
	/**
	 * 100ms调度一次
	 * @param numberOfBitReceived
	 */
	private static void CellStateMachineOrigin(int numberOfBitReceived, int offset){
		
		//read trace in 100ms
		boolean packetArrive =numberOfBitReceived > 0 ? true : false;	
		if(packetArrive) m_CellularQueueByte += numberOfBitReceived;
		
		//
		int currentRSSI = mSignalGenerator.GetCurrentSiganlStrength(currentTimeMilliSecond);
		int currentByteRate = ByteRate(currentRSSI);
		
		//cellular state machine
		switch(m_CellularStatus){
		case TX:
			int tempByte = m_CellularQueueByte;
			//reduce number of Byte tx in 100ms
			m_CellularQueueByte = m_CellularQueueByte < currentByteRate / 10 ? 0 : m_CellularQueueByte - currentByteRate / 10; 
			if(m_CellularQueueByte == 0){//transmit complete
				double timeToTx = tempByte * 1000.0 / currentByteRate;
				SwitchCellStatus(Util.CellStatus.TAIL, currentTimeMilliSecond +  timeToTx) ;
			}else{
				// go on to TX
			}
			break;
		case TAIL:
			if(packetArrive){
			   SwitchCellStatus(Util.CellStatus.TX , currentTimeMilliSecond);
			   tempByte = m_CellularQueueByte;
			   m_CellularQueueByte = m_CellularQueueByte < currentByteRate / 10 ? 0 : m_CellularQueueByte - currentByteRate / 10; 
			   if(m_CellularQueueByte == 0){
					double timeToTx = tempByte * 1000.0 / currentByteRate;
					SwitchCellStatus(Util.CellStatus.TAIL, currentTimeMilliSecond +  timeToTx) ;
			   }
		       break; 
			}
			//update inactive timer
			if(m_CellularInactiveTimer <  100){
				SwitchCellStatus(Util.CellStatus.IDLE, currentTimeMilliSecond + m_CellularInactiveTimer);
				m_CellularInactiveTimer = 0;
			}else{
				m_CellularInactiveTimer-=100;
			}
			break;
		case IDLE:
			if(packetArrive){
				 SwitchCellStatus(Util.CellStatus.TX , currentTimeMilliSecond);
				 tempByte = m_CellularQueueByte;
				 m_CellularQueueByte = m_CellularQueueByte < currentByteRate / 10 ? 0 : m_CellularQueueByte - currentByteRate / 10; 
				 if(m_CellularQueueByte == 0){
					double timeToTx = tempByte * 1000.0 / currentByteRate;
					SwitchCellStatus(Util.CellStatus.TAIL, currentTimeMilliSecond +  timeToTx) ;
			     }
			     break; 
			}
			break;
		}
				
	}
	
	/**
	 * number of byte tx per second in SignalStrength   
	 * @param SignalStrength
	 * @return Bps
	 */
	private static int ByteRate(int SignalStrength){
		return (int)(1000 * (2.667 * SignalStrength + 293.73)); //-50dBm 160kBps
																//-70dBm 107KBps
																//-90dBm 53Bps
																//-100dBm 27.03KBps
	}

	
	private static double mTxTime=0, mTailTime=0, mIdleTime=0;
	private static double startTAIL = 0, startTX=0, startIDLE =0; 
	/**
	 * 
	 * @param status
	 * @param timeToSwitch ms
	 */
	private static void SwitchCellStatus(Util.CellStatus status, double timeToSwitch){
		
		/* X-> TAIL*/
		if(m_CellularStatus != Util.CellStatus.TAIL && status == Util.CellStatus.TAIL){
			m_CellularInactiveTimer = 7311; // 3.287 + 4.024 s = 12 * 1000ms
			startTAIL = timeToSwitch;
		}
		/* TAIL->X*/
		if(m_CellularStatus == Util.CellStatus.TAIL && status != Util.CellStatus.TAIL){
			mTailTime += (timeToSwitch -startTAIL);
		}
		
		/* X-> TX*/
		if(m_CellularStatus !=Util.CellStatus.TX && status == Util.CellStatus.TX){
			startTX = timeToSwitch;
			//mTXDeadline = 0;
		}
		/* TX-> X*/
		if(m_CellularStatus == Util.CellStatus.TX && status != Util.CellStatus.TX){
			
			if(timeToSwitch  < startTX){
				System.out.println("");
			}
			mTxTime +=  (timeToSwitch -startTX);
		}
		
		/* X-> IDLE*/
		if(m_CellularStatus != Util.CellStatus.IDLE && status == Util.CellStatus.IDLE){
			startIDLE = timeToSwitch;
		}
		/* IDLE-> X*/
		if(m_CellularStatus == Util.CellStatus.IDLE && status != Util.CellStatus.IDLE){
			mIdleTime +=  (timeToSwitch -startIDLE);
		}
		
		m_CellularStatus = status;
		mPowerMonitor.onChangeStatus(m_CellularStatus, timeToSwitch);
	}
	
	
}
