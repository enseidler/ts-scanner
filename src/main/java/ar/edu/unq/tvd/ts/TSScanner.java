package ar.edu.unq.tvd.ts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class TSScanner {
	
	private static String MAP_COMMAND = "-map";
	private static String PAT_COMMAND = "-pat";
	private static String LEN_COMMAND = "-len";
	private static String EXTRACT_COMMAND = "-extract";
	
	public static void main(String[] args) throws Exception {
		String command;
		String filePath;
		
		
		if(args.length == 2) {
			command = args[0].trim();
			filePath = args[1].trim();
			
			if(command.equals(MAP_COMMAND)) {
				System.out.println("Mapping \"" + filePath + "\"...");
				map(filePath);
			} else if(command.equals(PAT_COMMAND)) {
				pat(filePath);
			} else if(command.equals(LEN_COMMAND)) {
				len(filePath);
			} else if(command.equals(EXTRACT_COMMAND)) {
				extract(filePath);
			} else {
				System.out.println("Please choose a valid action before file path:");
				System.out.println("              -map");
				System.out.println("              -pat");
			}
		} else {
			System.out.println("TSScanner's correct usage:");
			System.out.println("   <command> <file-path>");
		}
	}

	
	private static void extract(String stream) throws Exception {
		// path for extraction = <file-path>/extraction-<name-file>/
		String destinyPath =  "/" + FilenameUtils.getPath(stream) + "extraction-" + FilenameUtils.getBaseName(stream) + "/";
		
		System.out.println("Extracting video/audio from \"" + stream + "\" to \"" + destinyPath + "\"");
		System.out.println("--------------------------------------------------------------------------");
		for(byte[] pmt : pmts(stream)) {
			extractAllES(stream, destinyPath, pmt);
		}
		System.out.println("--------------------------------------------------------------------------");
		System.out.println("Extraction finished on \"" + stream + "\"...");
	}
	
	private static void len(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		int len = 0;
		
		while(readPacket(input, buffer)) {
			len++;
		}
		input.close();
		System.out.println("Packages amount of " + "\"" + stream + "\"" + ": " + len);
	}


	private static void pat(String stream) throws Exception {
		byte[] buffer = findPacket(stream, 0x00);
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
	
	private static byte[] findPacket(String stream, int pid) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		while(readPacket(input, buffer)) {
			if(pid(buffer) == pid) {
				return buffer;
			}
		}
		input.close();
		return null;
	}
	
	private static List<Integer> pids(String stream) throws Exception {
		byte[] buffer = findPacket(stream, 0x00);  // pat packet

		List<Integer> pids = new ArrayList<Integer>();
		
		int n = (sectionLength(buffer) - 9) / 4;
		int index = 13;
		
		for(int i = 0; i < n; i++) {
			int programNumber = (buffer[index] << 8 | buffer[index+1]) & 0x0000FFFF;
			
			if(programNumber != 0x00) {
				int pid = (buffer[index+2] << 8 | buffer[index+3]) & 0x00001FFF;
				pids.add(pid);
			}
			index += 4;
		}
		
		return pids;
	}
	
	private static List<byte[]> pmts(String stream) throws Exception {
		List<byte[]> pmts = new ArrayList<byte[]>();
		
		for(int pid : pids(stream)) {
			pmts.add(findPacket(stream, pid));
		}
		
		return pmts;
	}
	
	private static List<Integer> elementaryPIDS(byte[] pmtPacket) {
		List<Integer> pids = new ArrayList<Integer>();
			
		int index = 17 + programInfoLength(pmtPacket);
		int elementaryInfoLength = 0;
		
		while(notStartCRCOn(index, pmtPacket)) {			
			int bytes = pmtPacket[index+3] << 8 | pmtPacket[index+4];
			int mask = 0x00000FFF;
			elementaryInfoLength = bytes & mask;
			
			int pid = (pmtPacket[index+1] << 8 | pmtPacket[index+2]) & 0x00001FFF;
			pids.add(pid);
			
			index = index + elementaryInfoLength + 5;
		}
		
		return pids;
	}
	
	private static boolean notStartCRCOn(int index, byte[] buffer) {
		int byte1 = buffer[index] << 24 & 0xFF000000;
		int byte2 = buffer[index + 1] << 16 & 0x00FF0000;
		int byte3 = buffer[index + 2] << 8 & 0x0000FF00;
		int byte4 = buffer[index + 3] & 0x000000FF;
		
		int bytes = byte1 | byte2 | byte3 | byte4;
		
		return bytes != crc(buffer);
	}

	private static List<byte[]> elementaryPackets(String stream, byte[] pmtPacket) throws Exception {
		List<byte[]> elemPackets = new ArrayList<byte[]>();
		
		for(int pid : elementaryPIDS(pmtPacket)) {
			elemPackets.add(findPacket(stream, pid));
		}
		
		return elemPackets;
	}
	
	private static int programInfoLength(byte[] buffer) {
		int bytes = buffer[15] << 8 | buffer[16];
		int mask = 0x00000FFF;
		
		return bytes & mask;
	}
	
	private static int sectionLength(byte[] buffer) {
		int bytes = buffer[6] << 8 | buffer[7];
		int mask = 0x00000FFF;
		
		return bytes & mask;
	}

	private static int crc(byte[] buffer) {
		int byte1 = buffer[4 + sectionLength(buffer)] << 24 & 0xFF000000;
		int byte2 = buffer[5 + sectionLength(buffer)] << 16 & 0x00FF0000;
		int byte3 = buffer[6 + sectionLength(buffer)] << 8 & 0x0000FF00;
		int byte4 = buffer[7 + sectionLength(buffer)] & 0x000000FF;
		
		return byte1 | byte2 | byte3 | byte4;
	}
	
	private static void extractAllES(String stream, String destinyPath, byte[] pmtPacket) throws Exception {
		for(byte[] elemPacket : elementaryPackets(stream, pmtPacket)) {
			String finalPath = destinyPath + "elementary-stream-" + Integer.toHexString(pid(elemPacket)) + ".ts";
			
			File file = new File(finalPath);
			file.getParentFile().mkdirs();
			
			if(file.exists()) {
				file.delete();
			}
			
			file.createNewFile();				
			OutputStream output = new FileOutputStream(file);
			
			copyToStream(elemPacket, output, finalPath);
		}
	}
	
	private static void copyToStream(byte[] elemPacket, OutputStream output, String path) throws Exception {
		for(byte b : elemPacket) {
			output.write(b);
		}
		
		System.out.println("  - Elementary Stream " + Integer.toHexString(pid(elemPacket)) + " successfully created on: \"" + path + "\"");
		System.out.println();
		
		output.close();
	}

	private static void map(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		while(readPacket(input, buffer)) {
			mapPacket(buffer);
		}
		System.out.println("");
		input.close();
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
