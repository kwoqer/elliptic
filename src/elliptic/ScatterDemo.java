package elliptic;

import java.util.*;
import java.util.Random;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Parallelepiped;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ScatterDemo extends AbstractAnalysis{

	public static void main(String[] args) throws Exception {
		AnalysisLauncher.open(new ScatterDemo());
	}
	
	public void init(){
		int size = 500;
		float x;
		float y;
		float z;
		float a;
		Coord3d[] points = new Coord3d[size];
		List<Coord3d> lpoints = new ArrayList<Coord3d>();
		
		Color[] colors = new Color[size];
		Random r = new Random();
		r.setSeed(0);
		Coord3d point;
		for(int i=0; i<size; i++){			
			x = (float)r.nextGaussian() - 0.5f;
			y = (float)r.nextGaussian() - 0.5f;
			z = (float)r.nextGaussian() - 0.5f;
			
			point = new Coord3d(x, y, z);
			points[i] = point;
			lpoints.add(point);
			a = 0.25f;
			colors[i] = new Color(x, y, z, a);
		}
		Scatter scatter = new Scatter(points, colors);
		BoundingBox3d box = new BoundingBox3d(lpoints);
		
		chart = AWTChartComponentFactory.chart(Quality.Advanced, "awt");
		//chart.getScene().add(scatter);
		//Ellipsoid el = new Ellipsoid(new Coord3d(), 0.78f, 2.56f, 0.54f, Color.BLUE);
		//chart.getScene().add(el);
	}
}