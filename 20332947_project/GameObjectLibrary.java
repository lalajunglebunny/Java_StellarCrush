import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GameObjectLibrary {
	// Class for defining various game objects, and putting them together to create content
	// for the game world.  Default assumption is objects face in the direction of their velocity, and are spherical.

	// UNIVERSE CONSTANTS - TUNED BY HAND FOR RANDOM GENERATION
	//    private static final double ASTEROID_RADIUS = 0.5; // Location of asteroid belt for random initialization
	//    private static final double ASTEROID_WIDTH = 0.2; // Width of asteroid belt
	//    private static final double ASTEROID_MIN_MASS = 1E24;
	//    private static final double ASTEROID_MAX_MASS = 1E26;
	public 	static final double PLAYER_MASS = 1E25;
	public  static final double UNIVERSE_SCALE = 10000;  //10000px in km

	static final double G = 6.67e-11;

	public GameObjectLibrary() 
	{
	}

	public static Map<GameObject, Vector> getForces(Stack<GameObject> objects) {
		Map<GameObject, Vector> forces = new HashMap<>();
		int i = 0, j = 0;

		for (GameObject currentObject : objects) {
			Vector totalForce = new Vector(2);
			for (GameObject otherObject : objects) {
				if (i != j) totalForce = totalForce.plus(currentObject.getForce(otherObject));
				
				j++;
			}

			i++;
			j = 0;

			forces.put(currentObject, totalForce);
		}

		return forces;
	}
	// updates position of object based on forces
	public static void moveObjects(Stack<GameObject> objects, Map<GameObject, Vector> forces, double dt_mili){
		objects.clear();

		for (Map.Entry<GameObject, Vector> entry : forces.entrySet()) {
			GameObject key = entry.getKey();
			Vector value = entry.getValue();

			key.move(value, dt_mili/1000);
			objects.add(key);
		}     
	}
	public static Map<GameObject, GameObject> CheckColisions(Stack<GameObject> objects)
	{
		Map<GameObject, GameObject> colisions = new HashMap<>();
		int i = 0, j = 0;

		for (GameObject currentObject : objects) {
			for (GameObject otherObject : objects) 
			{
				if(currentObject != otherObject)
				{
					if(currentObject.isColliding(otherObject))
					{
						if(!colisions.containsValue(otherObject) && !colisions.containsValue(currentObject))
						{
							colisions.put(currentObject, otherObject);
						}
					}
				}
				j++;
			}

			i++;
			j = 0;        
		}
		return colisions;
	}

	public static  Stack<GameObject> mergeObjects(Map<GameObject,GameObject> colisionList, Stack<GameObject> AllObjects)
	{
		Stack<GameObject> newList = new Stack<GameObject>(); 
		int i = 0; int j = 0;

		for (GameObject checkObject : AllObjects) 
		{
			boolean Match = false;
			for (Map.Entry<GameObject, GameObject> entry : colisionList.entrySet()) 
			{
				GameObject ThisObject = entry.getKey();
				GameObject ThatObject = entry.getValue();  

				if(ThisObject == checkObject ||ThatObject == checkObject)
				{
					Match = true;
				}
				j++;
			}
			i++;
			if(!Match)
			{
				newList.add(checkObject);
			}
		} 
		
		for (Map.Entry<GameObject, GameObject> entry : colisionList.entrySet()) 
		{
			GameObject ThisObject = entry.getKey();
			GameObject ThatObject = entry.getValue();
			double newMass = ThisObject.getMass()+ThatObject.getMass();
			Vector newVel = momentumMergeVelocit(ThisObject.getVelocity(),ThisObject.getMass(), ThatObject.getVelocity(),ThatObject.getMass());
			double newRadius = (ThisObject.getRadius()+ThatObject.getRadius())*0.7;

			newList.add(new GameObject(newVel, ThisObject.getPosition(), newMass,newRadius, ThatObject.getParent()));
		}
		return newList;
	}

	public static Stack<GameObject> CheckAndSplit(Stack<GameObject> objects, playerObject player)
	{
		Stack<GameObject> colisions = new Stack<>();

		int i = 0, j = 0;

		for (GameObject currentObject : objects) 
		{
			if(player.getPosition().minus(currentObject.getPosition()).magnitude()<currentObject.getRadius())
			{
				GameObject[] pieces = splitObject(currentObject);

				colisions.add(pieces[0]);
				colisions.add(pieces[1]);
			}else
			{
				colisions.add(currentObject);
			}
		}

		return colisions;
	}
	public static GameObject[] splitObject(GameObject toSplit)
	{
		double randx = Math.random()*10E6-5E6;
		double randy = Math.random()*10E6-5E6;
		double randx2 = Math.random()*10E6-5E6;
		double randy2 = Math.random()*10E6-5E6;
		Vector v1 = new Vector(toSplit.getVelocity().getX()+randx,toSplit.getVelocity().getY()+randy);
		Vector v2 = new Vector(toSplit.getVelocity().getX()+randx2,toSplit.getVelocity().getY()+randy2);
		Vector p1 = toSplit.getPosition().plus(new Vector(toSplit.getRadius(),toSplit.getRadius()));
		Vector p2 = toSplit.getPosition().plus(new Vector(-toSplit.getRadius(),-toSplit.getRadius()));

		GameObject new1 = new GameObject(v1, p1, toSplit.getMass()/2, toSplit.getRadius()*0.7, toSplit.getParent());
		GameObject new2 = new GameObject(v2, p2, toSplit.getMass()/2, toSplit.getRadius()*0.7, toSplit.getParent());

		GameObject[] toreturn = new GameObject[2];

		toreturn[0]=new1;
		toreturn[1]=new2;

		return toreturn;
	}

	public static Vector momentumMergeVelocit (Vector V1, double M1, Vector V2, double M2)
	{
		Vector Movect1 = new Vector(V1.getX(),V1.getY()).times(M1);
		Vector Movect2 = new Vector(V2.getX(),V2.getY()).times(M2);
		
		return (Movect1.plus(Movect2)).times(1/(M1+M2));
	}
}

