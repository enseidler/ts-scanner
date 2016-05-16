package ar.edu.unq.tvd.ts.packets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import ar.edu.unq.tvd.ts.utils.TSScannerUtils;

abstract class Packet {

	protected String stream;
	protected Integer pid;
	protected String typePacket = "";
	protected byte[] data = new byte[188];
	protected Integer sectionLength;
	
	
	public Packet(String stream, Integer pid, byte[] data) {
		this.stream = stream;
		this.pid = pid;
		this.data = data;
		this.sectionLength = TSScannerUtils.sectionLength(this.data);
	}
	
	public Packet(String stream, Integer pid) throws Exception {
		this(stream, pid, TSScannerUtils.getPacket(stream, pid));
	}
	

	public String getStream() {
		return this.stream;
	}

	public Integer getPid() {
		return this.pid;
	}
	
	public String getTypePacket() {
		return typePacket;
	}

	public byte[] getData() {
		return this.data;
	}
	
	public Integer getSectionLength() {
		return this.sectionLength;
	}
	
	/////////////////////
	
	public boolean equals(Packet packet) {
		return this.typePacket == packet.getTypePacket() &&
				this.pid == packet.getPid() &&
				Arrays.equals(this.data, packet.getData());
	}

	public void printData() {
		this.printData(16);
	}
	
	public void printData(Integer lineLength) {
		Integer bytesPerLine = 0;
		
		for(Integer i = 0; i < this.data.length; i++){
			if(bytesPerLine == lineLength) {
				System.out.println();
				bytesPerLine = 0;
			}
			System.out.printf("%4X", this.data[i]);
			
			bytesPerLine++;
		}
	}
	
	public void copyOn(String destinyPath) throws IOException {
		String newFilePath = destinyPath + "/" + this.typePacket + "_PID_0x" + Integer.toHexString(this.pid).toUpperCase() + "_" + "tipo" + ".ts";
		
		File file = new File(newFilePath);
		
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		if(!file.exists()) {
			file.createNewFile();
		}
		
		OutputStream output = new FileOutputStream(file, true);
		
		for(int value : this.data) {
			output.write(value);
		}
		
		output.close();
	}

}
