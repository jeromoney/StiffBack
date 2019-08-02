package com.example.stiffback

/**
 * Calculates the slope and aspect from 9 elevation values
 */
object SlopeUtils {
    val THIRD_ARC_SECOND: Double? = 1.0 / 3600 * (1.0 / 3)


    private fun dx_helper(a: Double, c: Double, d: Double, f: Double, g: Double, i: Double): Double {
        return (c + 2 * f + i - (a + 2 * d + g)) / 8.0
    }

    private fun dy_helper(a: Double, b: Double, c: Double, g: Double, h: Double, i: Double): Double {
        return (g + 2 * h + i - (a + 2 * b + c)) / 8.0
    }

    /**
     * Changes an arbitary degree measurement and finds the nearest sixth of an arc_second, inbetween
     * two third of arc_seconds
     * @param degree
     * @return snapped_degree
     */
    fun arc_second_snap(degree: Double): Double {
        return Math.floor(degree / THIRD_ARC_SECOND!!) * THIRD_ARC_SECOND + THIRD_ARC_SECOND / 2.0
    }

    /**
     * * Algo based on http://desktop.arcgis.com/en/arcmap/10.3/tools/spatial-analyst-toolbox/how-slope-works.htm
     * @param cell
     * @param lng
     * @return
     */
    fun slope(cell: Array<DoubleArray>, lng: Double): Double {
        // Convert 1/3 arc-seconds to feet https://www.esri.com/news/arcuser/0400/wdside.html
        val Y_CELLSIZE = 101.27 / 3.0 // ft
        val x_cellsize = Y_CELLSIZE * Math.cos(lng)


        val a = cell[0][0]
        val b = cell[1][0]
        val c = cell[2][0]
        val d = cell[0][1]
        val f = cell[2][1]
        val g = cell[0][2]
        val h = cell[1][2]
        val i = cell[2][2]

        val dx = dx_helper(a, c, d, f, g, i) / x_cellsize
        val dy = dy_helper(a, b, c, g, h, i) / Y_CELLSIZE

        val rise_run = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0))
        return Math.atan(rise_run) * (180.0 / Math.PI)
    }

    fun aspect(cell: Array<DoubleArray>): Double {
        //http://desktop.arcgis.com/en/arcmap/10.3/tools/spatial-analyst-toolbox/how-aspect-works.htm
        val a = cell[0][0]
        val b = cell[0][1]
        val c = cell[0][2]
        val d = cell[1][0]
        val f = cell[1][2]
        val g = cell[2][0]
        val h = cell[2][1]
        val i = cell[2][2]

        val dx = dx_helper(a, c, d, f, g, i)
        val dy = dy_helper(a, b, c, g, h, i)

        val aspect = 180.0 / Math.PI * Math.atan2(dy, -dx)
        // Convert to compass direction values
        return (90 - aspect + 360) % 360
    }

}
