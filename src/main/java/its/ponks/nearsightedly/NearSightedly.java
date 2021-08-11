package its.ponks.nearsightedly;

import java.util.Objects;

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
			NSConfig.min = Objects.requireNonNullElse(NSConfig.min, NSConfig.MIN);
			NSConfig.max = Objects.requireNonNullElseGet(NSConfig.max, Option.RENDER_DISTANCE::getMax);
		});
	}
}
