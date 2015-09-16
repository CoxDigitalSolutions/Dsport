package frameWork;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileOperations {
	public static void mergeFiles(String Folder, String Destination,String Header) throws Exception {
		 File dir = new File(Folder);
		  File[] directoryListing = dir.listFiles();
		  
		  
		FileWriter fstream = null;
		BufferedWriter out = null;

		fstream = new FileWriter(Destination, false);
		out = new BufferedWriter(fstream);


		out.write(Header);
		for (File f : directoryListing) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;

			fis = new FileInputStream(f);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
			String aLine;
			while ((aLine = in.readLine()) != null) {
				if(aLine.length()>0){
					out.newLine();
					out.write(aLine);
				}
			}
 
			in.close();

		}
 

		out.close();

 
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
