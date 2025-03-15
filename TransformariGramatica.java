import java.util.*;

public class TransformariGramatica {
    static class Gramatica {
        Set<String> N; // neterminale 
        Set<String> T; // terminale
        Map<String, Set<String>> P; // productii
        String S; // simbol start

        public Gramatica(Set<String> N, Set<String> T, Map<String, Set<String>> P, String S) {
            this.N = N;
            this.T = T;
            this.P = P;
            this.S = S;
        }
    }

    // Lema 3.1.1 - Eliminarea neterminalelor neproductive
    public static Gramatica eliminaNeterminaleNeproductive(Gramatica G) {
        Set<String> N_nou = new HashSet<>();
        Set<String> T_nou = new HashSet<>(G.T);
        Map<String, Set<String>> P_nou = new HashMap<>();
        
        // Pasul initial - gasim neterminalele care produc direct terminale
        Set<String> productive = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : G.P.entrySet()) {
            for (String productie : entry.getValue()) {
                if (esteTerminal(productie, G.T)) {
                    productive.add(entry.getKey());
                    break;
                }
            }
        }

        // Iteratie pana cand nu mai gasim neterminale productive noi
        boolean schimbat;
        do {
            schimbat = false;
            for (Map.Entry<String, Set<String>> entry : G.P.entrySet()) {
                if (!productive.contains(entry.getKey())) {
                    for (String productie : entry.getValue()) {
                        if (poateProduceTerminale(productie, productive, G.T)) {
                            productive.add(entry.getKey());
                            schimbat = true;
                            break;
                        }
                    }
                }
            }
        } while (schimbat);

        // Construim noua gramatica
        for (String neterminal : productive) {
            N_nou.add(neterminal);
            Set<String> productiiNoi = new HashSet<>();
            for (String productie : G.P.get(neterminal)) {
                if (poateProduceTerminale(productie, productive, G.T)) {
                    productiiNoi.add(productie);
                }
            }
            if (!productiiNoi.isEmpty()) {
                P_nou.put(neterminal, productiiNoi);
            }
        }

        return new Gramatica(N_nou, T_nou, P_nou, G.S);
    }

    // Lema 3.1.2 - Eliminarea simbolurilor inaccesibile
    public static Gramatica eliminaSimboluriInaccesibile(Gramatica G) {
        Set<String> N_nou = new HashSet<>();
        Set<String> T_nou = new HashSet<>();
        Map<String, Set<String>> P_nou = new HashMap<>();
        
        // Incepem cu simbolul de start
        Set<String> accesibile = new HashSet<>();
        accesibile.add(G.S);
        N_nou.add(G.S);

        // Iteram pana gasim toate simbolurile accesibile
        boolean schimbat;
        do {
            schimbat = false;
            Set<String> nouAccesibile = new HashSet<>();
            
            for (String simbol : accesibile) {
                if (G.P.containsKey(simbol)) {
                    for (String productie : G.P.get(simbol)) {
                        for (char c : productie.toCharArray()) {
                            String sim = String.valueOf(c);
                            if (G.N.contains(sim) && !accesibile.contains(sim)) {
                                nouAccesibile.add(sim);
                                N_nou.add(sim);
                                schimbat = true;
                            } else if (G.T.contains(sim)) {
                                T_nou.add(sim);
                            }
                        }
                    }
                }
            }
            accesibile.addAll(nouAccesibile);
        } while (schimbat);

        // Construim noile productii
        for (String neterminal : N_nou) {
            if (G.P.containsKey(neterminal)) {
                Set<String> productiiNoi = new HashSet<>();
                for (String productie : G.P.get(neterminal)) {
                    if (esteProductieAccesibila(productie, N_nou, T_nou)) {
                        productiiNoi.add(productie);
                    }
                }
                if (!productiiNoi.isEmpty()) {
                    P_nou.put(neterminal, productiiNoi);
                }
            }
        }

        return new Gramatica(N_nou, T_nou, P_nou, G.S);
    }

    // Teorema 3.1.3 - Eliminarea productiilor unitare
    public static Gramatica eliminaProductiiUnitare(Gramatica G) {
        // Construim graful de dependenta pentru productii unitare
        Map<String, Set<String>> grafDependenta = new HashMap<>();
        for (String neterminal : G.N) {
            grafDependenta.put(neterminal, new HashSet<>());
        }

        // Adaugam arcele in graf
        for (Map.Entry<String, Set<String>> entry : G.P.entrySet()) {
            String de_la = entry.getKey();
            for (String catre : entry.getValue()) {
                if (catre.length() == 1 && G.N.contains(catre)) {
                    grafDependenta.get(de_la).add(catre);
                }
            }
        }

        // Calculam inchiderea tranzitiva
        Map<String, Set<String>> inchidere = calculeazaInchidereaTranzitiva(grafDependenta);

        // Construim noua gramatica fara productii unitare
        Map<String, Set<String>> P_nou = new HashMap<>();
        
        for (String neterminal : G.N) {
            Set<String> productiiNoi = new HashSet<>();
            
            // Adaugam productiile non-unitare directe
            if (G.P.containsKey(neterminal)) {
                for (String productie : G.P.get(neterminal)) {
                    if (productie.length() != 1 || !G.N.contains(productie)) {
                        productiiNoi.add(productie);
                    }
                }
            }

            // Adaugam productiile non-unitare din inchiderea tranzitiva
            if (inchidere.containsKey(neterminal)) {
                for (String accesibil : inchidere.get(neterminal)) {
                    if (G.P.containsKey(accesibil)) {
                        for (String productie : G.P.get(accesibil)) {
                            if (productie.length() != 1 || !G.N.contains(productie)) {
                                productiiNoi.add(productie);
                            }
                        }
                    }
                }
            }

            if (!productiiNoi.isEmpty()) {
                P_nou.put(neterminal, productiiNoi);
            }
        }

        return new Gramatica(G.N, G.T, P_nou, G.S);
    }

    // Metode ajutatoare
    private static boolean esteTerminal(String str, Set<String> terminale) {
        for (char c : str.toCharArray()) {
            if (!terminale.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }

    private static boolean poateProduceTerminale(String productie, Set<String> productive, Set<String> terminale) {
        for (char c : productie.toCharArray()) {
            String simbol = String.valueOf(c);
            if (!terminale.contains(simbol) && !productive.contains(simbol)) {
                return false;
            }
        }
        return true;
    }

    private static boolean esteProductieAccesibila(String productie, Set<String> neterminaleAccesibile, Set<String> terminaleAccesibile) {
        for (char c : productie.toCharArray()) {
            String simbol = String.valueOf(c);
            if (!neterminaleAccesibile.contains(simbol) && !terminaleAccesibile.contains(simbol)) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, Set<String>> calculeazaInchidereaTranzitiva(Map<String, Set<String>> graf) {
        Map<String, Set<String>> inchidere = new HashMap<>();
        
        // Initializam inchiderea cu conexiunile directe
        for (Map.Entry<String, Set<String>> entry : graf.entrySet()) {
            inchidere.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        // Calculam inchiderea tranzitiva
        boolean schimbat;
        do {
            schimbat = false;
            for (String k : graf.keySet()) {
                for (String i : graf.keySet()) {
                    if (inchidere.get(i).contains(k)) {
                        for (String j : inchidere.get(k)) {
                            if (inchidere.get(i).add(j)) {
                                schimbat = true;
                            }
                        }
                    }
                }
            }
        } while (schimbat);

        return inchidere;
    }

    // Metoda pentru testare
    public static void main(String[] args) {
        // Exemplu de utilizare
        Set<String> N = new HashSet<>(Arrays.asList("S", "A", "B", "C"));
        Set<String> T = new HashSet<>(Arrays.asList("a"));
        Map<String, Set<String>> P = new HashMap<>();
        P.put("S", new HashSet<>(Arrays.asList("A")));
        P.put("A", new HashSet<>(Arrays.asList("B", "a")));
        P.put("B", new HashSet<>(Arrays.asList("C")));
        P.put("C", new HashSet<>(Arrays.asList("S")));

        Gramatica G = new Gramatica(N, T, P, "S");

        // Testam lemele
        Gramatica G1 = eliminaNeterminaleNeproductive(G);
        Gramatica G2 = eliminaSimboluriInaccesibile(G1);
        Gramatica G3 = eliminaProductiiUnitare(G2);

        // Afisam rezultatele
        System.out.println("Gramatica originala:");
        afiseazaGramatica(G);
        
        System.out.println("\nDupa eliminarea neterminalelor neproductive:");
        afiseazaGramatica(G1);
        
        System.out.println("\nDupa eliminarea simbolurilor inaccesibile:");
        afiseazaGramatica(G2);
        
        System.out.println("\nDupa eliminarea productiilor unitare:");
        afiseazaGramatica(G3);
    }

    private static void afiseazaGramatica(Gramatica G) {
        System.out.println("N = " + G.N);
        System.out.println("T = " + G.T);
        System.out.println("S = " + G.S);
        System.out.println("P = ");
        for (Map.Entry<String, Set<String>> entry : G.P.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}
