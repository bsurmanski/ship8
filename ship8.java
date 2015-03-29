
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ship8 extends JFrame implements KeyListener, Runnable
{
    private static final long serialVersionUID = 1L;
    Dimension dim; // dimension of the window
    float scaleX, scaleY; // x and y scale. For fullscreen.
    static Dimension winSize;
    // dimensions of the window. Static, so can use in main method
    Image ship, shield; // image for ship and shield
    int maxBadGuys = 100; // size of baddie array
    baddie[] enemy = new baddie [maxBadGuys]; // #badguys
    Ammo[] ammo = new Ammo [40]; // ammo array. for player gunfire.
    upgrade[] items = new upgrade [5]; // for items, and upgrades
    formation[] Form = new formation [5]; // bad guy formation
    int score; // player's current score
    byte lives; // player's current life count
    byte bombs; // player's current bomb count
    int upgradeCountdown = 0; // countdown til next upgrade
    int selection = 0; // menu selection number
    int NumBaddieTypes = 3; //
    int[] highscore = new int [5]; // stores highscores.
    Image life, lifeLoss; // images for life
    Image bomb, explosion, tinyExplosion; // images for explosions.
    Image Title, gameOver; // images for title, and gameover
    Image alien; // top secret
    hitTest hit = new hitTest (); // for collision testing
    ship spaceship; // spaceship class.
    MediaUtil IU; // for loading, and splitting images (splitting for font)
    effects Effects; // for drawing effects.
    boolean LEFT = false, RIGHT = false, UP = false, DOWN = false; // ship
    // movement
    // flags
    boolean shoot = false; // ship shooting flag
    boolean empty = true;
    // if this is true, then there are no bad guys on screen
    // More badguys will be spawned if none are on screen
    static boolean fullScreen; // is fullscreen?
    int beamCounter;
    Thread th; // thread. controls game flow
    Image font0, font1, font2; // font
    MediaTracker mt; // controls media loading
    Container contentPane;
    AudioClip sineBeam, hitClip;
    static String EntryPassword = "p4ssw0rd";

    public void init ()
    {
	dim = winSize;
	scaleX = dim.width / 640f; // find xscale
	scaleY = dim.height / 480f; // find yscale
	spaceship = new ship (this); // make new ship
	IU = new MediaUtil (this); // make new media untility
	IU.MediaLoad (); // load media
	Effects = new effects (this); // make new effects handler
	th = new Thread (this); // create new thread
	th.start (); // start the thread
	addKeyListener (this); // add keyListener. for key controls

	// offscreen = createImage(dim.width, dim.height);
	// bufferGraphics = this.getGraphics();
	setBackground (Color.black);
	// set background as black, and foreground as white
	setForeground (Color.white);
	reset (); // set up all necesities for the game
    }


    public static void main (String[] args)
    {
	// ask for a password, and only allow them to play if it is correct
	/*if (!((JOptionPane.showInputDialog ("Please Input a Password") + "")
		    .equals (EntryPassword)))
	{
	    // Show message dialogue, then exit program
	    JOptionPane.showMessageDialog (null, "Sorry, Incorrect Password");
	    System.exit (0);
	}*/
	// ask player wheather they want fullscreen, or windowed mode.
	switch (JOptionPane.showConfirmDialog (null,
		    "Would you like to play fullscreen?"))
	{
	    case 0:
		winSize = Toolkit.getDefaultToolkit ().getScreenSize ();
		fullScreen = true;
		break;
	    case 1:
		winSize = new Dimension (640, 480);
		fullScreen = false;
		break;
	    case 2:
		System.exit (0);
	}
	// make new window
	ship8 window = new ship8 ();
	// make the size the designated window dimension
	window.setSize (winSize);
	if (fullScreen)
	    window.setUndecorated (true);
	window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	// closes when told to close
	window.setTitle ("Ship8 - An 8-Bit Spaceshooter");
	window.setVisible (true); // make the window visible
	window.createBufferStrategy (2);

    }


    public ship8 ()
    {
	contentPane = getContentPane (); // get the current contentPane
	init (); // initialize
    }


    public void paint (Graphics g)
    {
	BufferStrategy bf = this.getBufferStrategy (); // for double buffer
	Graphics bufferGraphics = null; // reset the graphics
	if (bf != null)
	{
	    try
	    {
		bufferGraphics = bf.getDrawGraphics ();
		// creates graphical context for drawing buffer
		bufferGraphics.setColor (Color.black);
		bufferGraphics.clearRect (0, 0, dim.width, dim.height);
		bufferGraphics.fillRect (0, 0, dim.width, dim.height);
		// clear the screen, and restart with a black canvas
		if (!Effects.menu)
		{ // if not in menu
		    if (!Effects.pause)
		    { // and not paused
			kill (); // see if anyone should die
			Effects.stars (bufferGraphics); // draw stars
			drawAmmo (bufferGraphics); // draw lasers
			BadGuy (bufferGraphics); // draw Badguys
			spaceship.moveShip (bufferGraphics); // move the ship
			Effects.score (bufferGraphics); // draw Score
		    }
		    if (Effects.pause)
		    { // if paused
			Effects.score (bufferGraphics); // draw Score
		    }
		    // Draw each of the relevent menus
		}
		else if (Effects.HSmenu)
		{ // if in highscore menu
		    Effects.highscore (bufferGraphics);
		}
		else if (Effects.passMenu)
		{ // if in password menu
		    Effects.passcode (bufferGraphics);
		}
		else if (Effects.credits)
		{ // if credits menu
		    Effects.credits (bufferGraphics);
		}
		else if (Effects.options)
		{ // if option menu base
		    Effects.options (bufferGraphics);
		}
		else if (Effects.menu)
		{
		    Effects.stars (bufferGraphics); // draw stars
		    Effects.menu (bufferGraphics);
		}
		if (Effects.gameOver)
		{ // if game over
		    Effects.gameOver (bufferGraphics);
		}
	    }
	    finally
	    {
		// It is best to dispose() a Graphics object when done with it.
		if (bufferGraphics != null)
		    bufferGraphics.dispose ();
	    }
	    bf.show (); // show graphics object
	    Toolkit.getDefaultToolkit ().sync ();
	    // sync graphics state
	}
	// g.drawImage(offscreen, 0, 0, this); // draw offscreen buffer to
	// screen
    }


    public void update (Graphics g)  // update the graphics
    {
	paint (g);
    }


    public void reset ()
    {
	// set up the right menu
	Effects.menu = true;
	Effects.gameOver = false;
	// sort the highscores.
	for (int i = 0 ; i < highscore.length ; i++)
	{
	    if (Math.max (score, highscore [i]) == score)
	    {
		// find the placement for the score
		int scoreHold1 = 0, scoreHold2 = score;
		for (int j = i ; j < highscore.length ; j++)
		{
		    scoreHold1 = highscore [j];
		    highscore [j] = scoreHold2;
		    scoreHold2 = scoreHold1;
		} // shuffle the highscores downward
		highscore [i] = score;
		break;
	    } // Essentially a custom sort method.
	}
	// set up proper player state
	score = 0;
	lives = 3;
	bombs = 3;
	selection = 0;
	upgradeCountdown = 4000;
	spaceship = new ship (this);
	// reset arrays
	for (int i = 0 ; i < ammo.length ; i++)
	{
	    ammo [i] = null;
	}
	for (int i = 0 ; i < enemy.length ; i++)
	{
	    enemy [i] = null;
	}
    }


    public void exit ()
    {
	// exit and close app
	System.exit (0);
    }


    public void keyPressed (KeyEvent e)
    {
	int key = e.getKeyCode (); // find which key was pressed
	if (!Effects.menu && !Effects.pause)
	{
	    switch (key)
	    {
		case KeyEvent.VK_LEFT:
		    LEFT = true; // move left
		    break;
		case KeyEvent.VK_RIGHT:
		    RIGHT = true; // move right
		    break;
		case KeyEvent.VK_UP:
		    UP = true; // move up
		    break;
		case KeyEvent.VK_DOWN:
		    DOWN = true; // move down
		    break;
	    }
	}
    }


    public void keyReleased (KeyEvent e)
    {
	int key = e.getKeyCode (); // find which key was released
	// if entering in password menu, add input to variable
	if (Effects.passMenu && key >= KeyEvent.VK_A && key <= KeyEvent.VK_Z
		&& Effects.passcode.length () < 10)
	{
	    Effects.passcode += (char) key;
	}
	// clear input if backspace is pressed
	if (Effects.passMenu && key == KeyEvent.VK_BACK_SPACE)
	{
	    Effects.passcode = "";
	}
	// for ingame
	switch (key)
	{
	    case KeyEvent.VK_LEFT:
		LEFT = false; // stop moving left
		break;
	    case KeyEvent.VK_RIGHT:
		RIGHT = false; // stop moving right
		break;
	    case KeyEvent.VK_UP:
		UP = false; // stop moving up, navigate menu
		if (Effects.pause || Effects.menu)
		{
		    selection--;
		    if (selection < 0)
		    {
			selection += 12;
		    }
		}
		break;
	    case KeyEvent.VK_DOWN:
		DOWN = false; // stop moving down, navigate menu
		if (Effects.pause || Effects.menu)
		{
		    selection++;
		}
		break;
	    case KeyEvent.VK_SPACE: // fire laser
		if (spaceship.laserType != 3 && !spaceship.dead)
		{
		    spawn (spaceship.location.x + spaceship.xdim / 2,
			    spaceship.location.y, spaceship.laserType + 30);
		}
		else if (spaceship.dead && Effects.deathTimer == 0 && lives >= 0)
		{
		    spaceship.respawn (); // respawn if player is dead
		}
		else if (Effects.gameOver)
		{
		    reset (); // reset game if the game is over
		    Effects.HSmenu = true; // and goto highscore menu
		}
		else if (lives < 0)
		{ // game is over if lives <0
		    Effects.gameOver = true;
		}
		shoot = false; // stop shooting
		break;
	    case KeyEvent.VK_CONTROL: // fire bomb, if allowed
		if (bombs > 0 && !spaceship.dead)
		{
		    spawn (bomb, spaceship.location.x + spaceship.xdim / 2,
			    spaceship.location.y, 38);
		}
		break;
	    case KeyEvent.VK_ENTER: // select in menu / pause
	    case KeyEvent.VK_ESCAPE: // escape does same as enter
		Effects.menuSelection ();
		break;
	}
    }


    public void keyTyped (KeyEvent e)
    {
    }


    public void drawAmmo (Graphics bufferGraphics)  // draw and calculate lasers
    {
	// find ammo instances that are not null, and draw
	// a linear search functions
	for (int i = 0 ; i < ammo.length ; i++)
	{
	    if (ammo [i] != null)
	    {
		ammo [i].draw (bufferGraphics); // draw valid ammo instances
		if (ammo [i].location.y < -ammo [i].dim.height
			|| ammo [i].location.y > dim.height
			|| ammo [i].timer == 0
			|| ammo [i].location.x < -ammo [i].dim.width
			|| ammo [i].location.x > dim.width)
		{
		    ammo [i] = null; // destroy if offscreen
		}
	    }
	    // find and draw any items
	    if (i < items.length && items [i] != null)
	    {
		items [i].draw (bufferGraphics);
		if (items [i].location.y > dim.height)
		{
		    items [i] = null; // erase if offscreen
		}
	    }
	    // shoot laser
	    if (beamCounter > 6 && shoot == true)
	    {
		spawn (spaceship.location.x + spaceship.xdim / 2,
			spaceship.location.y, spaceship.laserType + 30);
		beamCounter = 0;
	    } // if its a beam
	}
	beamCounter++; // delay between shots
	if (upgradeCountdown == 0)
	{ // spawn upgrades every ~1min
	    spawn ((int) Math.round (Math.random () * 9) + 61); // random upgrade
	    upgradeCountdown = 2000; // 4020 = ~1min
	}
	else if (upgradeCountdown > 0)
	{
	    upgradeCountdown--;
	}
    }


    public int openSlot (Object[] O)
    {
	// search for open slot, return value.
	for (int i = 0 ; i < O.length ; i++)
	{
	    if (O [i] == null)
	    {
		return i;
	    }
	}
	return -1; // if no empty slot, return -1
    }


    public void BadGuy (Graphics bufferGraphics)  // Instruct and draw baddies
    {
	for (int i = 0 ; i < enemy.length ; i++)
	{
	    // find all valid array entries, and do the following:
	    if (enemy [i] != null)
	    {
		enemy [i].instruct (); // tell em what to do
		enemy [i].draw (bufferGraphics); // draw em
		if (enemy [i].location.y > dim.height
			|| enemy [i].location.x > dim.width + 100
			|| enemy [i].location.x < -100)
		{
		    enemy [i] = null; // remove if offscreen
		}
	    }
	    if (i < Form.length && Form [i] != null)
	    {
		if (Form [i].instruct ()) // instruct formations
		{ // if no one left in formation, remove
		    Form [i] = null;
		}
		if (Form [i] != null && Form [i].location.y > dim.height)
		{
		    Form [i] = null; // remove if formation is offscreen
		}
	    }
	}
	if (empty)
	{ // spawn random badguy if empty
	    // either spawns a random single badguy, or a random formation
	    int typeToSpawn = (int) Math.round (Math.random ());
	    switch (typeToSpawn)
	    {
		case 0:
		    spawn ((int) Math.round (Math.random () * NumBaddieTypes));
		    break;
		case 1:
		    spawn ((int) Math.round (Math.random () * 4) + 90);
	    }
	}
    }


    public void spawn (int type)  // spawn new enemies/upgrades
	// for spawn methods, >0=enemy, >30=laser,>60=upgrade, >90 = formation
    {
	if (type < 30)
	{ // if enemy
	    int find = openSlot (enemy);
	    if (find != -1)
	    { // spawn enemy in open array slot, if exists
		enemy [find] = new baddie ((int) Math.ceil (Math.random ()
			    * dim.width), -50, type, this);
	    }
	}
	else if (type >= 60 && type < 90)   // if items
	{ // spawn an item into an open slot (if it exists)
	    int find = openSlot (items);
	    if (find != -1)
	    {
		items [find] = new upgrade (type, this);
	    }
	}
	else if (type >= 90)
	{ // if formation
	    // spawn formation into an open slot (if exists)
	    int find = openSlot (Form);
	    if (find != -1)
	    {
		Form [find] = new formation (type, this);
	    }
	}
    }


    public void spawn (int x, int y, int type)
    {
	// spawn ammo, lasers, enemy bullets
	if (type < 10)
	{
	    // look for empty array entry, then spawn enemy there.
	    for (int i = 0 ; i < enemy.length ; i++)
	    {
		if (enemy [i] == null)
		{
		    enemy [i] = new baddie (x, y, type, this);
		    break;
		}
	    }
	}
	else
	{
	    // look for empty array entry, then spawn ammo there.
	    for (int i = 0 ; i < ammo.length
		    && (i < spaceship.MaxAmmo || type == 13) ; i++)
	    {
		if (ammo [i] == null)
		{
		    ammo [i] = new Ammo (x, y, type, this);
		    break;
		}
	    }
	}
    }


    public void spawn (Image img, int x, int y, int type)  // spawn ammo w/ Image
	// essentially just for bombs. also explosions
    {
	for (int i = 0 ; i < ammo.length && i < spaceship.MaxAmmo ; i++)
	{
	    if (ammo [i] == null)
	    { // Essentially a custom search, to find a
		// null value
		ammo [i] = new Ammo (img, x, y, type, this);
		if (type == 38)
		{
		    bombs--; // decrement bomb variable if bomb fired
		}
		break;
	    }
	}
    }


    public void kill ()
	// kill enemies, hitTest
    {
	empty = true;
	// assign 'empty' true. Will be set false if there is any baddies
	// if remains true, then more baddies will spawn
	for (int i = 0 ; i < enemy.length ; i++)
	{
	    if (enemy [i] != null)
	    {
		empty = false;
		// if spaceship hits enemy, while not dead
		if (!spaceship.dead && hit.Test (spaceship.R, enemy [i].R))
		{
		    enemy [i] = null; // kill enemy
		    if (spaceship.shield)
		    { // remove shield if present
			spaceship.shield = false;
		    }
		    else if (!spaceship.respawn)
		    { // else kill player
			spawn (explosion, spaceship.location.x + spaceship.xdim
				/ 2, spaceship.location.y + spaceship.ydim / 2,
				39);
			lives--; // remove a life
			Effects.deathTimer = 30;
			// timer for explosion, and respawn time
			spaceship.dead = true; // spaceship is dead
		    }
		}
		for (int j = 0 ; j < ammo.length ; j++)
		{
		    if (enemy [i] != null && ammo [j] != null
			    && enemy [i].type != 5)
		    {
			if (hit.Test (enemy [i].R, ammo [j].R))
			{
			    if (ammo [j].type == 38)
			    { // if bomb, explode
				// essentially replace bomb with explosion
				// in same array index, at same location
				int x = ammo [j].location.x;
				int y = ammo [j].location.y;
				int w = ammo [j].dim.width;
				int h = ammo [j].dim.height;
				ammo [j] = null;
				ammo [j] = new Ammo (explosion, x + w / 2, y + h
					/ 2, 39, this);
			    }
			    else
			    {
				enemy [i].hp -= ammo [j].damage;
				// damage enemies if hit by laser / ammo
				if (ammo [j].timer <= 0)
				{
				    ammo [j] = null; // remove bomb aft timer
				}
			    }
			    if (enemy [i].hp <= 0)
			    { // enemy hp is zero
				if (Effects.BOOM)
				{ // boom cheat is true
				    int find = openSlot (ammo);
				    if (find != -1)
				    {
					// if BOOM cheat, make big explosion on
					// enemy death
					ammo [find] = new Ammo (explosion,
						enemy [i].location.x
						+ enemy [i].xdim / 2,
						enemy [i].location.y
						+ enemy [i].ydim / 2,
						39, this);
				    }
				}
				else
				{
				    // spawn tinyExplosion on enemy death
				    ammo [openSlot (ammo)] = new Ammo (
					    tinyExplosion, enemy [i].location.x
					    + enemy [i].xdim / 2,
					    enemy [i].location.y + enemy [i].ydim
					    / 2, 37, this);
				}
				score += enemy [i].score;
				enemy [i] = null;
			    }
			}
		    }
		}
	    }
	    if (i < items.length && items [i] != null
		    && hit.Test (spaceship.R, items [i].R))
	    {
		items [i].collect ();
		items [i] = null;
		// collect item
	    }
	}
    }


    public void run ()  // Thread
	// controls game speed, and game logic. Loops
    {
	long tm = System.currentTimeMillis ();
	while (true)
	{
	    repaint ();
	    try
	    { // wait: to control FPS
		tm += Effects.drawSpeed; // redraw every set interval
		// and wait until that interval is called
		Thread.sleep (Math.max (0, tm - System.currentTimeMillis ()));
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace ();
	    }
	}
    }
}
