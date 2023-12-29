package dev.isnow.puppy.helper;


import net.minecraft.client.Minecraft;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;


public class SaveLoad {

	public String configFileName;
	public final File dataFile;

	public Object item;
	   
	public SaveLoad(String configFileName, Object object) {
		this.item = object;
		this.configFileName = configFileName;

		File dir = new File(Minecraft.getMinecraft().mcDataDir, "Puppy");
		File configDir = new File(dir, "SaveableItems");

		dataFile = new File(configDir, configFileName);
		if(!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {e.printStackTrace();}

		}
	}

	public SaveLoad(String configFileName) {
		this.configFileName = configFileName;

		File dir = new File(Minecraft.getMinecraft().mcDataDir, "Puppy");
		File configDir = new File(dir, "SaveableItems");

		dataFile = new File(configDir, configFileName);
		if(!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {e.printStackTrace();}

		}

		loadImage();
	}

	public void load() {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader buffer = new BufferedReader(
				new FileReader(dataFile))) {

			String str;

			while ((str = buffer.readLine()) != null) {
				builder.append(str).append("\n");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		item = builder.toString();
	}

	public void save() {
		if(item instanceof BufferedImage) {
			try {
				ImageIO.write((BufferedImage) item, "png", dataFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if(item instanceof ConcurrentHashMap) {
			ConcurrentHashMap<String, String> addresses = (ConcurrentHashMap<String, String>) item;
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(dataFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			PrintWriter printWriter = new PrintWriter(fileWriter);

			for(String playerName : addresses.keySet()) {
				String ip = addresses.get(playerName);
				printWriter.print(playerName + ",," + ip + "\n");
			}

			printWriter.close();
		}
		if(item instanceof String) {
			FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(dataFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			PrintWriter printWriter = new PrintWriter(fileWriter);

			printWriter.print((String) item);
			printWriter.close();
		}
	}
	
	public void loadImage() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(dataFile);
		} catch (IOException e) {
		}
		this.item = img;
	}
}