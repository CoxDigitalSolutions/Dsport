package frameWork;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DataPreparer implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public FeatureInfo[] FeatureInfo;
	String Delimiter=",";
	String SubDelimiter="\t";
	int [] ColumnSizes;
	int [] usedFeatures;

	public NumericalFeatureSummary TargetSummary=new NumericalFeatureSummary();
	
	static final byte NewLineByte=0;
	static final byte DelimiterByteInt=1;
	static final byte SubDelimiterByteInt=2;
	static final byte DelimiterByteShort=3;
	static final byte SubDelimiterByteShort=4;
	static final byte DelimiterByteByte=5;
	static final byte SubDelimiterByteByte=6;
	static final byte PredictionFloat=7;
	
	public void init(int FeatureCount){
		FeatureInfo=new FeatureInfo[FeatureCount];
		for(int i=0;i<FeatureInfo.length;i++){
			FeatureInfo[i]=new FeatureInfo();
		}
	}
	
	public void Finalize(){
		TargetSummary.Finalize();
	}
	
	public void Update(String [] RawFeatures){
		for(int i=0;i<RawFeatures.length;i++){
			if(RawFeatures[i].contains(SubDelimiter)){
				String [] values=RawFeatures[i].split(SubDelimiter);
				for(int j=0;j<values.length;j++){
					FeatureInfo[i].Update(values[j]);
				}
			}else{
				FeatureInfo[i].Update(RawFeatures[i]);
			}
		}
	}
	
	public void PrepareFeatures(int minSmoothingLimit){
		for(int i=0;i<FeatureInfo.length;i++){
			FeatureInfo[i].PrepareFeatures(minSmoothingLimit);
			FeatureInfo[i].SetMaxID();
		}
	}
	
	public void SetSmoothingLimit(int minSmoothingLimit){
		ColumnSizes=new int[FeatureInfo.length];
		for(int i=0;i<FeatureInfo.length;i++){
			FeatureInfo[i].SmoothingLimit=minSmoothingLimit;
			ColumnSizes[i] = FeatureInfo[i].SetMaxID();
		}
	}
	
	public String GetPreparedString(String [] RawFeatures){
		String Result="";
		for(int i=0;i<RawFeatures.length;i++){
			if(RawFeatures[i].contains(SubDelimiter)){
				String [] values=RawFeatures[i].split(SubDelimiter);
				int added=0;
				for(int j=0;j<values.length;j++){
					int ReturnedFeature=FeatureInfo[i].GetPreparedFeature(values[j]);
					if(ReturnedFeature!=0){
						Result+=ReturnedFeature+SubDelimiter;
						added++;
					}
				}
				if(added==0){
					Result+="0";
				}else{
					Result=Result.substring(0,Result.length()-1);
				}
			}else{
				Result+=FeatureInfo[i].GetPreparedFeature(RawFeatures[i]);
			}
			Result+=",";
		}
		//remove last comma as it's not needed
		Result=Result.substring(0,Result.length()-1);
		return Result;
	}
	
	public int appendBytesToBytes(byte[] main, byte[] append , int Bcount){
		for(int i=0;i<append.length;i++){
			main[Bcount]=append[i];
			Bcount++;
		}
		
		return Bcount;
	}
	
	public byte [] PredictionToByte(String value){
		float val=Float.parseFloat(value);
		if(Float.isNaN(val)){
			System.out.println("Nan value found in predictionToByte");
		}
		ByteBuffer.allocate(4).putFloat(val).array();
		byte [] predictionByte=ByteBuffer.allocate(4).putFloat(Float.parseFloat(value)).array();
		byte [] Result=new byte[predictionByte.length+1];
		Result[0]=PredictionFloat;
		for(int i=0;i<predictionByte.length;i++){
			Result[i+1]=predictionByte[i];
		}
		return Result;
	}
	
	public int appendBytesToByte(byte[] main, byte append , int Bcount){
		main[Bcount]=append;
		Bcount++;
		
		return Bcount;
	}
	
	public int AddMainValue(byte [] ByteWorking,int Bcount,int ReturnedFeature){

		if(ReturnedFeature<Byte.MAX_VALUE){
			Bcount=appendBytesToByte(ByteWorking,DelimiterByteByte,Bcount);
			byte [] val=UtilByte.ByteToBytes((byte) ReturnedFeature);
			Bcount=appendBytesToBytes(ByteWorking,val,Bcount);
			
		}else if(ReturnedFeature<Short.MAX_VALUE){
			Bcount=appendBytesToByte(ByteWorking,DelimiterByteShort,Bcount);
			byte [] val=UtilByte.ShortToBytes((short) ReturnedFeature);
			Bcount=appendBytesToBytes(ByteWorking,val,Bcount);
			
		}else{
			Bcount=appendBytesToByte(ByteWorking,DelimiterByteInt,Bcount);
			byte [] val=UtilByte.IntToBytes(ReturnedFeature);
			Bcount=appendBytesToBytes(ByteWorking,val,Bcount);
		}

		return Bcount;
	}
	
	public int AddSubValue(byte [] ByteWorking,int Bcount,int ReturnedFeature){
		if(ReturnedFeature<Byte.MAX_VALUE){
			Bcount=appendBytesToByte(ByteWorking,SubDelimiterByteByte,Bcount);
			byte [] val=UtilByte.ByteToBytes((byte) ReturnedFeature);
			Bcount=appendBytesToBytes(ByteWorking,val,Bcount);
			
		}else if(ReturnedFeature<Short.MAX_VALUE){
			Bcount=appendBytesToByte(ByteWorking,SubDelimiterByteShort,Bcount);
			byte [] val=UtilByte.ShortToBytes((short) ReturnedFeature);
			Bcount=appendBytesToBytes(ByteWorking,val,Bcount);
			
		}else{
			Bcount=appendBytesToByte(ByteWorking,SubDelimiterByteInt,Bcount);
			byte [] val=UtilByte.IntToBytes(ReturnedFeature);
			Bcount=appendBytesToBytes(ByteWorking,val,Bcount);
		}
		return Bcount;
	}
	
	public byte [] GetPreparedByte(String [] RawFeatures) throws InterruptedException{
		int SubDelimiterCount=0;
		for(int i=0;i<RawFeatures.length;i++){
			if(RawFeatures[i].contains(SubDelimiter)){
				SubDelimiterCount+= RawFeatures[i].length() - RawFeatures[i].replace(SubDelimiter, "").length();
			}
		}
		int FeatureCount=RawFeatures.length+SubDelimiterCount;

		// +1 for end byte and +4 for Feature count int
		byte [] ByteWorking=new byte[FeatureCount*5+1+4];
		int Bcount=0;
		
		// add count of Features
		byte [] val=UtilByte.IntToBytes(FeatureCount);
		Bcount=appendBytesToBytes(ByteWorking,val,Bcount);

		

		for(int i=0;i<RawFeatures.length;i++){
			
			if(RawFeatures[i].contains(SubDelimiter)){

				String [] values=RawFeatures[i].split(SubDelimiter);
				int added=0;
				for(int j=0;j<values.length;j++){
					int ReturnedFeature=FeatureInfo[i].GetPreparedFeature(values[j]);
					if(ReturnedFeature!=0){
						if(added>0){
							Bcount=AddSubValue( ByteWorking, Bcount, ReturnedFeature);
						}else{
							Bcount=AddMainValue( ByteWorking, Bcount, ReturnedFeature);
						}
						added++;
					}
				}
				if(added==0){
					Bcount=AddMainValue( ByteWorking, Bcount, 0);
				}else{
					Bcount--;
				}
			}else{
				int ReturnedFeature=FeatureInfo[i].GetPreparedFeature(RawFeatures[i]);
				Bcount=AddMainValue( ByteWorking, Bcount, ReturnedFeature);
			}
			
			//Bcount=appendBytesToBytes(ByteWorking,DelimiterByte,Bcount);
		}
		//remove last comma as it's not needed
		//Bcount--;

		
		Bcount=appendBytesToByte(ByteWorking,NewLineByte,Bcount);
		byte [] Result=new byte[Bcount];
		for(int i=0;i<Result.length;i++){
			Result[i]=ByteWorking[i];
		}

		return Result;
	}
	
	public int CheckUpdateBuffer(FileChannel inChannel,ByteBuffer buffer){
		if(!buffer.hasRemaining()){
			buffer.clear();
			try {
				if(!(inChannel.read(buffer) > 0)){
					return -1;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			buffer.flip();
		}
		return 0;
	}
	
	public int BufferGetInt(FileChannel inChannel,ByteBuffer buffer){
		int bytesRemaining=buffer.remaining();
		if(bytesRemaining>=4){
			return buffer.getInt();
		}else{
			int Pos=0;
			int Result=0;
			for(int i=0;i<bytesRemaining;i++){
				if(i==0){
					Result+=(buffer.get()<<24)&0xff000000;
					Pos++;
				}else if(i==1){
					Result+=(buffer.get()<<16)&0x00ff0000;
					Pos++;
				}else if(i==2){
					Result+=(buffer.get()<<8)&0x0000ff00;
					Pos++;
				}else{
					System.out.println("error int Dataprepare.BufferGetInt");
				}
			}
			if(CheckUpdateBuffer(inChannel,buffer)==-1){
				return -1;
			}
			for(int i=Pos;i<4;i++){
				if(i==0){
					Result+=(buffer.get()<<24)&0xff000000;
					Pos++;
				}else if(i==1){
					Result+=(buffer.get()<<16)&0x00ff0000;
					Pos++;
				}else if(i==2){
					Result+=(buffer.get()<<8)&0x0000ff00;
					Pos++;
				}else if(i==3){
					Result+=(buffer.get()<<0)&0x000000ff;
					Pos++;
				}else{
					System.out.println("error int Dataprepare.BufferGetInt");
				}
			}
			return Result;
		}
	}
	
	
	public float BufferGetFloat(FileChannel inChannel,ByteBuffer buffer){
		int bytesRemaining=buffer.remaining();
		if(bytesRemaining>=4){
			return buffer.getFloat();
		}else{
			byte [] BytePrediction=new byte[4];
			int Pos=0;
			float Result=0;
			for(int i=0;i<bytesRemaining;i++){
				BytePrediction[i]=buffer.get();
				Pos++;
			}
			if(CheckUpdateBuffer(inChannel,buffer)==-1){
				return -1;
			}
			for(int i=Pos;i<4;i++){
				BytePrediction[i]=buffer.get();
				Pos++;
			}
			int asInt = ((BytePrediction[0] & 0xFF)  << 24)
		            | ((BytePrediction[1] & 0xFF) << 16) 
		            | ((BytePrediction[2] & 0xFF) << 8) 
		            | (BytePrediction[3] & 0xFF);
			Result = Float.intBitsToFloat(asInt);

			return Result;
		}
	}
	
	public int BufferGetShort(FileChannel inChannel,ByteBuffer buffer){
		int bytesRemaining=buffer.remaining();
		
		if(bytesRemaining>=2){
			return buffer.getShort();
		}else if(bytesRemaining==1){
			int Result=0;
			Result+=(buffer.get()<<8)&0x0000ff00;
			if(CheckUpdateBuffer(inChannel,buffer)==-1){
				return -1;
			}
			Result+=(buffer.get()<<0)&0x000000ff;
			return Result;
		}else{
			int Result=0;
			if(CheckUpdateBuffer(inChannel,buffer)==-1){
				return -1;
			}
			Result+=(buffer.get()<<8)&0x0000ff00;
			Result+=(buffer.get()<<0)&0x000000ff;
			return Result;
		}
	}
	
	public float GetPredictionBinary(FileChannel inChannel,ByteBuffer buffer){
		if(CheckUpdateBuffer(inChannel,buffer)==-1){
			System.out.println("issue getting prediction from binary empty buffer/inChannel");
			return -1;
		}
		byte b=buffer.get();
		if(b==PredictionFloat){
			return BufferGetFloat(inChannel,buffer);
		}else{
			System.out.println("issue getting prediction from binary wrong first byte");
			return -1;
		}
		
		
	}
	
	public int [][] GetFeatures(FileChannel inChannel,ByteBuffer buffer){
		
		int FeatureCount=BufferGetInt(inChannel,buffer);
		if(FeatureCount==-1){
			return null;
		}

		int [] FullFeatureSet=new int [FeatureCount];
		int [] Positions=new int [FeatureCount];
		int Feature=0;
		int counter2=0;
		

		if(CheckUpdateBuffer(inChannel,buffer)==-1){
			return null;
		}

			
		while (true){
			if(CheckUpdateBuffer(inChannel,buffer)==-1){
				return null;
			}
			byte b=buffer.get();

			int val=0;
			boolean sub=false;

			if(b==DelimiterByteByte){
				if(!buffer.hasRemaining()){
					if(CheckUpdateBuffer(inChannel,buffer)==-1){
						return null;
					}
				}
				val=buffer.get();
			}else if(b==DelimiterByteShort){
				val=BufferGetShort(inChannel,buffer);
			}else if(b==DelimiterByteInt){
				val=BufferGetInt(inChannel,buffer);
			}else if(b==NewLineByte){
					int [][] FinalResult=new int[2][];
					FinalResult[0]=FullFeatureSet;
					FinalResult[1]=Positions;
					return FinalResult;
			}else if(b==SubDelimiterByteShort){
				val=BufferGetShort(inChannel,buffer);
				sub=true;
			}else if(b==SubDelimiterByteInt){
				val=BufferGetInt(inChannel,buffer);
				sub=true;
			}else if(b==SubDelimiterByteByte){
				if(!buffer.hasRemaining()){
					if(CheckUpdateBuffer(inChannel,buffer)==-1){
						return null;
					}
				}
				val=buffer.get();
				sub=true;
			}
			FullFeatureSet[counter2]=val;
			Positions[counter2]=Feature;
			counter2++;
			if(!sub){
				
				
				Feature++;
			}
			
		}
		
	}
	
	public int [] GetFeaturesFromInt(int [] Features, int [] Positions){
		int [] WorkingSet=new int [Features.length];
		int usedFeature=0;
		int count=0;
		int Position=0;
		

		
		for(int i=0;i<Positions.length;i++){
			
			while(usedFeatures[usedFeature]<Positions[i] && usedFeature<usedFeatures.length-1){
				usedFeature++;
			}
			if(usedFeatures[usedFeature]==Positions[i]){

				WorkingSet[count]=Position+FeatureInfo[Positions[i]].GetProcessedFeatureInt(Features[i]);
				Position+=FeatureInfo[usedFeatures[usedFeature]].GetMaxID()+1;
				count++;
				continue;
			}



		}
		int [] Result=new int [count];
		for(int i=0;i<count;i++){
			Result[i]=WorkingSet[i];
		}
		return Result;
	}
	
	public int [] GetFeatures(String FeaturesString){
		//System.out.println(FeaturesString);
		String [] values=FeaturesString.split(Delimiter);
		
		if(values.length!=FeatureInfo.length){
			System.out.println("FeatureString has wrong number of features :" + FeaturesString);
		}
		
		int SubDelimiterCount= FeaturesString.length() - FeaturesString.replace(SubDelimiter, "").length();
		
		int [] FeatureList=new int [FeatureInfo.length+SubDelimiterCount];
		int Position=0;
		int counter=0;
		
		for(int i=0;i<usedFeatures.length;i++){
			if(values[usedFeatures[i]].contains(SubDelimiter)){
			//if(values[i].contains(SubDelimiter)){
				
				String [] valuesSub=values[usedFeatures[i]].split(SubDelimiter);
				//String [] valuesSub=values[i].split(SubDelimiter);
				int Added=0;
				for(int j=0;j<valuesSub.length;j++){
					
					int temp=Position+FeatureInfo[usedFeatures[i]].GetProcessedFeature(valuesSub[j]);
					if(temp!=Position){
							FeatureList[counter]=temp;
							counter++;
							Added++;
					}
				}
				if(Added==0){
					FeatureList[counter]=Position;
					counter++;
				}
				
			}else{
				int temp=Position+FeatureInfo[usedFeatures[i]].GetProcessedFeature(values[usedFeatures[i]]);
				//int temp=Position+FeatureInfo[usedFeatures[i]].GetProcessedFeature(values[i]);
				//System.out.println(usedFeatures[i]+" val="+values[usedFeatures[i]]+"  temp="+temp+ "  pos=" + Position);

				FeatureList[counter]=temp;

				counter++;
			}
			
			Position+=FeatureInfo[usedFeatures[i]].GetMaxID()+1;

		}
		
		int [] FinalFeatureList=new int [counter];
		for(int i=0;i<counter;i++){
			FinalFeatureList[i]=FeatureList[i];
		}
		
		return FinalFeatureList;
	}
	
	public void setUsedFeatures(int []usedFeatures){
		if(usedFeatures==null){
			this.usedFeatures= new int [FeatureInfo.length];
			for(int i=0;i<FeatureInfo.length;i++){
				this.usedFeatures[i]=i;
			}
		}else{
			this.usedFeatures=usedFeatures;
		}
	}

	public int GetTotalFeatureCount() {
		int TotalFeatureCount=0;
		for(int i=0;i<usedFeatures.length;i++){
			TotalFeatureCount+=ColumnSizes[usedFeatures[i]]+1;
		}
		
		return TotalFeatureCount;
	}
	
	public int PrintTotalFeatureCount(String File) {
		int TotalFeatureCount=0;
		try {
			PrintWriter writer = new PrintWriter(File+"-Main");

			for(int i=0;i<usedFeatures.length;i++){
				writer.write(i+","+TotalFeatureCount + "\n");
				FeatureInfo[usedFeatures[i]].printMain(TotalFeatureCount,File+"-"+i);
				
				TotalFeatureCount+=ColumnSizes[usedFeatures[i]]+1;
				
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return TotalFeatureCount;
	}

	public int[] GetColumns() {
		int [] Columns= new int [usedFeatures.length];
		for(int i=0;i<usedFeatures.length;i++){
			Columns[i]=ColumnSizes[usedFeatures[i]]+1;
		}
		return Columns;
	}
}
