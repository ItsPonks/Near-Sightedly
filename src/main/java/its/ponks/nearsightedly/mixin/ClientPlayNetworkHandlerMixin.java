package its.ponks.nearsightedly.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import its.ponks.nearsightedly.NSConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Option;
import net.minecraft.util.math.MathHelper;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private int chunkLoadDistance;

	@Final
	@Shadow
	private MinecraftClient client;
	private double previous;

	@Inject(at = @At("TAIL"), method = { "clearWorld" })
	private void clearWorld(@SuppressWarnings("unused") CallbackInfo info) {
		if (!client.isInSingleplayer()) {
			var current = Option.RENDER_DISTANCE.get(client.options);
			if (current != previous) {
				System.out.println("Resetting render distance from " + current + " to " + previous + "...");
				Option.RENDER_DISTANCE.set(client.options, previous);
				client.options.write();
			}

			previous = 0;
		}
	}

	@Inject(at = @At("TAIL"), method = { "onGameJoin", "onChunkLoadDistance" })
	private void onGameJoinAndChunkLoadDistance(@SuppressWarnings("unused") CallbackInfo info) {
		if (!client.isInSingleplayer()) {
			var min = Option.RENDER_DISTANCE.getMin();
			var max = Option.RENDER_DISTANCE.getMax();
			NSConfig.min = MathHelper.clamp(NSConfig.min, min, max);
			NSConfig.max = MathHelper.clamp(NSConfig.max, min, max);

			// For all those users who get min and max mixed up when configuring.
			if (NSConfig.min > NSConfig.max) {
				NSConfig.min -= NSConfig.max;
				NSConfig.max += NSConfig.min;
				NSConfig.min = NSConfig.max - NSConfig.min;
			}

			var current = Option.RENDER_DISTANCE.get(client.options);
			var value = MathHelper.clamp(chunkLoadDistance, NSConfig.min, NSConfig.max);
			if (current != value) {
				System.out.println("Changing render distance from " + current + " to " + value + "...");
				Option.RENDER_DISTANCE.set(client.options, value);
			}

			if (previous == 0)
				previous = current;
		}
	}
}
