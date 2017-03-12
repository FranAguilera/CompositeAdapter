/**
 *  Copyright 2017 Victorious,Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.victorious.compositeadapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: berickson926
 * <p/>
 * CompositeAdapter allows you to combine multiple distinct data sets into one RecyclerView.
 * <p/>
 * Usage: create RecyclerView Adapters which subclass RecyclerView.Adapter and define your
 * custom data/view holder binding as normal.
 * <p/>
 * Then add each adapter to your CompositeAdapter instance via CompositeAdapter.addAdapter().
 * <p/>
 * Only the CompositeAdapter should be bound to your single RecyclerView instance via ReyclerView.setAdapter().
 * <p/>
 * Note: adding child adapters to your CompositeAdapter AFTER setting it on your RecyclerView may
 * create indeterminate behavior at this time.
 * <p/>
 */
public class CompositeAdapter<AdapterType extends RecyclerView.Adapter> extends RecyclerView.Adapter {

    private List<AdapterType> adapters;
    private SparseArray<AdapterType> viewTypeMapping;

    public CompositeAdapter() {
        adapters = new ArrayList<>();
        viewTypeMapping = new SparseArray<>();
    }

    public void addAdapter(AdapterType adapter) {
        adapter.registerAdapterDataObserver(new AdapterObserver<>(adapter, this));
        adapters.add(adapter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterType adapter = viewTypeMapping.get(viewType);
        return adapter.onCreateViewHolder(parent, viewType);
    }

    //TODO: fix type params to avoid raw type here
    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AdapterInfo<AdapterType> info = getAdapterInfo(position);
        AdapterType adapter = info.getAdapter();
        int relativePosition = info.getRelativePosition();
        adapter.onBindViewHolder(holder, relativePosition);
    }

    @Override
    public int getItemCount() {
        int totalItemCount = 0;
        for (AdapterType adapter : adapters) {
            totalItemCount += adapter.getItemCount();
        }
        return totalItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        AdapterInfo<AdapterType> info = getAdapterInfo(position);
        AdapterType adapter = info.getAdapter();
        int relativePosition = info.getRelativePosition();
        int itemViewType = adapter.getItemViewType(relativePosition);
        viewTypeMapping.put(itemViewType, adapter);
        return itemViewType;
    }

    /**
     * Given the absolute index position within an entire list, identify which specific
     * RecyclerView.Adapter implementation is responsible for that view/data.
     *
     * @param absolutePosition- index position for a single data point within the aggregate list.
     * @return AdapterInfo - wrapper for the specific adapter that owns the VH/data and the relative
     * adapter position.
     */
    private AdapterInfo<AdapterType> getAdapterInfo(int absolutePosition) {
        int position = absolutePosition;
        for (AdapterType adapter : adapters) {
            int adapterItemCount = adapter.getItemCount();
            if (position < adapterItemCount) return new AdapterInfo<>(adapter, position);
            else {
                position -= adapterItemCount;
            }
        }
        throw new IndexOutOfBoundsException("absolutePosition index larger than total item count.");
    }

    /**
     * Given the relative index for a specific adapter, find the absolute position of the view holder
     * within the entire list.
     *
     * @param adapter          source adapter
     * @param relativePosition relative position for adapter.
     * @return absolute position
     */
    private int getAbsolutePosition(AdapterType adapter, int relativePosition) {
        if (!adapters.contains(adapter) || relativePosition < 0 || relativePosition > adapter.getItemCount()) {
            throw new IllegalArgumentException("No adapter exists with CompositeAdapter");
        }
        int absolutePosition = relativePosition;
        for (int i = 0, size = adapters.size(); i < size; i++) {
            AdapterType currentAdapter = adapters.get(i);
            if (currentAdapter.equals(adapter)) {
                return absolutePosition;
            } else {
                absolutePosition += currentAdapter.getItemCount();
            }
        }
        throw new IndexOutOfBoundsException("Relative position index doesn't map to an absolute position.");
    }

    /**
     * AdapterObserver will be attached to each child adapter the CompositeAdapter is responsible for.
     * When the backing data set changes for a given child adapter, the callbacks will be forwarded
     * so that the CompositeAdapter is made aware of overall list changes.
     * <p/>
     * IndexOutOfBounds exceptions will result otherwise.
     */
    private static class AdapterObserver<AdapterType extends RecyclerView.Adapter> extends RecyclerView.AdapterDataObserver {

        private WeakReference<AdapterType> childRef;
        private WeakReference<CompositeAdapter<AdapterType>> compositeRef;

        private AdapterObserver(AdapterType childAdapter, CompositeAdapter<AdapterType> adapter) {
            childRef = new WeakReference<>(childAdapter);
            compositeRef = new WeakReference<>(adapter);
        }

        @Override
        public void onChanged() {
            compositeRef.get().notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (positionStart >= 0) {
                int absolutePosition = compositeRef.get().getAbsolutePosition(childRef.get(), positionStart);
                compositeRef.get().notifyItemRangeChanged(absolutePosition, itemCount);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (positionStart >= 0) {
                int absolutePosition = compositeRef.get().getAbsolutePosition(childRef.get(), positionStart);
                compositeRef.get().notifyItemRangeInserted(absolutePosition, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (positionStart >= 0) {
                int absolutePosition = compositeRef.get().getAbsolutePosition(childRef.get(), positionStart);
                compositeRef.get().notifyItemRangeRemoved(absolutePosition, itemCount);
            }
        }
    }

    private static class AdapterInfo<AdapterType extends RecyclerView.Adapter> {
        private AdapterType adapter;
        private int relativeIndex;

        private AdapterInfo(AdapterType adapter, int relativeIndex) {
            this.adapter = adapter;
            this.relativeIndex = relativeIndex;
        }

        private AdapterType getAdapter() {
            return adapter;
        }

        private int getRelativePosition() {
            return relativeIndex;
        }
    }
}
