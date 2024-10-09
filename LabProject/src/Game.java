import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Game extends JPanel implements Runnable, MouseListener {

    boolean Running;
    Thread thread;
    BufferedImage view, background, floor, bird, StartGame;
    BufferedImage[] BirdAnim;
    Rectangle backgroundBox, floorBox, flappyBox, StartGameBox;

    int distort;
    int SCALE = 2;
    int SIZE = 256;

    int frameIndexFly = 0, intervalFrame = 5;
    direction direction;
    float velocity = 0;
    float gravity = 0.30f;
    boolean inGame;
    BufferedImage Upper, Lower;
    Pipes[] pipes;
    Font font;
    int HighScore = 0;
    int point = 0;

    public Game() {
        SIZE *= SCALE;
        setPreferredSize(new Dimension(SIZE, SIZE));
        addMouseListener(this);
    }
    //main
    public static void main(String[] args) {
        JFrame Game = new JFrame("Flappy Bird");
        Game.setResizable(false);
        Game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game.add(new Game());
        Game.pack();
        Game.setLocationRelativeTo(null);
        Game.setVisible(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            Running = true;
            thread.start();
        }
    }

    public void start() {


        try {
            //image loading.
            view = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
            background = ImageIO.read(Objects.requireNonNull(getClass().getResource("background.png")));
            floor = ImageIO.read(Objects.requireNonNull(getClass().getResource("floor.png")));
            StartGame = ImageIO.read(Objects.requireNonNull(getClass().getResource("start_game.png")));
            BufferedImage fly = ImageIO.read(Objects.requireNonNull(getClass().getResource("flappy_sprite.png")));
            Upper = ImageIO.read(Objects.requireNonNull(getClass().getResource("upperpipe.png")));
            Lower = ImageIO.read(Objects.requireNonNull(getClass().getResource("lowerpipe.png")));
            //animation for bird
            BirdAnim = new BufferedImage[3];
            for (int i = 0; i < 3; i++) {
                BirdAnim[i] = fly.getSubimage(i * 17, 0, 17, 12);
            }
            bird = BirdAnim[0];

            //to render the box in larger pixels
            distort = (SIZE / background.getHeight());

            pipes = new Pipes[4];
            startPipes();

            int widthStartGame = StartGame.getWidth() * distort;
            int heightStartGame = StartGame.getHeight() * distort;
            StartGameBox = new Rectangle((SIZE / 2) - (widthStartGame / 2), (SIZE / 2) - (heightStartGame / 2), widthStartGame, heightStartGame);
            flappyBox = new Rectangle(0, 0, bird.getWidth() * distort, bird.getHeight() * distort);
            backgroundBox = new Rectangle(0, 0, background.getWidth() * distort, background.getHeight() * distort);
            floorBox = new Rectangle(0, SIZE - (floor.getHeight() * distort), floor.getWidth() * distort, floor.getHeight() * distort);

            startBird();

            font = new Font("TimesRoman", Font.BOLD, 16 * distort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




    }
    //generate pipes
    public void startPipes() {
        for (int i = 0; i < 4; i++) {
            pipes[i] = new Pipes(0, 0, Upper.getWidth() * distort, Upper.getHeight() * distort);
            pipes[i].resetToNewPosition((SIZE + Upper.getWidth() * distort) + (i * 170));
        }
    }
    //generate the bird
    public void startBird() {
        direction = direction.NONE;
        inGame = false;
        flappyBox.x = (SIZE / 2) - (flappyBox.width * 3);
        flappyBox.y = (SIZE / 2) - flappyBox.height / 2;
    }
    //loop for the game animations
    public void update() {
        backgroundBox.x -= 1;
        floorBox.x -= 3;

        if (backgroundBox.x + backgroundBox.getWidth() <= 0) {
            backgroundBox.x = (int) (backgroundBox.x + backgroundBox.getWidth());
        }

        if (floorBox.x + floorBox.getWidth() <= 0) {
            floorBox.x = (int) (floorBox.x + floorBox.getWidth());
        }

        intervalFrame++;
        if (intervalFrame > 5) {
            intervalFrame = 0;
            frameIndexFly++;
            if (frameIndexFly > 2) {
                frameIndexFly = 0;
            }
            bird = BirdAnim[frameIndexFly];
        }

        if (inGame) {
            for (Pipes pipe : pipes) {
                pipe.moveX(3);

                if (pipe.x + pipe.width < 0) {
                    pipe.resetToNewPosition(SIZE + pipe.width + 65);
                }

                if (pipe.intersect(flappyBox)) {
                    gameOver();
                }

                if (pipe.passedOn(flappyBox)) {
                    pipe.isPassedOn = true;
                    point++;
                    if (point > HighScore) {
                        HighScore = point;
                    }
                }
            }
        }

        if (direction == direction.DOWN) {
            velocity += gravity;
            flappyBox.y += velocity;
        } else if (direction == direction.UP) {
            velocity = -4f;
            flappyBox.y -= -velocity;
        }

        if (flappyBox.y + flappyBox.getHeight() >= SIZE - floorBox.height || flappyBox.y <= 0) {
            gameOver();
        }
    }
    //restart game
    public void gameOver() {
        point = 0;
        startPipes();
        startBird();
    }
    //generate images from src
    public void draw() {
        Graphics2D g2 = (Graphics2D) view.getGraphics();
        g2.drawImage(
                background,
                backgroundBox.x,
                backgroundBox.y,
                (int) backgroundBox.getWidth(),
                (int) backgroundBox.getHeight(),
                null
        );
        g2.drawImage(
                background,
                (int) (backgroundBox.x + backgroundBox.getWidth()),
                backgroundBox.y,
                (int) backgroundBox.getWidth(),
                (int) backgroundBox.getHeight(),
                null
        );

        for (Pipes pipe : pipes) {
            g2.drawImage(Upper, pipe.x, pipe.Upper.y, pipe.width, pipe.height, null);
            g2.drawImage(Lower, pipe.x, pipe.Lower.y, pipe.width, pipe.height, null);
        }

        g2.drawImage(
                floor,
                floorBox.x,
                floorBox.y,
                (int) floorBox.getWidth(),
                (int) floorBox.getHeight(),
                null
        );
        g2.drawImage(
                floor,
                (int) (floorBox.x + floorBox.getWidth()),
                floorBox.y,
                (int) floorBox.getWidth(),
                (int) floorBox.getHeight(),
                null
        );

        g2.drawImage(
                bird,
                flappyBox.x,
                flappyBox.y,
                (int) flappyBox.getWidth(),
                (int) flappyBox.getHeight(),
                null
        );

        if (!inGame) {
            g2.drawImage(
                    StartGame,
                    StartGameBox.x,
                    StartGameBox.y,
                    (int) StartGameBox.getWidth(),
                    (int) StartGameBox.getHeight(),
                    null
            );
        }

        g2.setColor(Color.WHITE);
        g2.setFont(font);
        if (!inGame) {
            g2.drawString("Personal Best: " + HighScore, 10, 35);
        } else {
            g2.drawString("Score: "+point, SIZE - 150, 35);
        }

        Graphics g = getGraphics();
        g.drawImage(view, 0, 0, SIZE, SIZE, null);
        g.dispose();
    }

    @Override
    public void run() {
        try {
            start();
            while (Running) {
                update();
                draw();
                Thread.sleep(600 / 60);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        direction = direction.UP;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        inGame = true;
        direction = direction.DOWN;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
