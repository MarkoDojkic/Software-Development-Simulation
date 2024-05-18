package dev.markodojkic.softwaredevelopmentsimulation.transformer;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import lombok.Setter;
import org.springframework.boot.info.OsInfo;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.diogonunes.jcolor.Ansi.colorize;


@Setter
@MessageEndpoint(value = "PrinterTransformer")
public class PrinterTransformer {
	private String infoTextColorANSICode;
	private String errorTextColorANSICode;

	private static final String SPLITTER = String.format("%n***%s%n", "-".repeat(80));
	private static final String SPLITTER_LINE = String.format("%n %s%n", "─".repeat(138));

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

	@Transformer
	public String jiraActivityStreamOutput(String output){
		return colorize(String.format(" %s|%60s%-78s| %s%s %s", SPLITTER_LINE.stripLeading(), " ", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), SPLITTER_LINE, Arrays.stream(output.replace("$", SPLITTER_LINE).split("\\R")).map(value -> SPLITTER_LINE.contains(value) ? value.concat("\n") : ("| " + value + " ".repeat(SPLITTER_LINE.length() - value.replaceAll("\u001B\\[\\d*m", "").length() - (System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("windows") ? 6 : 4)) + "|\n")).collect(Collectors.joining()).stripTrailing(),SPLITTER_LINE).stripTrailing());
	}
}