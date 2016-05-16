package ar.edu.unq.tvd.ts.app.commands;

import ar.edu.unq.tvd.ts.TransportStream;
import ar.edu.unq.tvd.ts.utils.TSScannerUtils;

public class LenCommand extends TSScannerCommand {

	public LenCommand(TransportStream transportStream) {
		super(transportStream);
	}

	@Override
	public void execute() throws Exception {
		Integer len = TSScannerUtils.len(this.transporStream.getStream());
		System.out.println("Packages amount of " + "\"" + this.transporStream.getStream() + "\"" + ": " + len);
	}

}
