package com.howaboutthis.satyaraj.qnews;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItemEvent> listItem;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private int headerImage;

    NewsAdapter(List<ListItemEvent> listItem, Context context, int headerImage) {
        this.listItem = listItem;
        this.context = context;
        this.headerImage = headerImage;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
           View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header,parent,false);
            return new ViewHeader(v);

        }
        else if(viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_view,parent,false);
            return new ViewItem(v);

        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

         if(holder instanceof ViewItem ) {

            final ListItemEvent listItemEvent = listItem.get(position);
            ViewItem viewItem = (ViewItem) holder;

            viewItem.headingTextView.setText(listItemEvent.getHeading());
            viewItem.descriptionTextView.setText(listItemEvent.getDescription());
             if (listItemEvent.getTime() != null) {

                 final long daysOld = (System.currentTimeMillis() - listItemEvent.getTime());
                 final String[] time = {null};

                 if (daysOld < 60000) {
                     time[0] = "few seconds ago";
                 } else if (daysOld < 3600000) {
                     long minutes = TimeUnit.MILLISECONDS.toMinutes(daysOld);
                     time[0] = minutes + " minutes ago";
                 } else if (daysOld < 24 * 60 * 60 * 1000) {
                     long hours = TimeUnit.MILLISECONDS.toHours(daysOld);
                     if (hours == 1)
                         time[0] = "an hour ago";
                     else
                         time[0] = hours + " hours ago";
                 }
                 viewItem.timeTextView.setText(time[0]);
             }

            viewItem.imageView.setAlpha(0.5f);

            Picasso.with(context)
                    .load(listItemEvent.getUrlToImage())
                    .resize(1080, 600)
                    .into(viewItem.imageView);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, webActivity.class);
                    intent.putExtra("URL", listItemEvent.getUrl());
                    context.startActivity(intent);
                }
            });
        }
        else if(holder instanceof ViewHeader){
             ViewHeader viewHeader = (ViewHeader) holder;

             Picasso.with(context)
                     .load(headerImage)
                     .into(viewHeader.image);
         }
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

      private class ViewItem extends RecyclerView.ViewHolder {
            TextView headingTextView;
            TextView descriptionTextView;
            ImageView imageView;
            TextView timeTextView;
            ViewItem(View itemView) {
            super(itemView);

            headingTextView = itemView.findViewById(R.id.heading_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            imageView = itemView.findViewById(R.id.imageView);
              timeTextView = itemView.findViewById(R.id.timeTextView);

        }
    }

    private class ViewHeader extends RecyclerView.ViewHolder {
        ImageView image;
        ViewHeader(View v) {
            super(v);
            image = v.findViewById(R.id.header_image_view);
        }
    }
}
