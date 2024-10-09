import java.awt.*;
import java.util.Random;
public class Pipes {
    int x, y, width, height;
    Rectangle Upper, Lower;
    int distance = 150;
    boolean isPassedOn = false;

    public Pipes(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        Upper = new Rectangle(x, y, width, height);
        Lower = new Rectangle(x, height + distance, width, height);
    }
    //new pipe generation
    public void resetToNewPosition(int newX) {
        Upper.x = newX;
        Lower.x = newX;
        x = newX;
        Upper.y = -(new Random().nextInt(140) + 100);
        Lower.y = Upper.y + height + distance;
        isPassedOn = false;
    }
    //check for hitting pipes
    public boolean intersect(Rectangle rectangle) {
        return rectangle.intersects(Upper) || rectangle.intersects(Lower);
    }
    //check for crossing pipes
    public boolean passedOn(Rectangle rectangle) {
        return rectangle.x > x + width && !isPassedOn;
    }
    //check for pipe moving
    public void moveX(int dx) {
        x -= dx;
        Upper.x -= dx;
        Lower.x -= dx;
    }
}
