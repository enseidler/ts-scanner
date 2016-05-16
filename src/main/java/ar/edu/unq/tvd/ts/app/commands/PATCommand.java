package ar.edu.unq.tvd.ts.app.commands;

import ar.edu.unq.tvd.ts.TransportStream;

public class PATCommand extends TSScannerCommand {

	public PATCommand(TransportStream transportStream) {
		super(transportStream);
	}

	@Override
	public void execute() throws Exception {
		this.transporStream.getPat().print();
	}

}
