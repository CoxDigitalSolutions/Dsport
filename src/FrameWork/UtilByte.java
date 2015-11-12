package frameWork;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class UtilByte {
	public static byte[] IntToBytes(int i){
		byte[] result = new byte[4];
		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /*>> 0*/);
	
		return result;
	}
	public static byte[] ShortToBytes(short i){
		byte[] result = new byte[2];
		result[0] = (byte) (i >> 8);
		result[1] = (byte) (i /*>> 0*/);
	
		return result;
	}
	
	public static byte[] ByteToBytes(short i){
		byte[] result = new byte[1];
		result[0] = (byte) (i);
	
		return result;
	}
	
	public static int CheckUpdateBuffer(FileChannel inChannel,ByteBuffer buffer){
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
	
	
}
