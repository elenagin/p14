class linearRegression {
    private double m, b, sumX = 0, sumY = 0, sumXSquared = 0, sumXY = 0;
    private static int arrayLength;

    void fsumX(double[] x) {
        arrayLength = x.length;
        for (int i = 0; i < arrayLength; i++)
            sumX += x[i];
    }

    void fsumXSquared(double[] x) {
        arrayLength = x.length;
        for (int i = 0; i < arrayLength; i++)
            sumXSquared += (x[i] * x[i]);
    }

    void fsumY(double[] y) {
        arrayLength = y.length;
        for (int i = 0; i < arrayLength; i++)
            sumY += y[i];
    }

    void fsumXY(double[] x, double[] y) {
        arrayLength = x.length;
        for (int i = 0; i < arrayLength; i++)
            sumXY += (x[i] * y[i]);
    }

    void setSlope() {

        m = ((arrayLength * sumXY) - (sumX * sumY)) / ((arrayLength * sumXSquared) - (sumX * sumX));
    }

    double getSlope() {

        return m;
    }

    void setOrigin() {

        b = (sumY - (m * sumX)) / arrayLength;
    }

    double getOrigin() {
        return b;
    }
}
