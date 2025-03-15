import javax.swing.*;
import java.awt.*;

public class AnimatedLines extends JPanel {
    private int step = 0; 

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.RED);
        if (step >= 1) g.drawLine(50, 50, 200, 50); 

        g.setColor(Color.BLUE);
        if (step >= 2) g.drawLine(50, 100, 200, 200); 

        g.setColor(Color.GREEN);
        if (step >= 3) g.drawLine(100, 200, 300, 50); 
    }

    public void startDrawing() {
        new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    step = i;
                    repaint(); 
                    Thread.sleep(1000); 
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Desen Animat");
        AnimatedLines panel = new AnimatedLines();

        frame.add(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        panel.startDrawing(); 
    }
}
