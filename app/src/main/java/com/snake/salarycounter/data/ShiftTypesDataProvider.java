package com.snake.salarycounter.data;

import com.snake.salarycounter.models.ShiftType;

import java.util.List;

/**
 * Created by snake on 14.10.2015.
 */
public class ShiftTypesDataProvider extends AbstractDataProvider {

    private List<ShiftType> mData;
    private ShiftType mLastRemovedData;
    private int mLastRemovedPosition = -1;

    public ShiftTypesDataProvider() {
        //final String atoz = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        mData = ShiftType.allShiftTypes();
        /*new LinkedList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < atoz.length(); j++) {
                final long id = mData.size();
                final int viewType = 0;
                final String text = Character.toString(atoz.charAt(j));
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(id, viewType, text, swipeReaction));
            }
        }*/
    }

    @Override
    public int getCount() {
        return ShiftType.allShiftTypes().size();
    }

    @Override
    public AbstractDataProvider.Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return ShiftType.getByPosition(index);
    }

    @Override
    public int undoLastRemoval() {
        mData = ShiftType.allShiftTypes();
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            new ShiftType(insertedPosition, mLastRemovedData).save();

            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        ShiftType st1 = ShiftType.getByPosition(fromPosition);

        if(fromPosition < toPosition)
        {
            ShiftType.reorderTop(toPosition, st1.weight);
        }
        else
        {
            ShiftType.reorderBottom(toPosition, st1.weight);
        }


        st1.weight = toPosition;
        st1.save();

        //final ConcreteData item = mData.remove(fromPosition);

        //mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        //noinspection UnnecessaryLocalVariable
        //final ConcreteData removedItem = mData.remove(position);

        mLastRemovedData = ShiftType.getByPosition(position); // removedItem;
        mLastRemovedData.delete();
        mLastRemovedPosition = position;
    }
}
