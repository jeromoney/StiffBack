package com.example.stiffback;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SlopeUtilsTest {
    private Double[][] ARC_GIS_SLOPE = {{50.,45.,50.},{30.,30.,30.},{8.,10.,10.}};
    private Double[][] ARC_GIS_ASPECT = {{101.,92.,85.},{101.,92.,85.},{101.,91.,84.}};


    @Test
    public void arcSecondAssert(){
        Double arc_second_third = SlopeUtils.THIRD_ARC_SECOND;
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
            Double flat_slope = SlopeUtils.slope(flatArry, randomLat);
            assertTrue(flat_slope < 0.0001);
        }

        // Test slope described in ArcGis documents
        double arc_slope = SlopeUtils.slope(ARC_GIS_SLOPE, 0.);
        assertTrue(Math.abs(arc_slope - 29.37518223351121) < 0.0001);
    }

    // test for aspect
    @Test
    public void aspectTest(){
        double aspect = SlopeUtils.aspect(ARC_GIS_ASPECT);
        assertTrue(Math.abs(aspect - 92.64) < 0.01);
    }

}
