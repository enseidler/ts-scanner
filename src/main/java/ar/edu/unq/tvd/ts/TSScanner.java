package ar.edu.unq.tvd.ts;

import java.io.FileInputStream;
import java.io.InputStream;

public class TSScanner {

	public static void main(String[] args) throws Exception {
		map(args[0]);
	}

	private static void map(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		while(readPacket(input, buffer)) {
			mapPacket(buffer);
		}
		
	}
	
	private static void mapPacket(byte[] buffer) throws Exception {
		if(isPATPacket(buffer)) {
			mapPATPacket();
		} else if(isNullPacket(buffer)) {
			mapNullPacket();
		} else {
			mapSyncPacket();
		}
	}

	private static boolean isPATPacket(byte[] buffer) throws Exception {
		return pid(buffer) == 0x00;
	}
	
	private static boolean isNullPacket(byte[] buffer) throws Exception {
		return pid(buffer) == 0x1FFF;
	}
		
	private static int pid(byte[] buffer) throws Exception {
		int bytes = buffer[1] << 8 | buffer[2];
		int mask = 0x1FFF;
		
		return bytes & mask;
	}

	private static void mapPATPacket() {
		System.out.print("* ");
	}
	
	private static void mapNullPacket() {
		System.out.print("| ");
	}
	
	private static void mapSyncPacket() {
		System.out.print(". ");
	}
	
	////////////////////////////////////////////////
	
	private static InputStream getInputStream(String stream) throws Exception {
		InputStream input = new FileInputStream(stream);
		return input;
	}

	private static boolean readPacket(InputStream input, byte[] buffer) throws Exception {
		int bytesReaded = 0;
		
		while(bytesReaded < buffer.length) {
			int lastReaded = input.read(buffer);
			if(lastReaded == -1) {
				return false;
			}
			bytesReaded += lastReaded;
		}
		return true;
	}

}
