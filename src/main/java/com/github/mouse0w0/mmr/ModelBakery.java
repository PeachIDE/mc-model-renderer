package com.github.mouse0w0.mmr;

import com.github.mouse0w0.minecraft.model.McElement;
import com.github.mouse0w0.minecraft.model.McFace;
import com.github.mouse0w0.minecraft.model.McFacing;
import com.github.mouse0w0.minecraft.model.McModel;
import com.github.mouse0w0.mmr.graphics.BufferBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.function.Function;

public final class ModelBakery {

    private static final Matrix4f IDENTIFY = new Matrix4f();

    private static final float SCALE_ROTATION_22_5 = 1.0f / (float) Math.cos(Math.PI / 8d) - 1.0f;
    private static final float SCALE_ROTATION_GENERAL = 1.0f / (float) Math.cos(Math.PI / 4d) - 1.0f;

    private static final float TO_RADIANS = (float) (Math.PI / 180.0);

    private static final int[][] VERTEX_DATA_INDICES = {
            {1, 0, 4, 1, 4, 5}, // Down
            {2, 3, 7, 2, 7, 6}, // Up
            {6, 4, 0, 6, 0, 2}, // North
            {3, 1, 5, 3, 5, 7}, // South
            {2, 0, 1, 2, 1, 3}, // West
            {7, 5, 4, 7, 4, 6}  // East
    };
    private static final int[] TEX_COORDS_INDICES = {0, 1, 3, 0, 3, 2};
    private static final int[] TEX_COORDS_90_INDICES = {1, 3, 2, 1, 2, 0};
    private static final int[] TEX_COORDS_180_INDICES = {3, 2, 0, 3, 0, 1};
    private static final int[] TEX_COORDS_270_INDICES = {2, 0, 1, 2, 1, 3};

    public static void bake(McModel model, Function<String, Vector4f> uvGetter, BufferBuilder buf) {
        Map<String, String> textures = model.getTextures();
        for (McElement element : model.getElements()) {
            Vector3f[] boxPoints = createCubePoints(element);
            Map<McFacing, McFace> faces = element.getFaces();
            for (McFacing facing : faces.keySet()) {
                McFace face = faces.get(facing);
                bakeFace(facing, face, textures, boxPoints, uvGetter, buf);
            }
        }
    }

    private static void bakeFace(McFacing facing, McFace face, Map<String, String> textures, Vector3f[] boxPoints, Function<String, Vector4f> uvGetter, BufferBuilder buf) {
        Vector4f texCoord = createTexCoord(face, textures, uvGetter);
        Vector4f color = getFaceColor(facing);
        int[] posIndices = VERTEX_DATA_INDICES[facing.ordinal()];
        int[] texCoordIndices;
        switch (face.getRotation()) {
            default:
                texCoordIndices = TEX_COORDS_INDICES;
                break;
            case 90:
                texCoordIndices = TEX_COORDS_90_INDICES;
                break;
            case 180:
                texCoordIndices = TEX_COORDS_180_INDICES;
                break;
            case 270:
                texCoordIndices = TEX_COORDS_270_INDICES;
                break;
        }
        for (int i = 0; i < 6; i++) {
            buf.pos(boxPoints[posIndices[i]]).color(color);
            texCoord(texCoord, texCoordIndices[i], buf);
        }
    }

    private static Vector4f getFaceColor(McFacing facing) {
        switch (facing) {
            case DOWN:
                return new Vector4f(0.5f, 0.5f, 0.5f, 1f);
            case NORTH:
            case SOUTH:
                return new Vector4f(0.8f, 0.8f, 0.8f, 1f);
            case WEST:
            case EAST:
                return new Vector4f(0.6f, 0.6f, 0.6f, 1f);
            case UP:
            default:
                return new Vector4f(1f, 1f, 1f, 1f);
        }
    }

    private static Vector3f[] createCubePoints(McElement element) {
        Vector3f from = element.getFrom();
        Vector3f to = element.getTo();
        Matrix4f matrix = getRotationMatrix(element.getRotation());

        Vector3f[] boxPoints = new Vector3f[8];
        for (int i = 0; i < 8; i++) {
            boxPoints[i] = transform(createCubePoint(from, to, i), matrix);
        }
        return boxPoints;
    }

    private static Vector3f createCubePoint(Vector3f from, Vector3f to, int index) {
        float x = (index & 0x4) == 0 ? from.x : to.x;
        float y = (index & 0x2) == 0 ? from.y : to.y;
        float z = (index & 0x1) == 0 ? from.z : to.z;
        return new Vector3f(x, y, z).div(16);
    }

    private static Vector3f transform(Vector3f v, Matrix4f matrix) {
        Vector4f result = new Vector4f(v, 1f).mul(matrix);
        return v.set(result.x, result.y, result.z);
    }

    private static Matrix4f getRotationMatrix(McElement.Rotation rotation) {
        if (rotation == null) return IDENTIFY;

        Matrix4f matrix = new Matrix4f();
        Vector3f scale = new Vector3f();
        switch (rotation.getAxis()) {
            case X:
                matrix.rotateX(rotation.getAngle() * TO_RADIANS);
                scale.set(0f, 1f, 1f);
                break;
            case Y:
                matrix.rotateY(rotation.getAngle() * TO_RADIANS);
                scale.set(1f, 0f, 1f);
                break;
            case Z:
                matrix.rotateZ(rotation.getAngle() * TO_RADIANS);
                scale.set(1f, 1f, 0f);
                break;
        }

        if (rotation.isRescale()) {
            if (Math.abs(rotation.getAngle()) == 22.5F) {
                scale.mul(SCALE_ROTATION_22_5);
            } else {
                scale.mul(SCALE_ROTATION_GENERAL);
            }

            scale.add(1f, 1f, 1f);
        } else {
            scale.set(1f);
        }

        Vector3f origin = rotation.getOrigin().div(16, new Vector3f());
        return matrix.scale(scale).translateLocal(origin).translate(origin.negate());
    }

    private static Vector4f createTexCoord(McFace face, Map<String, String> textures, Function<String, Vector4f> uvGetter) {
        Vector4f texMapUv = uvGetter.apply(getTextureName(face.getTexture(), textures));
        Vector4f faceUv = face.getUv();
        return new Vector4f(
                lerp(normalizeTexCoord(faceUv.x), texMapUv.x, texMapUv.z),
                lerp(normalizeTexCoord(faceUv.y), texMapUv.y, texMapUv.w),
                lerp(normalizeTexCoord(faceUv.z), texMapUv.x, texMapUv.z),
                lerp(normalizeTexCoord(faceUv.w), texMapUv.y, texMapUv.w));
    }

    private static float normalizeTexCoord(float value) {
        return value / 16f;
    }

    private static float lerp(float value, float min, float max) {
        return min + value * (max - min);
    }

    private static String getTextureName(String name, Map<String, String> textures) {
        while (name.charAt(0) == '#') {
            name = textures.get(name.substring(1));
        }
        return name;
    }

    private static void texCoord(Vector4f texCoord, int index, BufferBuilder buf) {
        float u = (index & 0x2) == 0 ? texCoord.x : texCoord.z;
        float v = (index & 0x1) == 0 ? texCoord.y : texCoord.w;
        buf.tex(u, v);
    }
}
