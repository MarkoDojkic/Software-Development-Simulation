package dev.markodojkic.softwaredevelopmentsimulation.transformer;

import com.diogonunes.jcolor.Attribute;
import lombok.Setter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;

import static com.diogonunes.jcolor.Ansi.colorize;


@Setter
@MessageEndpoint(value = "PrinterTransformer")
public class PrinterTransformer {
	private String infoTextColorANSICode;
	private String errorTextColorANSICode;

	private static final String SPLITTER = String.format("%n***%s%n", "-".repeat(20));

	@Transformer
	public String infoOutput(String output){
		return colorize("/*\t- INFORMATION -\n\s\s* " +
				output.replace("$", SPLITTER).replace("\n", "\n\s\s* ").replace("* ***", "\r") +
				"\n\t- INFORMATION - */", Attribute.TEXT_COLOR(Integer.parseInt(infoTextColorANSICode)));
	}

	@Transformer
	public String errorOutput(String output){
		return colorize("/*\t- !ERROR! -\n\s\s!-- " +
				output.replace("$", SPLITTER).replace("\n", "\n\s\s!-- ").replace("!-- ***", "\r") +
				"\n\t - !ERROR! - */", Attribute.TEXT_COLOR(Integer.parseInt(errorTextColorANSICode)));
	}
}
