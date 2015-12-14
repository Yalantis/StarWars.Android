package com.yalantis.starwarsdemo.particlesys;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.yalantis.starwars.utils.gl.RawResourceReader;
import com.yalantis.starwars.utils.gl.ShaderHelper;
import com.yalantis.starwarsdemo.App;
import com.yalantis.starwarsdemo.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import timber.log.Timber;

/**
 * Created by Artem Kholodnyi on 11/12/15.
 */
public class ParticleSystemRenderer implements GLSurfaceView.Renderer {
    public float ratio;
    public int mvpMatrixHandle;
    public int mvMatrixHandle = -1;
    public int positionHandle;
    public int normalHandle;
    public int textureCoordinateHandle;
    public int programHandle;
    public int miscHandle;
    public int sizeX = 35;
    public int sizeY = 70;
    public float mTime;
    private GLSurfaceView mGlSurfaceView;
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
    private float[] mTemporaryMatrix = new float[16];
    private int timeHandle;
    private long mStartTime;
    private int frames;
    private long startTime;
    private boolean mStart;
    private long timePassed;
    private float dt;
    private long t_current;
    private long t_prev;
    private float dt_prev = 1;
    private ValueAnimator animator;
    private Bitmap mBitmap;
    private ParticleSystem mParticleSystem;
    private int resolutionHandle;
    private int mWidth;
    private int mHeight;
    private int timesRepeated;
    private float delta;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();


    public ParticleSystemRenderer(GLSurfaceView glSurfaceView) {
        mGlSurfaceView = glSurfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

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

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(App.getAppContext(), R.raw.star_vert);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(App.getAppContext(), R.raw.star_frag);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        programHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_TexCoordinate", "a_TileXY"});
    }

    /**
     *
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        mWidth = width;
        mHeight = height;

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;

        final float left = -ratio;
        @SuppressWarnings("UnnecessaryLocalVariable")
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        this.ratio = ratio;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        mStartTime = System.currentTimeMillis();

        mExecutor.execute(new ParticlesGenerator(this));
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        logFrame();
        drawGl();

        if (mParticleSystem != null) {
            mParticleSystem.render();
        }
    }

    private void drawGl() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(programHandle);

        // Set program handles
        mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mvMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVMatrix");
        timeHandle = GLES20.glGetUniformLocation(programHandle, "u_Time");
        resolutionHandle = GLES20.glGetUniformLocation(programHandle, "u_Resolution");

        positionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        normalHandle = GLES20.glGetAttribLocation(programHandle, "a_Normal");
        textureCoordinateHandle = GLES20.glGetAttribLocation(programHandle, "a_TexCoordinate");
        miscHandle = GLES20.glGetAttribLocation(programHandle, "a_Misc");

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, 5f);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in u_Time
        GLES20.glUniform1f(timeHandle, (System.currentTimeMillis() - mStartTime) / 3500f);

        // u_Resolution
        GLES20.glUniform2f(resolutionHandle, mWidth, mHeight);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
    }

    public void logFrame() {
        frames++;
        timePassed = (System.nanoTime() - startTime) / 1_000_000;
        if(timePassed >= 10_000) {
            Timber.d("fps %d @ %d stars || %f", frames / 10, ParticleSystem.PARTICLE_COUNT, mTime);
            frames = 0;
            startTime = System.nanoTime();
        }
    }

    public void onTouchEvent() {
        if (mStart) {
            reset();
        }
        mStart = !mStart;
        mStartTime = System.nanoTime();
    }

    private void reset() {
        if (animator != null) {
            animator.cancel();
        }
        mStartTime = 0;
        dt = 0;
        t_prev = 0;
    }


    public ParticleSystem getParticleSystem() {
        return mParticleSystem;
    }

    public void setParticleSystem(final ParticleSystem particleSystem) {
        mParticleSystem = particleSystem;
    }

    public void queue(Runnable runnable) {
        mGlSurfaceView.queueEvent(runnable);
    }
}
