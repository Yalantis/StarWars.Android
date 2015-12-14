package com.yalantis.starwars.render;

import android.opengl.GLES20;

import com.yalantis.starwars.Const;
import com.yalantis.starwars.interfaces.Renderable;

import java.nio.FloatBuffer;

/**
 * Started by Artem Kholodnyi on 11/1/15 12:40 PM -- com.yalantis.com.yalantis.starwars
 */
public class StarWarsTiles implements Renderable {

    private final int mBufferId;
    private final StarWarsRenderer mRenderer;

    public StarWarsTiles(StarWarsRenderer renderer, FloatBuffer vboBuffer) {
        this.mRenderer = renderer;

        // copy the buffer into OpenGL's memory

        final int buffers[] = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vboBuffer.capacity() * Const.BYTES_PER_FLOAT,
                vboBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        mBufferId = buffers[0];

        vboBuffer.limit(0);
        vboBuffer = null;
    }

    @Override
    public void render() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        final int stride = Const.BYTES_PER_FLOAT
                * (Const.POSITION_DATA_SIZE
                   + Const.NORMALS_DATA_SIZE
                   + Const.TEXTURE_COORDS_DATA_SIZE
                   + Const.TILE_DATA_SIZE);

        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.positionHandle);
        GLES20.glVertexAttribPointer(mRenderer.positionHandle,
                Const.POSITION_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                0
        );

        // Pass in the normal information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.normalHandle);
        GLES20.glVertexAttribPointer(mRenderer.normalHandle,
                Const.NORMALS_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                Const.BYTES_PER_FLOAT * Const.POSITION_DATA_SIZE
        );

        // Pass in the texture information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.textureCoordinateHandle);
        GLES20.glVertexAttribPointer(mRenderer.textureCoordinateHandle,
                Const.TEXTURE_COORDS_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                Const.BYTES_PER_FLOAT * (Const.POSITION_DATA_SIZE + Const.NORMALS_DATA_SIZE)
        );

        // Pass in the tile x,y information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferId);
        GLES20.glEnableVertexAttribArray(mRenderer.tileXyHandle);
        GLES20.glVertexAttribPointer(mRenderer.tileXyHandle,
                Const.TILE_DATA_SIZE,
                GLES20.GL_FLOAT,
                false,
                stride,
                Const.BYTES_PER_FLOAT * (Const.POSITION_DATA_SIZE + Const.NORMALS_DATA_SIZE + Const.TEXTURE_COORDS_DATA_SIZE)
        );

        // Clear the currently bound buffer (so future OpenGL calls do not use this buffer).
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Draw tiles
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mRenderer.sizeX * mRenderer.sizeY * 6);
    }

    @Override
    public void release() {
        // delete the buffer from OpenGL's memory
        final int[] buffersToDelete = new int[] {mBufferId};
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }

}
