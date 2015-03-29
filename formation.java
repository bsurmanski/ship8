import java.awt.Point;

public class formation {

	int[] formation; // holds baddie array value of formation members
	byte formOrder = 0;
	int formationNum; // which formation
	Point location; // location of formation
	byte xspeed, yspeed; // speed of formation
	byte cols, rows; // cols and rows in formation
	byte baddieNum; // baddie to spawn
	byte fireDelay = -1;
	ship8 field; // main applet reference

	public formation(int formationNum, ship8 field) {
		this.field = field;
		this.formationNum = formationNum;
		location = new Point(0, -200);
		defineForm();
	}

	private void defineForm() {
		cols = 10; // default 10, fills screen
		switch (formationNum) {
		case 90:
			formation = new int[1];
			rows = 1;
			location.x = (int) (Math.random() * field.dim.width);
			baddieNum = (byte) (Math.random() * field.NumBaddieTypes);
			break;
		case 91:
			formation = new int[10];
			rows = 1;
			baddieNum = 1;
			location.x = (int) (field.dim.width / 2 - (field.scaleX * 40
					* formation.length / rows) / 2);
			break;
		case 92:
			formation = new int[30];
			baddieNum = 1;
			rows = 3;
			location.x = (int) (field.dim.width / 2 - (field.scaleX * 40
					* formation.length / rows) / 2);
			fireDelay = 40;
			break;
		case 93:
			formation = new int[30];
			baddieNum = 0;
			rows = 3;
			location.x = 1;
			formOrder = 1;
			break;
		case 94:
			formation = new int[10];
			baddieNum = 3;
			formOrder = 2; // loose
			break;
		}
		for (int i = 0, j = 0, k = 1; i < field.enemy.length
				&& j < formation.length; i++) {
			// i checks all the instances in the baddie array for open slots
			// j is the array slot for formation[], k tracks the column
			if (field.enemy[i] == null) {
				switch (formOrder) {
				case 0:
					field.enemy[i] = new baddie(baddieNum, formationNum,
							(int) (location.x + (40 * field.scaleX)
									* (j - ((k - 1) * cols))), location.y + 50
									* k, field);
					break;
				case 1:
					field.enemy[i] = new baddie(baddieNum, formationNum,
							(int) (Math.random() * field.dim.width), location.y
									+ 50 * k + (int) (Math.random() * 20)
									- (int) (Math.random() * 20), field);
					break;
				case 2: // fix to make missle formation
					field.enemy[i] = new baddie(200, 200, baddieNum, field);
					break;
				}
				formation[j] = i;
				j++;
				if (j >= k * cols) {
					k++;
				}
			}
		}
	} // spawn baddies, type, formationNum, x,y

	public boolean instruct() {
		switch (formationNum) {
		case 90:
			yspeed = 4;
			break;
		case 91:
			yspeed = 2;
			break;
		case 92:
			if (location.y < 16) {
				yspeed = 1;
			} else {
				yspeed = 0;
				if (fireDelay > 0)
					fireDelay--;
				if (fireDelay == 0) {
					int shooter = (int) Math.floor(Math.random()
							* formation.length);
					if (field.enemy[formation[shooter]] != null) {
						fireDelay = 40;
						field.enemy[formation[shooter]].fire();
					}
				}
			}
			break;
		case 93:
			yspeed = 1;
			break;
		case 94:
			break;
		}
		boolean destroy = true;
		for (int i = 0; i < formation.length; i++) {
			if (field.enemy[formation[i]] != null) {
				if (formationNum != 94)
					field.enemy[formation[i]].instruct(xspeed, yspeed);
				destroy = false;
			}
		}
		location.translate(xspeed, yspeed);
		return destroy;
	}
}
