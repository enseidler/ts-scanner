package ar.edu.unq.tvd.ts.app.commands;

import org.apache.commons.io.FilenameUtils;
import ar.edu.unq.tvd.ts.TransportStream;

public class ExtractVideoCommand extends TSScannerCommand {

	public ExtractVideoCommand(TransportStream transportStream) {
		super(transportStream);
	}

	@Override
	public void execute() throws Exception {
		String destinyPath =  "/" + FilenameUtils.getPath(this.transporStream.getStream()) + "extraction-" + FilenameUtils.getBaseName(this.transporStream.getStream());
		this.transporStream.extractVideo(destinyPath);
	}

}