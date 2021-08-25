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
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Option;
import net.minecraft.util.math.MathHelper;

/**
 * The {@link ClientPlayNetworkHandler} {@code Mixin} for this mod.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	/**
	 * The view distance of the connected server. Updated only during
	 * {@link ClientPlayNetworkHandler#onGameJoin onGameJoin}. Shadowed from
	 * {@link ClientPlayNetworkHandler}.
	 */
	@Shadow
	private int chunkLoadDistance;

	/**
	 * The currently running {@code MinecraftClient}. Shadowed from
	 * {@link ClientPlayNetworkHandler}.
	 */
	@Shadow
	@Final
	private MinecraftClient client;

	/**
	 * The value of {@link Option#RENDER_DISTANCE} before
	 * {@link #onChunkLoadDistanceUpdatePackets} is invoked for
	 * {@link ClientPlayNetworkHandler#onGameJoin onGameJoin}.
	 */
	private int previous;

	/**
	 * Injects into the {@code RETURN} of {@link ClientPlayNetworkHandler#clearWorld
	 * clearWorld}.
	 * <p>
	 * Handles resetting {@link Option#RENDER_DISTANCE} to its value before
	 * {@link ClientPlayNetworkHandler#onGameJoin onGameJoin} was invoked, if
	 * needed. Nothing is done if {@code previous} is uninitialized, the client is
	 * in singleplayer, or {@code RENDER_DISTANCE} is equal to {@code previous}.
	 * <p>
	 * If {@code RENDER_DISTANCE} is modified, the changes are
	 * {@link GameOptions#write written} to disk. {@code previous} is reset to 0
	 * regardless of whether {@code RENDER_DISTANCE} is equal to {@code previous}.
	 *
	 * @param info The unused {@link CallbackInfo} required for injects
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
	 * Injects into the {@code RETURN} of
	 * {@link ClientPlayNetworkHandler#onChunkLoadDistance onChunkLoadDistance} and
	 * {@link ClientPlayNetworkHandler#onGameJoin onGameJoin}.
	 * <p>
	 * Handles syncing the client's {@link Option#RENDER_DISTANCE RENDER_DISTANCE}
	 * to the server's view distance, if needed. Nothing is done if the client is in
	 * singleplayer or {@code RENDER_DISTANCE} is equal to the new value.
	 * <p>
	 * The {@code min} and {@code max} bounds of {@code RENDER_DISTANCE} are used to
	 * clamp {@link NSConfig#min} and {@link NSConfig#max}. If the resulting
	 * {@code min} is greater than the resulting {@code max}, their values are
	 * swapped. Lastly, {@code RENDER_DISTANCE} is compared to
	 * {@code chunkLoadDistance + 1}, clamped by {@code min} and {@code max}.
	 * {@code previous} is initialized to {@code RENDER_DISTANCE} if it is currently
	 * uninitialized.
	 *
	 * @param info The unused {@link CallbackInfo} required for injects
	 * @see <a href=
	 *      "https://minecraft.fandom.com/wiki/Chunk#Ticket_types">Chunk/Ticket
	 *      Types</a>
	 * @implNote Both methods are injected here instead of using the Fabric API
	 *           listener for {@code onGameJoin} to reduce code complexity. Since
	 *           {@code onChunkLoadDistance} does not have a Fabric API listener,
	 *           this {@code class} would still be necessary. Unfortunately,
	 *           {@code public} fields and methods are disallowed in {@code Mixin}
	 *           {@code classes}, so the listener would need to be registered
	 *           elsewhere. As well, an {@code Accessor} and its containing
	 *           {@code interface} must be created to use {@code chunkLoadDistance}
	 *           in the listener. Lastly, this method's body would be moved to a
	 *           helper method to be invoked from both locations. Basically, it is
	 *           significantly more complicated to use listeners in this case.
	 *           <p>
	 *           In vanilla, the {@code min} and {@code max} bounds of
	 *           {@code RENDER_DISTANCE} are 2 and 32 respectively, but other mods
	 *           can potentially change this. Thus, both are queried when needed for
	 *           maximum compatibility.
	 *           <p>
	 *           1 is added to {@code chunkLoadDistance} to compensate for
	 *           differences in how the view distance is used in the server compared
	 *           to how the client uses {@code RENDER_DISTANCE}. From a few tests,
	 *           it appears that adding more than 1 to the value results in the fog
	 *           not properly covering the edge of the loaded chunks.
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
