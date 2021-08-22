package its.ponks.nearsightedly.client.network.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.points.BeforeReturn;

import its.ponks.nearsightedly.client.integration.NSConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.util.math.MathHelper;

/**
 * The class {@code ClientPlayNetworkHandlerMixin} contains the
 * {@linkplain Mixin}s used by this mod on
 * {@linkplain ClientPlayNetworkHandler}.
 *
 * @since 1.0.0
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow
	private int chunkLoadDistance;
	@Shadow
	@Final
	private MinecraftClient client;
	private int previous;

	/**
	 * Injects into the {@linkplain BeforeReturn RETURN} of
	 * {@linkplain ClientPlayNetworkHandler#clearWorld clearWorld} in
	 * {@linkplain ClientPlayNetworkHandler}.
	 *
	 * <p>
	 * Handles resetting {@linkplain Option#RENDER_DISTANCE RENDER_DISTANCE} to its
	 * value before {@linkplain #onGameJoin} was invoked, if needed. Nothing is done
	 * if the {@linkplain #previous} value of {@linkplain Option#RENDER_DISTANCE
	 * RENDER_DISTANCE} was never initialized, the
	 * {@linkplain MinecraftClient#isInSingleplayer}, or the current value of
	 * {@linkplain Option#RENDER_DISTANCE RENDER_DISTANCE} is equal to the
	 * {@linkplain #previous} value.
	 * </p>
	 *
	 * <p>
	 * If the {@linkplain Option#RENDER_DISTANCE RENDER_DISTANCE} is changed, the
	 * {@linkplain MinecraftClient#options} are {@linkplain GameOptions#write
	 * written} to disk. Even if the current value of
	 * {@linkplain Option#RENDER_DISTANCE RENDER_DISTANCE} is equal to the
	 * {@linkplain previous} value, {@linkplain previous} is reset to 0.
	 * </p>
	 *
	 * @param info The {@linkplain CallbackInfo} required by {@linkplain Inject}
	 * @since 1.0.0
	 */
	@Inject(at = @At("RETURN"), method = "clearWorld")
	private void clearWorld(@SuppressWarnings("unused") final CallbackInfo info) {
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

	/**
	 * Injects into the {@linkplain BeforeReturn RETURN} of
	 * {@linkplain ClientPlayNetworkHandler#onChunkLoadDistance onChunkLoadDistance}
	 * and {@linkplain ClientPlayNetworkHandler#onGameJoin onGameJoin} in
	 * {@linkplain ClientPlayNetworkHandler}.
	 *
	 * <p>
	 * TODO
	 * </p>
	 *
	 * @implNote Both methods are {@linkplain Inject}ed here instead of using the
	 *           Fabric API listener for
	 *           {@linkplain ClientPlayNetworkHandler#onGameJoin onGameJoin} to
	 *           reduce code complexity. Since
	 *           {@linkplain ClientPlayNetworkHandler#onChunkLoadDistance
	 *           onChunkLoadDistance} does not have a Fabric API listener, this
	 *           class would still be necessary. Unfortunately, public fields and
	 *           methods are disallowed in {@linkplain Mixin} classes, so the
	 *           listener would need to be registered elsewhere. As well, an
	 *           {@linkplain Accessor} and its containing interface must be created
	 *           to get the value of {@linkplain #chunkLoadDistance} in the
	 *           listener. Lastly, this method's body would be moved to a helper
	 *           method to be invoked from multiple classes. Basically, it is
	 *           significantly more complicated using listeners in this case.
	 *
	 * @param info The {@linkplain CallbackInfo} required by {@linkplain Inject}
	 * @since 1.2.0
	 */
	@Inject(at = @At("RETURN"), method = { "onChunkLoadDistance", "onGameJoin" })
	private void onChunkLoadDistanceUpdatePackets(@SuppressWarnings("unused") final CallbackInfo info) {
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
