package its.ponks.nearsightedly.client.network.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import its.ponks.nearsightedly.client.integration.NSConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Option;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private int chunkLoadDistance;
	@Shadow
	@Final
	private MinecraftClient client;
	private int previous;

	@Inject(at = @At("RETURN"), method = "clearWorld")
	private void clearWorld(final CallbackInfo info) {
		if (previous != 0 && !client.isInSingleplayer()) {
			final var current = (int) Option.RENDER_DISTANCE.get(client.options);
			if (current != previous) {
				System.out.println("Resetting render distance from " + current + " to " + previous + "...");
				Option.RENDER_DISTANCE.set(client.options, previous);
				client.options.write();
			}

			previous = 0;
		}
	}

	@Inject(at = @At("RETURN"), method = { "onChunkLoadDistance", "onGameJoin" })
	private void onChunkLoadDistanceUpdatePackets(final CallbackInfo info) {
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
			final var value = MathHelper.clamp(chunkLoadDistance + 1, min, max);
			if (current != value) {
				System.out.println("Changing render distance from " + current + " to " + value + "...");
				Option.RENDER_DISTANCE.set(client.options, value);
			}

			if (previous == 0)
				previous = current;
		}
	}
}
