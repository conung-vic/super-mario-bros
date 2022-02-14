package com.conungvic.gi.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.conungvic.gi.MarioBros;

import static com.conungvic.gi.MarioBros.V_HEIGHT;
import static com.conungvic.gi.MarioBros.V_WIDTH;

public class DesktopLauncher {

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = V_HEIGHT;
		config.width = V_WIDTH;
		config.title = "Mario Bros Clon";
		config.vSyncEnabled = false;
		config.foregroundFPS = 0;
		config.backgroundFPS = 0;
		config.resizable = false;
//		config.fullscreen = true;

		new LwjglApplication(new MarioBros(), config);
	}
}
