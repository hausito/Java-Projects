package Proiectul1;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Stack;

public class Copac extends JPanel {
    private String instructions;
    private final double angle = Math.toRadians(25);
    private final int segmentLength = 8;
    private final float initialThickness = 5.0f;
    private final float thicknessDecay = 0.5f;

    private int currentStep = 4;  
    private final int delay = 1; 

    public Copac(String instructions) {
        this.instructions = instructions;
        startAnimation();
    }

    private void startAnimation() {
        Timer timer = new Timer(delay, e -> {
            if (currentStep < instructions.length()) {
                currentStep++;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Stack<AffineTransform> stack = new Stack<>();
        Stack<Float> thicknessStack = new Stack<>();

        g2d.translate(getWidth() / 2, getHeight());
        float thickness = initialThickness;

        for (int i = 0; i < currentStep; i++) {  
            char cmd = instructions.charAt(i);
            switch (cmd) {
                case 'f' -> {
                    g2d.setStroke(new BasicStroke(thickness));
                    g2d.drawLine(0, 0, 0, -segmentLength);
                    g2d.translate(0, -segmentLength);
                }
                case '+' -> g2d.rotate(angle);
                case '-' -> g2d.rotate(-angle);
                case '[' -> {
                    stack.push(g2d.getTransform());
                    thicknessStack.push(thickness);
                    thickness *= thicknessDecay;
                    g2d.setColor(Color.RED);
                    g2d.fillOval(-3, -3, 6, 6);  
                    g2d.setColor(Color.BLACK); 
                }
                case ']' -> {
                    g2d.setTransform(stack.pop());
                    thickness = thicknessStack.pop();
                }
            }
        }
    }
}