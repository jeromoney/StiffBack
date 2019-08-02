package com.example.stiffback;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SlopeUtilsTest {
    private Double[][] ARC_GIS_SLOPE = {{50.,45.,50.},{30.,30.,30.},{8.,10.,10.}};
    private Double[][] ARC_GIS_ASPECT = {{101.,92.,85.},{101.,92.,85.},{101.,91.,84.}};
    private Double[][] SOUTH_ASPECT = {{3.,3.,3.},{2.,2.,2.},{1.,1.,1.}};
    private Double[][] NORTH_ASPECT = {{1.,1.,1.},{2.,2.,2.},{3.,3.,3.}};
    private Double[][] EAST_ASPECT = {{3.,2.,1.},{3.,2.,1.},{3.,2.,1.}};
    private Double[][] WEST_ASPECT = {{1.,2.,3.},{1.,2.,3.},{1.,2.,3.}};



    @Test
    public void arcSecondAssert(){
        Double arc_second_third = SlopeUtils.INSTANCE.getTHIRD_ARC_SECOND();
        Double difference = Math.abs(arc_second_third - 9.259259*Math.pow(10,-5));
        assertTrue(difference < 0.0001);
    }

    @Test
    public void slopeTest(){

        // Testing flat slopes where the slope should be zero
        for (int i=0; i<10; i++) {
            double randomLat = Math.random() * 180. - 90.;
            double randomElev = Math.random() * 30000.;
            Double[][] flatArry = new Double[3][3];
            for (Double[] row : flatArry) {
                Arrays.fill(row, randomElev);
            }
            Double flat_slope = SlopeUtils.INSTANCE.slope(flatArry, randomLat);
            assertTrue(flat_slope < 0.0001);
        }

        // Test slope described in ArcGis documents
        double arc_slope = SlopeUtils.INSTANCE.slope(ARC_GIS_SLOPE, 0.);
        assertTrue(Math.abs(arc_slope - 29.37518223351121) < 0.0001);
    }

    // test for aspect
    @Test
    public void aspectTest(){
        double aspect = SlopeUtils.INSTANCE.aspect(ARC_GIS_ASPECT);
        assertTrue(Math.abs(aspect - 92.64) < 0.01);

        aspect = SlopeUtils.INSTANCE.aspect(NORTH_ASPECT);
        assertTrue(Math.abs(aspect - 0.) < 0.01);

        aspect = SlopeUtils.INSTANCE.aspect(SOUTH_ASPECT);
        assertTrue(Math.abs(aspect - 180.) < 0.01);

        aspect = SlopeUtils.INSTANCE.aspect(EAST_ASPECT);
        assertTrue(Math.abs(aspect -90.) < 0.01);

        aspect = SlopeUtils.INSTANCE.aspect(WEST_ASPECT);
        assertTrue(Math.abs(aspect - 270.) < 0.01);
    }

}
