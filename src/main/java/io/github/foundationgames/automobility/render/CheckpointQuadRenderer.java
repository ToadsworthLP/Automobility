package io.github.foundationgames.automobility.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CheckpointQuadRenderer {
    private static class QuadDefinition {
        public QuadDefinition(Vec3d position, Direction.Axis axis) {
            this.position = position;
            this.axis = axis;
        }

        public Vec3d position;
        public Direction.Axis axis;
    }

    private static final double VIEW_DISTANCE_SQUARED = 10000;
    private static final double QUAD_WIDTH = 16;
    private static final Identifier FORCEFIELD_TEXTURE = new Identifier("textures/misc/forcefield.png");

    private final List<QuadDefinition> quads = new ArrayList<>();

    private final Vec3f color;
    private final float alpha;

    public CheckpointQuadRenderer(Vec3f color, float alpha) {
        this.color = color;
        this.alpha = alpha;

        WorldRenderEvents.LAST.register(this::renderQuads);
    }

    public void init() {
    }

    public void addAt(Vec3d position, Direction.Axis axis) {
        quads.add(new QuadDefinition(position, axis));
    }

    public void removeAt(Vec3d position, Direction.Axis axis) {
        quads.removeIf((QuadDefinition quad) -> {
            return quad.position.equals(position) && quad.axis.equals(axis);
        });
    }

    private void renderQuads(WorldRenderContext context) {
        if(quads.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = context.camera();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        double cameraX = camera.getPos().x;
        double cameraZ = camera.getPos().z;
        double h = client.gameRenderer.method_32796();

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShaderTexture(0, FORCEFIELD_TEXTURE);
        RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        RenderSystem.applyModelViewMatrix();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.polygonOffset(-3.0F, -3.0F);
        RenderSystem.enablePolygonOffset();
        RenderSystem.disableCull();

        float textureScrollingProgress = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0F;
        float p = (float)(h - MathHelper.fractionalPart(camera.getPos().y));

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float ah;
        float ae;
        double af;
        double ag;

        double q;
        double r;

        RenderSystem.setShaderColor(color.getX(), color.getY(), color.getZ(), alpha);

        for (QuadDefinition entry : quads) {
            if(camera.getPos().squaredDistanceTo(entry.position) > VIEW_DISTANCE_SQUARED) continue;

            if(entry.axis == Direction.Axis.Z) {

                double xCoord = entry.position.x;

                q = MathHelper.floor(entry.position.z - QUAD_WIDTH);
                r = MathHelper.ceil(entry.position.z + QUAD_WIDTH);

                ae = 0.0F;

                for(af = q; af < r; ae += 0.5F) {
                    ag = Math.min(1.0D, r - af);
                    ah = (float)ag * 0.5F;
                    bufferBuilder.vertex(xCoord - cameraX, -h, af - cameraZ).texture(textureScrollingProgress - ae, textureScrollingProgress + p).next();
                    bufferBuilder.vertex(xCoord - cameraX, -h, af + ag - cameraZ).texture(textureScrollingProgress - (ah + ae), textureScrollingProgress + p).next();
                    bufferBuilder.vertex(xCoord - cameraX, h, af + ag - cameraZ).texture(textureScrollingProgress - (ah + ae), textureScrollingProgress + 0.0F).next();
                    bufferBuilder.vertex(xCoord - cameraX, h, af - cameraZ).texture(textureScrollingProgress - ae, textureScrollingProgress + 0.0F).next();
                    ++af;
                }

            } else if(entry.axis == Direction.Axis.X) {

                double zCoord = entry.position.z;

                q = MathHelper.floor(entry.position.x - QUAD_WIDTH);
                r = MathHelper.ceil(entry.position.x + QUAD_WIDTH);

                ae = 0.0F;

                for(af = q; af < r; ae += 0.5F) {
                    ag = Math.min(1.0D, r - af);
                    ah = (float)ag * 0.5F;
                    bufferBuilder.vertex(af - cameraX, -h, zCoord - cameraZ).texture(textureScrollingProgress + ae, textureScrollingProgress + p).next();
                    bufferBuilder.vertex(af + ag - cameraX, -h, zCoord - cameraZ).texture(textureScrollingProgress + ah + ae, textureScrollingProgress + p).next();
                    bufferBuilder.vertex(af + ag - cameraX, h, zCoord - cameraZ).texture(textureScrollingProgress + ah + ae, textureScrollingProgress + 0.0F).next();
                    bufferBuilder.vertex(af - cameraX, h, zCoord - cameraZ).texture(textureScrollingProgress + ae, textureScrollingProgress + 0.0F).next();
                    ++af;
                }
            }
        }


        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableCull();
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
        RenderSystem.disableBlend();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
    }
}
