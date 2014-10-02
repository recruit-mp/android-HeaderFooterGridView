/*
 * Copyright (C) 2013 The Android Open Source Project
 * Copyright (C) 2014  Recruit Marketing Partners Co.,Ltd
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

package jp.co.recruit_mp.android.widget;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * A {@link GridView} that supports adding header rows and footer rows in a
 * very similar way to {@link android.widget.ListView}.
 * See {@link HeaderFooterGridView#addHeaderView(View, Object, boolean)} and
 * {@link HeaderFooterGridView#addFooterView(View, Object, boolean)}.
 *
 * This source code is based from
 * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.4_r1/com/android/photos/views/HeaderGridView.java
 */
public class HeaderFooterGridView extends GridView {
    private static final String TAG = "HeaderFooterGridView";

    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    private static class FixedViewInfo {
        /**
         * The view to add to the grid
         */
        public View view;
        public ViewGroup viewContainer;
        /**
         * The data backing the view. This is returned from {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed view should be selectable in the grid
         */
        public boolean isSelectable;
    }

    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();

    private ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<FixedViewInfo>();

    private int mRequestedNumColumns;

    private int mNumColmuns = 1;

    private void initHeaderGridView() {
        super.setClipChildren(false);
    }

    public HeaderFooterGridView(Context context) {
        super(context);
        initHeaderGridView();
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderGridView();
    }

    public HeaderFooterGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeaderGridView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRequestedNumColumns != AUTO_FIT) {
            mNumColmuns = mRequestedNumColumns;
        }
        if (mNumColmuns <= 0) {
            mNumColmuns = 1;
        }

        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter instanceof HeaderFooterViewGridAdapter) {
            ((HeaderFooterViewGridAdapter) adapter).setNumColumns(getNumColumns());
        }
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
        // Ignore, since the header rows depend on not being clipped
    }

    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p/>
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();

        if (adapter != null && !(adapter instanceof HeaderFooterViewGridAdapter)) {
            throw new IllegalStateException(
                    "Cannot add header view to grid -- setAdapter has already been called.");
        }

        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());
        fl.addView(v);
        info.view = v;
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);

        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            ((HeaderFooterViewGridAdapter) adapter).notifyDataSetChanged();
        }
    }

    /**
     * Add a fixed view to appear at the top of the grid. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p/>
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v The view to add.
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    /**
     * Add a fixed view to appear at the bottom of the grid. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p/>
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();

        if (adapter != null && !(adapter instanceof HeaderFooterViewGridAdapter)) {
            throw new IllegalStateException(
                    "Cannot add footer view to grid -- setAdapter has already been called.");
        }

        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());
        fl.addView(v);
        info.view = v;
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);

        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            ((HeaderFooterViewGridAdapter) adapter).notifyDataSetChanged();
        }
    }

    /**
     * Add a fixed view to appear at the bottom of the grid. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p/>
     * NOTE: Call this before calling setAdapter. This is so HeaderFooterGridView can wrap
     * the supplied cursor with one that will also account for header views.
     *
     * @param v The view to add.
     */
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public int getHeaderViewCount() {
        return mHeaderViewInfos.size();
    }

    public int getFooterViewCount() {
        return mFooterViewInfos.size();
    }

    /**
     * Removes a previously-added header view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a header view
     */
    public boolean removeHeaderView(View v) {
        if (mHeaderViewInfos.size() > 0) {
            boolean result = false;
            ListAdapter adapter = getAdapter();
            if (adapter != null && ((HeaderFooterViewGridAdapter) adapter).removeHeader(v)) {
                result = true;
            }
            removeFixedViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    /**
     * Removes a previously-added footer view.
     *
     * @param v The view to remove
     * @return true if the view was removed, false if the view was not a footer view
     */
    public boolean removeFooterView(View v) {
        if (mFooterViewInfos.size() > 0) {
            boolean result = false;
            ListAdapter adapter = getAdapter();
            if (adapter != null && ((HeaderFooterViewGridAdapter) adapter).removeFooter(v)) {
                result = true;
            }
            removeFixedViewInfo(v, mFooterViewInfos);
            return result;
        }
        return false;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; ++i) {
            FixedViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mHeaderViewInfos.size() > 0 || mFooterViewInfos.size() > 0) {
            HeaderFooterViewGridAdapter hadapter = new HeaderFooterViewGridAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
            int numColumns = getNumColumns();
            if (numColumns > 1) {
                hadapter.setNumColumns(numColumns);
            }
            super.setAdapter(hadapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    private class FullWidthFixedViewLayout extends FrameLayout {
        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int targetWidth = HeaderFooterGridView.this.getMeasuredWidth()
                    - HeaderFooterGridView.this.getPaddingLeft()
                    - HeaderFooterGridView.this.getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth,
                    MeasureSpec.getMode(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        // Store specified value for less than Honeycomb.
        mRequestedNumColumns = numColumns;
    }

    @Override
    public int getNumColumns() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return super.getNumColumns();
        }

        // Return value for less than Honeycomb.
        return mNumColmuns;
    }

    /**
     * ListAdapter used when a HeaderFooterGridView has header views. This ListAdapter
     * wraps another one and also keeps track of the header views and their
     * associated data objects.
     * <p>This is intended as a base class; you will probably not need to
     * use this class directly in your own code.
     */
    private static class HeaderFooterViewGridAdapter implements WrapperListAdapter, Filterable {

        // This is used to notify the container of updates relating to number of columns
        // or headers changing, which changes the number of placeholders needed
        private final DataSetObservable mDataSetObservable = new DataSetObservable();

        private final ListAdapter mAdapter;
        private int mNumColumns = 1;

        // This ArrayList is assumed to NOT be null.
        ArrayList<FixedViewInfo> mHeaderViewInfos;

        ArrayList<FixedViewInfo> mFooterViewInfos;

        boolean mAreAllFixedViewsSelectable;

        private final boolean mIsFilterable;

        public HeaderFooterViewGridAdapter(ArrayList<FixedViewInfo> headerViewInfos, ArrayList<FixedViewInfo> footerViewInfos, ListAdapter adapter) {
            mAdapter = adapter;
            mIsFilterable = adapter instanceof Filterable;

            if (headerViewInfos == null) {
                throw new IllegalArgumentException("headerViewInfos cannot be null");
            }
            if (footerViewInfos == null) {
                throw new IllegalArgumentException("footerViewInfos cannot be null");
            }
            mHeaderViewInfos = headerViewInfos;
            mFooterViewInfos = footerViewInfos;

            mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos));
        }

        public int getHeadersCount() {
            return mHeaderViewInfos.size();
        }

        public int getFootersCount() {
            return mFooterViewInfos.size();
        }

        @Override
        public boolean isEmpty() {
            return (mAdapter == null || mAdapter.isEmpty()) && getHeadersCount() == 0 && getFootersCount() == 0;
        }

        public void setNumColumns(int numColumns) {
            if (numColumns < 1) {
                throw new IllegalArgumentException("Number of columns must be 1 or more");
            }
            if (mNumColumns != numColumns) {
                mNumColumns = numColumns;
                notifyDataSetChanged();
            }
        }

        private boolean areAllListInfosSelectable(ArrayList<FixedViewInfo> infos) {
            if (infos != null) {
                for (FixedViewInfo info : infos) {
                    if (!info.isSelectable) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean removeHeader(View v) {
            for (int i = 0; i < mHeaderViewInfos.size(); i++) {
                FixedViewInfo info = mHeaderViewInfos.get(i);
                if (info.view == v) {
                    mHeaderViewInfos.remove(i);

                    mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos));

                    mDataSetObservable.notifyChanged();
                    return true;
                }
            }

            return false;
        }

        public boolean removeFooter(View v) {
            for (int i = 0; i < mFooterViewInfos.size(); i++) {
                FixedViewInfo info = mFooterViewInfos.get(i);
                if (info.view == v) {
                    mFooterViewInfos.remove(i);

                    mAreAllFixedViewsSelectable = (areAllListInfosSelectable(mHeaderViewInfos) && areAllListInfosSelectable(mFooterViewInfos));

                    mDataSetObservable.notifyChanged();
                    return true;
                }
            }

            return false;
        }

        @Override
        public int getCount() {
            if (mAdapter != null) {
                return (getHeadersCount() * mNumColumns) + mAdapter.getCount() + (mAdapter.getCount() % mNumColumns) + (getFootersCount() * mNumColumns);
            } else {
                return (getHeadersCount() * mNumColumns) + (getFootersCount() * mNumColumns);
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            if (mAdapter != null) {
                return mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
            } else {
                return true;
            }
        }

        @Override
        public boolean isEnabled(int position) {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                return (position % mNumColumns == 0)
                        && mHeaderViewInfos.get(position / mNumColumns).isSelectable;
            }

            // Adapter
            if (position < numHeadersAndPlaceholders + mAdapter.getCount()) {
                final int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = 0;
                if (mAdapter != null) {
                    adapterCount = mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return mAdapter.isEnabled(adjPosition);
                    }
                }
            }

            // Empty item
            if (position < numHeadersAndPlaceholders + mAdapter.getCount() + (mAdapter.getCount() % mNumColumns)) {
                return false;
            }

            // Footer
            int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders + mAdapter.getCount() + (mAdapter.getCount() % mNumColumns) + numFootersAndPlaceholders) {
                return (position % mNumColumns == 0)
                        && mFooterViewInfos.get((position - numHeadersAndPlaceholders - mAdapter.getCount() - (mAdapter.getCount() % mNumColumns)) / mNumColumns).isSelectable;
            }

            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public Object getItem(int position) {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                if (position % mNumColumns == 0) {
                    return mHeaderViewInfos.get(position / mNumColumns).data;
                }
                return null;
            }

            // Adapter
            if (position < numHeadersAndPlaceholders + mAdapter.getCount()) {
                final int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = 0;
                if (mAdapter != null) {
                    adapterCount = mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return mAdapter.getItem(adjPosition);
                    }
                }
            }

            // Empty item
            if (position < numHeadersAndPlaceholders + mAdapter.getCount() + (mAdapter.getCount() % mNumColumns)) {
                return null;
            }

            // Footer
            int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders + mAdapter.getCount() + (mAdapter.getCount() % mNumColumns) + numFootersAndPlaceholders) {
                if (position % mNumColumns == 0) {
                    return mFooterViewInfos.get((position - numHeadersAndPlaceholders - mAdapter.getCount() - (mAdapter.getCount() % mNumColumns)) / mNumColumns).data;
                }
            }

            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public long getItemId(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (mAdapter != null) {
                if (position >= numHeadersAndPlaceholders && position < numHeadersAndPlaceholders + mAdapter.getCount()) {
                    int adjPosition = position - numHeadersAndPlaceholders;
                    int adapterCount = mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return mAdapter.getItemId(adjPosition);
                    }
                }
            }
            return -1;
        }

        @Override
        public boolean hasStableIds() {
            if (mAdapter != null) {
                return mAdapter.hasStableIds();
            }
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                View headerViewContainer = mHeaderViewInfos
                        .get(position / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return headerViewContainer;
                } else {
                    convertView = new View(parent.getContext());
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(headerViewContainer.getHeight());
                    return convertView;
                }
            }

            // Adapter
            if (position < numHeadersAndPlaceholders + mAdapter.getCount()) {
                final int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = 0;
                if (mAdapter != null) {
                    adapterCount = mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return mAdapter.getView(adjPosition, convertView, parent);
                    }
                }
            }

            // Empty item
            if (position < numHeadersAndPlaceholders + mAdapter.getCount() + (mAdapter.getCount() % mNumColumns)) {
                // We need to do this because GridView uses the height of the last item
                // in a row to determine the height for the entire row.
                // TODO Current implementation may not be enough in the case of 3 or more column. May need to be careful on the INVISIBLE View height.
                convertView = mAdapter.getView(mAdapter.getCount() - 1, convertView, parent);
                convertView.setVisibility(View.INVISIBLE);
                return convertView;
            }

            // Footer
            int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders + mAdapter.getCount()  + (mAdapter.getCount() % mNumColumns) + numFootersAndPlaceholders) {
                View footerViewContainer = mFooterViewInfos
                        .get((position - numHeadersAndPlaceholders - mAdapter.getCount() - (mAdapter.getCount() % mNumColumns)) / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return footerViewContainer;
                } else {
                    convertView = new View(parent.getContext());
                    // We need to do this because GridView uses the height of the last item
                    // in a row to determine the height for the entire row.
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(footerViewContainer.getHeight());
                    return convertView;
                }
            }

            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public int getItemViewType(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders && (position % mNumColumns != 0)) {
                // Placeholders get the last view type number
                return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
            }
            if (mAdapter != null && position >= numHeadersAndPlaceholders && position < numHeadersAndPlaceholders + mAdapter.getCount()) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            int numFootersAndPlaceholders = getFootersCount() * mNumColumns;
            if (mAdapter != null && position < numHeadersAndPlaceholders + mAdapter.getCount() + numFootersAndPlaceholders) {
                return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
            }

            return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }

        @Override
        public int getViewTypeCount() {
            if (mAdapter != null) {
                return mAdapter.getViewTypeCount() + 1;
            }
            return 2;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        @Override
        public Filter getFilter() {
            if (mIsFilterable) {
                return ((Filterable) mAdapter).getFilter();
            }
            return null;
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return mAdapter;
        }

        public void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }
    }
}
