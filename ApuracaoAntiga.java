public class ApuracaoAntiga {
    public static int calcPts(String a, int[] pos, String[] inf) {
        int tot = 0;
        
        for (int p : pos) {
            if (p == 1) {
                tot += 50;
            } else {
                if (p == 2) {
                    tot += 30;
                } else {
                    if (p == 3) {
                        tot += 20;
                    } else {
                        tot += 5;
                    }
                }
            }
        }
        
        for (String i : inf) {
            if (i.equals("briga_quadra") || i.equals("briga_torcida")) {
                tot = tot - 50;
            } else {
                if (i.equals("preconceito")) {
                    System.out.println("Atletica " + a + " eliminada do JIUFMS");
                    return -1;
                }
            }
        }
        
        System.out.println("A atletica " + a + " fez " + tot + " pontos no geral");
        return tot;
    }
}
