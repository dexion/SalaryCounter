package com.snake.salarycounter.generic;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.GenericAbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.snake.salarycounter.R;
import com.snake.salarycounter.models.ShiftType;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GenericShiftTypeItem extends GenericAbstractItem<ShiftType, GenericShiftTypeItem, GenericShiftTypeItem.ViewHolder> {
    //the static ViewHolderFactory which will be used to generate the ViewHolder for this Item
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public StringHolder undoTextSwipeFromRight;
    public StringHolder undoTextSwipeFromLeft;
    public StringHolder undoTextSwipeFromTop;
    public StringHolder undoTextSwipeFromBottom;

    public int swipedDirection;
    private Runnable swipedAction;

    public GenericShiftTypeItem(ShiftType stype) {
        super(stype);
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.fastadapter_generic_shift_type_item_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.shift_type_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(ViewHolder viewHolder) {
        super.bindView(viewHolder);

        //define our data for the view
        viewHolder.name.setText(getModel().name);
        viewHolder.colored_pane.setBackgroundColor(getModel().color);

        viewHolder.swipeResultContent.setVisibility(swipedDirection != 0 ? View.VISIBLE : View.GONE);
        viewHolder.itemContent.setVisibility(swipedDirection != 0 ? View.GONE : View.VISIBLE);

        CharSequence swipedAction = null;
        CharSequence swipedText = null;
        if (swipedDirection != 0) {
            swipedAction = viewHolder.itemView.getContext().getString(R.string.action_undo);
            swipedText = swipedDirection == ItemTouchHelper.LEFT ? "Удалено" : "Archived";
            viewHolder.swipeResultContent.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), swipedDirection == ItemTouchHelper.LEFT ? R.color.md_red_900 : R.color.md_blue_900));
        }
        viewHolder.swipedAction.setText(swipedAction == null ? "" : swipedAction);
        viewHolder.swipedText.setText(swipedText == null ? "" : swipedText);
        viewHolder.swipedActionRunnable = this.swipedAction;
    }

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        @Bind(R.id.name)
        public TextView name;
        @Bind(R.id.colored_pane)
        public View colored_pane;
        @Bind(R.id.swipe_result_content)
        View swipeResultContent;
        @Bind(R.id.item_content)
        View itemContent;
        @Bind(R.id.swiped_text)
        TextView swipedText;
        @Bind(R.id.swiped_action)
        TextView swipedAction;

        public Runnable swipedActionRunnable;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            swipedAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (swipedActionRunnable != null) {
                        swipedActionRunnable.run();
                    }
                }
            });
        }
    }

    public void setSwipedDirection(int swipedDirection) {
        this.swipedDirection = swipedDirection;
    }

    public void setSwipedAction(Runnable action) {
        this.swipedAction = action;
    }
}
