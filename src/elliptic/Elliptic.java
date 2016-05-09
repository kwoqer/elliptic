package elliptic;

import java.util.Random;
import java.awt.Frame;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.primitives.Parallelepiped;

public class Elliptic {

	public static void main(String[] args) {
		
		int size = 50;
		Points points = new Points(size);
		points.generate();
		
		Rectangle rectangle = new Rectangle(100, 100, 800, 800);
		Scatter scatter = points.getScatter();
		
		Chart chart = AWTChartComponentFactory.chart(Quality.Advanced,"awt");
		chart.getScene().add(scatter);
		chart.getView().setBoundManual(new BoundingBox3d(-5.0f, 5.0f, -5.0f, 5.0f, -5.0f, 5.0f));
		Ellipsoid el = new Ellipsoid(points.getCenter(), points.getRad_x(), points.getRad_y(), points.getRad_z(),Color.GRAY,0f,0f);
		//Parallelepiped pr = new Parallelepiped(new BoundingBox3d(minx, maxx, miny, maxy, minz, maxz));
		Settings.getInstance().setHardwareAccelerated(true);
		//pr.setColor(Color.YELLOW);
		//pr.setWireframeDisplayed(true);
		//chart.getScene().add(el);
		/*Frame frame = new EllipticFrame(chart,rectangle,"Elliptic","[1]");
		System.out.println("------------------------------------");
		System.out.printf("minx = %f, maxx = %f => %f",minx,maxx,(maxx+minx)/2f);
		System.out.println("------------------------------------");
		System.out.printf("miny = %f, maxy = %f => %f",miny,maxy,(maxy+miny)/2f);
		System.out.println("------------------------------------");
		System.out.printf("minz = %f, maxz = %f => %f",minz,maxz,(maxz+minz)/2f);
		System.out.println("------------------------------------"); */
		ChartLauncher.openChart(chart, rectangle, "test");
	}

}
