import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Image;

public class Ammo {

	private static final long serialVersionUID = 1L;
	byte FPScount;
	Point location; // location of ammo
	int startY; // starting Y-location
	Dimension dim;// dimension of ammo
	Rectangle R; // hitbox for collision detection
	byte xspeed = 0, yspeed = 0; // speed x/y
	int type; // type of ammo
	int damage;// damage it does. Basedamage + ammo special damage
	byte damageBase; // base damage done.
	Image img; // image for ammo
	int timer = -1; // timer for bombs
	Color colour; // what color the ammo is
	ship8 field; // main field reference

	public Ammo(int x, int y, int type, ship8 field) {
		this.type = type;
		this.field = field;
		damageBase = field.spaceship.damage;
		define(x, y);
	}

	public Ammo(Image img, int x, int y, int type, ship8 field) {
		this.field = field;
		this.type = type;
		this.img = img;
		define(x, y);
	}

	public void define(int x, int y) {
		// define the characteristics of the ammo
		switch (type) {
		case 30: // laser
			dim = new Dimension(4, 8);
			damage = damageBase * 20;
			break;
		case 31: // sine laser
			dim = new Dimension(4, 4);
			damage = damageBase * 15;
			startY = y;
			break;
		case 32: // boomerang
			dim = new Dimension(8, 8);
			yspeed = -3;
			damage = damageBase * 15;
			FPScount = 0;
			break;
		case 33: // constant beam
			dim = new Dimension(16, 32);
			y += 16;
			damage = damageBase * 5;
			break;
		case 34: // spreader
			dim = new Dimension(4, 4);
			damage = damageBase * 25;
			xspeed = (byte) (Math.random() * 6 - Math.random() * 6);
			break;
		case 35: // Multi Dir shot
			dim = new Dimension(4, 4);
			damage = damageBase * 25;
			xspeed = (byte) (Math.random() * 6 - Math.random() * 6);
			yspeed = (byte) (Math.random() * 6 - Math.random() * 6);
			if (xspeed + yspeed == 0) {
				yspeed = 3;
			}
			break;
		case 37: // tiny explosion
			dim = new Dimension(12, 12);
			timer = 20;
			damage = 10;
			break;
		case 38: // bomb
			dim = new Dimension(16, 16);
			damage = 50;
			break;
		case 39: // explosion from bomb
			dim = new Dimension((int) (84 * field.scaleX),
					(int) (68 * field.scaleY));
			timer = 30;
			damage = 50;
			break;
		}
		location = new Point(x, y);
		R = new Rectangle(location.x, location.y, dim.width, dim.height);
		location.translate(-dim.width / 2, -dim.height / 2);
		R.translate(-dim.width / 2, -dim.height / 2);
	}

	public void instruct() {
		// AI for the ammo. They are smart
		switch (type) {
		case 30:
			yspeed = -4;
			break;
		case 31: // sin beam
			yspeed = -2;
			xspeed = (byte) Math.round(Math
					.sin((startY - location.y) / 15 + 90) * 4);
			break;
		case 32:
			FPScount++;
			if (FPScount >= 40) {
				yspeed += 1;
				FPScount = 0;
			}
			break;
		case 33:
			yspeed = -2;
			break;
		case 34:
			yspeed = -3;
			break;
		case 35:
			break;

		case 38:
			yspeed = -3;
			break;
		case 39:
		case 37:
			timer--;
			break;
		}
		switch (damageBase) {
		// color of ammo depends on how the players base power
		case 0:
			colour = Color.white;
			break;
		case 1:
			colour = Color.yellow;
			break;
		case 2:
			colour = Color.green;
			break;
		case 3:
			colour = Color.cyan;
			break;
		case 4:
			colour = Color.blue;
			break;
		case 5:
			colour = Color.magenta;
			break;
		case 6:
			colour = Color.red;
			break;
		default:
			colour = Color.red;
			break;
		}
		location.translate(xspeed, yspeed);
		R.translate(xspeed, yspeed);
	}

	public Graphics draw(Graphics bufferGraphics) {
		// draw the ammo
		instruct(); // instruct each time it redraws
		switch (type) {
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
			bufferGraphics.setColor(colour);
			bufferGraphics.fillRect(location.x, location.y, dim.width,
					dim.height);
		}
		if (img != null) { // draw image for special ammo
			bufferGraphics.drawImage(img, location.x, location.y, null);
		}
		return bufferGraphics;
	}
}
