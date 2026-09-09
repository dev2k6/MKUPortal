package vn.edu.mku.portal.ui.common;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class SkeletonHelper {

    public static void startPulseAnimation(View skeletonView) {
        if (skeletonView == null) return;
        ObjectAnimator animator = ObjectAnimator.ofFloat(skeletonView, "alpha", 0.35f, 0.9f);
        animator.setDuration(750);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
        skeletonView.setTag(animator);
    }

    public static void stopPulseAnimation(View skeletonView) {
        if (skeletonView == null) return;
        Object tag = skeletonView.getTag();
        if (tag instanceof ObjectAnimator) {
            ((ObjectAnimator) tag).cancel();
        }
        skeletonView.setAlpha(1.0f);
    }
}