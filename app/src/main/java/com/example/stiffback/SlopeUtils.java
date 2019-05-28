package com.example.stiffback;

/**
 * Calculates the slope and aspect from 9 elevation values
 */
public final class SlopeUtils {
    public static final Double THIRD_ARC_SECOND = (1.0/3600) * (1.0/3);



    private static double dx_helper(double a, double c, double d, double f, double g, double i){
        return ((c + 2*f + i) - (a + 2*d + g))/8.;
    }

    private static double dy_helper(double a, double b, double c, double g, double h, double i){
        return ((g + 2*h + i) - (a + 2*b + c))/8.;
    }

    /**
     *      * Algo based on http://desktop.arcgis.com/en/arcmap/10.3/tools/spatial-analyst-toolbox/how-slope-works.htm
     * @param cell
     * @param lng
     * @return
     */
    public static double slope(Double[][] cell, Double lng){
        // Convert 1/3 arc-seconds to feet https://www.esri.com/news/arcuser/0400/wdside.html
        double Y_CELLSIZE = 101.27 / 3.; // ft
        double x_cellsize = Y_CELLSIZE * Math.cos(lng);


        double a = cell[0][0];
        double b = cell[1][0];
        double c = cell[2][0];
        double d = cell[0][1];
        double f = cell[2][1];
        double g = cell[0][2];
        double h = cell[1][2];
        double i = cell[2][2];

        double dx = dx_helper(a,c,d,f,g,i) / x_cellsize;
        double dy = dy_helper(a,b,c,g,h,i) / Y_CELLSIZE;

        double rise_run = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double slope_degrees = Math.atan(rise_run) * (180. / Math.PI);

        return slope_degrees;
}

    public static Double aspect(Double[][] cell){
        //http://desktop.arcgis.com/en/arcmap/10.3/tools/spatial-analyst-toolbox/how-aspect-works.htm
        double a = cell[0][0];
        double b = cell[0][1];
        double c = cell[0][2];
        double d = cell[1][0];
        double f = cell[1][2];
        double g = cell[2][0];
        double h = cell[2][1];
        double i = cell[2][2];

        double dx = dx_helper(a,c,d,f,g,i);
        double dy = dy_helper(a,b,c,g,h,i);

        double aspect = (180. / Math.PI) * Math.atan2(dy, -dx);
        // Convert to compass direction values
        return (90 - aspect)%360;
}

}
