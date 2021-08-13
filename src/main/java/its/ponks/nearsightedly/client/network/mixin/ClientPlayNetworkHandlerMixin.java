package its.ponks.nearsightedly.client.network.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerMixin {
	@Accessor
	int getChunkLoadDistance();
}
