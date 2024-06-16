package dev.markodojkic.softwaredevelopmentsimulation.transformer;

import com.diogonunes.jcolor.Attribute;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.util.Strings;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;


import static com.diogonunes.jcolor.Ansi.colorize;

@MessageEndpoint
public class PrinterTransformer {
	private int infoTextColorANSICode;
	private int errorTextColorANSICode;

	private static final String SPLITTER = String.format("%n***%s%n", "-".repeat(80));
	private static final String SPLITTER_LINE = String.format("%n %s%n", "─".repeat(145));

	@PostConstruct
	public void init(){
		infoTextColorANSICode = 68;
		errorTextColorANSICode = 196;
	}

	@Transformer
	public String infoOutput(String output){
		return colorize("/*\t- INFORMATION -".concat(System.lineSeparator()).concat("\s\s* ").concat(output.replace("$", SPLITTER)).replace(System.lineSeparator(), System.lineSeparator().concat("\s\s* ")).replace("* ***", "\r").concat(System.lineSeparator()).concat("\t- INFORMATION - */"), Attribute.TEXT_COLOR(infoTextColorANSICode));
	}

	@Transformer
	public String errorOutput(String output){
		return colorize("/*\t- !ERROR! -".concat(System.lineSeparator()).concat("\s\s!-- ").concat(output.replace("$", SPLITTER)).replace(System.lineSeparator(), System.lineSeparator().concat("\s\s!-- ")).replace("!-- ***", "\r").concat(System.lineSeparator()).concat("\t - !ERROR! - */"), Attribute.TEXT_COLOR(errorTextColorANSICode));
	}

	@Transformer
	public String jiraActivityStreamOutput(String output){
		int osOffset = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("windows") ? 6 : 4;

		return colorize(String.format(" %s|%64s%-81s| %s%s %s", SPLITTER_LINE.stripLeading(), " ", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), SPLITTER_LINE, Arrays.stream(output.replace("$", SPLITTER_LINE).split("\\R")).map(value -> SPLITTER_LINE.contains(value) ? value.concat(System.lineSeparator()) : ("| ".concat(value).concat(" ").repeat(Math.abs(SPLITTER_LINE.length() - value.replaceAll("\u001B\\[\\d*m", Strings.EMPTY).length() - osOffset)).concat("|".concat(System.lineSeparator())))).collect(Collectors.joining()).stripTrailing(), SPLITTER_LINE).stripTrailing());
	}
}