/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

public class SkeletonHelper {

    public static void startPulseAnimation(View skeletonView) {
        if (skeletonView == null) return;
        stopPulseAnimation(skeletonView);

        ObjectAnimator animator = ObjectAnimator.ofFloat(skeletonView, "alpha", 0.35f, 0.92f);
        animator.setDuration(750);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
        skeletonView.setTag(animator);
    }

    public static void stopPulseAnimation(View skeletonView) {
        if (skeletonView == null) return;
        Object tag = skeletonView.getTag();
        if (tag instanceof Animator) {
            ((Animator) tag).cancel();
        }
        skeletonView.setAlpha(1.0f);
    }

    public static void showSkeleton(View skeletonView, View contentView) {
        if (skeletonView != null) {
            skeletonView.setVisibility(View.VISIBLE);
            skeletonView.setAlpha(1.0f);
            startPulseAnimation(skeletonView);
        }
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
    }

    public static void hideSkeleton(View skeletonView, View contentView) {
        if (skeletonView == null && contentView == null) return;

        if (skeletonView != null && skeletonView.getVisibility() == View.VISIBLE) {
            stopPulseAnimation(skeletonView);
            skeletonView.animate()
                    .alpha(0f)
                    .setDuration(220)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            skeletonView.setVisibility(View.GONE);
                            skeletonView.setAlpha(1.0f);
                        }
                    })
                    .start();
        }

        if (contentView != null) {
            contentView.setAlpha(0f);
            contentView.setVisibility(View.VISIBLE);
            contentView.animate()
                    .alpha(1f)
                    .setDuration(250)
                    .setListener(null)
                    .start();
        }
    }

    public static void populateSkeletonRows(Context context, ViewGroup container, int rowLayoutResId, int count) {
        if (context == null || container == null) return;
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < count; i++) {
            View row = inflater.inflate(rowLayoutResId, container, false);
            container.addView(row);
        }
        startPulseAnimation(container);
    }
}