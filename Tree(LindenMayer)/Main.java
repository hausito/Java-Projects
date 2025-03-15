package Proiectul1;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        RuleSet rs = new RuleSet();
        rs.addRule('f', "ff-[-f+f+f]+[+f-f-f]");


        OL ol = new OL("f", rs);
        String result = ol.derive(5);  

        JFrame frame = new JFrame("L-System Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(new Copac(result));
        frame.setVisible(true);
    }
}
