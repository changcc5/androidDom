package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


public abstract class GameCanvas extends Canvas {
    // keystate constants
    public static final int UP_PRESSED = 1 << Canvas.UP;
    public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
    public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
    public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
    public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
    public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
    public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
    public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
    public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;


    /** Creates a new instance of GameCanvas */
    protected GameCanvas(boolean suppressKeyEvents) {
    }

    public void paint(Graphics g) {
    }

    public void flushGraphics(int x, int y, int width, int height) {
        flushGraphics();
    }

    public void flushGraphics() {
        repaint();
        serviceRepaints();
    }

    public int getKeyStates() {
        //TODO
        return 0;
    }
}


