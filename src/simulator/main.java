package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class main {

	private static int m_CellularQueueByte = 0;
	private static int m_WiFiPacket = 0;

	private static int m_CellularInactiveTimer = 0;
	private static CellStatus m_CellularStatus = CellStatus.IDLE;
	
	private final static int mPacketDeadline = 60000; //60s
	
	
	private static int mCurrenttime, mStarttime;
	private static int mTXDeadline;
	enum CellStatus{
		IDLE,
		TX, 
		TAIL
	}
	
	
	/**
	 *  统计信息
	 *  TX energy   J
	 *  TAIL energy J
	 *  TX   Time   ms
	 *  TAIL Time   ms
	 *  IDLE Time   ms
	 */
	private static double mTxEnergy = 0, mTailEnergy =0;
	private static double mTxTime=0, mTailTime=0, mIdleTime=0;
	
	private static double startTAIL = 0, startTX=0, startIDLE =0; 
	
	/**
	 *  每100ms更加状态
	 */
	private static void UpdateEnergy(){
		
	}
	/**
	 * 
	 * @param status
	 * @param timeToSwitch ms
	 */
	private static void SwitchCellStatus(CellStatus status, double timeToSwitch){
		
		/* X-> TAIL*/
		if(m_CellularStatus != CellStatus.TAIL && status == CellStatus.TAIL){
			m_CellularInactiveTimer = 7000; //12s = 12 * 1000ms
			startTAIL = timeToSwitch;
		}
		/* TAIL->X*/
		if(m_CellularStatus == CellStatus.TAIL && status != CellStatus.TAIL){
			mTailTime += (timeToSwitch -startTAIL);
		}
		
		/* X-> TX*/
		if(m_CellularStatus != CellStatus.TX && status == CellStatus.TX){
			startTX = timeToSwitch;
			mTXDeadline = 0;
		}
		/* TX-> X*/
		if(m_CellularStatus == CellStatus.TX && status != CellStatus.TX){
			
			if(timeToSwitch  < startTX){
				System.out.println("");
			}
			mTxTime +=  (timeToSwitch -startTX);
		}
		
		/* X-> IDLE*/
		if(m_CellularStatus != CellStatus.IDLE && status == CellStatus.IDLE){
			startIDLE = timeToSwitch;
		}
		/* IDLE-> X*/
		if(m_CellularStatus == CellStatus.IDLE && status != CellStatus.IDLE){
			mIdleTime +=  (timeToSwitch -startIDLE);
		}
		
		m_CellularStatus = status;
	}
	
	/**
	 * number of byte tx per second in SignalStrength   
	 * @param SignalStrength
	 * @return Bps
	 */
	private static int ByteRate(int SignalStrength){
		return (int)(1000 * (2.667 * SignalStrength + 293.73));
	}

	
	
	/**
	 * 
	 * @param timeMs
	 * @return
	 */
	private static int GetSignalStrength(int timeMs){
		
	    int h = timeMs / (3600 * 1000);
		int m = (timeMs - h * 3600 * 1000) / (60 * 1000);
		int s = (timeMs - h * 3600 * 1000 - m * 60 * 1000) / 1000;
		
		/*int minute = (timeMs - mStarttime) / (60 * 1000);
		if(minute % 4 == 0){
			return -70;
		}
		return -90;
*/
		return -90;
	}
	
	
	private static class Packet{
		int index;
		String source;
		String destination;
		int size;
		int timestampMs;
		String protocol;
	}
	
	private static int ParseTime(String time){
		//17:34:32.538866
		int timeMs = 0;
		int index = 0;
		StringTokenizer tokenizer = new StringTokenizer(time,":");
		
		while( tokenizer.hasMoreElements() ){
			String temp =  tokenizer.nextToken();
			switch(index){
			case 0: timeMs = Integer.parseInt(temp) * 3600 * 1000; break;
			case 1: timeMs += Integer.parseInt(temp) * 60 * 1000; break;
			case 2: timeMs += (int)(Double.parseDouble(temp) * 1000) ; break;
			}
			index++;
		}
		return timeMs;
	}
		
	public static Packet ParseRecord(String line){
		Packet tempPacket = new Packet();
		StringTokenizer tokenizer = new StringTokenizer(line,",");
		int index =0;
		//5,120.131.3.109,10.15.169.136,78,17:34:32.538866,HTTPS
		while( tokenizer.hasMoreElements() ){
			String temp =  tokenizer.nextToken();
			switch(index){
			case 0: tempPacket.index = Integer.parseInt(temp); break;
			case 1: tempPacket.source = temp; break;
			case 2: tempPacket.destination = temp; break;
			case 3: tempPacket.size =Integer.parseInt(temp); break;
			case 4: tempPacket.timestampMs = ParseTime(temp); break;
			case 5: tempPacket.protocol = temp; break;
			}
			index++;
		}
		/*System.out.println("index: "+ tempPacket.index +
							"\r\nsource:" +  tempPacket.source + 
							"\r\ndest:"   +  tempPacket.destination + 
							"\r\nsize:"   +  tempPacket.size + 
							"\r\ntimestamp: " + tempPacket.timestampMs );*/
		return tempPacket;
	}
	

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//open trace file
		File file = new File("F:/eclipse-java-luna-SR1a-win32/workspace/simulator/data/shark_dump_xd_npu.csv");
		BufferedReader reader = null;
		try {
	
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			
			reader.readLine(); //dummy read
			reader.readLine(); //dummy read
			
			//first packet
			Packet newPacket = ParseRecord(reader.readLine());
			mCurrenttime = newPacket.timestampMs; 
			mStarttime = mCurrenttime;
			
			startIDLE = mCurrenttime;
			
			int offset =0;
			boolean flagEof= false;
			while(!flagEof || m_CellularQueueByte !=0 || m_CellularStatus != CellStatus.IDLE){
				
			if(flagEof){
				//end of file
				newPacket = null;
			}
			//read packet in current beacon interval
			int numberOfRX = 0;
				
			if(newPacket != null && newPacket.timestampMs < mCurrenttime + 100){
				
				if(newPacket.source.equals("112.80.255.58") || newPacket.destination.equals("112.80.255.58"))
				{numberOfRX++;}
				offset = newPacket.timestampMs - mCurrenttime; 
				//read more
				flagEof = true;
				while ((tempString = reader.readLine()) != null) {
				
					System.out.println("line " + line + ": " + tempString);
					line++;
					newPacket = ParseRecord(tempString);
					if(newPacket.timestampMs < mCurrenttime + 100){
						
						
						if(newPacket.source.equals("112.80.255.58") || newPacket.destination.equals("112.80.255.58"))
						numberOfRX++;
					}else{
						System.out.println(mCurrenttime + "-------------------" + numberOfRX + "----------------------------");
						flagEof = false;
						break;
					}			
			    }
		
			}
			
			CellStateMachineOrigin(numberOfRX * 1024,offset);
			mCurrenttime += 100;
			}
			
			System.out.println("total time : "+ (mTxTime + mTailTime +  mIdleTime)/1000.0 + "s");
			System.out.println("total tail time : "+ mTailTime/1000.0 + "s");
			System.out.println("total TX time : "+ mTxTime/1000.0 + "s");
			System.out.println("total IDLE time : "+ mIdleTime/1000.0 + "s");	
	
			
		}catch (IOException e) {
		    e.printStackTrace();
		}finally{
			if(reader !=null){
				
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 100ms调度一次
	 * @param numberOfBitReceived
	 * @param offset time To Receive the packet in 100ms 
	 */
	private static void CellStateMachine(int numberOfBitReceived, int offset){
		//read trace in 100ms
		boolean packetArrive =numberOfBitReceived > 0 ? true : false;	
		m_CellularQueueByte += numberOfBitReceived;
		
		//cellular state machine
		switch(m_CellularStatus){
		case TX:
			int tempByte = m_CellularQueueByte;
			m_CellularQueueByte = m_CellularQueueByte < ByteRate(GetSignalStrength(mCurrenttime)) / 10 ? 0 : m_CellularQueueByte - ByteRate(GetSignalStrength(mCurrenttime))/ 10;  // /10 -> 100ms
			if(m_CellularQueueByte == 0){
				double timeToTx = tempByte * 1000.0 / ByteRate(GetSignalStrength(mCurrenttime));
				SwitchCellStatus(CellStatus.TAIL, mCurrenttime +  timeToTx) ;
			}else{
				// go on to TX
			}
			break;
		case TAIL:
			if(packetArrive){
			    if(m_CellularInactiveTimer / 7000.0 < 0.38){
			    	//do not tx / wait to deadline
			    	int deadline = mCurrenttime + mPacketDeadline;
			    	if(mTXDeadline == 0)
			    		mTXDeadline = deadline;
			    }else{
			    	SwitchCellStatus(CellStatus.TX , mCurrenttime);
			    	tempByte = m_CellularQueueByte;
					m_CellularQueueByte = m_CellularQueueByte < ByteRate(-GetSignalStrength(mCurrenttime)) / 10 ? 0 : m_CellularQueueByte - ByteRate(GetSignalStrength(mCurrenttime))/ 10;  // /10 -> 100ms
					if(m_CellularQueueByte == 0){
						double timeToTx = tempByte * 1000.0 / ByteRate(GetSignalStrength(mCurrenttime));
						SwitchCellStatus(CellStatus.TAIL, mCurrenttime +  timeToTx) ;
					}
			    	return;
			    }
			}
			//update inactive timer
			m_CellularInactiveTimer-=100;
			if(m_CellularInactiveTimer ==0){
				SwitchCellStatus(CellStatus.IDLE, mCurrenttime + 100);
			}
			break;
		case IDLE:
			//deadline arrive and Cellular queue not empty
			if((mCurrenttime == mTXDeadline) && (mTXDeadline != 0) && ( m_CellularQueueByte !=0) ){
				SwitchCellStatus(CellStatus.TX, mTXDeadline);
				tempByte = m_CellularQueueByte;
				m_CellularQueueByte = m_CellularQueueByte < ByteRate(GetSignalStrength(mCurrenttime)) / 10 ? 0 : m_CellularQueueByte - ByteRate(GetSignalStrength(mCurrenttime))/ 10;  // /10 -> 100ms
				if(m_CellularQueueByte == 0){
					double timeToTx = tempByte * 1000.0 / ByteRate(GetSignalStrength(mCurrenttime));
					SwitchCellStatus(CellStatus.TAIL, mCurrenttime +  timeToTx) ;
				}
			    return;
			}
			//packet arrive but deadline don't arrive
			if(packetArrive){
				//calculate the deadline for the packets
				int deadline = mCurrenttime + mPacketDeadline;
		    	if(mTXDeadline == 0)
		    		mTXDeadline = deadline;
			}
			break;
		}
				
	}

	
	/**
	 * 100ms调度一次
	 * @param numberOfBitReceived
	 */
	private static void CellStateMachineOrigin(int numberOfBitReceived, int offset){
		//read trace in 100ms
		boolean packetArrive =numberOfBitReceived > 0 ? true : false;	
		m_CellularQueueByte += numberOfBitReceived;
		
		//cellular state machine
		switch(m_CellularStatus){
		case TX:
			int tempByte = m_CellularQueueByte;
			//reduce number of Byte tx in 100ms
			m_CellularQueueByte = m_CellularQueueByte < ByteRate(GetSignalStrength(mCurrenttime)) / 10 ? 0 : m_CellularQueueByte - ByteRate(GetSignalStrength(mCurrenttime)) / 10; 
			if(m_CellularQueueByte == 0){//transmit complete
				double timeToTx = tempByte * 1000.0 / ByteRate(GetSignalStrength(mCurrenttime));
				SwitchCellStatus(CellStatus.TAIL, mCurrenttime +  timeToTx) ;
			}else{
				// go on to TX
			}
			break;
		case TAIL:
			if(packetArrive){
			   SwitchCellStatus(CellStatus.TX , mCurrenttime);
			   tempByte = m_CellularQueueByte;
			   m_CellularQueueByte = m_CellularQueueByte < ByteRate(GetSignalStrength(mCurrenttime)) / 10 ? 0 : m_CellularQueueByte - ByteRate(GetSignalStrength(mCurrenttime)) / 10; 
			   if(m_CellularQueueByte == 0){
					double timeToTx = tempByte * 1000.0 / ByteRate(GetSignalStrength(mCurrenttime));
					SwitchCellStatus(CellStatus.TAIL, mCurrenttime +  timeToTx) ;
			   }
		       break; 
			}
			//update inactive timer
			m_CellularInactiveTimer-=100;
			if(m_CellularInactiveTimer == 0){
				SwitchCellStatus(CellStatus.IDLE, mCurrenttime + 100);
			}
			break;
		case IDLE:
			if(packetArrive){
				 SwitchCellStatus(CellStatus.TX , mCurrenttime);
				 tempByte = m_CellularQueueByte;
				 m_CellularQueueByte = m_CellularQueueByte < ByteRate(GetSignalStrength(mCurrenttime)) / 10 ? 0 : m_CellularQueueByte - ByteRate(GetSignalStrength(mCurrenttime)) / 10; 
				 if(m_CellularQueueByte == 0){
					double timeToTx = tempByte * 1000.0 / ByteRate(GetSignalStrength(mCurrenttime));
					SwitchCellStatus(CellStatus.TAIL, mCurrenttime +  timeToTx) ;
			     }
			     break; 
			}
			break;
		}
				
	}
}
