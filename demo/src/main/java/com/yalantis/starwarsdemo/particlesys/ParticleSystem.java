package com.yalantis.starwarsdemo.particlesys;

import android.opengl.GLES20;

import com.yalantis.starwars.interfaces.Renderable;
import com.yalantis.starwarsdemo.util.gl.Const;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import timber.log.Timber;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;

/**
 * Created by Artem Kholodnyi on 11/12/15.
 */
public class ParticleSystem implements Renderable {
    private final ParticleSystemRenderer mRenderer;
    public static final int PARTICLE_COUNT = 1_000;
    private int mBufferId;

    public static final int POS_DATA_SIZE = 3;
    public static final int TEXTURE_COORDS_DATA_SIZE = 2;
    public static final int MISC_DATA_SIZE = 3;


    public ParticleSystem(ParticleSystemRenderer renderer, FloatBuffer vertexBuffer) {
        long startTime = System.currentTimeMillis();

        mRenderer = renderer;

        Timber.d("generated in %d ms", System.currentTimeMillis() - startTime);

        // Copy buffer into OpenGL's memory. After, we don't need to keep the client-side buffers around.
        final int buffers[] = new int[1];
        glGenBuffers(1, buffers, 0);

        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Const.BYTES_PER_FLOAT, vertexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        mBufferId = buffers[0];

        vertexBuffer.limit(0);
        Timber.d("done in %d ms", System.currentTimeMillis() - startTime);
    }


    // use to make native order buffers
    private ShortBuffer makeShortBuffer(short[] arr) {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer ib = bb.asShortBuffer();
        ib.put(arr);
        ib.position(0);
        return ib;
    }

    @Override
    public void render() {
        final int stride = Const.BYTES_PER_FLOAT
                * (POS_DATA_SIZE + TEXTURE_COORDS_DATA_SIZE + MISC_DATA_SIZE);

        // a_Position
        glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.positionHandle);
        GLES20.glVertexAttribPointer(mRenderer.positionHandle,
                POS_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                0
        );

        // a_TexCoordinate
        glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.textureCoordinateHandle);
        GLES20.glVertexAttribPointer(mRenderer.textureCoordinateHandle,
                TEXTURE_COORDS_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                Const.BYTES_PER_FLOAT * (POS_DATA_SIZE)
        );

        // a_Misc
        glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.miscHandle);
        GLES20.glVertexAttribPointer(mRenderer.miscHandle,
                MISC_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                Const.BYTES_PER_FLOAT * (POS_DATA_SIZE + TEXTURE_COORDS_DATA_SIZE)
        );

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Draw tiles
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, PARTICLE_COUNT * 6);
    }

    @Override
    public void release() {
        final int[] buffersToDelete = new int[] { mBufferId };
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
