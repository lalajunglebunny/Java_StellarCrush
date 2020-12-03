import java.awt.Color;

public class playerObject extends GameObject{


	public playerObject(Vector velocity, Vector position, double mass, double radius, GameState Parent) 
	{
		super(velocity, position, mass, radius, Parent);		
		setColour(Color.blue);
	}
	Vector aDir = new Vector(0,0);

	@Override 
	public void draw(Draw canvas)
	{
		for(int i = 0;i < 20;i++)
		{
			float R = 1;
			float G = i*0.05f;
			float B = i*0.05f;
			canvas.setPenColor(new Color(R,G,B));
			canvas.filledCircle(this.getPosition().getX()-(aDir.getX() *i*10), this.getPosition().getY()-aDir.getY()*i*10, 5 + i*0.51f);
		}
		super.draw(canvas);
	}
	// Acceleration is dependent on magnitude of dif between player and mouse (likespring) 
	public void updateAcceleration(double x, double y) 
	{
		double aX = (x-this.getPosition().getX())*0.01;
		double aY = (y-this.getPosition().getY())*0.01;

		this.setVelocity(getVelocity().getX()*0.9+aX,getVelocity().getY()*0.9+aY);
		aDir.setX(aX);
		aDir.setY(aY);

		this.setPosition(getPosition().getX()+getVelocity().getX(), getPosition().getY()+getVelocity().getY());
	}




}
