import java.awt.Rectangle;

class hitTest {

    public hitTest() {
    }

    public boolean Test(Rectangle O, Rectangle P) {
	if (O.intersects(P)) {
	    return true;
	}
	return false;
    }
}
