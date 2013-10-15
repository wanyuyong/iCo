/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package magic.yuyong.animaiton;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

public class Rotate3dAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mFromX;
    private final float mToX;
    private final float mFromY;
    private final float mToY;
    private final float mFromAlpha;
    private final float mToAlpha;
    private final float mCenterX;
    private final float mCenterY;
    private Camera mCamera;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees   the end angle of the 3D rotation
     * @param fromX
     * @param toX
     * @param fromY
     * @param toY
     * @param fromAlpha
     * @param toAlpha
     * @param centerX     the X center of the 3D rotation
     * @param centerY     the Y center of the 3D rotation
     */
    public Rotate3dAnimation(float fromDegrees, float toDegrees, float fromX, float toX, float fromY, float toY,
                             float fromAlpha, float toAlpha,
                             float centerX, float centerY) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mFromX = fromX;
        mToX = toX;
        mFromY = fromY;
        mToY = toY;
        mFromAlpha = fromAlpha;
        mToAlpha = toAlpha;
        mCenterX = centerX;
        mCenterY = centerY;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees = mFromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);
        float dx = mFromX + ((mToX - mFromX) * interpolatedTime);
        float dy = mFromY + ((mToY - mFromY) * interpolatedTime);
        float alpha = mFromAlpha + ((mToAlpha - mFromAlpha) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();

        camera.translate(dx, dy, 0);
        camera.rotateY(degrees);
        camera.getMatrix(matrix);

        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);

        t.setAlpha(alpha);
    }
}
