package its.ponks.nearsightedly.client.integration;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

/**
 * TODO
 *
 * @since 1.1.0
 */
@Config(name = "nearsightedly")
public class NSConfig implements ConfigData {
	@Excluded
	public static NSConfig instance;

	/**
	 * Initializes values for the class. This method registers the class using
	 * {@linkplain AutoConfig} and stores the resulting {@linkplain #instance} for
	 * accessing config values.
	 *
	 * @since 1.1.0
	 */
	public static void init() {
		instance = AutoConfig.register(NSConfig.class, GsonConfigSerializer::new).getConfig();
	}

	@Tooltip(count = 3)
	public int min, max = Integer.MAX_VALUE;
}
