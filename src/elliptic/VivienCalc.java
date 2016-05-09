package elliptic;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;

public class VivienCalc {
	
	private Point[] points8;
	private float alpha = 0f, beta = 0f;
	private float volume;
	
	public float getVolume() {
		return volume;
	}

	public VivienCalc(){
		points8 = new Point[8];
						
		
		// call minPPD
		try {
			Runtime.getRuntime().exec("./minPPD");
			boolean res_exist = false;
			while (!res_exist) {
				File resfile = new File("./_vivien.txt");
				if (resfile.exists()) {
					res_exist = true;
				}
			}
			BufferedReader in = new BufferedReader(new FileReader("./_vivien.txt"));			
			String line;
			String numbers[];
			for (int i = 0; i < points8.length; i++) {
			
							
				line = in.readLine();
				numbers = line.split("\\s+");
				
				points8[i] = new Point(new Coord3d(Float.parseFloat(numbers[0]), 
												   Float.parseFloat(numbers[1]), 
												   Float.parseFloat(numbers[2])),Color.GREEN);
				
			}
			volume = PetuninCalc.calcVolume(points8);
			// calc angles
			float zX = points8[0].xyz.x;
			float zY = points8[0].xyz.y;
			float zZ = points8[0].xyz.z;
			
			Point M = new Point(new Coord3d(points8[3].xyz.x -zX,points8[3].xyz.y -zY,points8[3].xyz.z -zZ));
			double l = Math.sqrt(M.xyz.x*M.xyz.x+M.xyz.y*M.xyz.y+M.xyz.z*M.xyz.z);
			double l2 = Math.sqrt(M.xyz.x*M.xyz.x+M.xyz.y*M.xyz.y);
			if (l!=0) {
				alpha = (float)Math.asin(M.xyz.z/l);
			}
			if (l2!=0) {
				beta = (float)Math.asin(M.xyz.y/l2);
			}
			
			in.close();
			
		} catch (IOException ex) {
				System.out.println(ex.getMessage());
		}
		
	}

	public float getAlpha() {
		//System.out.println(alpha);
		return (float)(alpha/(Math.PI/180));				
	}

	public float getBeta() {
		
		return (float)(beta/(Math.PI/180));
	}

	public Point[] getPoints8() {
		return points8;
	}
	
	public Coord3d[] getBox(){
		Coord3d[] box = new Coord3d[8];
		for (int i = 0; i < points8.length; i++) {
			box[i] = new Coord3d(points8[i].xyz.x,points8[i].xyz.y,points8[i].xyz.z);
		}
		return box;
	}
	public Point getCenter(){
		Coord3d p1 = points8[0].xyz;    	
    	Coord3d p3 = points8[2].xyz;    	
    	Coord3d p5 = points8[4].xyz;
    	Coord3d p7 = points8[6].xyz;
    	    	
    	// calculate of diagonales intersect - center
    	float x0 = p1.x; float y0 = p1.y; float z0 = p1.z;
    	float k = p7.x - p1.x; float q = p7.y - p1.y; float r = p7.z - p1.z;
    	
    	float x1 = p3.x; float y1 = p3.y; float z1 = p3.z;
    	float k1 = p5.x - p3.x; float q1 = p5.y - p3.y; float r1 = p5.z - p3.z;
    	    	    	
    	float xi = (x0 * q * k1 - x1 * q1 * k - y0 * k * k1 + y1 * k * k1) /
                (q * k1 - q1 * k);
        float yi = (y0 * k * q1 - y1 * k1 * q - x0 * q * q1 + x1 * q * q1) /
                (k * q1 - k1 * q);
        float zi = (z0 * q * r1 - z1 * q1 * r - y0 * r * r1 + y1 * r * r1) /
                (q * r1 - q1 * r);
    	return new Point (new Coord3d(xi, yi, zi));
	}
	public Coord3d getRadiuses(){
		Coord3d p1 = points8[0].xyz;
		Coord3d p2 = points8[1].xyz;    	
    	Coord3d p4 = points8[3].xyz;
    	Coord3d p5 = points8[4].xyz;    	
		// calculate radiuses
		float x2R = (float)Math.sqrt((p5.x-p1.x)*(p5.x-p1.x)+(p5.y-p1.y)*(p5.y-p1.y)+(p5.z-p1.z)*(p5.z-p1.z));
		float y2R = (float)Math.sqrt((p4.x-p1.x)*(p4.x-p1.x)+(p4.y-p1.y)*(p4.y-p1.y)+(p4.z-p1.z)*(p4.z-p1.z));    	
		float z2R = (float)Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y)+(p2.z-p1.z)*(p2.z-p1.z));
	
	
		float h,w,a,b, xR, yR, zR;
		h = x2R; w = y2R;
		a = (float)Math.sqrt(h*h+w*w)/2f+0.5f;
		b = (float)Math.sqrt((a*a*(h/2f)*(h/2f))/(a*a-(w/2f)*(w/2f)));
		if (x2R > y2R) {
			xR = a; yR = b;		
		} else {
			xR = b; yR = a;
		}
		w = z2R;
		a = (float)Math.sqrt(h*h+w*w)/2f+0.5f;
		b = (float)Math.sqrt((a*a*(h/2f)*(h/2f))/(a*a-(w/2f)*(w/2f)));
		if (x2R > z2R) {
			zR = b;		
		} else {
			zR = a;
		}
        return new Coord3d(xR*1.25f, yR*1.25f, zR*1.25f);	
		
	}	
}
