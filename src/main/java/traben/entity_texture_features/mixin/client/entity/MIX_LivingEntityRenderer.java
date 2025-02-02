package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.entity_texture_features_METHODS;
import traben.entity_texture_features.client.randomCase;

import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

@Mixin(LivingEntityRenderer.class)
public abstract class MIX_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>, entity_texture_features_METHODS {
    @Shadow public abstract M getModel();




    protected MIX_LivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    //  [0] = total randoms, [1] = self random
    //private static float ticker = 0;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void applyEmissive(T livingEntity, float a, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        UUID id = livingEntity.getUuid();
        String fileString = getTexture(livingEntity).getPath();
        //System.out.println(fileString);
        if (UUID_randomTextureSuffix.containsKey(id)) {
            if (UUID_randomTextureSuffix.get(id) != 0 && optifineOldOrVanilla.containsKey(fileString)) {
                fileString = returnOptifineOrVanillaPath(fileString,UUID_randomTextureSuffix.get(id),"");
            }
        }
        if (Texture_Emissive.containsKey(fileString)) {
            if (Texture_Emissive.get(fileString) != null) {
                //VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(Texture_Emissive.get(fileString)));
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString),true));
                //one check most efficient instead of before and after applying
                if (irisDetected) {
                    matrixStack.scale(1.01f, 1.01f, 1.01f);
                    this.getModel().render(matrixStack
                            , textureVert
                            , 15728640
                            , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrixStack.scale(1f, 1f, 1f);
                }else{
                    this.getModel().render(matrixStack
                            , textureVert
                            , 15728640
                            , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        } else {//creates and sets emissive for texture if it exists
            Identifier fileName_e;
            boolean found = false;
            for (String suffix1:
            emissiveSuffix) {
                fileName_e = new Identifier(fileString.replace(".png", suffix1+".png"));
                if(isExistingFile( fileName_e)){
                        VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(fileName_e,true));
                        Texture_Emissive.put(fileString, fileName_e);
                        //one check most efficient instead of before and after applying
                        if (irisDetected) {
                            matrixStack.scale(1.01f, 1.01f, 1.01f);
                            this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            matrixStack.scale(1f, 1f, 1f);
                        }else{
                            this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        }
                    break;
                }
            }
            if (!Texture_Emissive.containsKey(fileString)) {
                Texture_Emissive.put(fileString, null);
            }
        }
        //cheeky lil fun for the dev
        //just makes my player look enchanted to others in multiplayer ;P
        if (livingEntity.getUuid().toString().equals("fd22e573-178c-415a-94fe-e476b328abfd")){
            //glow
            //VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(getTexture(livingEntity),true));

            //enchanted
            VertexConsumer textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(this.getTexture(livingEntity)), false, true);
            this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.16F);

            //creeper overlay
//            ticker += 0.3;
//            int f = (int) ticker;
//            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEnergySwirl(new Identifier("textures/entity/creeper/creeper_armor.png"), f* 0.01F  % 1.0F, f * 0.01F % 1.0F));
//            matrixStack.scale(1.015f, 1.015f, 1.015f);
//            this.getModel().render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 0.5F, 0.5F, 0.5F, 0.5F);
//            matrixStack.scale(1f, 1f, 1f);
//            if (ticker >640) ticker=0;
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier returnOwnRandomTexture(@SuppressWarnings("rawtypes") LivingEntityRenderer instance, Entity entity) {

        @SuppressWarnings("unchecked") Identifier vanilla = getTexture((T) entity);
        String path = vanilla.getPath();
        UUID id = entity.getUuid();
        try {
            if (!Texture_OptifineOrTrueRandom.containsKey(path)) {
                processNewRandomTextureCandidate(path);
            }
            //if needs to check if change required
            if (UUID_entityAwaitingDataClearing.containsKey(id)){
                if (!hasUpdatableRandomCases.containsKey(id)){
                    //modMessage("Error - mob will no longer have texture updated",false);
                    hasUpdatableRandomCases.put(id ,false);
                    UUID_entityAwaitingDataClearing.remove(id);
                }
                if (hasUpdatableRandomCases.get(id)) {
                    //skip a few ticks
                    //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                    if ((UUID_entityAwaitingDataClearing.get(id)/100)+1 < (System.currentTimeMillis()/100)){
                        if (Texture_OptifineOrTrueRandom.get(path)) {
                            int hold = UUID_randomTextureSuffix.get(id);
                            resetSingleData(id);
                            testCases(path, id, entity);
                            //if didnt change keep the same
                            if (!UUID_randomTextureSuffix.containsKey(id)) {
                                UUID_randomTextureSuffix.put(id, hold);
                            }
                        }//else here would do something for true random but no need really - may optimise this
                        UUID_entityAwaitingDataClearing.remove(id);
                    }

                }else{
                    UUID_entityAwaitingDataClearing.remove(id);
                }

            }
            if (Texture_OptifineOrTrueRandom.get(path)) {//optifine random
                //if it doesn't have a random already assign one
                if (!UUID_randomTextureSuffix.containsKey(id)) {
                    testCases(path,id ,entity);
                    //if all failed set to vanilla
                    if (!UUID_randomTextureSuffix.containsKey(id)) {
                        //System.out.println("Entity Texture Features - optifine properties failed to assign texture. setting "+entity.getEntityName()+" to vanilla texture");
                        UUID_randomTextureSuffix.put(id, 0);
                    }
                    if (!UUID_entityAlreadyCalculated.contains(id)){UUID_entityAlreadyCalculated.add(id);}
                }
                if (UUID_randomTextureSuffix.get(id) == 0) {
                    return vanilla;
                } else {
                    return returnOptifineOrVanillaIdentifier(path, UUID_randomTextureSuffix.get(id));
                }

            } else {//true random assign
                hasUpdatableRandomCases.put(id ,false);
                if (Texture_TotalTrueRandom.get(path) > 0) {
                    if (!UUID_randomTextureSuffix.containsKey(id)) {
                        int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();
                        randomReliable %= Texture_TotalTrueRandom.get(path) + 1;
                        if (randomReliable == 1 && ignoreOnePNG.get(path)) {
                            randomReliable = 0;
                        }
                        UUID_randomTextureSuffix.put(id, randomReliable);
                        if (!UUID_entityAlreadyCalculated.contains(id)){UUID_entityAlreadyCalculated.add(id);}
                    }
                    if (UUID_randomTextureSuffix.get(id) == 0) {
                        return vanilla;
                    } else {
                        return returnOptifineOrVanillaIdentifier(path, UUID_randomTextureSuffix.get(id));
                    }
                } else {
                    return vanilla;
                }
            }

        }catch(Exception e){
            modMessage(e.toString(),false);
            return vanilla;
        }
    }
}


