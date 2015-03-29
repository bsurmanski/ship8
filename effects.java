import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class effects {

	int[][] stars = new int[2][20]; // stars array. [0][*] = x, [1][*] = y
	boolean init = true; // init for stars
	private ship8 field; // reference of main field
	BufferedImage[][] Font = new BufferedImage[3][]; // font sprite array
	byte deathTimer = 0, TPTimer = 50;
	String passcode = ""; // hold password input
	String cheats[] = { "CATS", "RASPUTIN", "SHOOPIT", "REDDIT", "DERP",
			"FFFFUUUUUU", "FTLDRIVE", "POP", "SHIELD", "OVERDRIVE", "SLOBRO" };
	// cheat inputs
	// cheat boolean flags
	boolean reddit = false, BOOM = false, gameOver = false;
	boolean pause = false, menu = true, HSmenu = false, options = false,
			passMenu = false, credits = false;
	byte drawSpeed = 15; // used to change speed. slomode etc

	public effects(ship8 field) {
		this.field = field;
		// set up font images.
		Font[0] = field.IU.splitImage(field.font0, 10, 4);
		Font[1] = field.IU.splitImage(field.font1, 10, 4);
		Font[2] = field.IU.splitImage(field.font2, 10, 4);
	}

	public Graphics stars(Graphics bufferGraphics)
	// method for production of scrolling star background
	{
		bufferGraphics.setColor(Color.white);
		for (int i = 0; i < stars[0].length; i++) {
			if (stars[1][i] > field.dim.height || init) // star exits scene,
			// reset star
			{
				int randnum = (int) Math.ceil(Math.random() * field.dim.width);
				// random x value
				randnum -= randnum % 4;
				stars[0][i] = randnum;
				randnum = (int) Math.ceil(Math.random() * field.dim.height);
				// random y value
				randnum -= randnum % 4;
				stars[1][i] = -randnum;
				if (init) {
					stars[1][i] = randnum;
				}
			}
			stars[1][i]++; // move the stars down
			bufferGraphics.fillRect(stars[0][i], stars[1][i], 4, 4);
			// paint stars
		}
		init = false;
		return bufferGraphics;
	}

	public Graphics score(Graphics bufferGraphics)
	// Keep track of, and draw score and lives
	{
		String s = Integer.toString(field.score);
		// convert score to string, then draw
		for (int i = 0; i < s.length() || i < field.lives || i < field.bombs; i++) {
			if (i < s.length()) {
				int a = s.charAt(i) - 48;
				bufferGraphics.drawImage(Font[0][a], i * 20 + 8, 40, null);
			}
			// draw remaining lives
			if (i < field.lives) {
				bufferGraphics.drawImage(field.life,
						(int) (field.dim.width - (i * 20 + 24) * field.scaleX),
						40, null);
			}
			// draw player's remaining bombs
			if (i < field.bombs) {
				bufferGraphics.drawImage(field.bomb,
						(int) (field.dim.width - (i * 20 + 28) * field.scaleX),
						60, null);
			}
		}
		// just for drawing the image that comes up when you lose a life
		if (deathTimer > 0) {
			// drawn up at life icons
			bufferGraphics.drawImage(field.lifeLoss,
					(int) (field.dim.width - (field.lives * 20 + 24)
							* field.scaleX), 20, null);
			deathTimer--;
		}
		// draw pause menu
		if (pause) {
			String[] pm = { "PAUSED", "CONTINUE", "EXIT" };
			Write(bufferGraphics, pm[0], field.dim.width / 2,
					field.dim.height * (1) / 4, 2);
			// PAUSED text
			// write each of the selections
			for (int i = 1; i < pm.length; i++) {
				Write(bufferGraphics, pm[i], field.dim.width / 2,
						field.dim.height * (i + 1) / 4,
						(field.selection + i) % 2);
			} // CONTINUE/EXIT on pause menu
		}
		return bufferGraphics;
	}

	public void menuSelection()
	// figure out what to select when someone clicks enter on a menu
	{
		// if enter is clicked do the following
		if (pause) {
			// unpause
			pause = false;
			// or exit to main menu
			if (field.selection % 2 == 1) {
				field.reset();
			}
		} else if (HSmenu) {
			// exit highscore menu
			HSmenu = false;
		} else if (passMenu) {
			// exit password menu, and test if the input is valid
			passMenu = false;
			passcodeTest(); // ACCEPT PASS
		} else if (credits) {
			// exit credits screen
			credits = false;
		} else if (options) {
			// find which selection to goto
			switch (field.selection % 3) {
			case 0: // passcode
				passMenu = true;
				break;
			case 1: // credits
				credits = true;
				break;
			case 2: // exit
				options = false;
				break;
			}
		} else if (menu) {
			// find which menu selection to choose
			switch ((field.selection % 4)) {
			case 0: // to game
				menu = false;
				break;
			case 1: // to highscores
				HSmenu = true;
				break;
			case 2: // to options
				options = true;
				break;
			case 3: // exit game
				field.exit();
			}
		} else {
			pause = true;
		}
		field.selection = 0; // reset selection cursor
	}

	public Graphics menu(Graphics bufferGraphics) // draw the main menu
	{
		String[] pm = { "SHIP", "START", "HIGHSCORES", "OPTIONS", "QUIT" };
		// draw title image
		bufferGraphics.drawImage(field.Title, field.dim.width / 2
				- field.Title.getWidth(null) / 2, field.dim.height * 3 / 16,
				null);
		// draw all the menu selections
		for (int i = 1, color; i < pm.length; i++) {
			// highlight the current selection
			if (i - 1 == field.selection % 4) {
				color = 1;
			} else {
				color = 0;
			}
			Write(bufferGraphics, pm[i], field.dim.width / 2, field.dim.height
					* (i + 1) / 6, color);
			// START/HIGHSCORE selections
		}
		bufferGraphics.setColor(Color.white);
		return bufferGraphics;
	}

	public Graphics gameOver(Graphics bufferGraphics) {
		// draw game over image
		bufferGraphics.drawImage(field.gameOver, field.dim.width / 2 - 42,
				field.dim.height / 2, null);
		return bufferGraphics;
	}

	public Graphics highscore(Graphics bufferGraphics)
	// draw the highscores. for highscore screen.
	{
		String[] pm = { "HIGHSCORES" };
		// draw Highscores title
		for (int i = 0; i < pm.length; i++) {
			Write(bufferGraphics, pm[i], field.dim.width / 2, field.dim.height
					* (i + 1) / 4, 2);
		}
		// draw the Highscores
		for (int i = 0; i < field.highscore.length; i++) {
			String s = Integer.toString(field.highscore[i]);
			Write(bufferGraphics, s, field.dim.width / 2, 30 * i
					+ field.dim.height * 3 / 8, 0);
		}
		// something something easteregg
		if (reddit) {
			bufferGraphics.drawImage(field.alien, field.dim.width - 50,
					field.dim.height - 50, null); // top secret.
		}
		return bufferGraphics;
	}

	public Graphics options(Graphics bufferGraphics)
	// draw option menu
	{
		String[] pm = { "OPTIONS", "PASSCODE", "CREDITS", "BACK" };

		for (int i = 0, color; i < pm.length; i++) {
			if (i - 1 == field.selection % 3) {
				color = 1;
			} else {
				color = 0;
			}
			if (i == 0) {
				Write(bufferGraphics, pm[i], field.dim.width / 2,
						field.dim.height * (i + 1) * 3 / 16, 2);
			} else {
				Write(bufferGraphics, pm[i], field.dim.width / 2,
						field.dim.height * (i + 1) * 3 / 16, color);
			}
		} // draw Options Title
		return bufferGraphics;
	}

	public Graphics credits(Graphics bufferGraphics)
	// draw credits menu
	{
		// Horray! My name
		String[] pm = { "MADE BY", "BRANDON SURMANSKI", "2010" };
		for (int i = 0; i < pm.length; i++) {
			Write(bufferGraphics, pm[i], field.dim.width / 2, field.dim.height
					* (i + 1) / 4, 1);
		}
		return bufferGraphics;
	}

	public Graphics passcode(Graphics bufferGraphics) {
		String[] pm = { "PASSCODE", passcode };
		for (int i = 0, color; i < pm.length; i++) {
			if (i == 0) {
				color = 2;
			} else {
				color = 1;
			}
			// write out the password and password menu title
			Write(bufferGraphics, pm[i], field.dim.width / 2, field.dim.height
					* (i + 1) / 4, color);
		}
		return bufferGraphics;
	}

	public void passcodeTest()
	// test if the inputed text matches a cheat.
	{
		for (int i = 0; i < cheats.length; i++) {
			if (passcode.equals(cheats[i])) {
				switch (i) {
				case 0: // cats
					field.lives = 9;
					break;
				case 1: // rasputin
					field.lives = 20;
					break;
				case 2: // shoopit
					field.spaceship.laserType = 3;
					field.spaceship.damage = 3;
					break;
				case 3: // reddit
					reddit = true;
					break;
				case 4: // derp
					field.lives = 0;
					field.bombs = 0;
					break;
				case 5: // ffffuuuuuu
					field.spaceship.damage = 127;
					field.bombs = 127;
					break;
				case 6: // FTLDRIVE
					field.spaceship.speed = 7;
					break;
				case 7: // POP
					BOOM = true;
					break;
				case 8: // SHIELD
					field.spaceship.shield = true;
					break;
				case 9: // OVERDRIVE
					drawSpeed = 10;
					break;
				case 10: // SLOBRO
					drawSpeed = 25;
					// slooooooowwwww motioooooonnn
					break;
				}
				break;
			}
		}
		passcode = ""; // reset code input
	}

	public Graphics Write(Graphics bufferGraphics, String S, int x, int y,
			int color)
	// write the text, using my custom text images.
	{ // 0 blue, 1 green, 2 white
		for (int i = 0, WM = 0, a; i < S.length(); i++) {
			if (S.charAt(i) == 77) {
				a = S.charAt(i) - 41;// for fixing M
			} else if (S.charAt(i) == 87) {
				a = S.charAt(i) - 50; // for fixing W
			} else if (S.charAt(i) >= 65) {
				a = S.charAt(i) - 55;
			} else {
				a = S.charAt(i) - 48;
			}
			// draw all of the letters in the word
			if (a >= 0)
				bufferGraphics.drawImage(Font[color][a], i * 20 + WM + x
						- (20 * S.length() / 2), y, null);
			// below to fix W/M. They are double width characters.
			if (S.charAt(i) == 77) {
				WM += 8;
			} else if (S.charAt(i) == 87) {
				WM += 8;
			}
		} // centered x draw, offset y
		return bufferGraphics;
	}
}
