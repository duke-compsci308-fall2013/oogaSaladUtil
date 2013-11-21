package imagetool;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * @author Dylan Jackson and Bernard Park
 * Class that provides methods for resizing images to specified dimensions. Can be used to obtain a scaled version of
 * a BufferedImage, scale and overwrite an image at a specified path, or scale and overwrite all image files at a specified folder path.
 * The libraries used support JPEG, PNG, GIF, BMP and WBMP. 
 */
public class ImageTool {
	
	//look up BufferedImage, Graphics2D, ImageIO
	
	public static BufferedImage scaleImage(String originalImagePath, int desiredWidth, int desiredHeight) {
		File originalImageFile = new File(originalImagePath);
		BufferedImage originalImage = null;
		try {
			originalImage = ImageIO.read(originalImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int type = originalImage.getType();
		BufferedImage scaledImage = new BufferedImage(desiredWidth, desiredHeight, type);
		Graphics2D graphics = scaledImage.createGraphics();
		graphics.drawImage(originalImage, 0, 0, desiredWidth, desiredHeight, null);
		graphics.dispose();
		return scaledImage;
	}
	
	public static void scaleAndOverwriteImage(String originalImagePath, int desiredWidth, int desiredHeight) {
		BufferedImage scaledImage = scaleImage(originalImagePath, desiredWidth, desiredHeight);
		try {
			ImageIO.write(scaledImage, filePathExtension(originalImagePath), new File(originalImagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void scaleAndOverwriteAllImages(String folderPath, int desiredWidth, int desiredHeight) {
		File imageFolder = new File(folderPath);
		File[] imageList = imageFolder.listFiles();
		for (File image : imageList) {
			String imagePath = image.getPath();
			scaleAndOverwriteImage(imagePath, desiredWidth, desiredHeight);
		}
	}
	
	private static String filePathExtension(String filePath) {
		return filePath.substring(filePath.lastIndexOf('.') + 1);
	}
	
}
