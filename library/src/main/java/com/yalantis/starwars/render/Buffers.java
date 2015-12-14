package com.yalantis.starwars.render;

import com.yalantis.starwars.Const;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Started by Artem Kholodnyi on 11/1/15 12:48 PM
 */
public class Buffers {

    public static FloatBuffer makeInterleavedBuffer(
            float[] positionData,
            float[] normals,
            float[] uvData,
            float[] tileXyData,
            int tiles) {

        int dataLength = positionData.length + normals.length * tiles + uvData.length + tileXyData.length;

        final FloatBuffer interleavedBuffer = ByteBuffer.allocateDirect(dataLength * Const.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        int positionOffset = 0, normalOffset = 0, textureOffset = 0, tileXyOffset = 0;

        for (int i = 0; i < tiles; i++) {
            for (int j = 0; j < Const.POINTS_PER_TILE; j++) {
                interleavedBuffer.put(positionData, positionOffset, Const.POSITION_DATA_SIZE);
                positionOffset += Const.POSITION_DATA_SIZE;

                interleavedBuffer.put(normals, normalOffset, Const.NORMALS_DATA_SIZE);
                // Normals are the same

                interleavedBuffer.put(uvData, textureOffset, Const.TEXTURE_COORDS_DATA_SIZE);
                textureOffset += Const.TEXTURE_COORDS_DATA_SIZE;

                interleavedBuffer.put(tileXyData, tileXyOffset, Const.TILE_DATA_SIZE);
                tileXyOffset += Const.TILE_DATA_SIZE;
            }
        }


        interleavedBuffer.position(0);
        return interleavedBuffer;
    }
}
