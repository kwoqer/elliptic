package elliptic;

import java.lang.System;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractWireframeable;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;
import org.jzy3d.plot3d.transform.Transform;

public class Ellipsoid extends AbstractWireframeable implements ISingleColorable {
    private float xRadius;
    private float yRadius;
    private float zRadius;
    private Coord3d center;
    private float alpha = 0f, beta = 0f;
    protected static GLUT glut = new GLUT();
    
    private GLUquadric qobj;
    protected int slices = 20;
    protected int stacks = 20;   
    
    protected Color color = Color.GRAY;
    
    public Ellipsoid(Coord3d center, float xRadius, float yRadius, float zRadius, Color c, float yAngle, float zAngle) {
    	super();
        this.center = center;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.zRadius = zRadius;
        this.color = c;
        this.alpha = yAngle;
        this.beta = zAngle;
        
        //color.a = 0.5f;
        
        this.bbox = new BoundingBox3d(
                center.x - xRadius, center.x + xRadius,
                center.y - yRadius, center.y + yRadius, 
                center.z - zRadius, center.z + zRadius);
        
    }
    
    public Ellipsoid(Coord3d[] box, Color c, float yAngle, float zAngle) {
    	super();
    	    	
    	Coord3d p1 = box[0];
    	Coord3d p2 = box[1];
    	Coord3d p3 = box[2];
    	Coord3d p4 = box[3];
    	Coord3d p5 = box[4];
    	Coord3d p7 = box[6];
    	
    	
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
    	this.center = new Coord3d(xi, yi, zi);
    	
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
    	
    	this.xRadius = xR*1.25f;
    	this.yRadius = yR*1.25f;
    	this.zRadius = zR*1.25f;
    	this.color = c;
        this.alpha = yAngle;
        this.beta = zAngle;
        
        this.bbox = new BoundingBox3d(
                center.x - xRadius, center.x + xRadius,
                center.y - yRadius, center.y + yRadius, 
                center.z - zRadius, center.z + zRadius);
    }
    
    @Override
    public void draw(GL gl, GLU glu, Camera camera) {
    	doTransform(gl, glu, camera);        
        
    	if (gl.isGL2()) {
			
			gl.getGL2().glTranslatef(center.x,center.y,center.z);
			gl.getGL2().glScalef(1.0f, yRadius/xRadius, zRadius/xRadius);
			if (alpha != 0f) {
				gl.getGL2().glRotatef(alpha,0f,1f,0f);
				
			} 
			if (beta != 0f) {
				gl.getGL2().glRotatef(beta,0f,0f,1f);
				
			}

			if(qobj==null)
	            qobj = glu.gluNewQuadric();

		    if(facestatus){
		            gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		            gl.getGL2().glColor4f(color.r, color.g, color.b, color.a);
		            glu.gluSphere(qobj, xRadius, slices, stacks);
		            //glut.glutSolidSphere(radius, slices, stacks);
		        }
		        if(wfstatus){
		            gl.getGL2().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
		            gl.getGL2().glLineWidth(wfwidth);
		            gl.getGL2().glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
		            glu.gluSphere(qobj, xRadius, slices, stacks);
		            //glut.glutSolidSphere(radius, slices, stacks);
		        }
		    
			
			
		} else {
			GLES2CompatUtils.glTranslatef(center.x, center.y,
					center.z);
			if (alpha != 0f) {
				GLES2CompatUtils.glRotatef(alpha,0f,1f,0f);
			} 
			if (beta != 0f) {
				GLES2CompatUtils.glRotatef(beta,0f,0f,1f);
			}
			// Draw
			// if(qobj==null)
			// qobj = glu.gluNewQuadric();

			if (facestatus) {
				GLES2CompatUtils.glPolygonMode(GL2GL3.GL_FRONT_AND_BACK,
						GL2GL3.GL_FILL);
				GLES2CompatUtils.glColor4f(color.r, color.g, color.b,
						color.a);
				// glu.gluSphere(qobj, radius, slices, stacks);
				glut.glutSolidSphere(xRadius, slices, stacks);
			}
			if (wfstatus) {
				GLES2CompatUtils.glPolygonMode(GL.GL_FRONT_AND_BACK,
						GL2GL3.GL_LINE);
				GLES2CompatUtils.glLineWidth(wfwidth);
				GLES2CompatUtils.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b,
						wfcolor.a);
				// glu.gluSphere(qobj, radius, slices, stacks);
				glut.glutSolidSphere(xRadius, slices, stacks);

				// gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
				// gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
			}
			
		}

		doDrawBounds(gl, glu, camera);
    	                                                
    }
    
    @Override
	public void applyGeometryTransform(Transform transform) {
		center.set(transform.compute(center));
		
		updateBounds();
	}
    
    @Override
	public void updateBounds() {
		bbox.reset();
		bbox.add(center.x + xRadius, center.y + yRadius, center.z + zRadius);
		bbox.add(center.x - xRadius, center.y - yRadius, center.z - zRadius);
	}
    
    @Override
    public void setColor(Color color) {
		this.color = color;

		fireDrawableChanged(new DrawableChangedEvent(this,
				DrawableChangedEvent.FIELD_COLOR));
	}

	@Override
    public Color getColor() {
		return color;
	}
	
	public void setCenter(Coord3d position) {
		this.center = position;
		updateBounds();
	}

	public Coord3d getCenter() {
		return center;
	}
}