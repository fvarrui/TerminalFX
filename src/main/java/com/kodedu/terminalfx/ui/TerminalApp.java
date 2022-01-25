package com.kodedu.terminalfx.ui;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TerminalApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		TerminalConfig terminalConfig = new TerminalConfig();
		terminalConfig.setWindowsTerminalStarter("powershell");
		terminalConfig.setBackgroundColor(Color.DARKBLUE);
		terminalConfig.setForegroundColor(Color.WHITE);
		terminalConfig.setCursorColor(Color.WHITE);
		terminalConfig.setCursorBlink(true);
		
		Terminal terminal = new Terminal(terminalConfig);
		terminal.onTerminalFxReady(() -> {
			System.out.println("Terminal ready!");
			CommandConsumer consumer = new CommandConsumer(terminal.getInputStream(), s -> {
					System.out.print(s);
			});
			consumer.start();
		});
		
		Scene scene = new Scene(terminal, 800, 600);
		
		primaryStage.setTitle("PoorShell");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
