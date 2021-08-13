package its.ponks.nearsightedly.client.integration;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "nearsightedly")
public class NSConfig implements ConfigData {
	@Excluded
	public static NSConfig instance;

	public static void init() {
		instance = AutoConfig.register(NSConfig.class, GsonConfigSerializer::new).getConfig();
	}

	@Tooltip(count = 3)
	public int min, max = Integer.MAX_VALUE;
}
