package its.ponks.nearsightedly.client.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;

/**
 * The implementation of {@link ModMenuApi} for this mod.
 */
public class NSModMenuApiImpl implements ModMenuApi {
	/**
	 * Creates the {@link ConfigScreenFactory} needed for a GUI configuration. Uses
	 * {@link AutoConfig} to construct a default {@code Screen} implementation for
	 * {@linkplain NSConfig}.
	 */
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> AutoConfig.getConfigScreen(NSConfig.class, screen).get();
	}
}
