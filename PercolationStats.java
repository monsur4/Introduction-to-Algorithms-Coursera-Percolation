import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private final int numberOfTrials;
    private final double[] trialPercolationTresholds;
    private double confidenceInterval;
    private double mean;

    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException(
                    "arguments are not in range range");
        }
        numberOfTrials = trials;
        trialPercolationTresholds = new double[trials];

        for (int i = 0; i < numberOfTrials; i++) {
            int rowSlot = StdRandom.uniform(1, n + 1);
            int columnSlot = StdRandom.uniform(1, n + 1);

            Percolation percs = new Percolation(n);
            percs.open(rowSlot, columnSlot);
            while (!percs.percolates()) {
                percs.open(rowSlot, columnSlot);
                rowSlot = StdRandom.uniform(1, n + 1);
                columnSlot = StdRandom.uniform(1, n + 1);
            }
            int numberOfSitesOpened = percs.numberOfOpenSites();
            double totalNumberOfSites = n * n;
            double percolationThreshold = (double) numberOfSitesOpened / totalNumberOfSites;
            // System.out.println("Percolation Threshold is " + i + ": " + percolationThreshold);

            trialPercolationTresholds[i] = percolationThreshold;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        mean = StdStats.mean(trialPercolationTresholds);
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(trialPercolationTresholds);
    }

    private void confidenceInterval() {
        double zValue = 1.96;
        confidenceInterval = stddev() * zValue / (calculateSquareRoot(numberOfTrials));
    }

    private double calculateSquareRoot(int number) {
        double i;
        double v = 1;
        while (v * v < number) {
            v++;
        }
        double result = v;

        while (result * result < number) {
            result += 0.0001; // precision
        }
        i = result;
        return i;
    }

    private double getConfidenceInterval() {
        return confidenceInterval;
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - getConfidenceInterval();
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + getConfidenceInterval();
    }

    public static void main(String[] args) {
        int numberOfTrials = Integer.parseInt(args[1]);
        int numberOfRowsAndColumns = Integer.parseInt(args[0]);

        PercolationStats percStats = new PercolationStats(numberOfRowsAndColumns,
                                                          numberOfTrials);

        System.out.println("Mean = " + percStats.mean());
        System.out.println("Standard Deviation = " + percStats.stddev());
        percStats.confidenceInterval();
        System.out.println(
                "95% Confidence Interval = [" + percStats.confidenceLo() + ", " + percStats
                        .confidenceHi() + "]");

    }
}
