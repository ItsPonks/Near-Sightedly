package its.ponks.nearsightedly;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.option.Option;

public class NearSightedly implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AutoConfig.register(NSConfig.class, GsonConfigSerializer::new);

		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			if (NSConfig.min == 0)
				NSConfig.min = Option.RENDER_DISTANCE.getMin();
			if (NSConfig.max == 0)
				NSConfig.max = Option.RENDER_DISTANCE.getMax();
		});
	}
}
