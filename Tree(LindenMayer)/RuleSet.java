package Proiectul1;
import java.util.HashMap;

public class RuleSet {
    private HashMap<Character, String> rules = new HashMap<>();

    public void addRule(char left, String right) {
        rules.put(left, right);
    }

    public String get(char c) {
        return rules.getOrDefault(c, String.valueOf(c));      
    }
}
