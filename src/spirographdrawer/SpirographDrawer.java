
package spirographdrawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;


public class SpirographDrawer extends JFrame {
    
    double radiusHeight = 200;
    double radiusWidth = 50;
    double rotationSpeed = 720;
    boolean axisVisible = true;
    double lineRadius = 300;
    double x, y;
    double theta = Math.PI/2;    // so it starts drawing at the top of the screen
    double alpha = 0;
    double centre = 350;
    ArrayList<PenPoint> points = new ArrayList();
    int sleepTime = 6;
    
    public void setValues() {
        
        Scanner s = new Scanner(System.in);
        
        System.out.println("Enter the radius for the height: ");
        radiusHeight = s.nextInt();
        System.out.println("Enter the radius for the width: ");
        radiusWidth = s.nextInt();
        System.out.println("Enter the speed of rotation: (360|540|720|900|1080|1260|1440)");
        rotationSpeed = s.nextInt();
        System.out.println("Enter the speed of drawing: (5 - 10)");
        sleepTime = s.nextInt();
        System.out.println("Would you like to see the rotation axis? (y/n)");
        if (s.next().equals("n")) {
            axisVisible = false;
        }
        
    }
    
    public void sleep( int numMilliseconds ) {
        try {
            Thread.sleep( numMilliseconds );
        } 
        catch (Exception e) {
        }
    }
    
    public void paint ( Graphics g ) {
        Image img = buffImage();
        g.drawImage(img, 0, 0, this);
    }
    
    public void calcNextPoint() {
        
        // calculates where the current point is (because having different radii for height/width messes up angles)
        // and calculates the predicted spot with the rotation factor
        double deltaX = x - centre;
        double deltaY = centre - y;
        double b = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        double newTheta = Math.atan(deltaY/deltaX);

        if (alpha == 2*Math.PI) {
            theta = 0;
        }

        if (theta > 2*Math.PI) {
            theta = theta % (2*Math.PI);
        }

        if (theta > 3*Math.PI/2) {
            newTheta = 2*Math.PI - Math.abs(newTheta);
        }
        else if (theta > Math.PI) {
            newTheta = Math.PI + Math.abs(newTheta);
        }
        else if (theta > Math.PI/2) {
            newTheta = Math.PI - Math.abs(newTheta);
        }

        double gamma = alpha + newTheta;

        double xC = b*Math.cos(gamma);
        double yC = b*Math.sin(gamma);

        double xN = centre + xC;
        double yN = centre - yC;

        // adds current point to an array of points that are drawn later
        PenPoint temp = new PenPoint(xN,yN);
        points.add(temp);
    }
    
    private Image buffImage () {
        
        BufferedImage bi = new BufferedImage(700,700, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = bi.getGraphics();
        
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 700, 700);
        
        // calculates points on the base ellipse (with no rotation)
        x = centre + radiusWidth*Math.cos(theta);
        y = centre - radiusHeight*Math.sin(theta);
        
        
        if (alpha % 2*Math.PI == 0) {
            PenPoint temp = new PenPoint(x,y);
            points.add(temp);
        }
        // with rotation            
        else {
            calcNextPoint();
        }
        
        // rotation axis
        double lineX1 = centre + lineRadius*Math.cos(alpha);
        double lineX2 = centre - lineRadius*Math.cos(alpha);
        double lineY1 = centre - lineRadius*Math.sin(alpha);
        double lineY2 = centre + lineRadius*Math.sin(alpha);

        if (axisVisible == true) {
            g2.setColor(Color.red);
            g2.drawLine((int)lineX1, (int)lineY1, (int)lineX2, (int)lineY2);
        }
        
        // drawing the points
        g2.setColor(Color.white);
        for (int k = 0; k < points.size(); k++) {
            g2.fillRect((int) points.get(k).xVal, (int) points.get(k).yVal, 2, 2);
        }                    
        return bi;
    }

    public static void main(String[] args) {
        
        SpirographDrawer sd = new SpirographDrawer();
        
        sd.setBackground(Color.BLACK);
        sd.setSize(700, 700);
        sd.setDefaultCloseOperation(EXIT_ON_CLOSE);
        boolean firstPaint = true;
        
        sd.setValues();

        while (sd.alpha < 2*Math.PI ) {
            
            // lets the user set custom values before drawing anything onscreen
            if (firstPaint == true) {
                sd.setVisible(true);
                firstPaint = false;
            }
            else {
                
                sd.theta += Math.PI/180;                // variable for drawing all the points in the ellipse
                sd.alpha += Math.PI/sd.rotationSpeed;   // variable for the degree of rotation

                // checking if the first point and last point are the same
                // if yes, then we've drawn all the points that the user will see
                if (sd.points.size() > 1) {
                    if ((int)sd.points.get(0).xVal == (int)sd.points.get(sd.points.size()-1).xVal && (int)sd.points.get(0).yVal == (int)sd.points.get(sd.points.size()-1).yVal) {
                        sd.alpha = 2*Math.PI;   // ends the while loop
                    }
                }

                sd.repaint();
                sd.sleep(sd.sleepTime);
            }
        }
        
    }
}
