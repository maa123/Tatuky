/* Copyright 2017 Andrew Dawson
 *
 * This file is part of Tusky.
 *
 * Tusky is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky. If not, see
 * <http://www.gnu.org/licenses/>. */

package com.keylesspalace.tusky;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter implements AdapterItemRemover {
    private static final int VIEW_TYPE_STATUS = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private List<Status> statuses;
    private StatusActionListener statusListener;
    private FooterActionListener footerListener;

    public TimelineAdapter(StatusActionListener statusListener,
            FooterActionListener footerListener) {
        super();
        statuses = new ArrayList<>();
        this.statusListener = statusListener;
        this.footerListener = footerListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            default:
            case VIEW_TYPE_STATUS: {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_status, viewGroup, false);
                return new StatusViewHolder(view);
            }
            case VIEW_TYPE_FOOTER: {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_footer, viewGroup, false);
                return new FooterViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position < statuses.size()) {
            StatusViewHolder holder = (StatusViewHolder) viewHolder;
            Status status = statuses.get(position);
            holder.setupWithStatus(status, statusListener, position);
        } else {
            FooterViewHolder holder = (FooterViewHolder) viewHolder;
            holder.setupButton(footerListener);
        }
    }

    @Override
    public int getItemCount() {
        return statuses.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == statuses.size()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_STATUS;
        }
    }

    public int update(List<Status> newStatuses) {
        int scrollToPosition;
        if (statuses == null || statuses.isEmpty()) {
            statuses = newStatuses;
            scrollToPosition = 0;
        } else {
            int index = newStatuses.indexOf(statuses.get(0));
            if (index == -1) {
                statuses.addAll(0, newStatuses);
                scrollToPosition = 0;
            } else {
                statuses.addAll(0, newStatuses.subList(0, index));
                scrollToPosition = index;
            }
        }
        notifyDataSetChanged();
        return scrollToPosition;
    }

    public void addItems(List<Status> newStatuses) {
        int end = statuses.size();
        statuses.addAll(newStatuses);
        notifyItemRangeInserted(end, newStatuses.size());
    }

    public void removeItem(int position) {
        statuses.remove(position);
        notifyItemRemoved(position);
    }

    public @Nullable Status getItem(int position) {
        if (position >= 0 && position < statuses.size()) {
            return statuses.get(position);
        }
        return null;
    }
}
