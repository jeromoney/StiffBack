package com.example.stiffback.treelineDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A sqlite table to store the values of various treeline data around US. At start, the app finds
 * the user's closest treeline entry and uses the value as the treeline. There are probably more
 * refined ways to guess the user's treeline value.
 *
 * Based on wiki article https://en.wikipedia.org/wiki/Tree_line
 */
@Entity(tableName = "treeline")
class TreelineEntity(@field:ColumnInfo(name = "mountain_range")
                     val mountainRange: String, @field:ColumnInfo(name = "treeline_elevation")
                     val treelineElevation: Int // in meters
                     , @field:ColumnInfo(name = "notes")
                     val notes: String, @field:ColumnInfo(name = "mountain")
                     val mountain: String // The peak that the location is based on. Usually the highest in the range
                     , @field:ColumnInfo(name = "lat")
                     val lat: Double, @field:ColumnInfo(name = "lng")
                     val lng: Double) {

    @PrimaryKey(autoGenerate = true)
    var xid: Int = 0

    /**
     * Calculates the length of degrees between mountain and point. A crude way to find the closest
     * point. Skips the square-root in the calculation for efficiency
     * @param lat
     * @param lng
     * @return distance in degrees
     */
    fun latlngDistance(lat: Double, lng: Double): Double {
        return Math.pow(this.lat - lat, 2.0) + Math.pow(this.lng - lng, 2.0)
    }

    companion object {
        /**
         * prepopulates database with treeline values across US
         *
         * @return
         */
        fun populateData(): Array<TreelineEntity> {
            return arrayOf(TreelineEntity("Chugach Mountains, Alaska", 700, "Tree line around 1,500 feet (460 m) or lower in coastal areas", "Mt Marcus Baker", 61.437778, -147.750556),
                    TreelineEntity("Olympic Mountains WA, United States", 1500, "Heavy winter snowpack buries young trees until late summer", "Mt Olympus", 47.801299, -123.710837),
                    TreelineEntity("Mount Katahdin, Maine, United States", 1150, "", "Mt Katahdin", 45.904248, -68.921412),
                    TreelineEntity("New Hampshire, United States", 1350, "Some peaks have even lower treelines because of fire and subsequent loss of soil, such as Grand Monadnock and Mount Chocorua.", "Mt Washington", 44.270236, -71.303706),
                    TreelineEntity("Wyoming, United States", 3000, "", "Gannet Peak", 43.184374, -109.654076),
                    TreelineEntity("Wasatch Mountains, Utah, United States", 2900, "Higher (nearly 11,000 feet or 3,400 metres in the Uintas)", "Mt Nebo", 39.821586, -111.760232),
                    TreelineEntity("Rocky Mountain NP, Colorado, United States", 3250, "On northeast slopes. On warm southwest slopes, the treelinE is 3,550m.", "Longs Peak", 40.254817, -105.616478),
                    TreelineEntity("Yosemite NP, California, United States", 3600, "East side of Sierra Nevada. On the west side of the Sierras, the treeline is 3,200m.", "El Capitan", 37.733665, -119.637668),
                    TreelineEntity("Hawaii, United States", 3000, "Geographic isolation and no local tree species with high tolerance to cold temperatures.", "Mauna Kea", 19.465621, -155.588359)
            )
        }
    }
}