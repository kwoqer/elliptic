package gov.usgs.dismodel.geom.overlays.jzy;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import net.masagroup.jzy3d.colors.Color;
import net.masagroup.jzy3d.maths.BoundingBox3d;
import net.masagroup.jzy3d.maths.Coord3d;
import net.masagroup.jzy3d.plot3d.primitives.AbstractWireframeable;
import net.masagroup.jzy3d.plot3d.rendering.view.Camera;

public class Ellipsoid extends AbstractWireframeable {
    private float xRadius;
    private float yRadius;
    private float zRadius;
    private Coord3d center;
    
    private GLUquadric qobj;
    protected int slices = 15;
    protected int stacks = 15;   
    
    protected Color color = Color.GRAY;;
    
    public Ellipsoid(Coord3d center, float xRadius, float yRadius, float zRadius) {
        this.center = center;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.zRadius = zRadius;
        
        color.a = 0.5f;
        
        this.bbox = new BoundingBox3d(
                center.x - xRadius, center.x + xRadius,
                center.y - yRadius, center.y + yRadius, 
                center.z - zRadius, center.z + zRadius);
        init();
    }
    
    @Override
    public void draw(GL gl, GLU glu, Camera camera) {
        if(transform!=null)
            transform.execute(gl);
        
        gl.glTranslatef(center.x,center.y,center.z);
        gl.glScalef(1.0f, yRadius/xRadius, zRadius/xRadius);
        
        if(qobj==null)
            qobj = glu.gluNewQuadric();
        
        if(facestatus){
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
            gl.glColor4f(color.r, color.g, color.b, color.a);
            glu.gluSphere(qobj, xRadius, slices, stacks);
            //glut.glutSolidSphere(radius, slices, stacks);
        }
        if(wfstatus){
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            gl.glLineWidth(wfwidth);
            gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);
            glu.gluSphere(qobj, xRadius, slices, stacks);
            //glut.glutSolidSphere(radius, slices, stacks);
        }       
    }
}
