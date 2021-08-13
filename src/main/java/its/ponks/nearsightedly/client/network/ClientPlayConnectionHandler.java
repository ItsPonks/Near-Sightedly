package its.ponks.nearsightedly.client.network;

import its.ponks.nearsightedly.client.integration.NSConfig;
import its.ponks.nearsightedly.client.network.mixin.ClientPlayNetworkHandlerMixin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.Option;
import net.minecraft.util.math.MathHelper;

public class ClientPlayConnectionHandler {
	private static int previous;

	public static void init() {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			if (!client.isInSingleplayer()) {
				final var current = (int) Option.RENDER_DISTANCE.get(client.options);
				if (current != previous) {
					System.out.println("Resetting render distance from " + current + " to " + previous + "...");
					Option.RENDER_DISTANCE.set(client.options, previous);
					client.options.write();
				}
			}
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (!client.isInSingleplayer()) {
				final var minAllowed = (int) Option.RENDER_DISTANCE.getMin();
				final var maxAllowed = (int) Option.RENDER_DISTANCE.getMax();
				var min = MathHelper.clamp(NSConfig.instance.min, minAllowed, maxAllowed);
				var max = MathHelper.clamp(NSConfig.instance.max, minAllowed, maxAllowed);

				if (min > max) {
					min ^= max;
					max ^= min;
					min ^= max;
				}

				final var current = (int) Option.RENDER_DISTANCE.get(client.options);
				final var value = MathHelper.clamp(((ClientPlayNetworkHandlerMixin) handler).getChunkLoadDistance() + 1,
						min, max);
				if (current != value) {
					System.out.println("Changing render distance from " + current + " to " + value + "...");
					Option.RENDER_DISTANCE.set(client.options, value);
				}

				previous = current;
			}
		});
	}
}
