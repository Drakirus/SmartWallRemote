package sdw.drakirus.xyz.smartwallremote.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.widget.CardView
import android.widget.CheckBox
import com.google.gson.annotations.SerializedName
import sdw.drakirus.xyz.smartwallremote.component.helpers.colorBorderFromPos
import sdw.drakirus.xyz.smartwallremote.component.helpers.colorBorderReset
import sdw.drakirus.xyz.smartwallremote.component.helpers.resetColor
import sdw.drakirus.xyz.smartwallremote.component.helpers.setColor
import java.net.URL


data class WallConfig(
        val wall: List<WallItem>
)

data class WallItem(
        val name: String,
        val screen: List<Screen>,
        val rows: Int,
        val cols: Int
)
{
    fun getCheckBoxAt(col:Int, row:Int): Screen? =
            screen.filter { it.col == col && it.row == row}.getOrNull(0)

    fun updateColorGroup(layout: Layout){
        screen.forEach {
            it.updateColor(layout)
        }
    }

}

data class Screen(
        val row: Int,
        val col: Int
) {

    @Transient
    lateinit var checkBox: CheckBox

    @Transient
    lateinit var cardView: CardView

    fun updateColor(layout: Layout) {
        cardView.colorBorderReset()
        checkBox.resetColor()

        layout.grpScreen.forEach { grpScreen ->
            val position = grpScreen.getPosition(this)
            if (position != grpScreen.NOPOS) {
//                println("test: $col $row -> $position")

                checkBox.setColor(grpScreen.color)
                cardView.colorBorderFromPos(grpScreen.color, position)
            }
        }
    }

}

data class LayoutConfig(
        val layouts: MutableList<Layout>
){
    fun getForWall(wall: WallItem) = layouts.filter { it.rows == wall.rows && it.cols == wall.cols }
    fun isEmpty() = layouts.isEmpty()
}

data class Layout(
        val cols: Int,
        val rows: Int,
        val grpScreen: List<GrpScreen>,
        val name: String
)

data class GrpScreen(
        val listScreen: MutableList<Screen>, // not a UI screen cannot access Checkbox and CardView!!
        @field:SerializedName("color")
        private val _color: String
){
    val color: Int
        get() = Color.parseColor(_color)

    val hexColor: String
        get() = _color

    @Transient
    val NOPOS = "NOPOS"

    fun getPosition(screen: Screen): String {

        if (!listScreen.contains(screen)){
            return NOPOS
        }

        val hasOneLeft: Boolean = listScreen.any { it.copy(col = it.col-1) == screen }
        val hasOneRight: Boolean = listScreen.any { it.copy(col = it.col+1) == screen }

        val hasOneTop: Boolean = listScreen.any { it.copy(row = it.row+1) == screen }
        val hasOneBottom: Boolean = listScreen.any { it.copy(row = it.row-1) == screen }

        return when (listOf(hasOneLeft, hasOneRight, hasOneTop, hasOneBottom)) {
            listOf(true, false, false, false) -> "right-only"
            listOf(false, true, false, false) -> "left-only"

            listOf(false, false, true, false) -> "bottom-only"
            listOf(false, false, false, true) -> "top-only"

            listOf(true, true, false, false) -> "horizontal"
            listOf(false, false,true, true) -> "vertical"

            listOf(true, true, true, true) -> "all"
            listOf(false, true, true, true) -> "right"
            listOf(true, false, true, true) -> "left"
            listOf(true, true, false, true) -> "top"
            listOf(true, true, true, false) -> "bottom"
            listOf(true, false, true, false) -> "bottom-left"
            listOf(false, true, false, true) -> "top-right"

            listOf(true, false, false, true) -> "top-left"
            listOf(false, true, true, false) -> "bottom-right"

            listOf(false, false, false, false) -> "alone"
            else -> NOPOS
        }
    }
}

data class VideoConfig(
        val videos: List<VideoModel>
)

data class VideoModel(
        val title: String,
        val imageUrl: String,
        val duration: Int
) {

    @Transient
    private var image: Bitmap? = null

    fun getStringDuration(): String {
        return String.format("%d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60)
    }

    //throw an exception to indicate the view that we couldn't retrieve the image
    @Throws(Exception::class)
    fun getImage(): Bitmap? {
        if (image == null) {
            val url = URL(imageUrl)
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }
        return image
    }
}

data class ScenarioConfig(
        val scenarioList: MutableList<Scenario>
){
    fun getForWall(wall: WallItem) = scenarioList.filter {
        it.layout.cols == wall.cols && it.layout.rows == wall.rows
    }
    fun isEmpty() = scenarioList.isEmpty()
}

data class Scenario(
        val name: String,
        val isDistributed: Boolean,
        val layout: Layout,
        val video: List<VideoModel?>,
        val timeStart: Int
)

