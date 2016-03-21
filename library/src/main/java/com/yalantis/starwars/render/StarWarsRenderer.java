package com.yalantis.starwars.render;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.yalantis.starwars.Const;
import com.yalantis.starwars.R;
import com.yalantis.starwars.TilesFrameLayout;
import com.yalantis.starwars.utils.gl.RawResourceReader;
import com.yalantis.starwars.utils.gl.ShaderHelper;
import com.yalantis.starwars.utils.gl.TextureHelper;
import com.yalantis.starwars.widget.StarWarsTilesGLSurfaceView;

import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;


/**
 * Created by Artem Kholodnyi on 11/2/15.
 */
public class StarWarsRenderer implements GLSurfaceView.Renderer {
    /** Store the accumulated rotation. */
    private final float[] mAccumulatedRotation = new float[16];
    /** Store the current rotation. */
    private final float[] mCurrentRotation = new float[16];
    public StarWarsTiles mPlane;
    public GLSurfaceView mGlSurfaceView;
    public float ratio;
    public int mvpMatrixHandle;
    public int mvMatrixHandle;
    public int textureUniformHandle;
    public int positionHandle;
    public int normalHandle;
    public int textureCoordinateHandle;
    public int programHandle;
    public int tileXyHandle;
    public int deltaPosHandle;
    public int sizeX;
    public int sizeY;
    private TilesFrameLayout mListener;
    private int mNumberOfTilesX = 35;
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];
    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];
    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];
    private int mAndroidDataHandle;
    private float[] mTemporaryMatrix = new float[16];
    private int frames;
    private long startTime;

    private float deltaPosX;
    private long timePassed;
    private ValueAnimator animator;
    private long mAnimationDuration;
    private boolean requestedReveal;


    public StarWarsRenderer(StarWarsTilesGLSurfaceView glSurfaceView,
                            TilesFrameLayout TilesFrameLayout,
                            int animationDuration, int numberOfTilesX) {
        mGlSurfaceView = glSurfaceView;
        mListener = TilesFrameLayout;
        mAnimationDuration = animationDuration;
        mNumberOfTilesX = numberOfTilesX;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CW);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Position the eye in front of the origin.
        final float eyeX =  0.0f;
        final float eyeY =  0.0f;
        final float eyeZ =  0.0f;

        // We are looking toward the distance
        final float lookX =  0.0f;
        final float lookY =  0.0f;
        final float lookZ =  1.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mGlSurfaceView.getContext(), R.raw.tiles_vert);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mGlSurfaceView.getContext(), R.raw.tiles_frag);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        programHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_Normal", "a_TexCoordinate"});

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);
    }

    private void genTilesData() {
        Executors.newSingleThreadExecutor().submit(new GenerateVerticesData(this));
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        sizeX = mNumberOfTilesX;
        sizeY = height * sizeX /  width;

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;

        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        this.ratio = ratio;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        genTilesData();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        logFrame();
        drawGl();
        if (!requestedReveal && mAndroidDataHandle > 0) {
            requestedReveal = true;
            mListener.reveal();
        }
    }

    private void drawGl() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mAndroidDataHandle > 0) {

            GLES20.glUseProgram(programHandle);

            // Set program handles
            mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
            mvMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVMatrix");
            textureUniformHandle = GLES20.glGetUniformLocation(programHandle, "u_Texture");
            deltaPosHandle = GLES20.glGetUniformLocation(programHandle, "u_DeltaPos");

            positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
            normalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
            textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate");
            tileXyHandle = GLES20.glGetAttribLocation(programHandle, "a_TileXY");

            Matrix.setIdentityM(mModelMatrix, 0);
            Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, 5f);

            // Set a matrix that contains the current rotation.
            Matrix.setIdentityM(mCurrentRotation, 0);

            Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
            System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

            // Pass in the modelview matrix.
            GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mMVPMatrix, 0);

            Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
            System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

            // Pass in the combined matrix.
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mMVPMatrix, 0);

            // Pass in u_Gravity
            GLES20.glUniform1f(deltaPosHandle, deltaPosX);

            // Pass in the texture information
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mAndroidDataHandle);

            GLES20.glUniform1i(textureUniformHandle, 0);

            if (mPlane != null) {
                mPlane.render();
            }

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }

    public int getTilesCount() {
        return sizeX * sizeY;
    }
    
    public void logFrame() {
        frames++;
        timePassed = (System.nanoTime() - startTime) / 1_000_000;
        if(timePassed >= 1000) {
            Timber.d("%d tiles @ %d fps", getTilesCount(), frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }

    public void startAnimation() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                animator = ValueAnimator.ofFloat(0, -Const.PLANE_HEIGHT * 2); // plane height
                animator.setDuration(mAnimationDuration);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        deltaPosX = value;
                        mGlSurfaceView.requestRender();
                    }
                });
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mGlSurfaceView.requestRender();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mListener.onAnimationFinished();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                animator.start();

            }
        });
    }

    public void updateTexture(final Bitmap bitmap) {
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                requestedReveal = false;
                mAndroidDataHandle = TextureHelper.loadTexture(bitmap);
                mGlSurfaceView.requestRender();
            }
        });
    }

    public void cancelAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.removeAllListeners();
            animator.cancel();
        }
    }
}
