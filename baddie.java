import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;

class baddie {

	int xdim = 36, ydim = 24, type; // demnsions, badguy type
	byte xspeed = 0, yspeed = 0; //movement speed
	int formation; // what type of formation they are in
	byte shipNum; // ship num in formation (?)
	short random;
	short delay; 
	Point location; // position
	Rectangle R; // hitbox
	String sprite; // sprite file name
	int score = 100; // score given to player for killing
	int hp = 5; // hp
	ship8 field;
	Image img; // image of baddie. 
	boolean FORMATION = false;

	public baddie(int x, int y, int type, ship8 field) {
		location = new Point(x, y);
		this.type = type;
		this.field = field;
		define();
	}

	public baddie(int type, int formation, int x, int y, ship8 field) {
		this.type = type;
		this.field = field;
		location = new Point(x, y);
		FORMATION = true;
		define();
	}

	public void define() {
		switch (type) {
		case 0: // asteroid
			random = (short) Math.ceil(Math.random() * 3);
			switch (random) {
			case 1: // big asteroid
				sprite = "bin/Sprites/asteroid1.gif";
				xdim = 40;
				ydim = 32;
				score = 350;
				hp = 100;
				break;
			case 2: // small asteroid
				sprite = "bin/Sprites/asteroid2.gif";
				xdim = 16;
				ydim = 24;
				score = 20;
				hp = 50;
				break;
			case 3:
				sprite = "bin/Sprites/asteroid3.gif";
				xdim = 76;
				ydim = 56;
				score = 500;
				hp = 120;
				break;
			}
			yspeed = 1;
			break;
		case 1: // shooter
			sprite = "bin/Sprites/bad1.gif";// FIX TO PROPER SPRITE:shooter
			xdim = 36;
			ydim = 24;
			score = 200;
			hp = 100;
			yspeed = 1;
			break;
		case 2: // seeker
			sprite = "bin/Sprites/seeker.gif";
			xdim = 28;
			ydim = 28;
			hp = 50;
			break;
		case 3: // rocket
			xdim = 40;
			ydim = 20;
			location.y = (int) (Math.random() * field.dim.height);
			yspeed = (byte) (Math.random() * 5 + Math.random() * -5);
			switch ((int) Math.round(Math.random() * 1)) {
			case 0:
				location.x = -50 - (int) (Math.random() * 50);
				xspeed = (byte) (3 + Math.random() * 2);
				sprite = "bin/Sprites/rocket.gif";
				break;
			case 1:
				location.x = field.dim.width + 50 + (int) (Math.random() * 50);
				xspeed = (byte) -(3 + Math.random() * 2);
				sprite = "bin/Sprites/rocket2.gif";
			}
			break;
		case 4: // mine
			sprite = "bin/Sprites/mine.gif";
			xdim = 52;
			ydim = 52;
			break;
		case 5: // bullet
			xdim = 4;
			ydim = 4;
			break;
		}
		xdim *= field.scaleX;
		ydim *= field.scaleY;
		R = new Rectangle(location.x, location.y, xdim, ydim);
		// img = field.getImage(field.getDocumentBase(), sprite);
		if (sprite != null) {
			try {
				File file = new File(sprite);
				img = ImageIO.read(file).getScaledInstance(xdim, ydim,
						Image.SCALE_DEFAULT);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void instruct()
	// instruct the AI
	{
		if (!FORMATION) {
			switch (type) {
			// maybe would have been better if i just put the different method
			// contents in here.
			// considering i would not call those methods anywhere else, and it
			// would already be
			// similarly organized.
			case 0:
				meteor();
				break;
			case 1:
				shooter();
				break;
			case 2:
				seeker();
				break;
			case 5:
				bullet();
				break;
			}
			R.translate(xspeed, yspeed);
			location.translate(xspeed, yspeed);
		}
	}

	public void instruct(byte xmove, byte ymove) { // instruct if in formation
		this.xspeed = xmove;
		this.yspeed = ymove;
		R.translate(xspeed, yspeed);
		location.translate(xspeed, yspeed);
	}

	public void meteor() // 0
	{
	}

	public void shooter() // 1
	//shooter AI
	{
		if (location.y > 80) {
			yspeed = 0;
			if (field.spaceship.location.x - location.x >= 5) {
				xspeed = 1;
			} else if (field.spaceship.location.x - location.x <= -5) {
				xspeed = -1;
			} else if (delay >= 20) {
				xspeed = 0;
				if (!field.spaceship.dead)
					fire();
				delay = 0;
			}
		} else {
			yspeed = 1;
		}
		delay++;
	}

	public void seeker() // 2
	{
		//move towards player
		yspeed = 0;
		xspeed = 0;
		double xdist = field.spaceship.location.x - location.x;
		double ydist = field.spaceship.location.y - location.y;
		double distance = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2));
		xspeed = (byte) (xdist / distance * 2);
		yspeed = (byte) (ydist / distance * 2);
	}

	public void rocket()// 3
	{
	}

	public void mine() // 4
	{
	}

	public void bullet() // 5
	{
		yspeed = 4;
	}

	public void fire()
	// if the baddie fires a shot
	{
		field.spawn(location.x + xdim / 2 - 2, location.y + ydim, 5);
	}

	public Graphics draw(Graphics bufferGraphics)
	// draw the baddie
	{
		if (type == 5) {
			bufferGraphics.setColor(Color.red);
			bufferGraphics.fillRect(location.x, location.y, xdim, ydim);
		} else {
			bufferGraphics.drawImage(img, location.x, location.y, null);
		}
		return bufferGraphics;
	}
}
