package com.yalantis.starwarsdemo.particlesys;

import com.yalantis.starwarsdemo.util.gl.Const;
import com.yalantis.starwarsdemo.util.gl.math.MathHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Started by Artem Kholodnyi on 11/29/15 6:13 PM -- com.yalantis.starwarsdemo.particlesys
 */
public class ParticlesGenerator implements Runnable {
    private final ParticleSystemRenderer mRenderer;

    public ParticlesGenerator(ParticleSystemRenderer particleSystemRenderer) {
        mRenderer = particleSystemRenderer;
    }

    @Override
    public void run() {
        final FloatBuffer vertexBuffer = makeInterleavedBuffer(
                generatePositions(),
                generateTextureUv(),
                generateMisc(),
                ParticleSystem.PARTICLE_COUNT
        );
        mRenderer.queue(new Runnable() {
            @Override
            public void run() {
                mRenderer.setParticleSystem(new ParticleSystem(mRenderer, vertexBuffer));
            }
        });
    }

    private float[] generatePositions() {
        final int size = ParticleSystem.PARTICLE_COUNT * ParticleSystem.POS_DATA_SIZE * 6;

        final float height = 5.0f;
        final float width = height * mRenderer.ratio;

        final float[] posData = new float[size];

        final float z = 0f;

        int offset = 0;
        for (int p = 0; p < ParticleSystem.PARTICLE_COUNT; p++) {
            final float uc = MathHelper.mix(-width, width, Math.random());
            final float vc = MathHelper.mix(-height, height, Math.random());

            final float u0 = uc;// + delta;
            final float v0 = vc;// + delta;
            final float u1 = uc;// - delta;
            final float v1 = vc;// - delta;

            final int elementsPerPoint = ParticleSystem.POS_DATA_SIZE;

            //  1---2
            //  | / |
            //  3---4

            final float[] p1 = {u1, v0, z};
            final float[] p2 = {u0, v0, z};
            final float[] p3 = {u1, v1, z};
            final float[] p4 = {u0, v1, z};

            for (int i = 0; i < elementsPerPoint; i++)
                posData[offset++] = p1[i];
            for (int i = 0; i < elementsPerPoint; i++)
                posData[offset++] = p3[i];
            for (int i = 0; i < elementsPerPoint; i++)
                posData[offset++] = p2[i];

            for (int i = 0; i < elementsPerPoint; i++)
                posData[offset++] = p3[i];
            for (int i = 0; i < elementsPerPoint; i++)
                posData[offset++] = p4[i];
            for (int i = 0; i < elementsPerPoint; i++)
                posData[offset++] = p2[i];
        }

        return posData;
    }

    private float[] generateTextureUv() {
        int size = ParticleSystem.TEXTURE_COORDS_DATA_SIZE * 6;

        final float[] thisUvData = new float[size];

        final float u0 = 0f;
        final float v0 = 0f;
        final float u1 = 1f;
        final float v1 = 1f;

        final int elementsPerPoint = ParticleSystem.TEXTURE_COORDS_DATA_SIZE;

        int offset = 0;

        //  1---2
        //  | / |
        //  3---4

        final float[] p1 = {u1, v0};
        final float[] p2 = {u0, v0};
        final float[] p3 = {u1, v1};
        final float[] p4 = {u0, v1};

        for (int i = 0; i < elementsPerPoint; i++)
            thisUvData[offset++] = p1[i];
        for (int i = 0; i < elementsPerPoint; i++)
            thisUvData[offset++] = p3[i];
        for (int i = 0; i < elementsPerPoint; i++)
            thisUvData[offset++] = p2[i];

        for (int i = 0; i < elementsPerPoint; i++)
            thisUvData[offset++] = p3[i];
        for (int i = 0; i < elementsPerPoint; i++)
            thisUvData[offset++] = p4[i];
        for (int i = 0; i < elementsPerPoint; i++)
            thisUvData[offset++] = p2[i];

        return thisUvData;
    }

    private float[] generateMisc() {
        final int dataLength = ParticleSystem.PARTICLE_COUNT * ParticleSystem.MISC_DATA_SIZE * 6;

        float[] starData = new float[dataLength];

        int offset = 0;
        for (int i = 0; i < ParticleSystem.PARTICLE_COUNT; i++) {
            final float particleSize = MathHelper.mix(0.05f, 0.15f, Math.random());
            float accel = MathHelper.mix(1.1f, 3f, Math.random());
            float rand = (float) Math.random();

            for (int v = 0; v < 6; v++) {
                starData[offset++] = particleSize;
                starData[offset++] = accel;
                starData[offset++] = rand;
            }
        }

        return starData;

    }


    private FloatBuffer makeInterleavedBuffer(float[] posData,
                                              float[] uvData,
                                              float[] miscData,
                                              int numStars) {

        int dataLength = posData.length
                + uvData.length * numStars
                + miscData.length;

        final FloatBuffer interleavedBuffer = ByteBuffer.allocateDirect(dataLength * Const.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        int positionOffset = 0, uvOffset = 0, starDataOffset = 0;

        for (int i = 0; i < numStars; i++) {
            for (int v = 0; v < 6; v++) {
                interleavedBuffer.put(posData, positionOffset, ParticleSystem.POS_DATA_SIZE);
                positionOffset += ParticleSystem.POS_DATA_SIZE;

                interleavedBuffer.put(uvData, uvOffset, ParticleSystem.TEXTURE_COORDS_DATA_SIZE);
                uvOffset = (uvOffset + ParticleSystem.TEXTURE_COORDS_DATA_SIZE ) % uvData.length;

                interleavedBuffer.put(miscData, starDataOffset, ParticleSystem.MISC_DATA_SIZE);
                starDataOffset += ParticleSystem.MISC_DATA_SIZE;
            }
        }

        interleavedBuffer.position(0);
        return interleavedBuffer;
    }
}
