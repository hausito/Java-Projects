package Proiectul1;

public class OL {
    private String omega;
    private RuleSet rs;

    public OL(String omega, RuleSet rs) {
        this.omega = omega;
        this.rs = rs;
    }

    public String derive(int nr_steps) {
        String result = omega;

        for (int i = 0; i < nr_steps; i++) {
            StringBuilder newResult = new StringBuilder();

            for (int j = 0; j < result.length(); j++) {
                char c = result.charAt(j);
                newResult.append(rs.get(c));
            }

            result = newResult.toString(); 
        }

        return result;
    }
}
