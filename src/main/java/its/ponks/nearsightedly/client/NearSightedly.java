package its.ponks.nearsightedly.client;

import its.ponks.nearsightedly.client.integration.NSConfig;
import net.fabricmc.api.ClientModInitializer;

/**
 * TODO
 *
 * @since 1.1.0
 */
public class NearSightedly implements ClientModInitializer {
	/**
	 * TODO
	 *
	 * @since 1.1.0
	 */
	@Override
	public void onInitializeClient() {
		NSConfig.init();
	}
}
