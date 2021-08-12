package its.ponks.nearsightedly;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "nearsightedly")
public class NSConfig implements ConfigData {
	@Tooltip(count = 3)
	public static double min, max;
}
