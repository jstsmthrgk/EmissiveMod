package traben.entity_texture_features.mixin.client.entity.textureUpdaters;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

@Mixin(LivingEntity.class)
public abstract class MIX_LivingEntity{

    @Inject(method = "damage", at = @At("TAIL"))
    private void injected(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
       // if (((LivingEntity)(Object)this).world.isClient()) {
            UUID id = ((LivingEntity) (Object) this).getUuid();
            if (!UUID_entityAwaitingDataClearing.containsKey(id)){ UUID_entityAwaitingDataClearing.put(id,System.currentTimeMillis());}
        //}
    }
    @Inject(method = "heal", at = @At("TAIL"))
    private void injected2(float amount, CallbackInfo ci) {
            UUID id = ((LivingEntity) (Object) this).getUuid();
            if (!UUID_entityAwaitingDataClearing.containsKey(id)){ UUID_entityAwaitingDataClearing.put(id,System.currentTimeMillis());}
    }

}


