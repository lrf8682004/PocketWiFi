package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;




public class PacketGenerator {

	
	private static PacketGenerator mPacketGenerator;
	private BufferedReader reader = null;
	private Packet mCurrentPacket = null;
	public static PacketGenerator getInstance(String fileName){ 
		if( mPacketGenerator == null){
			mPacketGenerator = new PacketGenerator(fileName);
		}
		return mPacketGenerator;
	}
	
	public  class Packet{
		int index;
		String source;
		String destination;
		int size;
		int timestampMs;
		String protocol;
	}
	
	private int mStartTimeMillionSecond;
	
	public int GetStartTimeMillionSecond(){
		return mStartTimeMillionSecond;
	}
	
	/**
	 * intiate File 
	 * read the first packet to decide the startTime
	 * @param fileName
	 */
	private PacketGenerator(String fileName){
			//open trace file
			File file = new File(fileName);
			try {
		
				reader = new BufferedReader(new FileReader(file));
				reader.readLine(); //dummy read
				reader.readLine(); //dummy read
				
				//first packet
				mCurrentPacket = ParseRecord(reader.readLine());
				mStartTimeMillionSecond = mCurrentPacket.timestampMs; 
			}catch(Exception e){
				e.printStackTrace();
			}
	}
	
	/**
	 * return number of bytes arrived [from] currentTimeMilliSecond [to] currentTimeMilliSecond + intervalMillionSecond
	 * @param currentTimeMilliSecond
	 * @param intervalMillionSecond
	 * @return int number of bytes arriving
	 * 		   -1 End of file
	 * 			
	 */
	private boolean mEoFflag = false;
	public class ArrivePacketInform{
		public int mNumofBytes;
		public int offsetMilliSecond;
		public ArrivePacketInform(int bytes , int offset){mNumofBytes = bytes;offsetMilliSecond = offset;}		
	}
	
	private boolean IPfilter(String ip){
	/*	if(mCurrentPacket.source.equals("112.80.255.58") || mCurrentPacket.destination.equals("112.80.255.58"))
			return true;
	
		if(mCurrentPacket.source.equals("123.138.60.183") || mCurrentPacket.destination.equals("123.138.60.183"))
			return true;
		
		if(mCurrentPacket.source.equals("221.204.23.16") || mCurrentPacket.destination.equals("221.204.23.16"))
			return true;
		
		return false;*/
      return true;
	}
	public  ArrivePacketInform GeneratePacket(int currentTimeMilliSecond, int intervalMillionSecond) throws IOException{
		//read packet in current beacon interval
		int numberOfRX = 0;	
		String tempString;
		
		if(mEoFflag == true){ return new ArrivePacketInform(-1 , 0);}
		if(mCurrentPacket != null && mCurrentPacket.timestampMs < currentTimeMilliSecond + intervalMillionSecond){
			if(IPfilter(mCurrentPacket.source) || IPfilter(mCurrentPacket.destination))
				numberOfRX++;
			int offset = mCurrentPacket.timestampMs - currentTimeMilliSecond; 
			//read more
			while ((tempString = reader.readLine()) != null) {
			
				//System.out.println( tempString);	
				mCurrentPacket = ParseRecord(tempString);
				if(mCurrentPacket.timestampMs < currentTimeMilliSecond + intervalMillionSecond){
					if(IPfilter(mCurrentPacket.source) || IPfilter(mCurrentPacket.destination))
					numberOfRX++;
				}else{
					//System.out.println(currentTimeMilliSecond + "-------------------" + numberOfRX + "----------------------------");
					break;
				}			
		    }
			if(tempString == null){//end of file 
				mEoFflag = true;
			}
				
		/*	Util.drawLine(currentTimeMilliSecond - GetStartTimeMillionSecond(), 0, 
					currentTimeMilliSecond - GetStartTimeMillionSecond(), numberOfRX);*/
			return new ArrivePacketInform( numberOfRX , offset);
		}else{
			return new ArrivePacketInform(0,0);
		}
		
	
	}
	
	private  int ParseTime(String time){
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
	
	public  Packet ParseRecord(String line){
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
	
}
