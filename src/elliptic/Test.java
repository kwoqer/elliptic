package elliptic;

import javax.swing.*;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;

public class Test {

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        Chart chart = getChart();

        frame.add((javax.swing.JComponent) chart.getCanvas());
        //frame.add()
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setTitle("test");
        frame.setVisible(true);
    }

    public static Chart getChart() {
        // Define a function to plot
        Mapper mapper = new Mapper() {
            public double f(double x, double y) {
                return 10 * Math.sin(x / 10) * Math.cos(y / 20) * x;
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-150, 150);
        int steps = 50;

        // Create the object to represent the function over the given range.
        org.jzy3d.plot3d.primitives.Shape surface = (Shape) Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);
        //surface.setFace(new ColorbarFace(surface));
        surface.setFaceDisplayed(true);
        //surface.setFace2dDisplayed(true); // opens a colorbar on the right part of the display

        // Create a chart
        Chart chart = new Chart();
        chart.getScene().getGraph().add(surface);
        return chart;
    }
}
