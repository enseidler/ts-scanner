package ar.edu.unq.tvd.ts.app.commands;

import ar.edu.unq.tvd.ts.TransportStream;
import ar.edu.unq.tvd.ts.utils.TSScannerUtils;

public class MapCommand extends TSScannerCommand {

	public MapCommand(TransportStream transportStream) {
		super(transportStream);
	}

	@Override
	public void execute() throws Exception {
		TSScannerUtils.map(this.transporStream.getStream());
	}

}
