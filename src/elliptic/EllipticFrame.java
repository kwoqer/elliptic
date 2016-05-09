package elliptic;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Button;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Label;
import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;

public class EllipticFrame extends java.awt.Frame implements IFrame {
	
	// public constructor for easier construction by reflexion
	public EllipticFrame() {	
	}
	
	public EllipticFrame(Chart chart, Rectangle bounds, String title) {
		initialize(chart, bounds, title);
	}

	public EllipticFrame(Chart chart, Rectangle bounds, String title, String message) {
		initialize(chart, bounds, title, message);
	}
	
	@Override
	public void initialize(Chart chart, Rectangle bounds, String title) {
		initialize(chart, bounds, title, "[Awt]");
	}
	
	@Override
    public void initialize(Chart chart, Rectangle bounds, String title, String message) {
		this.chart = chart;
		if(message!=null){
	        this.setTitle(title + message);		    
		}
		else{
            this.setTitle(title);         
        }
		Panel control_panel = new Panel();
		TextField quantity = new TextField("50");
		Button button1 = new Button("Push me!");
		Label label1 = new Label("Quantity");
		control_panel.add(label1);
		control_panel.add(quantity);
		control_panel.add(button1);
		
		
		this.add(control_panel);
		configureControllers(chart, title, true, false);
		chart.render();
		
		
		this.add((java.awt.Component) chart.getCanvas());
		this.pack();
		this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		this.add(control_panel);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent e) {
				EllipticFrame.this.remove((java.awt.Component) EllipticFrame.this.chart
						.getCanvas());
				EllipticFrame.this.chart.dispose();
				EllipticFrame.this.chart = null;
				EllipticFrame.this.dispose();
			}
		});
	}
	
	public ICameraMouseController configureControllers(final Chart chart, final String title, boolean allowSlaveThreadOnDoubleClick, boolean startThreadImmediatly) {
        chart.addKeyController();
        chart.addScreenshotKeyController();
        return chart.addMouseController();
    }

	private Chart chart;
	private static final long serialVersionUID = 1L;


}