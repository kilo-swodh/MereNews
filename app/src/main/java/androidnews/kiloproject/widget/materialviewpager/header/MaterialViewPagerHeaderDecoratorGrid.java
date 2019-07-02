package androidnews.kiloproject.widget.materialviewpager.header;

import android.content.Context;
import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import androidnews.kiloproject.widget.materialviewpager.MaterialViewPagerAnimator;
import androidnews.kiloproject.widget.materialviewpager.MaterialViewPagerHelper;
import androidnews.kiloproject.widget.materialviewpager.Utils;

public class MaterialViewPagerHeaderDecoratorGrid extends RecyclerView.ItemDecoration {

    boolean registered = false;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
        final Context context = recyclerView.getContext();

        if(!registered) {
            MaterialViewPagerHelper.registerRecyclerView(context, recyclerView);
            registered = true;
        }

        int headerCells = 1;

        MaterialViewPagerAnimator animator = MaterialViewPagerHelper.getAnimator(context);
        if (animator != null) {
            if (holder.getAdapterPosition() < headerCells) {
                outRect.top = Math.round(Utils.dpToPx(animator.getHeaderHeight() + 10, context));
            }
        }
    }
}
