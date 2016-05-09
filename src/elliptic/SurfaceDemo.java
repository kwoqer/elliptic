package elliptic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

public class SurfaceDemo extends AbstractAnalysis {
	
	public static void main(String[] args) throws Exception {
		AnalysisLauncher.open(new SurfaceDemo());
	}
	
	@Override
	public void init() {
		
		int size = 50;
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
			a = 0.25f;
			colors[i] = Color.BLUE;//new Color(x, y, z, a);
		}
		Scatter scatter = new Scatter(points, colors);
		scatter.setWidth(5.0f);
		//scatter.setColor(Color.BLACK);
		
		Ellipsoid el = new Ellipsoid(new Coord3d(), 5.1f, 2.47f, 1.29f,Color.GRAY,0f,0f);
		//el.setWireframeDisplayed(false);
		// Create a chart
		chart = AWTChartComponentFactory.chart(Quality.Nicest, getCanvasType());
		//chart.getScene().getGraph().add(surface1);
		//chart.getScene().getGraph().add(surface2);
		
		chart.getView().setBoundManual(new BoundingBox3d(-5.0f, 5.0f, -5.0f, 5.0f, -5.0f, 5.0f));
		chart.getScene().add(scatter);
		chart.getScene().add(el);
		
		el.setDisplayed(false);
		el.setDisplayed(true);
		
		
	}
}