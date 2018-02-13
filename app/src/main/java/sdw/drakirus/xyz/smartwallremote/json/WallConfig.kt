package sdw.drakirus.xyz.smartwallremote.json

import android.widget.CheckBox

data class WallConfig(
	val wall: List<WallItem>
)

data class WallItem(
		val name: String,
		val screen: List<Screen>,
		val rows: Int,
		val cols: Int,
		val scenario: List<Scenario>){
	fun getCheckBoxAt(col:Int, row:Int): Screen? =
			screen.filter { it.col == col && it.row == row}.getOrNull(0)
}

data class Screen(
		val row: Int,
		val col: Int,

		@Transient
		var checkBox: CheckBox? = null
)

data class Scenario(
		val name: String,
		val video: List<Video>
)

data class Video(
		val volume: Int,
		val file: String,
		val screens: List<Screen>,
		val loop: Int,
		val distributed: Int,
		val idv: String,
		val mute: Int,
		val departure: String,
		val state: String
)
