package ar.edu.unq.tvd.ts;

import java.util.ArrayList;
import java.util.List;

import ar.edu.unq.tvd.ts.packets.PAT;
import ar.edu.unq.tvd.ts.packets.PMT;

public class TransportStream {

	private String stream;
	private PAT pat;
	private List<PMT> pmts = new ArrayList<PMT>();
	
	
	public TransportStream(String stream) throws Exception {
		this.stream = stream;
		this.pat = new PAT(this.stream);
		this.parsePMTs();
	}
	

	public String getStream() {
		return this.stream;
	}

	public PAT getPat() {
		return this.pat;
	}

	public List<PMT> getPmts() {
		return this.pmts;
	}
	
	///////////////////////////////////////////
	
	private void parsePMTs() throws Exception {
		for(Integer pid : this.pat.pids()) {
			if(pid != 0x10) {
				this.pmts.add(new PMT(this.stream, pid));
			}
		}
	}
	
	public void extractVideo(String destinyPath) throws Exception {
		System.out.println("Extracting video streams from \"" + this.stream + "\"");
		System.out.println("--------------------------------------------------------------------------");
		
		for(PMT pmt : this.pmts) {
			pmt.extractVideo(destinyPath);
		}
		
		System.out.println("--------------------------------------------------------------------------");
		System.out.println("Extraction finished on \"" + destinyPath + "\"...");
	}
	
	public void extractAudio(String destinyPath) throws Exception {
		System.out.println("Extracting audio streams from \"" + this.stream + "\"");
		System.out.println("--------------------------------------------------------------------------");
		
		for(PMT pmt : this.pmts) {
			pmt.extractAudio(destinyPath);
		}
		
		System.out.println("--------------------------------------------------------------------------");
		System.out.println("Extraction finished on \"" + destinyPath + "\"...");
	}
	
}
