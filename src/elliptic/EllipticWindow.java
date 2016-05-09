package elliptic;

import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class EllipticWindow extends JFrame {

	private JPanel contentPane;
    private JLabel lblqnt, lblV1, lblV2;
	private JTextField textField;
	private JButton btnGen, btnPetunin, btnVivien, btnExit;
	private JToggleButton tglbtnOn1, tglbtnOn2, tglbtnOn3, tglbtnOn4, tglbtnOn5;
	
	private Points points;
	private Chart chart;
	private PetuninCalc petunin;
	private VivienCalc vivien;
	private String resultFileName;
	private FileWriter resultFile;
	
	private int quantity;
	private boolean isChartCreated;
	private boolean isPointsGenerated;
	
	private AbstractDrawable scatter, petuninBox, petuninEllipsoid, vivienBox, vivienEllipsoid;
	private org.jzy3d.colors.Color petuninColor;
	private org.jzy3d.colors.Color vivienColor;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EllipticWindow frame = new EllipticWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EllipticWindow() {
		isChartCreated = false;
		isPointsGenerated = false;
		scatter = null;
		petuninBox = null;
		petuninEllipsoid = null;
		vivienBox = null;
		vivienEllipsoid = null;
		resultFileName = "_report.txt";
		try
        {
			resultFile = new FileWriter(resultFileName, false);
        }
        catch(IOException ex){
             
            System.out.println(ex.getMessage());
        } 
		
		petuninColor = new org.jzy3d.colors.Color(204,229,255);
		vivienColor = new org.jzy3d.colors.Color(255, 204, 204);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 520, 300);
		this.setTitle("Ellipsoid");
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		lblqnt = new JLabel("Количество точек:");
		lblqnt.setBounds(41, 34, 180, 15);
		contentPane.add(lblqnt);
		
		textField = new JTextField("50");
		textField.setBounds(196, 32, 114, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		lblV1 = new JLabel("V = ");
		lblV1.setBounds(239, 108, 120, 15);
		lblV1.setForeground(Color.RED);
		contentPane.add(lblV1);
		
		btnPetunin = new JButton("Алгоритм Петунина");
		btnPetunin.setBounds(41, 103, 180, 25);
		btnPetunin.setEnabled(false);
		btnPetunin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {																				
				petunin = new PetuninCalc(points);
				String v1 = "V = ".concat(String.format("%.3f",petunin.getVolume()));
				lblV1.setText(v1);
				tglbtnOn2.setEnabled(true);
				tglbtnOn4.setEnabled(true);
				try{				
					resultFile.write("Petunin alghoritm");
					resultFile.write('\n');
					resultFile.write("Box coordinates:");
					resultFile.write('\n');
						for (int i = 0; i < 8; i++) {
							resultFile.write(Float.toString(petunin.getBox()[i].x));
							resultFile.write('\t');
							resultFile.write(Float.toString(petunin.getBox()[i].y));
							resultFile.write('\t');
							resultFile.write(Float.toString(petunin.getBox()[i].z));
							resultFile.write('\n');
						};
					resultFile.write("Box volume: ");
					resultFile.write(Float.toString(petunin.getVolume()));
					resultFile.write('\n');	
					resultFile.write("Ellipsoid center:");
					resultFile.write('\n');	
					resultFile.write(Float.toString(petunin.getCenter().xyz.x));
					resultFile.write('\t');
					resultFile.write(Float.toString(petunin.getCenter().xyz.y));
					resultFile.write('\t');
					resultFile.write(Float.toString(petunin.getCenter().xyz.z));
					resultFile.write('\n');
					resultFile.write("Ellipsoid radiuses:");
					resultFile.write('\n');
					resultFile.write(Float.toString(petunin.getRadX()));
					resultFile.write('\t');
					resultFile.write(Float.toString(petunin.getRadY()));
					resultFile.write('\t');
					resultFile.write(Float.toString(petunin.getRadZ()));
					resultFile.write('\n');
				}		
				catch(IOException ex){		             
		            System.out.println(ex.getMessage());
		        }
			}}
		);
		
		lblV2 = new JLabel("V = ");
		lblV2.setBounds(240, 145, 120, 15);
		lblV2.setForeground(Color.RED);
		contentPane.add(lblV2);
		
		btnVivien = new JButton("Vivien-Wicker");
		btnVivien.setBounds(41, 140, 180, 25);
		btnVivien.setEnabled(false);
		btnVivien.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {																				
				vivien = new VivienCalc();
				String v2 = "V = ".concat(String.format("%.3f",vivien.getVolume()));
				lblV2.setText(v2);
				tglbtnOn3.setEnabled(true);
				tglbtnOn5.setEnabled(true);
				try{				
					resultFile.write("Vivien-Wicker alghoritm");
					resultFile.write('\n');
					resultFile.write("Box coordinates:");
					resultFile.write('\n');
						for (int i = 0; i < 8; i++) {
							resultFile.write(Float.toString(vivien.getBox()[i].x));
							resultFile.write('\t');
							resultFile.write(Float.toString(vivien.getBox()[i].y));
							resultFile.write('\t');
							resultFile.write(Float.toString(vivien.getBox()[i].z));
							resultFile.write('\n');
						};
					resultFile.write("Box volume: ");
					resultFile.write(Float.toString(vivien.getVolume()));
					resultFile.write('\n');	
					resultFile.write("Ellipsoid center:");
					resultFile.write('\n');	
					resultFile.write(Float.toString(vivien.getCenter().xyz.x));
					resultFile.write('\t');
					resultFile.write(Float.toString(vivien.getCenter().xyz.y));
					resultFile.write('\t');
					resultFile.write(Float.toString(vivien.getCenter().xyz.z));
					resultFile.write('\n');
					resultFile.write("Ellipsoid radiuses:");
					resultFile.write('\n');
					resultFile.write(Float.toString(vivien.getRadiuses().x));
					resultFile.write('\t');
					resultFile.write(Float.toString(vivien.getRadiuses().y));
					resultFile.write('\t');
					resultFile.write(Float.toString(vivien.getRadiuses().z));
					resultFile.write('\n');
				}		
				catch(IOException ex){		             
		            System.out.println(ex.getMessage());
		        }
			}}
		);
		
		btnGen = new JButton("Генерация");
		btnGen.setBounds(193, 61, 117, 25);
		btnGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quantity = Integer.parseInt(textField.getText());
				points = new Points(quantity);																		
				points.generate();
				points.writeToFile();
				tglbtnOn1.setEnabled(true);
				btnPetunin.setEnabled(true);
				btnVivien.setEnabled(true);
				btnGen.setEnabled(false);	
				try{				
					resultFile.write(Integer.toString(points.getSize()));
					resultFile.write('\n');
						for (int i = 0; i < points.getSize(); i++) {
							resultFile.write(Float.toString(points.getPoint(i).x));
							resultFile.write('\t');
							resultFile.write(Float.toString(points.getPoint(i).y));
							resultFile.write('\t');
							resultFile.write(Float.toString(points.getPoint(i).z));
							resultFile.write('\n');
						};
				}		
				catch(IOException ex){		             
		            System.out.println(ex.getMessage());
		        } 			
		         
		          
			}}
		);
		contentPane.add(btnGen);
				
		contentPane.add(btnPetunin);
						
		contentPane.add(btnVivien);
		
		
		
		tglbtnOn1 = new JToggleButton("Вкл");
		tglbtnOn1.setBounds(346, 61, 78, 25);
		tglbtnOn1.setEnabled(false);
		tglbtnOn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {								
				if (!isChartCreated) {
					isChartCreated = true;
					
					chart = AWTChartComponentFactory.chart(Quality.Advanced,"awt");
					chart.getView().setBoundManual(new BoundingBox3d(-7.0f, 7.0f, -7.0f, 7.0f, -7.0f, 7.0f));
					Rectangle rectangle = new Rectangle(100, 100, 800, 800);
					ChartLauncher.openChart(chart, rectangle, "3D отображение");
				}				
				if (scatter==null) {
					scatter = points.getScatter();
					chart.getScene().add(points.getScatter());
				}
								
				//chart.getScene().add(points.getDiam());
				
				
				if (tglbtnOn1.isSelected()) {
					tglbtnOn1.setText("Выкл");
					scatter.setDisplayed(true);
					//points.getDiam().setDisplayed(true);
				} else {
					tglbtnOn1.setText("Вкл");
					scatter.setDisplayed(false);
					//points.getDiam().setDisplayed(false);
				}
			}}
		);
		contentPane.add(tglbtnOn1);
		
		tglbtnOn2 = new JToggleButton("Вкл");
		tglbtnOn2.setBounds(346, 103, 78, 25);
		tglbtnOn2.setEnabled(false);
		tglbtnOn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//chart.getScene().add(petunin.getPoints().getScatter());
				//chart.getScene().add(petunin.getDiam());
				/*
				chart.getScene().add(PetuninCalc.minPPD(petunin.getRedpoints()));
				chart.getScene().add(petunin.getCenter());
				*/
				if (petuninBox == null) {
					petuninBox = PetuninCalc.minPPD(petunin.getPoints8(),org.jzy3d.colors.Color.BLUE);
					chart.getScene().add(petuninBox);
				} 
				
				
				/*
				chart.getScene().add(new Ellipsoid(petunin.getCenter().xyz, 
												   petunin.getRadX(), petunin.getRadY(), petunin.getRadZ(), 
												   org.jzy3d.colors.Color.GRAY, 
												   petunin.getAlpha(),petunin.getBeta()));
												   
				
				chart.getScene().add(new Ellipsoid(petunin.getBox(), 						    
						   org.jzy3d.colors.Color.GRAY, 
						   petunin.getAlpha(),petunin.getBeta()));
				*/
				if (tglbtnOn2.isSelected()) {
					tglbtnOn2.setText("Выкл");
					petuninBox.setDisplayed(true);
				} else {
					tglbtnOn2.setText("Вкл");
					petuninBox.setDisplayed(false);
				}				
			}}
		);
		contentPane.add(tglbtnOn2);
		
		tglbtnOn3 = new JToggleButton("Вкл");
		tglbtnOn3.setBounds(346, 140, 78, 25);
		tglbtnOn3.setEnabled(false);
		tglbtnOn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (vivienBox==null) {
					vivienBox = PetuninCalc.minPPD(vivien.getPoints8(),org.jzy3d.colors.Color.RED);
					chart.getScene().add(vivienBox);
				}
				
				if (tglbtnOn3.isSelected()) {
					tglbtnOn3.setText("Выкл");
					vivienBox.setDisplayed(true);
				} else {
					tglbtnOn3.setText("Вкл");
					vivienBox.setDisplayed(false);
				}				
			}}
		);
		contentPane.add(tglbtnOn3);
		
		btnExit = new JButton("Выход");
		btnExit.setBounds(307, 202, 117, 25);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 try{
				    resultFile.close();
				 } catch(IOException ex){		             
			        System.out.println(ex.getMessage());
				 }  
				 setVisible(false);
                 System.exit(0);	
			}}
		);
		
		contentPane.add(btnExit);
		
		tglbtnOn4 = new JToggleButton("Э");
		tglbtnOn4.setBounds(436, 103, 47, 25);
		tglbtnOn4.setEnabled(false);
		contentPane.add(tglbtnOn4);
		tglbtnOn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//chart.getScene().add(PetuninCalc.minPPD(vivien.getPoints8(),org.jzy3d.colors.Color.RED));
				if (petuninEllipsoid == null) {
					/*petuninEllipsoid = new Ellipsoid(petunin.getCenter().xyz, 
							   petunin.getRadX(), petunin.getRadY(), petunin.getRadZ(), 
							   petuninColor, 
							   petunin.getAlpha(),petunin.getBeta());*/
					petuninEllipsoid = new Ellipsoid(petunin.getBox(), 						    
							   petuninColor, 
							   petunin.getAlpha(),petunin.getBeta());
					chart.getScene().add(petuninEllipsoid);		
				}
				if (tglbtnOn4.isSelected()) {
					tglbtnOn4.setText("-");
					petuninEllipsoid.setDisplayed(true);
				} else {
					tglbtnOn4.setText("Э");
					petuninEllipsoid.setDisplayed(false);
				}				
			}}
		);
		
		tglbtnOn5 = new JToggleButton("Э");
		tglbtnOn5.setBounds(436, 140, 47, 25);
		tglbtnOn5.setEnabled(false);
		contentPane.add(tglbtnOn5);
		tglbtnOn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//chart.getScene().add(PetuninCalc.minPPD(vivien.getPoints8(),org.jzy3d.colors.Color.RED));
				if (vivienEllipsoid == null) {
					/*petuninEllipsoid = new Ellipsoid(petunin.getCenter().xyz, 
							   petunin.getRadX(), petunin.getRadY(), petunin.getRadZ(), 
							   petuninColor, 
							   petunin.getAlpha(),petunin.getBeta());*/
					vivienEllipsoid = new Ellipsoid(vivien.getBox(), 						    
							   vivienColor, 
							   vivien.getAlpha(),vivien.getBeta());
					chart.getScene().add(vivienEllipsoid);		
				}
				if (tglbtnOn5.isSelected()) {
					tglbtnOn5.setText("-");
					vivienEllipsoid.setDisplayed(true);
				} else {
					tglbtnOn5.setText("Э");
					vivienEllipsoid.setDisplayed(false);
				}				
			}}
		);
	}
}
