package ar.edu.unq.tvd.ts.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ar.edu.unq.tvd.ts.utils.TSScannerUtils;

public class PMT extends Packet {

	Integer programNumber;
	Map<Integer, Integer> elementaryPIDs = new HashMap<Integer, Integer>();
	
	
	public PMT(String stream, Integer pid, byte[] data) {
		super(stream, pid, data);
		this.typePacket = "PMT";
		this.parseElementaryPIDs();
	}
	
	public PMT(String stream, Integer pid) throws Exception {
		super(stream, pid);
		this.typePacket = "PMT";
		this.programNumber = TSScannerUtils.programNumber(this);
		this.parseElementaryPIDs();
	}
	
	
	public Map<Integer, Integer> getElementaryPIDs() {
		return this.elementaryPIDs;
	}
	
	///////////////////////////////////
	
	public void print() {
		System.out.println("|--------------------------|");
		System.out.printf("|     PROGRAM PID %4X     |\n", this.pid);
		System.out.println("|--------------------------|");
		System.out.println("|    Elementary Streams    |");
		System.out.println("|-------------|------------|");
		System.out.println("|     Type    |    PID     |");
		System.out.println("|-------------|------------|");
		
		if(this.elementaryPIDs.isEmpty()) {
			System.out.println("There is no elementary streams..");
		}
		
		for(Entry<Integer, Integer> row : this.elementaryPIDs.entrySet()) {
			System.out.printf("|%9X      |%4X   |\n", row.getValue(), row.getKey());
		}
		
		System.out.println("|-------------|------------|");
	}
	
	private void parseElementaryPIDs() {				
		Integer index = this.esStart();
		Integer elementaryInfoLength = 0;
		
		while(index < this.crcStart()) {			
			Integer bytes = TSScannerUtils.byteFormat(this.data[index+3]) << 8 | TSScannerUtils.byteFormat(this.data[index+4]);
			Integer mask = 0x00000FFF;
			elementaryInfoLength = bytes & mask;
			
			Integer type = TSScannerUtils.byteFormat(this.data[index]);
			Integer pid = (TSScannerUtils.byteFormat(this.data[index+1]) << 8 | TSScannerUtils.byteFormat(this.data[index+2])) & 0x00001FFF;
		
			this.elementaryPIDs.put(pid, type);
			
			index = index + elementaryInfoLength + 5;
		}
	}
		
	private Integer programInfoLength() {
		Integer byte1 = TSScannerUtils.byteFormat(this.data[15]) << 8;
		Integer byte2 = TSScannerUtils.byteFormat(this.data[16]);
		Integer bytes = byte1 | byte2;
		Integer mask = 0x00000FFF;
		
		return bytes & mask;
	}

	private Integer esStart() {
		return 17 + this.programInfoLength();
	}

	private int crcStart() {
		return 4 + this.sectionLength;
	}
	
	public void extractVideo(String destinyPath) throws Exception {
		String finalPath = destinyPath + "/video/service-pid-0x" + Integer.toHexString(this.pid).toUpperCase();
		
		System.out.print("      - Service PID 0x" + Integer.toHexString(this.pid).toUpperCase() + " extracting video... ");
		
		for(PES pes : this.getAllVideoPES()) {
			pes.copyOn(finalPath);
		}
		
		System.out.println("DONE");
	}
	
	public void extractAudio(String destinyPath) throws Exception {
		String finalPath = destinyPath + "/audio/service-pid-0x" + Integer.toHexString(this.pid).toUpperCase();
		System.out.print("      - Service PID 0x" + Integer.toHexString(this.pid).toUpperCase() + " extracting audio... ");
		
		for(PES pes : this.getAllAudioPES()) {
			pes.copyOn(finalPath);
		}
		
		System.out.println("DONE");
	}
	
	private List<PES> getAllVideoPES() throws Exception {
		List<PES> videoPackets = new ArrayList<PES>();
		
		for(Integer pid : this.videoPIDs()) {
			videoPackets.addAll(TSScannerUtils.getAllPESPackets(this.stream, pid));
		}
		
		return videoPackets;
	}
	
	private List<PES> getAllAudioPES() throws Exception {
		List<PES> audioPackets = new ArrayList<PES>();
		
		for(Integer pid : this.audioPIDs()) {
			audioPackets.addAll(TSScannerUtils.getAllPESPackets(this.stream, pid));
		}
		
		return audioPackets;
	}
	
	private List<Integer> videoPIDs() {
		List<Integer> pids = new ArrayList<Integer>();
		
		for(Entry<Integer, Integer> e : this.elementaryPIDs.entrySet()) {
			if(this.isVideoType(e.getValue())) {
				pids.add(e.getKey());
			}
		}
		
		return pids;
	}
	
	private List<Integer> audioPIDs() {
		List<Integer> pids = new ArrayList<Integer>();
		
		for(Entry<Integer, Integer> e : this.elementaryPIDs.entrySet()) {
			if(this.isAudioType(e.getValue())) {
				pids.add(e.getKey());
			}
		}
		
		return pids;
	}
	
	private boolean isVideoType(Integer type) {
		return type == 0x1B;
	}
	
	private boolean isAudioType(Integer type) {
		return type == 0x11;
	}
	
}
