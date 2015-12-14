package com.yalantis.starwars.render;

import com.yalantis.starwars.Const;

/**
 * Created by Artem Kholodnyi on 11/2/15.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public class GenerateVerticesData implements Runnable {

    private final StarWarsRenderer mStarWarsRenderer;

    public GenerateVerticesData(StarWarsRenderer starWarsRenderer) {
        mStarWarsRenderer = starWarsRenderer;
    }

    @Override
    public void run() {
            // X, Y, Z
            final float[] normalData = {
                            // Tile
                            0.0f, 0.0f, -1.0f,
                            0.0f, 0.0f, -1.0f,
                            0.0f, 0.0f, -1.0f,
                            0.0f, 0.0f, -1.0f,
                            0.0f, 0.0f, -1.0f,
                            0.0f, 0.0f, -1.0f
                    };

            final float[] positionData = genPositionData();

            final float[] uvData = genUvData();

            final float[] tileXYData = genTileData();

            // Run on the GL thread -- the same thread the other members of the renderer run in.
            mStarWarsRenderer.mGlSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (mStarWarsRenderer.mPlane != null) {
                        mStarWarsRenderer.mPlane.release();
                        mStarWarsRenderer.mPlane = null;
                    }

                    mStarWarsRenderer.mPlane = new StarWarsTiles(
                            mStarWarsRenderer,
                            Buffers.makeInterleavedBuffer(
                                    positionData,
                                    normalData,
                                    uvData,
                                    tileXYData,
                                    mStarWarsRenderer.sizeX * mStarWarsRenderer.sizeY
                            )
                    );

                }
            });
    }

    private float[] genTileData() {
        float[] tileData = new float[6 * Const.TILE_DATA_SIZE * mStarWarsRenderer.getTilesCount()];

        int tileDataOffset = 0;
        for (int x = 0; x < mStarWarsRenderer.sizeX; x++) {
            for (int y = 0; y < mStarWarsRenderer.sizeY; y++) {

                float[] thisTileData = new float [6 * Const.TILE_DATA_SIZE];

                float rand = (float) Math.random();
                for(int v = 0; v < 6 * Const.TILE_DATA_SIZE; v+= Const.TILE_DATA_SIZE) {
                    thisTileData[v] = x;
                    thisTileData[v + 1] = y;
                    thisTileData[v + 2] = rand;
                }

                System.arraycopy(
                        thisTileData, 0,
                        tileData, tileDataOffset,
                        thisTileData.length
                );

                tileDataOffset += thisTileData.length;
            }
        }

        return tileData;
    }

    private float[] genPositionData() {
        float[] positionData = new float[6 * Const.POSITION_DATA_SIZE * mStarWarsRenderer.sizeX * mStarWarsRenderer.sizeY];

        float height = Const.PLANE_HEIGHT;
        float width = height * mStarWarsRenderer.ratio;

        final float stepX = width * 2f / mStarWarsRenderer.sizeX;
        final float stepY = height * 2f / mStarWarsRenderer.sizeY;

        final float minPositionX = -width;
        final float minPositionY = -height;
        final float z = 0f;

        int positionDataOffset = 0;
        for (int x = 0; x < mStarWarsRenderer.sizeX; x++) {
            for (int y = 0; y < mStarWarsRenderer.sizeY; y++) {
                final float x1 = minPositionX + x * stepX;
                final float x2 = x1 + stepX;

                final float y1 = minPositionY + y * stepY;
                final float y2 = y1 + stepY;

                // Define points for a plane.
                final float[] p1 = {x1, y2, z};
                final float[] p2 = {x2, y2, z};
                final float[] p3 = {x1, y1, z};
                final float[] p4 = {x2, y1, z};

                int elementsPerPoint = p1.length;
                final int size = elementsPerPoint * 6;
                final float[] thisPositionData = new float[size];

                int offset = 0;
                // Build the triangles
                //  1---2
                //  | / |
                //  3---4
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p1[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p3[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p2[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p3[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p4[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p2[i];
                }

                System.arraycopy(
                        thisPositionData, 0,
                        positionData, positionDataOffset,
                        thisPositionData.length
                );
                positionDataOffset += thisPositionData.length;
            }
        }

        return positionData;
    }

    private float[] genUvData() {
        float[] uvData = new float[6 * Const.TEXTURE_COORDS_DATA_SIZE *
                mStarWarsRenderer.sizeX * mStarWarsRenderer.sizeY];
        int uvDataOffset = 0;

        final float stepX = 1f / mStarWarsRenderer.sizeX;
        final float stepY = 1f / mStarWarsRenderer.sizeY;

        for (int x = mStarWarsRenderer.sizeX - 1; x >= 0; x--) {
            for (int y = mStarWarsRenderer.sizeY - 1; y >= 0; y--) {
                final float u0 = x * stepX;
                final float v0 = y * stepY;
                final float u1 = u0 + stepX;
                final float v1 = v0 + stepY;

                final int elementsPerPoint = 2;
                final int size = elementsPerPoint * 6;
                final float[] thisUvData = new float[size];

                int offset = 0;
                // Build the triangles
                //  1---2
                //  | / |
                //  3---4
                // Define points for a plane.

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

                System.arraycopy(
                        thisUvData, 0,
                        uvData, uvDataOffset,
                        thisUvData.length
                );
                uvDataOffset += thisUvData.length;
            }
        }

        return uvData;
    }

}

