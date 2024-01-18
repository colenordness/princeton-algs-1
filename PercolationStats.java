import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;


/*
 * Class that runs simulations for the Princeton Percolation challenge.
 * Note: The concept of "backwash" is extra credit for this challenge,
 * and not accounted for in this implementation.
 * 
 * Backwash: when a site is included in the Union-Find component that connects
 * to the top site, but in reality would not be "full" per the problem description.
 */
public class PercolationStats {
    private double[] pctThresholds;
    private int numTrials;
    private static final String MEAN_STR = "mean";
    private static final String STDDEV_STR = "stddev";
    private static final String CONFIDENCE_STR = "95% confidence interval";

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {

        // one percentage threshold per trial
        pctThresholds = new double[trials];
        numTrials = trials;
        int totalSites = n*n;

        // repeat trials
        for(int i = 1; i <= trials; i++){
            Percolation percolation = new Percolation(n);

            int row;
            int col;

            while(!percolation.percolates()){
                // pick a site at random and open it
                row = StdRandom.uniformInt(1, n + 1);
                col = StdRandom.uniformInt(1, n + 1);
                percolation.open(row, col);
            }

            // when system percolates, collect stats
            pctThresholds[i - 1] = ((double)percolation.numberOfOpenSites())/((double)totalSites);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(pctThresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(pctThresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return this.mean() - ((1.96 * this.stddev()) / Math.sqrt(numTrials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return this.mean() + ((1.96 * this.stddev()) / Math.sqrt(numTrials));
    }

   // test client (see below)
   public static void main(String[] args) {
        int nDimensionArg = Integer.parseInt(args[0]);
        int numTrialsArg = Integer.parseInt(args[1]);
        if(nDimensionArg <= 0 || numTrialsArg <= 0){
            throw new IllegalArgumentException();
        }
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        int indent = CONFIDENCE_STR.length() + 1;
        System.out.printf(("mean%" + (indent - MEAN_STR.length()) + "s= " + ps.mean() + "\n"), "");
        System.out.printf(("stddev%" + (indent  - STDDEV_STR.length()) + "s= " + ps.stddev() + "\n"), "");
        System.out.printf(("95%% confidence interval = [" + ps.confidenceLo() + "," + " " + ps.confidenceHi() + "]\n"),"");
   }

}