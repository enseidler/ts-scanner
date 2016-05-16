package ar.edu.unq.tvd.ts.packets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PAT extends Packet {

	private Map<Integer, Integer> table = new HashMap<Integer, Integer>();
	
	
	public PAT(String stream, byte[] data) {
		super(stream, 0x00, data);
		this.typePacket = "PAT";
		this.parsePAT();
	}
	
	public PAT(String stream) throws Exception {
		super(stream, 0x00);
		this.typePacket = "PAT";
		this.parsePAT();
	}
	
	
	public Map<Integer, Integer> getTable() {
		return this.table;
	}
	
	//////////////////////////	
	
	public void print() {
		System.out.println("|--------------------------|");
		System.out.println("|         PAT TABLE        |");
		System.out.println("|----------------|---------|");
		System.out.println("| Program Number |   PID   |");
		System.out.println("|----------------|---------|");
		
		if(this.table.isEmpty()) {
			System.out.println("There is no PMT tables..");
		}
		
		for(Entry<Integer, Integer> row : this.table.entrySet()) {
			System.out.printf("|%10X      |%6X   |\n", row.getKey(),row.getValue());
		}
		
		System.out.println("|----------------|---------|");
	}
	
	private void parsePAT() {		
		int index = 13;
		
		for(int i = 0; i < this.programsAmount(); i++) {
			int programNumber = (this.data[index] << 8 | this.data[index+1]) & 0x0000FFFF;			
			int pid = (this.data[index+2] << 8 | this.data[index+3]) & 0x00001FFF;
			this.table.put(programNumber, pid);
			
			index += 4;
		}
		
	}
	
	private int programsAmount() {
		return (this.sectionLength - 9) / 4;
	}
	
	public Collection<Integer> pids() {
		return this.table.values();
	}
	
	public Collection<Integer> programNumbers() {
		return this.table.keySet();
	}

}
