package its.ponks.nearsightedly.client;

import its.ponks.nearsightedly.client.integration.NSConfig;
import net.fabricmc.api.ClientModInitializer;

/**
 * The client-side entry point for this mod.
 */
public class NearSightedly implements ClientModInitializer {
	/**
	 * Invokes relevant methods to initialize this mod on the client. Currently only
	 * invokes {@link NSConfig#init}.
	 */
	@Override
	public void onInitializeClient() {
		NSConfig.init();
	}
}
