import java.awt.Color;

public class GameObject {
	// Default implementation of a game object
	private Vector velocity;
	private Vector position;
	private double mass;
	private double radius;
	private Color color = Color.black;

	// Everything has hierachy. Children sometimes need access to properties of their parent, therefore you pass it to the parent so they can ask them questions.
	private GameState ParentGame;

	public GameObject (Vector velocity, Vector position, double mass, double radius, GameState Parent){

		ParentGame = Parent;
		this.velocity = velocity;
		this.position = position;
		this.mass = mass;
		this.radius = radius;
	}

	protected void setColour(Color Color) {
		color = Color;
	}
	protected Color getColour(Color Color) {
		return color;
	}
	public void setVelocity(double x, double y)
	{
		velocity.setX(x);
		velocity.setY(y);
	}

	public Vector getPosition()
	{
		return position;
	}
	public void move(Vector force, double dt){
		Vector acceleration = force.times(0.5 / this.mass);
		this.velocity = this.velocity.plus(acceleration.times(dt));
		this.position = this.position.plus(this.velocity.times(dt/GameObjectLibrary.UNIVERSE_SCALE));

		//Boundry conditions
		if(position.getX() > ParentGame.W)
		{
			velocity.setX(-velocity.getX()*0.5);
			position.setX(ParentGame.W);	
		}
		if(position.getX() < -ParentGame.W)
		{
			velocity.setX(-velocity.getX()*0.5);
			position.setX(-ParentGame.W);	
		}

		if(position.getY() > ParentGame.H)
		{
			velocity.setY(-velocity.getY()*0.5);
			position.setY(ParentGame.H);	
		}
		if(position.getY() < -ParentGame.H)
		{
			velocity.setY(-velocity.getY()*0.5);
			position.setY(-ParentGame.H);	
		}

	}

	public Vector getForce(GameObject that)
	{
		double m1 = this.mass;
		double m2 = that.mass;        
		Vector relatPos = that.position.minus(this.position);
		double distance = relatPos.magnitude();
		double forceMag = (m1 * m2 * GameObjectLibrary.G) / Math.pow(distance, 2);   

		Vector direction = new Vector(0,0);
		if(relatPos.magnitude() != 0)
		{
			direction = relatPos.direction();
		}

		return direction.times(forceMag);
	}

	public boolean isColliding(GameObject that)
	{
		double distance = that.position.minus(this.position).magnitude();
		if (this.radius + that.radius > distance)
			return true;
		else return false;
	}

	public void draw(Draw canvas)
	{
		ParentGame.setPenColour(color, canvas);
		canvas.filledCircle(this.position.getX(), this.position.getY(), radius);
	}

	public void setPosition(double mouseX, double mouseY) 
	{
		position.setX(mouseX);
		position.setY(mouseY);
	}

	public double getMass()
	{
		return mass;
	}
	public Vector getVelocity() 
	{
		return velocity;
	}

	public double getRadius() 
	{
		return radius;
	}

	public GameState getParent()
	{
		return ParentGame;
	}
}
