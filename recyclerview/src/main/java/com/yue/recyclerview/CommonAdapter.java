package com.yue.recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 介绍：通用adapter
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2016/11/14.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    protected static final int TYPE_DIVIDER = Integer.MAX_VALUE/2;
    private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER_VIEW = Integer.MIN_VALUE + 1;
    /**
     * RecyclerView使用的，真正的Adapter
     */
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;
    private CommonViewHolder.OnItemClickListener<T> onItemClickListener;
    private CommonViewHolder.OnItemLongClickListener<T> onItemLongClickListener;


    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    protected Context context;
    protected List<T> list ;
    protected LayoutInflater inflater;

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            notifyItemMoved(fromPosition,toPosition);
        }
    };


    /**
     *
     * @param context
     */
    public CommonAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mInnerAdapter = new RealAdapter<>();
        setAdapter(mInnerAdapter);
    }

    public CommonAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        mInnerAdapter = new RealAdapter<>();
        setAdapter(mInnerAdapter);
    }

    public void setOnItemClickListener(CommonViewHolder.OnItemClickListener<T> l) {
        this.onItemClickListener = l;
    }

    public void setOnItemLongClickListener(CommonViewHolder.OnItemLongClickListener<T> l) {
        this.onItemLongClickListener = l;
    }

    /**
     * 刷新数据
     */
    public void notifyDataSetChangedReally(){
        if (null != mInnerAdapter){
            mInnerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 单条刷新
     * @param position
     */
    public void notifyItemChangedReally(int position){
        if (null != mInnerAdapter && position > -1 && position < mInnerAdapter.getItemCount()){
            mInnerAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 单条插入
     * @param position
     */
    public void notifyItemInsertedReally(int position){
        if (null != mInnerAdapter && position > -1 && position < mInnerAdapter.getItemCount()){
            mInnerAdapter.notifyItemInserted(position);
            mInnerAdapter.notifyItemRangeChanged(position, list.size() - position);
        }
    }

    /**
     * 单条删除
     * @param position
     */
    public void notifyItemRemovedReally(int position){
        if (null != mInnerAdapter && position > -1 && position < mInnerAdapter.getItemCount()){
            mInnerAdapter.notifyItemRemoved(position);
            mInnerAdapter.notifyItemRangeChanged(position, list.size() - position);
        }
    }


    /**
     * 单条移动
     * @param fromPosition
     * @param toPosition
     */
    public void notifyItemMovedReally(int fromPosition, int toPosition){
        if (null != mInnerAdapter && fromPosition > -1 && fromPosition < mInnerAdapter.getItemCount()
                && toPosition > -1 && toPosition < mInnerAdapter.getItemCount()){
            mInnerAdapter.notifyItemMoved(fromPosition, toPosition);
            mInnerAdapter.notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition)+1);
        }
    }


    public List<T> getList(){
        return list;
    }

    public void clear(){
        if(list!=null && list.size()>0) {
            list.clear();
        }
    }

    /**
     * 设置数据
     * @param list
     */
    public void setList(List<T> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public void addList(Collection<T> list){
        if (null != list) {
            if (null != this.list){
                this.list.addAll(list);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 添加单条数据
     * @param t
     */
    public void addItem(T t){
        if (null != t){
            if (null != list){
                list.add(t);
            }
        }
        notifyDataSetChanged();
    }

    public void remove(T t){
        if (null != t){
            if (null != list){
                list.remove(t);
            }
        }
        notifyDataSetChanged();
    }

    public void remove(int position){
        if (position > -1 && null != list && list.size() > position){
            list.remove(position);
            if (null != mInnerAdapter){
                mInnerAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * 设置adapter
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {

//        if (adapter != null) {
//            if (!(adapter instanceof RecyclerView.Adapter))
//                throw new RuntimeException("your adapter must be a RecyclerView.Adapter");
//        }

        this.mInnerAdapter = adapter;
        if (mInnerAdapter != null) {
            notifyItemRangeRemoved(getHeaderViewsCount(), mInnerAdapter.getItemCount());
//            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
            mInnerAdapter.registerAdapterDataObserver(mDataObserver);
            notifyItemRangeInserted(getHeaderViewsCount(), mInnerAdapter.getItemCount());
        }

    }

    public RecyclerView.Adapter<RecyclerView.ViewHolder> getInnerAdapter() {
        return mInnerAdapter;
    }

    public void addHeaderView(View header) {

        if (header == null) {
            throw new RuntimeException("header is null");
        }

        mHeaderViews.add(header);
        this.notifyDataSetChanged();
    }

    public void addFooterView(final View footer) {

        if (footer == null) {
            throw new RuntimeException("footer is null");
        }

        mFooterViews.add(footer);
        this.notifyDataSetChanged();
    }

    /**
     * 返回第一个FooterView
     * @return
     */
    public View getFooterView() {
        return  getFooterViewsCount()>0 ? mFooterViews.get(getFooterViewsCount()-1) : null;
    }

    /**
     * 返回第一个HeaderView
     * @return
     */
    public View getHeaderView() {
        return  getHeaderViewsCount()>0 ? mHeaderViews.get(0) : null;
    }

    public void removeHeaderView(View view) {
        mHeaderViews.remove(view);
        this.notifyDataSetChanged();
    }

    public void removeFooterView(View view) {
        mFooterViews.remove(view);
        this.notifyDataSetChanged();
    }

    /**
     * 隐藏或展示Item
     * @param v
     * @param visible
     */
    protected void setItemVisible(View v, boolean visible){
        if (null != v){
            if (visible) {
                if (null != v.getLayoutParams()){
                    v.getLayoutParams().width = AbsListView.LayoutParams.MATCH_PARENT;
                    v.getLayoutParams().height = AbsListView.LayoutParams.WRAP_CONTENT;
                }else {
                    v.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
                }
                v.setVisibility(View.VISIBLE);
            }else {
                if (null != v.getLayoutParams()){
                    v.getLayoutParams().width = -1;
                    v.getLayoutParams().height = 1;
                }else {
                    v.setLayoutParams(new AbsListView.LayoutParams(-1,1));
                }
                v.setVisibility(View.GONE);
            }
        }
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    public boolean isHeader(int position) {
        return getHeaderViewsCount() > 0 && position == 0;
    }

    public boolean isFooter(int position) {
        int lastPosition = getItemCount() - 1;
        return getFooterViewsCount() > 0 && position == lastPosition;
    }

    public View getFooter(int position){
        int footerPosition = getItemCount() - getHeaderViewsCount() - position;
        if (null != mFooterViews && mFooterViews.size() > footerPosition && footerPosition > -1){
            return mFooterViews.get(footerPosition);
        }
        return null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mInnerAdapter.onAttachedToRecyclerView(recyclerView);
        //为了兼容GridLayout
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(position < getHeaderViewsCount()){
                        return gridLayoutManager.getSpanCount();
                    }else if(position >= getHeaderViewsCount() + mInnerAdapter.getItemCount()){
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (viewType < TYPE_HEADER_VIEW + headerViewsCountCount) {
            return new ViewHolder(mHeaderViews.get(viewType - TYPE_HEADER_VIEW));
        } else if (viewType >= TYPE_FOOTER_VIEW && viewType < TYPE_DIVIDER) {
            return new ViewHolder(mFooterViews.get(viewType - TYPE_FOOTER_VIEW));
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType - TYPE_DIVIDER);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (position >= headerViewsCountCount && position < headerViewsCountCount + mInnerAdapter.getItemCount()) {
            mInnerAdapter.onBindViewHolder(holder, position - headerViewsCountCount, payloads);
        } else {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if(layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + getFooterViewsCount() + mInnerAdapter.getItemCount();
    }

    public int getReallyItemCount(){
        return mInnerAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        int innerCount = mInnerAdapter.getItemCount();
        int headerViewsCountCount = getHeaderViewsCount();
        if (position < headerViewsCountCount) {
            return TYPE_HEADER_VIEW + position;
        } else if (headerViewsCountCount <= position && position < headerViewsCountCount + innerCount) {

            int innerItemViewType = mInnerAdapter.getItemViewType(position - headerViewsCountCount);
            if(innerItemViewType >= Integer.MAX_VALUE / 2) {
                throw new IllegalArgumentException("your adapter's return value of getViewTypeCount() must < Integer.MAX_VALUE / 2");
            }
            return innerItemViewType + Integer.MAX_VALUE / 2;
        } else {
            return TYPE_FOOTER_VIEW + position - innerCount;
        }
    }

    public List<T> getData() {
        return list;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected void setVisable(View view,boolean visable){
        if(null!=view){
            if(visable){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.GONE);
            }
        }

    }

    /**
     * 获取具体View
     * @param context
     * @param parent
     * @param viewType
     * @return
     */
    public RecyclerView.ViewHolder getViewHolder(Context context, ViewGroup parent, int viewType){
        return CommonViewHolder.getHolder(context, getLayoutIdByType(viewType), parent);
    }

    /**
     * 获取当前Item数据
     * @param position
     * @return
     */
    public T getItem(final int position){
        // FIXME: 2016/6/15 此处判断条件有问题

        if(list!=null&&position<list.size()&&position>=0){
            return list.get(position);
        }
        return null;
    }

    /**
     * 还是兼容下DiffUtil
     * @param holder
     * @param position
     * @param t
     * @param bundle
     */
    public void changeItem(CommonViewHolder<T> holder, int position, T t, Bundle bundle){

    }

    /**
     * 真实的Adapter
     * @param <K>
     */
    private final class RealAdapter<K> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(context, parent, viewType);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder  holder, int position) {

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            if (null != holder && holder instanceof CommonViewHolder) {
                CommonViewHolder<T> m = (CommonViewHolder<T>) holder;
                if (payloads.isEmpty()) {
                    m.setOnItemClickListener(position, getItem(position), onItemClickListener);
                    m.setOnItemLongClickListener(position, getItem(position), onItemLongClickListener);
                    T t = getItem(position);
                    setItemVisible(holder.itemView, t != null);
                    if(t!=null){
                        bindData(m, position,t);
                    }
                } else {
                    T t = getItem(position);
                    changeItem(m, position, t, (Bundle) payloads.get(0));
                }
            }else{
                throw new RuntimeException("Holder must be not null !");
            }
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return getInnerViewType(position);
        }
    }


    public final int getInnerViewType(int position) {
        return getViewType(position);
    }

    /**
     * 为了方便不想每次都重写，默认设置0
     * @param position
     * @return
     */
    protected int getViewType(int position){
        return 0;
    }

    public abstract int getLayoutIdByType(int viewType);

    public abstract void bindData(CommonViewHolder<T> holder, int position, T t);

}



