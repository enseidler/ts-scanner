package ar.edu.unq.tvd.ts.app;

import ar.edu.unq.tvd.ts.TransportStream;
import ar.edu.unq.tvd.ts.app.commands.ExtractAudioCommand;
import ar.edu.unq.tvd.ts.app.commands.ExtractVideoCommand;
import ar.edu.unq.tvd.ts.app.commands.LenCommand;
import ar.edu.unq.tvd.ts.app.commands.MapCommand;
import ar.edu.unq.tvd.ts.app.commands.PATCommand;

public class TSScanner {

	private static String MAP_COMMAND = "-map";
	private static String PAT_COMMAND = "-pat";
	private static String LEN_COMMAND = "-len";
	private static String EXTRACT_VIDEO_COMMAND = "-extract:video";
	private static String EXTRACT_AUDIO_COMMAND = "-extract:audio";
	
	public static void main(String[] args) throws Exception {
		String command;
		String filePath;
		if(args.length == 2) {
			command = args[0].trim();
			filePath = args[1].trim();
			
			TransportStream ts = new TransportStream(filePath);
			
			if(command.equals(MAP_COMMAND)) {
				System.out.println("Mapping \"" + filePath + "\"...");
				new MapCommand(ts).execute();
			} else if(command.equals(PAT_COMMAND)) {
				new PATCommand(ts).execute();
			} else if(command.equals(LEN_COMMAND)) {
				new LenCommand(ts).execute();
			} else if(command.equals(EXTRACT_VIDEO_COMMAND)) {
				new ExtractVideoCommand(ts).execute();
			} else if(command.equals(EXTRACT_AUDIO_COMMAND)) {
				new ExtractAudioCommand(ts).execute();
			} else {
				System.out.println("Please choose a valid action before file path:");
				System.out.println("    -map");
				System.out.println("    -pat");
				System.out.println("    -len");
				System.out.println("    -extract:video");
				System.out.println("    -extract:audio");
			}
		} else {
			System.out.println("TSScanner's correct usage:");
			System.out.println("    <command> <file-path>");
		}
	}

}
