import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/*
 * Class representing an NxN grid for the Princeton Percolation challenge
 */
public class Percolation {
    private final int CLOSED = 0;
    private final int OPEN = 1;
    private final int TOP_VIRTUAL_SITE_INDEX = 0;
    private int BOTTOM_VIRTUAL_SITE_INDEX;

    private int numOpenSites = 0;
    // total does not include top and bottom virtual sites.
    private int numSitesTotal;
    private int[][] grid;
    private WeightedQuickUnionUF weightedQuickUnionUF;
    private int nDimension;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {

        if(n <= 0){
            throw new IllegalArgumentException();
        }

        nDimension = n;
        // Create the grid (1,1) through (n,n)
        grid = new int[n][n];
        for(int i = 1; i <= n; i++){
            for(int j = 1; j <= n; j++){
                grid[i - 1][j - 1] = CLOSED;
            }
        }

        // Create WeightedQuickUnionUF object. It should have elements for a top and bottom site.
        numSitesTotal = n*n;
        weightedQuickUnionUF = new WeightedQuickUnionUF(numSitesTotal + 2);
        BOTTOM_VIRTUAL_SITE_INDEX = numSitesTotal + 1;

        // Union all top-row sites to top virtual node
        for(int i = 1; i <= n; i++){
            weightedQuickUnionUF.union(siteToUfIndex(1, i), TOP_VIRTUAL_SITE_INDEX);
        }

        // Union all bottom-row sites to bottom node
        for(int i = 1; i <= n; i++){
            weightedQuickUnionUF.union(siteToUfIndex(n, i), BOTTOM_VIRTUAL_SITE_INDEX);
        }
    }

    // opens the site (row, col) if it is not open already
    // Indices for API start at 1, while array starts at 0, so adjust.
    public void open(int row, int col) {

        if(row < 1 || row > nDimension || col < 1 || col > nDimension){
            throw new IllegalArgumentException();
        }

        if(!isOpen(row, col)){
            grid[row - 1][col - 1] = OPEN;
            numOpenSites++;

            // Union to surrounding sites as appropriate

            // top site
            if(row > 1 && isOpen(row - 1, col)) {
                weightedQuickUnionUF.union(siteToUfIndex(row, col), siteToUfIndex(row - 1, col));
            }

            // bottom site
            if(row < nDimension && isOpen(row + 1, col)) {
                weightedQuickUnionUF.union(siteToUfIndex(row, col), siteToUfIndex(row + 1, col));
            }

            // left site
            if(col > 1 && isOpen(row, col - 1)) {
                weightedQuickUnionUF.union(siteToUfIndex(row, col), siteToUfIndex(row, col - 1));
            }

            // right site
            if(col < nDimension && isOpen(row, col + 1)) {
                weightedQuickUnionUF.union(siteToUfIndex(row, col), siteToUfIndex(row, col + 1));
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {

        if(row < 1 || row > nDimension || col < 1 || col > nDimension){
            throw new IllegalArgumentException();
        }

        if(grid[row - 1][col - 1] == OPEN){
            return true;
        }
        else {
            return false;
        }
    }

    // is the site (row, col) full?
    // does the top virtual node have the same root as this site?
    public boolean isFull(int row, int col) {

        if(row < 1 || row > nDimension || col < 1 || col > nDimension){
            throw new IllegalArgumentException();
        }

        // translate the site coordinates to the UF index
        int ufIndex = siteToUfIndex(row, col);
        boolean isInTopVirtualSiteComponent = weightedQuickUnionUF.find(TOP_VIRTUAL_SITE_INDEX) == weightedQuickUnionUF.find(ufIndex);
        return isInTopVirtualSiteComponent && isOpen(row, col);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        // root of top site and bottom site must match, AND at least one bottom-row site must be open
        boolean percolates;
        boolean topBottomSameComponent;

        // is the root of the top site the same as the root of the bottom site?
        topBottomSameComponent = (weightedQuickUnionUF.find(TOP_VIRTUAL_SITE_INDEX) == weightedQuickUnionUF.find(BOTTOM_VIRTUAL_SITE_INDEX));

        // handle corner case of n = 1 or n = 2
        if(topBottomSameComponent){
            if(nDimension == 1){
                if(isFull(1, 1)){
                    percolates = true;
                }
                else {
                    percolates = false;
                }
            }
            else if(nDimension == 2){
                if((isFull(1, 1) && isFull(2, 1)) || (isFull(1, 2) && isFull(2, 2))){
                    percolates = true;
                }
                else {
                    percolates = false;
                }
            }
            else {
                percolates = true;
            }
        }
        else {
            percolates = false;
        }
        //System.out.println("Top component: " + weightedQuickUnionUF.);

        return percolates;
    }

    // test client (optional)
    public static void main(String[] args) {
        // Percolation percolation = new Percolation(3);
        
        // // Check that open sites = 0
        // if(percolation.numberOfOpenSites() == 0){
        //     System.out.println("GOOD: num open sites is 0");
        // }
        // else {
        //     System.out.println("BAD: num open sites is " + percolation.numberOfOpenSites());
        // }

        // // Check that top row is not full
        // if(percolation.isFull(1, 1)){
        //     System.out.println("BAD: first site is full");
        // }
        // else {
        //     System.out.println("GOOD: First site isn't full");
        // }
        
        // // Make sure percolates isn't true right away
        // if(percolation.percolates()){
        //     System.out.println("BAD: system percolates");
        // }
        // else {
        //     System.out.println("GOOD: System does not percolate yet.");
        // }

        // // Open one site, ensure it doesn't percolate
        // percolation.open(1, 1);
        // if(percolation.isOpen(1,1)){
        //     System.out.println("GOOD: (1,1) is open. Nice work!");
        // }
        // else {
        //     System.out.println("BAD: (1,1) is not open");
        // }
        // if(percolation.isFull(1, 1)){
        //     System.out.println("GOOD: (1,1) is full. Nice work!");
        // }
        // else {
        //     System.out.println("BAD: (1,1) is not full.");
        // }

        // // Make sure percolates isn't true right away
        // if(percolation.percolates()){
        //     System.out.println("BAD: system percolates");
        // }
        // else {
        //     System.out.println("GOOD: System does not percolate yet.");
        // }

        // // Open sites that should cause the system to percolate.
        // percolation.open(2,1);
        // percolation.open(3,1);
        // if(percolation.percolates()){
        //     System.out.println("GOOD: system percolates");
        // }
        // else {
        //     System.out.println("BAD: System does not percolate.");
        // }
    }

    private int siteToUfIndex(int row, int col){
        return ((row - 1) * nDimension) + col;
    }
}
