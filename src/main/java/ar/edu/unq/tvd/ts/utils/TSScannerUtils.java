package ar.edu.unq.tvd.ts.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unq.tvd.ts.packets.PAT;
import ar.edu.unq.tvd.ts.packets.PES;
import ar.edu.unq.tvd.ts.packets.PMT;

public class TSScannerUtils {
	
	public static byte[] getPacket(String stream, Integer pid) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		while(readPacket(input, buffer)) {
			if(pid(buffer).intValue() == pid.intValue()) {
				return buffer;
			}
		}
		input.close();
		
		return null;
	}
	
	public static List<PES> getAllPESPackets(String stream, Integer pid) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];

		List<PES> pesPackets = new ArrayList<PES>();
		
		while(readPacket(input, buffer)) {
			if(pid(buffer).intValue() == pid.intValue()) {
				pesPackets.add(new PES(stream, pid, buffer));
			}
		}
		input.close();

		return pesPackets;
	}
	
	public static List<Integer> getPids(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		List<Integer> pids = new ArrayList<Integer>();
		
		while(readPacket(input, buffer)) {
			pids.add(pid(buffer));
		}
		input.close();
		
		return pids;
	}
	
	public static Integer pid(byte[] buffer) {
		Integer byte1 = byteFormat(buffer[1]) << 8;
		Integer byte2 = byteFormat(buffer[2]);
		Integer bytes = byte1 | byte2;
		Integer mask = 0x1FFF;
		
		return bytes & mask;
	}
	
	public static Integer sectionLength(byte[] buffer) {
		Integer byte1 = byteFormat(buffer[6]) << 8;
		Integer byte2 = byteFormat(buffer[7]);
		Integer bytes = byte1 | byte2;
		Integer mask = 0x00000FFF;
		
		return bytes & mask;
	}
	
	public static Integer programNumber(PMT pmt) throws Exception {
		PAT pat = new PAT(pmt.getStream());
		
		return pat.getTable().get(pmt.getPid());
	}

	public static Integer len(String stream) throws Exception {
		InputStream input = getInputStream(stream);
		byte[] buffer = new byte[188];
		
		int len = 0;
			
		while(readPacket(input, buffer)) {
			len++;
		}
		input.close();
		
		return len;
	}
	
	public static void map(String stream) throws Exception {
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

	private static void mapPATPacket() {
		System.out.print("* ");
	}
	
	private static void mapNullPacket() {
		System.out.print("| ");
	}
	
	private static void mapSyncPacket() {
		System.out.print(". ");
	}
	
	public static Integer byteFormat(int b) {
		return b & 0x000000FF;
	}

	///////////////////////////////////////////////////////////////////////////
	
	public static InputStream getInputStream(String stream) throws Exception {
		InputStream input = new FileInputStream(stream);
		return input;
	}
	
	private static boolean readPacket(InputStream input, byte[] buffer) throws Exception {
		Integer bytesReaded = 0;
		
		while(bytesReaded < buffer.length) {
			Integer lastReaded = input.read(buffer);
			if(lastReaded == -1) {
				return false;
			}
			bytesReaded += lastReaded;
		}
		return true;
	}
	
}
