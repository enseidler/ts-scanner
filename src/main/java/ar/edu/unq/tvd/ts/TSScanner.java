package ar.edu.unq.tvd.ts;

import java.io.FileInputStream;
import java.io.InputStream;

public class TSScanner {
	
	private static String MAP_COMMAND = "map";
	private static String PAT_COMMAND = "pat";
	private static String LEN_COMMAND = "len";
	
	public static void main(String[] args) throws Exception {
		String command;
		String filePath;
		
		if(args.length == 2) {
			command = args[0].trim();
			filePath = args[1].trim();
			
			if(command.equals(MAP_COMMAND)) {
				System.out.println("Mapping " + filePath + " ...");
				map(filePath);
			} else if(command.equals(PAT_COMMAND)) {
				pat(filePath);
			} else if(command.equals(LEN_COMMAND)) {
				len(filePath);
			} else {
				System.out.println("Please choose a valid action before file path:");
				System.out.println("              map");
				System.out.println("              pat");
			}
		} else {
			System.out.println("TSScanner's correct usage:");
			System.out.println("   <command> <file-path>");
		}
	}

	
	private static void len(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		int len = 0;
		
		while(readPacket(input, buffer)) {
			len++;
		}
			
		System.out.println("Packages amount of " + "\"" + stream + "\"" + ": " + len);
	}


	private static void pat(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		while(readPacket(input, buffer)) {
			if(isPATPacket(buffer)) {
				break;
			}
		}
		
		showPAT(buffer);
	}
	

	
	private static void showPAT(byte[] buffer) throws Exception {
		int n = (sectionLength(buffer) - 9) / 4;

		int index = 13;
		
		System.out.println("--------------------------------");
		System.out.println("-------------- PAT -------------");
		System.out.println("--------------------------------");		
		for(int i = 0; i < n; i++) {
			int programNumber = (buffer[index] << 8 | buffer[index+1]) & 0x0000FFFF;
			int pid = (buffer[index+2] << 8 | buffer[index+3]) & 0x00001FFF;
			
			System.out.printf("    Program Number: 0x%4X\n" , programNumber);
			System.out.printf("             - PID: 0x%4X\n" , pid);
			System.out.println("--------------------------------");
			
			index += 4;
		}
	}

	private static int sectionLength(byte[] buffer) {
		int bytes = buffer[6] << 8 | buffer[7];
		int mask = 0x00000FFF;
		
		return bytes & mask;
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
		System.out.println("");
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
