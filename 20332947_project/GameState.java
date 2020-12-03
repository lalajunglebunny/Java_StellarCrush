import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Stack;



public class GameState{
	// Class representing the game state and implementing main game loop update step.

	// private Collection<GameObject> objects;
	//private final PlayerObject player;


	// CONSTANTS TUNED FOR GAMEPLAY EXPERIENCE
	static final int GAME_DELAY_TIME = 5000; // in-game time units between frame updates
	static final int TIME_PER_MS = 1000; // how long in-game time corresponds to a real-time millisecond
	static final double G = 6.67e-11; // gravitational constant
	static final double softE = 0.001; // softening factor to avoid division by zero calculating force for co-located objects
	static double scale = 5e10; // plotted universe size
	public int W = 500;
	public int H = 500;

	private boolean running;
	private boolean MainMenuActive;

	GameObjectLibrary uni = new GameObjectLibrary();

	Stack<GameObject> objects = new Stack<>();
	Stack<GameObject> objects3D = new Stack<>();
	playerObject player; 
	private Draw TopViewCanvas;
	private Draw FPSCanvas;

	float FOG_FACTOR = 0.06f;
	int NUMTOWIN;

	int fps = 60;

	/*Camera details */
	Vector CamPos = new Vector(0,-500);
	double focalLength = 500;

	//Stop method 
	public void stop(){
		running = false;
	}
	//run method
	public void run(){
		try{
			StartMainScreen();
		}finally{
		}
	}

	public void StartMainScreen()
	{
		MainMenuActive = true;
		TopViewCanvas = new Draw();
		TopViewCanvas.setXscale(-500, 500);
		TopViewCanvas.setYscale(-500, 500);
		long startTime = System.currentTimeMillis();
		long cumTime = startTime;

		InittializeSim(100);
		while (!TopViewCanvas.hasNextKeyTyped() && MainMenuActive)
		{
			long timePassed = System.currentTimeMillis() - cumTime;
			cumTime += timePassed;

			drawObjects(objects, TopViewCanvas);
			updateObjects(10);

			TopViewCanvas.setPenColor(Color.BLUE);
			TopViewCanvas.text(0, 400, "BALL FONDLERS");
			TopViewCanvas.text(0, 10, "Select Difficulty! ");
			TopViewCanvas.text(0, 60, "Rotate the mouse to control your ball and split the other balls!");
			TopViewCanvas.text(0, -480, "Quit(m), Screencap(p)");
			TopViewCanvas.filledRectangle(0,-100 , 200, 50);
			TopViewCanvas.setPenColor(Color.BLACK);
			TopViewCanvas.text(0,-100, "EASY - Ball Count 7");
			TopViewCanvas.setPenColor(Color.ORANGE);
			TopViewCanvas.filledRectangle(0,-210 , 200, 50);
			TopViewCanvas.setPenColor(Color.BLACK);
			TopViewCanvas.text(0,-210, "MEDIUM - Ball Count 8");
			TopViewCanvas.setPenColor(Color.RED);
			TopViewCanvas.filledRectangle(0,-320 , 200, 50);
			TopViewCanvas.setPenColor(Color.BLACK);
			TopViewCanvas.text(0,-320, "HARD - Ball count 9");
			TopViewCanvas.setPenColor(Color.BLACK);
			TopViewCanvas.filledRectangle(0,-400 , 200, 20);
			TopViewCanvas.setPenColor(Color.WHITE);
			TopViewCanvas.text(0,-400, "IMPOSSIBLE - ball count 12");

			if(TopViewCanvas.mousePressed())
			{
				if(TopViewCanvas.mouseX() < 25 && TopViewCanvas.mouseX() > -25 && TopViewCanvas.mouseY() < -50 &&TopViewCanvas.mouseY() > -150)
				{
					StartGame(6);
					MainMenuActive = false;
				}
				if(TopViewCanvas.mouseX() < 25 && TopViewCanvas.mouseX() > -25 && TopViewCanvas.mouseY() < -160 &&TopViewCanvas.mouseY() > -260)
				{
					StartGame(7);
					MainMenuActive = false;
				}
				if(TopViewCanvas.mouseX() < 25 && TopViewCanvas.mouseX() > -25 && TopViewCanvas.mouseY() < -270 &&TopViewCanvas.mouseY() > -370)
				{
					StartGame(8);
					MainMenuActive = false;
				}
				if(TopViewCanvas.mouseX() < 25 && TopViewCanvas.mouseX() > -25 && TopViewCanvas.mouseY() < -380 &&TopViewCanvas.mouseY() > -420)
				{
					StartGame(11);
					MainMenuActive = false;
				}
			}

			show(TopViewCanvas); 
			TopViewCanvas.clear(Color.BLUE);
			try{
				Thread.sleep(20);
			}catch(Exception ex){}
		}

		MainMenuActive = false;
		try {
			Thread.sleep(5000);
			System.exit(1);
		} catch (InterruptedException e) {
		}
	}

	private void StartGame(int NumEnemies)
	{
		FPSCanvas = new Draw();
		FPSCanvas.setXscale(-500, 500);
		FPSCanvas.setYscale(-500, 500);
		objects.clear();
		NUMTOWIN = NumEnemies;
		for(int i = 0; i<6;i++)
		{
			Vector velocity = new Vector(2);
			double[] pos1 = {Math.random()*1000 -500, Math.random()*1000 - 500 };        

			GameObject body = new GameObject(velocity, new Vector(pos1), 1e20, 10, this);

			addObject(body);
		}     

		player = new playerObject(new Vector(0,0), new Vector(0,0), GameObjectLibrary.PLAYER_MASS , 15, this);

		running = true;
		gameLoop();
	}

	public void addObject(GameObject b) {
		this.objects.add(b);
	}


	@SuppressWarnings("deprecation")
	public void show(Draw currentScreen) 
	{
		currentScreen.show(10);
	}

	public void drawObjects(Stack<GameObject> objects, Draw Canvas)
	{
		Canvas.clear();    
		for (GameObject b : objects) {

			b.draw(Canvas);
		}
	}


	private void InittializeSim(int num)
	{
		objects.clear();
		for(int i = 0; i<num;i++)
		{
			Vector velocity = new Vector(2);
			double[] pos1 = {Math.random()*1000 -500, Math.random()*1000 - 500 };        

			GameObject body = new GameObject(velocity, new Vector(pos1), 1e20, 10, this);
			body.setColour(Color.GRAY);
			addObject(body);
		}       
	}

	//main gameloop
	public void gameLoop()
	{
		long startTime = System.currentTimeMillis();
		long cumTime = startTime;

		//----------------------------------------------- Thing that runs after init
		while (running && !TopViewCanvas.isKeyPressed(KeyEvent.VK_M))
		{
			if(TopViewCanvas.isKeyPressed(KeyEvent.VK_P))
			{
				TopViewCanvas.save("asd.jpg");
			}

			long timePassed = System.currentTimeMillis() - cumTime;
			cumTime += timePassed;

			updateObjects(timePassed); 
			updateObjects3D();

			player.updateAcceleration(TopViewCanvas.mouseX(),TopViewCanvas.mouseY());

			drawObjects(objects, TopViewCanvas);
			drawObjects(objects3D, FPSCanvas);
			drawPlayer3D(player,FPSCanvas);

			TopViewCanvas.text(-400, 450, "Ball Count: " + objects.size());

			player.draw(TopViewCanvas);

			if(objects.size() > NUMTOWIN)
			{
				TopViewCanvas.setPenColor(new Color(1 , 1, 1, 0.5f));
				TopViewCanvas.filledRectangle(0, 0, 500, 500);
				TopViewCanvas.setPenColor(Color.RED);
				TopViewCanvas.text(0, 0, "YOU HAVE SUCCESSFULLY SPLIT YOUR BALLS");

				show(TopViewCanvas); 
				running = false;
			}

			show(TopViewCanvas);    
			show(FPSCanvas); 
			try{
				Thread.sleep(20);
			}catch(Exception ex){}
		}
		//----------------------------------------------------------------
	}
	// Uses a focal length and a hyperbolic expression to set the X placement and size of the object respectively, similar triangle is used to set the X with foal length being the factor
	private void drawPlayer3D(playerObject p, Draw c)
	{
		Vector pos = new Vector(0,0);
		double L = p.getPosition().minus(CamPos).magnitude();
		pos.setX(p.getPosition().getX()*focalLength/(L));
		double R = 500*p.getRadius()/L;
		GameObject newob = new GameObject(new Vector(0,0), pos, 10, R, this);
		float grey = (float) L/1000;
		if(grey > 1) grey = 1;

		newob.setColour(Color.BLUE);
		newob.draw(FPSCanvas);
	}

	public void setPenColour(Color colour, Draw canvas)
	{
		canvas.setPenColor(colour);

	}
	private void updateObjects(double timePassed)
	{   
		//work out forces
		Map<GameObject,Vector> ForceMap = GameObjectLibrary.getForces(objects);

		GameObjectLibrary.moveObjects(objects, ForceMap, timePassed);

		Map<GameObject,GameObject> collList = GameObjectLibrary.CheckColisions(objects);

		objects = GameObjectLibrary.mergeObjects( collList, objects);

		if(running)
		{
			objects = GameObjectLibrary.CheckAndSplit(objects, player);
		}
		//apply forces and new positions
	} 
	private void updateObjects3D()
	{
		Stack<GameObject> newObjects = new Stack<GameObject>();
		Vector V = new Vector(0,0);
		for (GameObject currentObject : objects)
		{
			Vector pos = new Vector(0,0);
			double L = currentObject.getPosition().minus(CamPos).magnitude();
			pos.setX(currentObject.getPosition().getX()*focalLength/(L));
			double R = 500*currentObject.getRadius()/L;
			GameObject newob = new GameObject(V, pos, 10, R, this);
			float grey = (float) L*FOG_FACTOR*0.01f;
			if(grey > 1) grey = 1;
			newob.setColour(new Color(grey,grey,grey));

			newObjects.add(newob);
		}
		objects3D = newObjects;
	}
}





