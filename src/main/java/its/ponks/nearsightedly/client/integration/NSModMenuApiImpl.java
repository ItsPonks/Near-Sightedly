package its.ponks.nearsightedly.client.integration;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

/**
 * TODO
 *
 * @since 1.1.0
 */
public class NSModMenuApiImpl implements ModMenuApi {
	/**
	 * Creates the {@linkplain ConfigScreenFactory} needed for {@linkplain ModMenu}.
	 * This method uses {@linkplain AutoConfig} to construct the default
	 * {@linkplain Screen} from {@linkplain NSConfig}.
	 *
	 * @since 1.1.0
	 */
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> AutoConfig.getConfigScreen(NSConfig.class, screen).get();
	}
}
