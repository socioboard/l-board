package com.socioboard.lbroadpro.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.WebActivity;
import com.socioboard.lbroadpro.models.CommentModel;

public class CommentsAdapter extends BaseAdapter{

	Context context;
	ArrayList<CommentModel> commentlist;
	String comment;
	
	public CommentsAdapter(Context context,ArrayList<CommentModel> commentlist) {
 
		this.commentlist = commentlist;
		this.context = context;
	}
	@Override
	public int getCount() {
		return commentlist.size();
	}

	@Override
	public Object getItem(int position) {
		return commentlist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		 if (convertView == null)
	        {
	            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.comment_listitem, parent, false);
	        }
		 
		 TextView comment_item = (TextView) convertView.findViewById(R.id.comment_item);
		 
		 if(commentlist.get(position).getComment().length()>30)
		 {
			 comment = commentlist.get(position).getComment().toString().substring(0,30);
		 }

		 comment_item.setText(comment+"...");
		
		 comment_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent webactivity = new Intent(context, WebActivity.class);
				webactivity.putExtra("WEB_URL", commentlist.get(position).getCommenturl());
				context.startActivity(webactivity);
				
			}
		});
		return convertView;
	}
	

}
