package its.ponks.nearsightedly.client;

import its.ponks.nearsightedly.client.integration.NSConfig;
import its.ponks.nearsightedly.client.network.ClientPlayConnectionHandler;
import net.fabricmc.api.ClientModInitializer;

public class NearSightedly implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionHandler.init();
		NSConfig.init();
	}
}
