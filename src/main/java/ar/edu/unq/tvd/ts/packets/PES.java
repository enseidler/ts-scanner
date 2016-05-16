package ar.edu.unq.tvd.ts.packets;

public class PES extends Packet {
	
	public PES(String stream, Integer pid, byte[] data) {
		super(stream, pid, data);
		this.typePacket = "ES";
	}
	
	public PES(String stream, Integer pid) throws Exception {
		super(stream, pid);
		this.typePacket = "ES";
	}

	

}
