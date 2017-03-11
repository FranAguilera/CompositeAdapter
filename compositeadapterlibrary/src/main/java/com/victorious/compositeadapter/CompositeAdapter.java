package com.victorious.compositeadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CompositeAdapter<T extends RecyclerView.Adapter> extends RecyclerView.Adapter {

    private List<T> adapters;
    private Map<T, List<Integer>> adapterViewTypeMapping;

    public CompositeAdapter() {
        adapters = new ArrayList<>();
        adapterViewTypeMapping = new HashMap<>();
    }

    public void addAdapter(T adapter) {
        adapter.registerAdapterDataObserver(new AdapterObserver(adapter, this));
        adapters.add(adapter);
        adapterViewTypeMapping.put(adapter, new ArrayList<Integer>());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (int i = 0, size = adapters.size(); i < size; i++) {
            T adapter = adapters.get(i);
            List<Integer> supportedTypes = adapterViewTypeMapping.get(adapter);
            //noinspection AutoBoxing
            if (supportedTypes.contains(viewType)) {
                return adapter.onCreateViewHolder(parent, viewType);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AdapterInfo info = getAdapterInfo(position);
        T adapter = info.getAdapter();
        int relativePosition = info.getRelativePosition();
        //noinspection unchecked
        adapter.onBindViewHolder(holder, relativePosition);
    }

    @Override
    public int getItemCount() {
        int totalItemCount = 0;
        for (int i = 0, size = adapters.size(); i < size; i++) {
            totalItemCount += adapters.get(i).getItemCount();
        }
        return totalItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        AdapterInfo info = getAdapterInfo(position);
        T adapter = info.getAdapter();
        int relativePosition = info.getRelativePosition();
        int itemViewType = adapter.getItemViewType(relativePosition);
        registerItemViewType(adapter, itemViewType);
        return itemViewType;
    }

    /**
     *  Records the itemViewTypes associated with a given RecyclerView.Adapter.
     *  The CompositeAdapter uses this information to avoid delegating onCreateViewHolder() calls
     *  to an adapter which does not support the requested itemViewType.
     *
     *  Maintaining this map internally DOES NOT free individual adapters from being cognizant of
     *  itemViewTypes supported by their counterparts. ItemViewType collision is still possible.
     *
     * @param adapter - a single adapter held by this CompositeAdapter instance.
     * @param itemViewType - view type supported by the given adapter.
     */
    @SuppressWarnings("AutoBoxing")
    private void registerItemViewType(T adapter, int itemViewType) {
        List<Integer> supportedItemViewTypes = adapterViewTypeMapping.get(adapter);
        if (!supportedItemViewTypes.contains(itemViewType)) {
            supportedItemViewTypes.add(itemViewType);
        }
    }

    /**
     * Given the absolute index position within an entire list, identify which specific
     * RecyclerView.Adapter implementation is responsible for that view/data.
     *
     * @param absolutePosition- index position for a single data point within the aggregate list.
     * @return AdapterInfo - wrapper for the specific adapter that owns the VH/data and the relative
     * adapter position.
     */
    private AdapterInfo getAdapterInfo(int absolutePosition) {
        int position = absolutePosition;
        T adapter;
        for (int i = 0, size = adapters.size(); i < size; i++) {
            adapter = adapters.get(i);
            int adapterItemCount = adapter.getItemCount();
            if (position < adapterItemCount) return new AdapterInfo(adapter, position);
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
    private int getAbsolutePosition(T adapter, int relativePosition) {
        if (!adapters.contains(adapter) || relativePosition < 0 || relativePosition > adapter.getItemCount()) {
            throw new IllegalArgumentException("No adapter exists with CompositeAdapter");
        }
        int absolutePosition = relativePosition;
        for (int i = 0, size = adapters.size(); i < size; i++) {
            T currentAdapter = adapters.get(i);
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
    private static class AdapterObserver extends RecyclerView.AdapterDataObserver {

        private WeakReference<RecyclerView.Adapter> childRef;
        private WeakReference<CompositeAdapter> compositeRef;

        private AdapterObserver(RecyclerView.Adapter childAdapter, CompositeAdapter adapter) {
            childRef = new WeakReference<>(childAdapter);
            compositeRef = new WeakReference<>(adapter);
        }

        @Override
        public void onChanged() {
            compositeRef.get().notifyDataSetChanged();
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (positionStart >= 0) {
                int absolutePosition = compositeRef.get().getAbsolutePosition(childRef.get(), positionStart);
                compositeRef.get().notifyItemRangeChanged(absolutePosition, itemCount);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (positionStart >= 0) {
                int absolutePosition = compositeRef.get().getAbsolutePosition(childRef.get(), positionStart);
                compositeRef.get().notifyItemRangeInserted(absolutePosition, itemCount);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (positionStart >= 0) {
                int absolutePosition = compositeRef.get().getAbsolutePosition(childRef.get(), positionStart);
                compositeRef.get().notifyItemRangeRemoved(absolutePosition, itemCount);
            }
        }
    }

    private class AdapterInfo {
        private T adapter;
        private int relativeIndex;

        private AdapterInfo(T adapter, int relativeIndex) {
            this.adapter = adapter;
            this.relativeIndex = relativeIndex;
        }

        private T getAdapter() {
            return adapter;
        }

        private int getRelativePosition() {
            return relativeIndex;
        }
    }
}
