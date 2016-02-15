package diveengine2d;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFrame;

public class Engine extends Canvas {
	public static String gameFolder = null;
	public static int WIDTH, HEIGHT;
	public static String startScene = null;
	public static String name = null;
	public static BufferStrategy bs;

	public Engine(String gameFolder) {

		// setup the folder
		this.gameFolder = gameFolder;

		System.out.println("Engine started with folder " + gameFolder + " ...");

		boolean configFile = false;
		try {

			// get the config values from the config file
			loadConfig();
			configFile = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		// if we failed, screw this.
		if (!configFile)
			return;

		// now, lets make our window.

		System.out.println("start scene: " + startScene);
		System.out.println("resolution:  " + WIDTH + " X " + HEIGHT);

		SceneManager.loadScene(startScene);

		SceneManager.entityDump();

		JFrame frame = new JFrame(name);
		frame.add(this);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createBufferStrategy(2);
		bs = getBufferStrategy();
		
		while(true) {
			repaint();
			try{
				Thread.sleep(17);
			}catch(Exception e) {
				
			}
		}

	}

	private void loadConfig() throws Exception {

		File configFile = null;
		try {
			configFile = new File(gameFolder + "\\build.config");
		} catch (Exception e) {
			throw new Exception("Configuration File not found");
		}

		List<String> lines = Files.readAllLines(Paths.get(gameFolder, "build.config"));

		for (String line : lines) {

			if (line.startsWith("#"))
				continue;

			String[] parts = line.split("=");

			if (parts.length != 2) {
				System.out.println("line has incorrect parts length: '" + line + "'");
				System.out.println("ignoring...");
				continue;
			}

			parts[0] = parts[0].trim();
			parts[1] = parts[1].trim();

			if (parts[0].equals("StartScene")) {
				this.startScene = parts[1];
			} else if (parts[0].equals("Resolution")) {

				String[] resparts = parts[1].split("x");

				if (resparts.length != 2) {
					System.out.println("line has incorrect parts length: '" + resparts + "'");
					System.out.println("ignoring...");
					continue;
				}

				resparts[0] = resparts[0].trim();
				resparts[1] = resparts[1].trim();

				try {
					WIDTH = Integer.parseInt(resparts[0]);
					HEIGHT = Integer.parseInt(resparts[1]);
				} catch (NumberFormatException e) {
					System.out.println("line has incorrect resolution: '" + parts[1] + "'");
					System.out.println("ignoring...");
					continue;
				}
			} else if (parts[0].equals("name")) {
				name = parts[1].trim();
			}

		}

		System.out.println("Loaded Config File...");

	}

	public void update(Graphics g) {
		Graphics2D g2 = null;
		try {
			g2 = (Graphics2D) bs.getDrawGraphics();
			render(g2);
		} finally {
			g2.dispose();
		}
		bs.show();
	}

	private void render(Graphics2D g) {
		SceneManager.render(g);
		g.setColor(Color.BLACK);
	}
}
