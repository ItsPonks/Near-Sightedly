package its.ponks.nearsightedly.client;

import its.ponks.nearsightedly.client.integration.NSConfig;
import net.fabricmc.api.ClientModInitializer;

public class NearSightedly implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NSConfig.init();
	}
}
