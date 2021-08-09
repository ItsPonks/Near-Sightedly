package its.ponks.nearsightedly.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.Option;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private double previous;

	@Inject(at = @At("HEAD"), method = "clearWorld")
	private void clearWorld(CallbackInfo info) {
		if (!CLIENT.isInSingleplayer()) {
			System.out.println("Resetting client render distance to " + previous + "...");
			Option.RENDER_DISTANCE.set(CLIENT.options, previous);
		}
	}

	@Inject(at = @At("HEAD"), method = "onChunkLoadDistance")
	private void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet, CallbackInfo info) {
		set(packet.getDistance());
	}

	@Inject(at = @At("HEAD"), method = "onGameJoin")
	private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
		set(packet.getViewDistance());
	}

	private void set(double value) {
		if (!CLIENT.isInSingleplayer()) {
			previous = Option.RENDER_DISTANCE.get(CLIENT.options);
			value = MathHelper.clamp(value, Option.RENDER_DISTANCE.getMin(), previous);
			System.out.println("Setting client render distance to server view distance, clamped to " + value + "...");
			Option.RENDER_DISTANCE.set(CLIENT.options, value);
		}
	}
}
