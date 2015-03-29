//imports
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;

public class MediaUtil {
	ship8 field;

	public MediaUtil(ship8 field) {
		this.field = field;
	}

	public BufferedImage[] splitImage(Image image, int cols, int rows)
	// split image into buffered image array
	{
		BufferedImage img = new BufferedImage(image.getWidth(null), image
				.getHeight(null), 5);
		Graphics g = img.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		int w = (img.getWidth() / cols);
		int h = img.getHeight() / rows;
		BufferedImage imgs[] = new BufferedImage[w * h];
		int num = 0;
		for (int y = 0; y < rows; y++) {
			for (int x = 0, Offset = 0, Offset2 = 0; x < cols; x++) {
				if (y == 3 && (x == 6 || x == 7)) {
					if (x == 7) {
						Offset -= 1;
						Offset2 -= 4;
					}
					w += 4;
					Offset += 4;
				} else {
					w = (img.getWidth() / cols);
					Offset = 0;
				}
				imgs[num] = new BufferedImage(w, h, img.getType());
				imgs[num] = img.getSubimage(x * (w - Offset) + Offset2, y * h,
						w, h);
				num++;
			}
		}
		return imgs;
	}

	public void MediaLoad()
	// load all the images.
	{
		try {
			// load all the images from file, and scale them
			field.ship = ImageIO.read(new File(field.spaceship.sprite))
					.getScaledInstance((int) (field.spaceship.xdim),
							(int) (field.spaceship.ydim), Image.SCALE_DEFAULT);
			field.shield = ImageIO.read(new File("bin/Sprites/shield.gif"))
					.getScaledInstance((int) (56 * field.scaleX),
							(int) (56 * field.scaleY), Image.SCALE_DEFAULT);
			field.life = ImageIO.read(new File("bin/Sprites/life.gif"))
					.getScaledInstance((int) (12 * field.scaleX),
							(int) (16 * field.scaleY), Image.SCALE_DEFAULT);
			field.lifeLoss = ImageIO.read(new File("bin/Sprites/lifeLoss.gif"))
					.getScaledInstance((int) (12 * field.scaleX),
							(int) (16 * field.scaleY), Image.SCALE_DEFAULT);
			field.bomb = ImageIO.read(new File("bin/Sprites/bombIcon.gif"))
					.getScaledInstance((int) (16 * field.scaleX),
							(int) (16 * field.scaleY), Image.SCALE_DEFAULT);
			field.explosion = ImageIO.read(new File("bin/Sprites/explosion.gif"))
					.getScaledInstance((int) (84 * field.scaleX),
							(int) (68 * field.scaleY), Image.SCALE_DEFAULT);
			field.tinyExplosion = ImageIO.read(
					new File("bin/Sprites/tiny_explosion.gif")).getScaledInstance(
					(int) (12 * field.scaleX), (int) (12 * field.scaleY),
					Image.SCALE_DEFAULT);
			// probably could have done this faster if i put them all in an
			// array, and used 'for each'
			field.font0 = ImageIO.read(new File("bin/Sprites/font0.gif"));
			field.font1 = ImageIO.read(new File("bin/Sprites/font1.gif"));
			field.font2 = ImageIO.read(new File("bin/Sprites/font2.gif"));
			field.Title = ImageIO.read(new File("bin/Sprites/titleImg.gif"));
			field.Title = field.Title.getScaledInstance(
					(int) (120 * field.scaleX), (int) (40 * field.scaleY),
					Image.SCALE_DEFAULT);
			field.alien = ImageIO.read(new File("bin/Sprites/alien.gif"));
			field.gameOver = ImageIO.read(new File("bin/Sprites/gameOver.gif"));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// load all of the images with the media tracker.
		// so then they all load before they are needed.
		field.mt = new MediaTracker(field);
		field.mt.addImage(field.ship, 0);
		field.mt.addImage(field.font0, 1);
		field.mt.addImage(field.font1, 2);
		field.mt.addImage(field.font2, 3);
		field.mt.addImage(field.bomb, 4);
		field.mt.addImage(field.life, 5);
		field.mt.addImage(field.lifeLoss, 6);
		field.mt.addImage(field.explosion, 7);
		field.mt.addImage(field.explosion, 8);
		field.mt.addImage(field.Title, 10);
		field.mt.addImage(field.alien, 11);
		field.mt.addImage(field.shield, 12);
		field.mt.addImage(field.gameOver, 12);
		try {
			field.mt.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
