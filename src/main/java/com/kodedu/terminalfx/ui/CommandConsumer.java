package com.kodedu.terminalfx.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Procesa flujos de entrada en segundo plano.
 * 
 * @author fvarrui
 */
public class CommandConsumer extends Thread {
	
	private static final Pattern CONTROL_SEQUENCE_PATTERN = Pattern.compile("\033\\[\\??\\d+(;\\d+)*[A-Za-z]");

	private static final char EOT = (char) 4;	// End Of Transmission character
	private static final char ESC = (char) 27;	// Escape
	private static final char NEWLINE = '\n';	// New line

	private String buffer = "";

	private volatile boolean stop;
	private InputStreamReader reader;
	private Consumer<String> consumer;
	private boolean disableOutput = false;

	public CommandConsumer(InputStream input, Consumer<String> consumer) {
		super("CommandConsumer");
		this.reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		this.consumer = consumer;
		setDaemon(false);
	}
	
	private char next() throws IOException {
		return (char) reader.read();
	}
	
	private String add(char ch) {
		return buffer += ch;
	}
	
	private void reset() {
		buffer = "";
	}

	@Override
	public void run() {
		try {
			reset();
			stop = false;
			while (!stop) {
				char currentChar = next();
				switch (currentChar) {
				case EOT:
					stop = true;
					break;
				case NEWLINE:
					consumer.accept(buffer);
					reset();
					break;
				default:
					add(currentChar);
					if (currentChar == ESC) {
						disableOutput = true;
					} 
					if (!disableOutput) {
//						System.out.print(currentChar);
					}					
					if (StringUtils.isAlpha("" + currentChar)) {
						disableOutput = false;
					}
//					buffer = buffer.replaceAll("\033\\[\\d+m", "<SGR>");
//					buffer = buffer.replaceAll("\033\\[\\d+;\\d+m", "<SGR>");
//					buffer = buffer.replaceAll("\033\\[\\d+;\\d+;\\d+m", "<SGR>");
//					buffer = buffer.replaceAll("\033\\[\\d*K", "<EL>");
//					buffer = buffer.replaceAll("\033\\[\\?25[hl]", "<DECTCEM>");
//					buffer = buffer.replaceAll("\033\\[\\d*G", "<CHA>");
					hasControlSequence();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void hasControlSequence() {
		Matcher m = CONTROL_SEQUENCE_PATTERN.matcher(buffer);
		if (m.find()) {
			buffer = buffer.replace(m.group(0), "");
			System.out.println("---> CONTROL SEQUENCE: " + m.group(0));
		}
	}
	
	private void controlSequence() {
		
	}

	public void requestStop() {
		this.stop = true;
	}

}
