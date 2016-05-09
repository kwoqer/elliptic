package elliptic;

import java.util.Calendar;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.LineStrip;


public class Points {

	private Coord3d[] points;
	private Color[] colors;
	private int size;
	private Coord3d center;
	private Scatter scatter;
	private Color color;
	private Random random;
	private float rad_x;	
	private float rad_y;
	private float rad_z;
	
	private Point a,b; 		
	private int i_a, i_b;
	
	@SuppressWarnings("deprecation")
	public Points(int s) {
		this(s, Color.BLUE);		
	}
	
	public Points(int s, Color c){		
		this.size = s;
		this.points = new Coord3d[size];
		this.center = new Coord3d();				
		this.random = new Random();
		this.random.setSeed(Calendar.getInstance().getTime().getSeconds()*Calendar.getInstance().getTime().getMinutes());
		this.colors = new Color[size];
		this.color = c;
		i_a = 0;
		i_b = 0;
	}
	
	public Points(Points ps){
		this.size = ps.getSize();
		this.points = new Coord3d[size];
		this.center = new Coord3d();				
		this.colors = new Color[size];
		this.color = new Color(ps.getColor().r,ps.getColor().g,ps.getColor().b,ps.getColor().a);
		i_a = 0;
		i_b = 0;
		for (int i = 0; i < this.size; i++) {
			Coord3d c = new Coord3d(ps.getPoint(i).x,ps.getPoint(i).y,ps.getPoint(i).z);			
			this.points[i] = c;			 
			this.colors[i] = color;
		}
		this.calcParams();
		scatter = new Scatter(points, colors);
		scatter.setWidth(5f);				
		
	}
	
	public Points(Points ps, Color col){
		this.size = ps.getSize();
		this.points = new Coord3d[size];
		this.center = new Coord3d();
		this.colors = new Color[size];
		this.color = col;
		i_a = 0;
		i_b = 0;
		for (int i = 0; i < this.size; i++) {
			Coord3d c = new Coord3d(ps.getPoint(i).x,ps.getPoint(i).y,ps.getPoint(i).z);			
			this.points[i] = c;			 
			this.colors[i] = color;
		}
		this.calcParams();
		scatter = new Scatter(points, colors);
		scatter.setWidth(5f);				
		
	}
	
	

	public Point getA() {
		return a;
	}

	public Point getB() {
		return b;
	}
	
	public LineStrip getDiam() {
		LineStrip diam = new LineStrip();
		diam.add(a);
		diam.add(b);
		diam.setWidth(1f);		
		return diam;
	}
		

	public Scatter getScatter() {
		return scatter;
	}

	public Color[] getColors() {
		return colors;
	}

	public int getSize() {
		return size;
	}

	public void reset(int s) {
		this.size = s;
		this.generate();
	}

	public Coord3d getCenter() {
		return center;
	}
	

	public Coord3d[] getPoints() {
		return points;
	}

	public float getRad_x() {
		return rad_x;
	}

	public float getRad_y() {
		return rad_y;
	}

	public float getRad_z() {
		return rad_z;
	}
	
	public Color getColor(){
		return color;
	}

	public void generate(){		
					
		Coord3d point;
		float x,y,z;		
		
		for(int i=0; i<this.size; i++){			
			x = (float)random.nextGaussian();				
			y = (float)random.nextGaussian();			
			z = (float)random.nextGaussian();	
			
			point = new Coord3d(x, y, z);
			points[i] = point;						
			colors[i] = color;//new Color(x, y, z, a);
			
			
						
			
		}
		calcParams();
		
		
		
		scatter = new Scatter(points, colors);
		scatter.setWidth(4f);
	}
	
	public Coord3d getPoint(int i){
		return this.points[i];
	}
	
	public void setPoint(int i,Coord3d c){
		points[i] = new Coord3d(c.x, c.y, c.z);
	}
	
	public void calcParams(){
		float min_x=0, min_y=0, min_z=0, max_x=0, max_y=0, max_z=0;
		float x,y,z;	
		Coord3d point;
		a = new Point(points[0],color);
		b = new Point(points[0],color);
		for(int i=0; i<this.size; i++){
			x = points[i].x;
			y = points[i].y;
			z = points[i].z;
			point = new Coord3d(x, y, z);
			if (min_x>x) {
				min_x = x;
				a = new Point(point,color);
				i_a = i;
			}
			if (min_y>y) min_y = y;
			if (min_z>z) min_z = z;
			if (max_x<x) {
				max_x = x;
				b = new Point(point,color);
				i_b = i;
			}
			if (max_y<y) max_y = y;
			if (max_z<z) max_z = z;
		}
		this.center.x = (min_x+max_x)/2f;
		this.center.y = (min_y+max_y)/2f;
		this.center.z = (min_z+max_z)/2f;
		rad_x = max_x-center.x;
		rad_y = max_y-center.y;
		rad_z = max_z-center.z;
	}

	public int getI_a() {
		return i_a;
	}

	public int getI_b() {
		return i_b;
	}
	
	/*private void calcCenter(){
		float min_x=0, min_y=0, min_z=0, max_x=0, max_y=0, max_z=0;
		for(int i=0; i<this.size; i++){	
			if (min_x>points[i].x) min_x = points[i].x;
			if (min_y>points[i].y) min_y = points[i].y;
			if (min_z>points[i].z) min_z = points[i].z;
			if (max_x<points[i].x) max_x = points[i].x;
			if (max_x<points[i].y) max_x = points[i].y;
			if (max_x<points[i].z) max_x = points[i].z;
		}
		center.x = (min_x+max_x)/2f;
		center.y = (min_y+max_y)/2f;
		center.z = (min_z+max_z)/2f;
		rad_x = max_x-center.x;
		rad_y = max_y-center.y;
		rad_z = max_z-center.z;
	}*/
	
	public void writeToFile(){
		File file1 = new File("_points.txt");
		File file2 = new File("__qconvex_input_file");
		File file3 = new File("__qconvex_output_file");
		File file4 = new File("_vivien.txt");
		boolean exists1 = file1.exists();
		if (exists1) {
			file1.delete();	
		}
		boolean exists2 = file2.exists();
		if (exists2) {
			file2.delete();	
		}
		boolean exists3 = file3.exists();
		if (exists3) {
			file3.delete();	
		}
		boolean exists4 = file4.exists();
		if (exists4) {
			file4.delete();	
		}
		try(FileWriter writer = new FileWriter("_points.txt", false))
        {
           writer.write(Integer.toString(this.size));
           writer.write('\n');
           for (int i = 0; i < size; i++) {
        	   writer.write(Float.toString(points[i].x));
        	   writer.write('\t');
        	   writer.write(Float.toString(points[i].y));
        	   writer.write('\t');
        	   writer.write(Float.toString(points[i].z));
        	   writer.write('\n');
           }
           writer.close();
        }
        catch(IOException ex){
             
            System.out.println(ex.getMessage());
        } 
		
	}
}
