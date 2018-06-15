import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;


public class snakeCanvas extends Canvas implements Runnable, KeyListener
{
	private final int BOX_HEIGHT = 15;
	private final int BOX_WIDTH = 15;
	private final int GRID_WIDTH = 25;
	private final int GRID_HEIGHT = 25;
	
	private LinkedList<Point> snake;
	private Point fruit;
	private int direction = Direction.NO_DIRECTION;
	
	private Thread runThread;
	private Graphics globalGraphics;
	private int score = 0;
	

	
	public void paint(Graphics g)
	{
		this.setPreferredSize(new Dimension(640, 480));
		snake = new LinkedList<Point>();
		GenerateDefaultSnake();
		PlaceFruit();
		globalGraphics = g.create();
		this.addKeyListener(this);
		if (runThread == null)
		{
			runThread = new Thread(this);
			runThread.start();
		}
	}
	
	public void GenerateDefaultSnake()
	{
		score = 0;
		snake.clear();
		
		snake.add(new Point(0,2));
		snake.add(new Point(0,1));
		snake.add(new Point(0,0));
		direction = Direction.NO_DIRECTION;
	}
	
	public void Draw(Graphics g)
	{
		g.clearRect(0, 0, BOX_WIDTH * GRID_WIDTH + 10, BOX_HEIGHT * GRID_HEIGHT + 20);
		//create a new image
		BufferedImage buffer = new BufferedImage(BOX_WIDTH * GRID_WIDTH + 10, BOX_HEIGHT * GRID_HEIGHT + 20, BufferedImage.TYPE_INT_ARGB);
		Graphics bufferGraphics = buffer.getGraphics();
		
		DrawFruit(bufferGraphics);
		DrawGrid(bufferGraphics);
		DrawSnake(bufferGraphics);
		DrawScore(bufferGraphics);
		
		//flip
		g.drawImage(buffer, 0, 0, BOX_WIDTH * GRID_WIDTH + 10,  BOX_HEIGHT * GRID_HEIGHT + 20, this);
	}
	
	public void Move()
	{
		Point head = snake.peekFirst();
		Point newPoint = head;
		switch (direction) {
		case Direction.NORTH:
			newPoint = new Point(head.x, head.y - 1);
			break;
		case Direction.SOUTH:
			newPoint = new Point(head.x, head.y + 1);
			break;
		case Direction.WEST:
			newPoint = new Point(head.x - 1, head.y);
			break;
		case Direction.EAST:
			newPoint = new Point(head.x + 1, head.y);
			break;
		}
		
		snake.remove(snake.peekLast());
		
		if (newPoint.equals(fruit))
		{
			//the snake has hit fruit
			score+=10;
			
			Point addPoint = (Point) newPoint.clone();
			
			switch (direction) {
			case Direction.NORTH:
				newPoint = new Point(head.x, head.y - 1);
				break;
			case Direction.SOUTH:
				newPoint = new Point(head.x, head.y + 1);
				break;
			case Direction.WEST:
				newPoint = new Point(head.x - 1, head.y);
				break;
			case Direction.EAST:
				newPoint = new Point(head.x + 1, head.y);
				break;
			}
			
			snake.push(addPoint);
			PlaceFruit();
			
		}
		else if (newPoint.x < 0 || newPoint.x > (GRID_WIDTH - 1))
		{
			//we went oob, reset game
			GenerateDefaultSnake();
			return;
		}
		else if (newPoint.y < 0 || newPoint.y > (GRID_HEIGHT - 1))
		{
			//we went oob, reset game
			GenerateDefaultSnake();
			return;
		}
		else if (snake.contains(newPoint))
		{
			//we ran into ourselves, reset game
			
			GenerateDefaultSnake();
			return;
		}

		//if we reach this point in code, we're still good
		snake.push(newPoint);
	}
	
	public void DrawScore(Graphics g)
	{
		g.drawString("Score: " + score, 0, BOX_HEIGHT * GRID_HEIGHT + 10);
	}
	
	public void DrawGrid(Graphics g)
	{
		//drawing an outside rect
		g.drawRect(0, 0, GRID_WIDTH * BOX_WIDTH, GRID_HEIGHT * BOX_HEIGHT);
		//drawing the vertical lines
		for (int x = BOX_WIDTH; x < GRID_WIDTH * BOX_WIDTH; x+=BOX_WIDTH)
		{
			g.drawLine(x, 0, x, BOX_HEIGHT * GRID_HEIGHT);
		}
		//drawing the horizontal lines
		for (int y = BOX_HEIGHT; y < GRID_HEIGHT * BOX_HEIGHT; y+=BOX_HEIGHT)
		{
			g.drawLine(0, y, GRID_WIDTH * BOX_WIDTH, y);
		}
	}
	
	public void DrawSnake(Graphics g)
	{
		g.setColor(Color.GREEN);
		for (Point p : snake)
		{
			g.fillRect(p.x * BOX_WIDTH, p.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		}
		g.setColor(Color.BLACK);
	}
	
	public void DrawFruit(Graphics g)
	{
		g.setColor(Color.RED);
		g.fillOval(fruit.x * BOX_WIDTH, fruit.y * BOX_HEIGHT, BOX_WIDTH, BOX_HEIGHT);
		g.setColor(Color.BLACK);
	}

	public void PlaceFruit()
	{
		Random rand = new Random();
		int randomX = rand.nextInt(GRID_WIDTH);
		int randomY = rand.nextInt(GRID_HEIGHT);
		Point randomPoint = new Point(randomX, randomY);
		while (snake.contains(randomPoint))
		{
			randomX = rand.nextInt(GRID_WIDTH);
			randomY = rand.nextInt(GRID_HEIGHT);
			randomPoint = new Point(randomX, randomY);
		}
		fruit = randomPoint;
	}
	
	@Override
	public void run() {
		while (true)
		{
			//runs indefinitely
			Move();
			Draw(globalGraphics);
			
			try
			{
				Thread.currentThread();
				Thread.sleep(100);
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			if (direction != Direction.SOUTH)
				direction = Direction.NORTH;
			break;
		case KeyEvent.VK_DOWN:
			if (direction != Direction.NORTH)
				direction = Direction.SOUTH;
			break;
		case KeyEvent.VK_RIGHT:
			if (direction != Direction.WEST)
				direction = Direction.EAST;
			break;
		case KeyEvent.VK_LEFT:
			if (direction != Direction.EAST)
				direction = Direction.WEST;
			break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
