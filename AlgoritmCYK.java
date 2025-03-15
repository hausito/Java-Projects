import java.util.*;

public class AlgoritmCYK {
    private Map<String, List<String>> productii;
    private Set<String> neterminale;
    private String simbolStart;
    private String[][] tabel;
    
    public AlgoritmCYK(Map<String, List<String>> productii, Set<String> neterminale, String simbolStart) {
        this.productii = productii;
        this.neterminale = neterminale;
        this.simbolStart = simbolStart;
    }
    
    public boolean analizeazaCuvant(String cuvant) {
        int n = cuvant.length();
        tabel = new String[n][n];
        
        // Initializare tabel
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                tabel[i][j] = "";
            }
        }
        
        // Pasul 1: Completare pentru simboluri terminale
        for(int i = 0; i < n; i++) {
            String simbol = String.valueOf(cuvant.charAt(i));
            for(String neterminal : neterminale) {
                if(productii.containsKey(neterminal)) {
                    for(String productie : productii.get(neterminal)) {
                        if(productie.equals(simbol)) {
                            adaugaLaTabel(i, 0, neterminal);
                        }
                    }
                }
            }
        }
        
        // Pasul 2: Completare pentru lungimi mai mari
        for(int j = 1; j < n; j++) {
            for(int i = 0; i < n-j; i++) {
                for(int k = 0; k < j; k++) {
                    for(String A : neterminale) {
                        if(productii.containsKey(A)) {
                            for(String productie : productii.get(A)) {
                                if(productie.length() == 2) {
                                    String B = productie.substring(0,1);
                                    String C = productie.substring(1,2);
                                    
                                    if(tabel[i][k].contains(B) && tabel[i+k+1][j-k-1].contains(C)) {
                                        adaugaLaTabel(i, j, A);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return tabel[0][n-1].contains(simbolStart);
    }
    
    private void adaugaLaTabel(int i, int j, String simbol) {
        if(tabel[i][j].isEmpty()) {
            tabel[i][j] = simbol;
        } else if(!tabel[i][j].contains(simbol)) {
            tabel[i][j] += "," + simbol;
        }
    }
    
    public void afiseazaTabel() {
        System.out.println("Tabelul CYK:");
        int n = tabel.length;
        
        // Afișăm antetul cu numerele coloanelor
        System.out.print("    ");
        for(int j = 1; j <= n; j++) {
            System.out.printf("%-8d", j);
        }
        System.out.println();
        
        // Afișăm liniile tabelului în ordine inversă
        for(int i = 0; i < n; i++) {
            // Afișăm numărul liniei
            System.out.printf("%-3d ", i+1);
            
            // Afișăm celulele pentru linia curentă
            for(int j = 0; j < n-i; j++) {
                // Inversăm i și j pentru a obține oglindirea
                String continut = tabel[j][i].isEmpty() ? "-" : tabel[j][i];
                System.out.printf("%-8s", continut);
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        // Definim gramatica
        Map<String, List<String>> productii = new HashMap<>();
        productii.put("S", Arrays.asList("AB", "BC"));
        productii.put("A", Arrays.asList("BA", "a"));
        productii.put("B", Arrays.asList("CC", "b"));
        productii.put("C", Arrays.asList("AB", "a"));
        
        Set<String> neterminale = new HashSet<>(Arrays.asList("S", "A", "B", "C"));
        
        // Cream obiectul CYK
        AlgoritmCYK cyk = new AlgoritmCYK(productii, neterminale, "S");
        
        // Analizam cuvantul
        String cuvant = "baaba";
        boolean acceptat = cyk.analizeazaCuvant(cuvant);
        
        // Afisam rezultatele
        System.out.println("Gramatica:");
        System.out.println("S → AB | BC");
        System.out.println("A → BA | a");
        System.out.println("B → CC | b");
        System.out.println("C → AB | a");
        System.out.println("\nCuvant de analizat: " + cuvant);
        System.out.println();
        cyk.afiseazaTabel();
        System.out.println("\nCuvantul " + cuvant + " este " + 
                          (acceptat ? "acceptat" : "respins") + 
                          " de gramatica.");
    }
}