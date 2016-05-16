package ar.edu.unq.tvd.ts.app.commands;

import ar.edu.unq.tvd.ts.TransportStream;

public abstract class TSScannerCommand {
	
	protected TransportStream transporStream;
	
	
	public TSScannerCommand(TransportStream transportStream) {
		this.transporStream = transportStream;
	}


	public TransportStream getTransporStream() {
		return transporStream;
	}
	
	public abstract void execute() throws Exception ;

}
