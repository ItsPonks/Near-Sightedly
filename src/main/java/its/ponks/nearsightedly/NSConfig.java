package its.ponks.nearsightedly;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import net.minecraft.client.option.Option;

@Config(name = "nearsightedly")
public class NSConfig implements ConfigData, ModMenuApi {
	public static Double min, max;
	@Excluded
	public static final double MIN = Option.RENDER_DISTANCE.getMin();

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> AutoConfig.getConfigScreen(NSConfig.class, parent).get();
	}
}
