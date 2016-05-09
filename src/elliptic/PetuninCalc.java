package elliptic;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.colors.Color;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.rendering.legends.ILegend;
import org.jzy3d.plot3d.rendering.view.ViewportConfiguration;
import org.jzy3d.plot3d.rendering.view.ViewportMode;



public class PetuninCalc {
	
	private Points points1, points2;
	private int i_a, i_b;
	private Point zero, M, center;
	private float alpha, beta;
	private float minX,maxX,minY,maxY,minZ,maxZ;
	private float radX;	
	private float radY;
	private float radZ;
	private float volume;
	private Point[] points8;
	private Point[] redpoints;
	

	public Point[] getPoints8() {
		return points8;
	}

	public PetuninCalc(Points points) {
		this.points1 = points;
		i_a = points.getI_a();
		i_b = points.getI_b();
		this.points2 = new Points(points1,Color.RED);
		points8 = new Point[8];
		redpoints = new Point[8];
		transposition();
	}
	
	public LineStrip getDiam(){
		LineStrip diam = new LineStrip();	
		Point a = new Point(points2.getPoint(i_a),points2.getColor());
		Point b = new Point(points2.getPoint(i_b),points2.getColor());
		diam.add(a);
		diam.add(b);
		return diam;
	}
	
	private void transposition(){
		
		float zX, zY, zZ;
		
		float x,y,z;
		zero = new Point(points1.getA().xyz);
		zX = zero.xyz.x;
		zY = zero.xyz.y;
		zZ = zero.xyz.z;
		
		
		
		// transfer
		for (int i = 0; i < points1.getSize(); i++) {
			Coord3d coord = new Coord3d();
			x = points1.getPoint(i).x;
			coord.x = x-zX;
			y = points1.getPoint(i).y;
			coord.y = y-zY;
			z = points1.getPoint(i).z;
			coord.z = z-zZ;
			points2.setPoint(i, coord);
		}
		
		M = new Point(points2.getPoint(i_b));
		double l = Math.sqrt(M.xyz.x*M.xyz.x+M.xyz.y*M.xyz.y+M.xyz.z*M.xyz.z);
		double l2 = Math.sqrt(M.xyz.x*M.xyz.x+M.xyz.y*M.xyz.y);
		if (l!=0) {
			alpha = (float)Math.asin(M.xyz.z/l);
		}
		if (l2!=0) {
			beta = (float)Math.asin(M.xyz.y/l2);
		}
		
		//points2.calcParams();
		
		// calculate rotate						
		for (int i = 0; i < points1.getSize(); i++) {
			Coord3d coord = new Coord3d();
			coord = rotateOYf(alpha, points2.getPoint(i));
			points2.setPoint(i, coord);
		}
		
		for (int i = 0; i < points1.getSize(); i++) {
			Coord3d coord = new Coord3d();
			coord = rotateOZf(beta, points2.getPoint(i));
			points2.setPoint(i, coord);
		}
		
		
		minX = points2.getPoint(points2.getI_a()).x;
		maxX = points2.getPoint(points2.getI_b()).x;
		minY = points2.getPoint(0).y;
		maxY = minY;
		minZ = points2.getPoint(0).z;
		maxZ = minZ;
		for (int i = 0; i < points1.getSize(); i++) {
			if (points2.getPoint(i).y<minY) {
				minY = points2.getPoint(i).y;
			}
			if (points2.getPoint(i).y>maxY) {
				maxY = points2.getPoint(i).y;
			}
			if (points2.getPoint(i).z<minZ) {
				minZ = points2.getPoint(i).z;
			}
			if (points2.getPoint(i).z>maxZ) {
				maxZ = points2.getPoint(i).z;
			}
		}
		/*
		center = new Point(new Coord3d(((minX+maxX)/2+zX), ((minY+maxY)/2+zY), ((minZ+maxZ)/2)+zZ), Color.BLACK);
		radX = maxX - minX;
		radY = maxY - minY;
		radZ = maxZ - minZ;
		*/
		points8[0] = new Point(new Coord3d(minX, minY, minZ));		
		points8[1] = new Point(new Coord3d(minX, minY, maxZ));
		points8[2] = new Point(new Coord3d(minX, maxY, maxZ));
		points8[3] = new Point(new Coord3d(minX, maxY, minZ));
		points8[4] = new Point(new Coord3d(maxX, minY, minZ));
		points8[5] = new Point(new Coord3d(maxX, minY, maxZ));
		points8[6] = new Point(new Coord3d(maxX, maxY, maxZ));
		points8[7] = new Point(new Coord3d(maxX, maxY, minZ),new Color(0, 0, 160));
		
		redpoints[0] = new Point(new Coord3d(minX, minY, minZ),points2.getColor());
		redpoints[1] = new Point(new Coord3d(minX, minY, maxZ),points2.getColor());
		redpoints[2] = new Point(new Coord3d(minX, maxY, maxZ),points2.getColor());
		redpoints[3] = new Point(new Coord3d(minX, maxY, minZ),points2.getColor());
		redpoints[4] = new Point(new Coord3d(maxX, minY, minZ),points2.getColor());
		redpoints[5] = new Point(new Coord3d(maxX, minY, maxZ),points2.getColor());
		redpoints[6] = new Point(new Coord3d(maxX, maxY, maxZ),points2.getColor());
		redpoints[7] = new Point(new Coord3d(maxX, maxY, minZ),points2.getColor());
		
		float coeffY = maxX/(maxY-minY);
		float coeffZ = maxX/(maxZ-minZ);
		
		
		for (int i = 0; i < points2.getSize(); i++) {
			points2.setPoint(i, new Coord3d(points2.getPoint(i).x, points2.getPoint(i).y*coeffY, points2.getPoint(i).z*coeffZ));
		}
		
		for (int i = 0; i < redpoints.length; i++) {
			redpoints[i] = new Point(new Coord3d(redpoints[i].xyz.x, redpoints[i].xyz.y*coeffY, redpoints[i].xyz.z*coeffZ), redpoints[i].rgb); 
		}
		
		center = new Point(new Coord3d((redpoints[4].xyz.x+redpoints[0].xyz.x)/2+zX, 
									   (redpoints[3].xyz.y+redpoints[0].xyz.y)/2+zY, 
									   (redpoints[2].xyz.z+redpoints[0].xyz.z)/2+zZ),Color.BLACK);
		
		center.setWidth(5f);
		float R = 0f;
		float d;
		for (int i = 0; i < points2.getSize(); i++) {
			d = dist(center,points2.getPoint(i));
			if (d>R) {
				R = d; 
			}
		}
		radX = R;
		radY = R/coeffY;
		radZ = R/coeffZ;
		
		
		
		for (int i = 0; i < points8.length; i++) {
			points8[i].xyz.x=rotateOZf(-beta, points8[i].xyz).x;
			points8[i].xyz.y=rotateOZf(-beta, points8[i].xyz).y;
			points8[i].xyz.z=rotateOZf(-beta, points8[i].xyz).z;
			
			points8[i].xyz.x=rotateOYf(-alpha, points8[i].xyz).x;
			points8[i].xyz.y=rotateOYf(-alpha, points8[i].xyz).y;
			points8[i].xyz.z=rotateOYf(-alpha, points8[i].xyz).z;
			
			
		}
		for (int i = 0; i < points8.length; i++) {
			points8[i] = new Point(new Coord3d(points8[i].xyz.x+zX, points8[i].xyz.y+zY, 
					                           points8[i].xyz.z+zZ),points1.getColor());
		}
		
		volume = calcVolume(points8);
		
	}
	
	public float getVolume() {
		return volume;
	}

	public Point[] getRedpoints() {
		return redpoints;
	}

	public void setPoints8(Point[] points8) {
		this.points8 = points8;
	}

	public Coord3d[] getBox(){
		Coord3d[] box = new Coord3d[8];
		for (int i = 0; i < points8.length; i++) {
			box[i] = new Coord3d(points8[i].xyz.x,points8[i].xyz.y,points8[i].xyz.z);
		}
		return box;
	}
	
	
	public float getAlpha() {
		return (float)(-alpha/(Math.PI/180));
	}

	public float getBeta() {
		return (float)(-beta/(Math.PI/180));
	}

	public float getRadX() {
		return radX;
	}

	public float getRadY() {
		return radY;
	}

	public float getRadZ() {
		return radZ;
	}

	public Point getCenter() {
		return center;
	}

	public Points getPoints(){
		return points2;
	}
	
	public static LineStrip minPPD(Point[] pts, Color c){		
		
		Point[] pts8 = new Point[8];
		for (int i = 0; i < pts8.length; i++) {
			pts8[i] = new Point(new Coord3d(pts[i].xyz.x, pts[i].xyz.y, pts[i].xyz.z),c);			
		}		
		
		LineStrip ppd = new LineStrip();			
		ppd.add(pts8[0]);
		ppd.add(pts8[1]);
		ppd.add(pts8[2]);
		ppd.add(pts8[3]);
		ppd.add(pts8[0]);
		ppd.add(pts8[4]);
		ppd.add(pts8[5]);
		ppd.add(pts8[6]);
		ppd.add(pts8[7]);
		ppd.add(pts8[4]);
		ppd.add(pts8[0]);
		ppd.add(pts8[3]);
		ppd.add(pts8[7]);
		ppd.add(pts8[6]);
		ppd.add(pts8[2]);
		ppd.add(pts8[1]);
		ppd.add(pts8[5]);
		ppd.setWidth(1f);				
		return ppd;
	}
	
	public static float calcVolume(Point[] pts){
		float p;
		Coord3d v1 = new Coord3d(pts[1].xyz.x-pts[0].xyz.x, 
				                 pts[1].xyz.y-pts[0].xyz.y, 
				                 pts[1].xyz.z-pts[0].xyz.z);
		Coord3d v2 = new Coord3d(pts[3].xyz.x-pts[0].xyz.x, 
                                 pts[3].xyz.y-pts[0].xyz.y, 
                                 pts[3].xyz.z-pts[0].xyz.z);
		Coord3d v3 = new Coord3d(pts[4].xyz.x-pts[0].xyz.x, 
                				 pts[4].xyz.y-pts[0].xyz.y, 
                				 pts[4].xyz.z-pts[0].xyz.z);
		
		p=Math.abs(v1.x*(v2.y*v3.z-v2.z*v3.y)-
				   v2.x*(v1.y*v3.z-v1.z*v3.y)+
				   v3.x*(v1.y*v2.z-v1.z*v2.y));
		
		return p;
	}
	
	private Coord3d rotateOY(double phi, Coord3d p){
		double c = Math.cos(phi);
		double s = Math.sin(phi);
		return new Coord3d(p.x*c+p.z*s, p.y, -p.x*s+p.z*c);
	}
	
	private Coord3d rotateOZ(double phi, Coord3d p){
		double c = Math.cos(phi);
		double s = Math.sin(phi);
		return new Coord3d(p.x*c+p.y*s, -p.x*s+p.y*c, p.z);
	}
	
	private Coord3d rotateOYf(float phi, Coord3d p){
		float c = (float)Math.cos(phi);
		float s = (float)Math.sin(phi);
		return new Coord3d(p.x*c+p.z*s, p.y, -p.x*s+p.z*c);
	}
	
	private Coord3d rotateOZf(float phi, Coord3d p){
		float c = (float)Math.cos(phi);
		float s = (float)Math.sin(phi);
		return new Coord3d(p.x*c+p.y*s, -p.x*s+p.y*c, p.z);
	}
	
	private float dist(Point a, Coord3d b){
		return (float)Math.sqrt((a.xyz.x - b.x)*(a.xyz.x - b.x)+
								(a.xyz.y - b.y)*(a.xyz.y - b.y)+
								(a.xyz.z - b.z)*(a.xyz.z - b.z));
	}
}
