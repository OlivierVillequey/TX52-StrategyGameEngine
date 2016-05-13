﻿package tx52.environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class DynamicObject extends EnvironmentObject{
	
	float perceptionDistance; //for agent body
	

	//Agent créé son corps : on appellera ce constructeur lors de la création de l'agent
	// P.S : la position x,y sera surement donné par l'agent également
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width -> we may delete it
	 * @param height -> we may delete it
	 * @param agentId
	 * @param w
	 */
	public DynamicObject(float x, float y,float width,float height,UUID agentId,World w,EnvMap map){
		super(x,y,width,height,agentId);
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.type = BodyType.KINEMATIC; //maybe DYNAMIC type is better
		Vec2[] vertices = { new Vec2(0.0f, -10.0f), new Vec2(+10.0f, +10.0f), new Vec2(-10.0f, +10.0f) };//need to change this shape generation
		PolygonShape ps = new PolygonShape();
		ps.set(vertices, vertices.length);

		
		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 0.5f;
		fd.friction = 0.3f;
		fd.restitution = 0.5f;
		fd.userData = id; 
		// what is userData ? can we store UUID in this field ?

		body = w.createBody(bd);
		body.createFixture(fd);

		//set the box to the exact object

				Fixture fixtureList = body.getFixtureList();
				for (Fixture f = fixtureList;f!=null;f=f.m_next)
				{
					ShapeType shapeType = f.getType();
					if ( shapeType == ShapeType.CIRCLE)
					{
					    CircleShape circleShape = (CircleShape) f.getShape();
					    // not possible in dynamic objects for now
					}
					else if ( shapeType == ShapeType.POLYGON )
					{
					    PolygonShape polygonShape = (PolygonShape)f.getShape();
					    for(int i=0;i<polygonShape.getVertexCount();i++){
					    	Vec2 vecteur = polygonShape.getVertex(i);
					    	box.add(vecteur.x,vecteur.y); //add all points of the shape to the box 
					    }   	
					}
				}
	}
	/**
	 * New creation of object
	 * @param x
	 * @param y
	 * @param radius
	 * @param agentId
	 * @param w
	 */
	public DynamicObject(float x, float y,float radius,UUID agentId,World w,EnvMap map){
		super(x,y,agentId);
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.type = BodyType.KINEMATIC; //maybe DYNAMIC type is better
		CircleShape cs = new CircleShape();
		cs.setRadius(radius); // is that all we need for a circle shape ?

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 0.5f;
		fd.friction = 0.3f;
		fd.restitution = 0.5f;
		fd.userData = this; // we store a reference to the object there

		body = w.createBody(bd);
		body.createFixture(fd);

		box.inflate(radius, radius,radius,radius);
	}
	
	/**
	 * only for agent body
	 * should we return the body of the agent in perception ?
	 * @return 
	 */
	public ArrayList<Perceivable> computePerception(){
		
		TreeNode currentNode = this.node;
		Circle2f range = new Circle2f(getPosition(),perceptionDistance);
		ArrayList<Perceivable> percept = new ArrayList<Perceivable>();
		
		//current node
		for(EnvironmentObject o:currentNode.getObjects()){
			if(o.box.intersects(range)){
				percept.add(new Perceivable(o));
			}
		}
		//other node
		if(this.getPosition().distance(currentNode.getBox().getClosestPointTo(getPosition()))<=range.getRadius()){
			//how can we limit the search ?
		}

		
		
		return percept;
		
	}

}