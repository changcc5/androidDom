package javax.microedition.m3g;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public final class Graphics3D {

    public static final int TRUE_COLOR = 8;

    private static Graphics3D instance;

    private Graphics targetGraphics;
    private int vpX = 0;
    private int vpY = 0;
    private int vpW = 0;
    private int vpH = 0;

    private Graphics3D() {
    }

    public static Graphics3D getInstance() {

        if (instance == null) {
            instance = new Graphics3D();
        }
        return instance;
    }

    public void bindTarget(Object target, boolean depthBuffer, int hints) {

        targetGraphics = (Graphics) target;
    }

    public void releaseTarget() {

    }

    public void clear(Background background) {
        if (background == null) {
            // clear to black
            targetGraphics.setColor(0xff000000);
            targetGraphics.fillRect(vpX, vpY, vpW, vpH);
        } else if (background.getImage() == null) {
            // clear to target color
            targetGraphics.setColor(background.getColor());
            targetGraphics.fillRect(vpX, vpY, vpW, vpH);
        } else {
            Bitmap bitmap = background.getImage().getImage().getBitmap();
            Canvas canvas = targetGraphics.getCanvas();
            Paint paint = targetGraphics.getPaint();

            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            int tx = targetGraphics.getTranslateX();
            int ty = targetGraphics.getTranslateY();
            Rect dst = new Rect(tx+vpX, ty+vpY, tx+vpX + vpW, ty+vpY + vpH);

            int a = paint.getAlpha();
            paint.setAlpha(0xFF); // not not use alpha with images
            canvas.drawBitmap(bitmap, src, dst, paint );
            paint.setAlpha(a);
        }
    }

    public void setViewport(int x, int y, int width, int height) {
        this.vpX = x;
        this.vpY = y;
        this.vpW = width;
        this.vpH = height;
    }

    public static Hashtable getProperties() {
        // TODO Auto-generated method stub
        return null;
    }
}
