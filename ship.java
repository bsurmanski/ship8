import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import javax.imageio.ImageIO;

class ship {

	private static final long serialVersionUID = 1L;
	int hp = 100; // health
	// dimensions
	short xdim;
	short ydim;
	Point location; // location
	int xspeed = 0;
	int x2speed = 0;
	int yspeed = 0;
	Rectangle R; // hitbox
	Image img; // ship image
	int y2speed = 0;
	byte speed = 2;
	String sprite = "bin/Sprites/ship8.gif"; // sprite file
	byte laserType = 0; // type of gun currently using
	byte damage = 1;
	byte MaxAmmo = 35;
	boolean dead = false, respawn = false, shield = false;
	ship8 field;

	public ship(ship8 field) {
		xdim = (short) (40 * field.scaleX);
		ydim = (short) (40 * field.scaleY);
		try {// load image
			File file = new File("bin/Sprites/ship8.gif");
			img = ImageIO.read(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.field = field;
		location = new Point(field.dim.width / 2 - xdim / 2,
				(int) (field.dim.height - (ydim + 20) * field.scaleY));
		R = new Rectangle(location.x, location.y, xdim, ydim);
	}

	public Graphics moveShip(Graphics bufferGraphics)
	// for moving the ship
	// (and poly bound) and painting it to buffer
	// also draws image
	{
		if (field.LEFT && location.x > 0) {
			xspeed = -speed;
		} else {
			xspeed = 0;
		}
		if (field.RIGHT && location.x + xdim < field.dim.width) {
			x2speed = speed;
		} else {
			x2speed = 0;
		}
		if (field.UP && location.y > 0) {
			yspeed = -speed;
		} else {
			yspeed = 0;
		}
		if (field.DOWN && location.y + ydim < field.dim.height) {
			y2speed = speed;
		} else {
			y2speed = 0;
		}
		// translate location, and hitBox according to proper speed
		location.x += xspeed + x2speed;
		location.y += yspeed + y2speed;
		R.translate(xspeed + x2speed, yspeed + y2speed);
		if (!dead) {
			bufferGraphics.drawImage(field.ship, location.x, location.y, null);
			// draw ship if not dead
		}
		if (shield) {
			// draw shield, if has shield
			bufferGraphics.drawImage(field.shield, location.x
					- (int) (8 * field.scaleX),
					(int) (location.y - 8 * field.scaleY), null);
		}
		return bufferGraphics;
	}

	public void respawn()
	// called when player is dead, then clicks space.
	{
		int movex = field.dim.width / 2 - location.x;
		int movey = (field.dim.height - 50) - location.y;
		location.x = field.dim.width / 2;
		location.y = field.dim.height - 50;
		R.translate(movex, movey);
		dead = false;
	}
}
