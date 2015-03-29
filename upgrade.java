import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;

import javax.imageio.ImageIO;

public class upgrade {

	Rectangle R; // hitbox, for collision detection
	Point location = new Point(); // location(x,y)
	byte xspeed = 0, yspeed = 0; // speed
	byte xdim, ydim; // dimensions
	int type; // what type of upgrade it is
	String sprite; // the sprite for the upgrade
	Image img; // the actual sprite image
	ship8 field; // reference to main field

	public upgrade(int type, ship8 field) {
		// create the upgrade
		this.field = field;
		this.type = type;
		switch (type) {
		case 60:
			sprite = "Sprites/upgrade0.gif";
			ydim = 16;
			xdim = 16;
			break;
		case 61:
			sprite = "Sprites/upgrade1.gif";
			ydim = 24;
			xdim = 24;
			break;
		case 62: // ammo up
			type = 64; // spawn bomb upgrade instead
			break;
		case 63: // one up
			sprite = "Sprites/life.gif";
			xdim = 12;
			ydim = 16;
			break;
		case 64: // bomb up
			sprite = "Sprites/bombUp.gif";
			xdim = 12;
			ydim = 12;
			break;
		case 65: // speed up
			sprite = "Sprites/speedUp.gif";
			ydim = 24;
			xdim = 24;
			break;
		case 66:
			sprite = "Sprites/shieldIcon.gif";
			ydim = 24;
			xdim = 24;
			break;
		case 67:
			sprite = "Sprites/sineIcon.gif";
			ydim = 20;
			xdim = 12;
			break;
		case 68:
			sprite = "Sprites/boomerangIcon.gif";
			ydim = 20;
			xdim = 12;
			break;
		case 69:
			sprite = "Sprites/spreaderIcon.gif";
			ydim = 20;
			xdim = 12;
			break;
		case 70:
			// sprite = "Sprites/MultiDirIcon.gif";
			sprite = "Sprites/spreaderIcon.gif";
			ydim = 20;
			xdim = 12;
		}
		// start offscreen
		location.y = -100;
		location.x = (int) Math.ceil(Math.random() * field.dim.width);
		R = new Rectangle(location.x, location.y, xdim, ydim);
		try {
			// load image
			File file = new File("bin/" + sprite);
			img = ImageIO.read(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void instruct()
	// AI for upgrades. heh
	{
		yspeed = 1;
		location.translate(xspeed, yspeed);
		R.translate(xspeed, yspeed);
	}

	public Graphics draw(Graphics bufferGraphics)
	// draw image
	{
		bufferGraphics.drawImage(img, location.x, location.y, null);
		instruct();
		return bufferGraphics;
	}

	public void collect()
	// if player collects the upgrade, decide what to do.
	{
		switch (type) {
		case 60:
			field.spaceship.laserType = 3;
			break;
		case 61:
			field.spaceship.damage++;
			break;
		case 62:
			field.spaceship.MaxAmmo++;
			break;
		case 63:
			field.lives++;
			break;
		case 64:
			field.bombs++;
			break;
		case 65:
			field.spaceship.speed++;
			break;
		case 66:
			field.spaceship.shield = true;
			break;
		case 67:
			field.spaceship.laserType = 1;
			break;
		case 68:
			field.spaceship.laserType = 2;
			break;
		case 69:
			field.spaceship.laserType = 4;
			break;
		case 70:
			field.spaceship.laserType = 5;
			break;
		}
	}
}
