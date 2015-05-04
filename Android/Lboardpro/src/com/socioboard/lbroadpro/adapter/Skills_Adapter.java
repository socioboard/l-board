package com.socioboard.lbroadpro.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.models.SkillsModel;

public class Skills_Adapter extends BaseAdapter{

	Context context;
	ArrayList<SkillsModel> modellist = new ArrayList<SkillsModel>();
	
	public Skills_Adapter(ArrayList<SkillsModel> modellist,Context context) {
		this.context=context;
		this.modellist=modellist;
	}
	
	@Override
	public int getCount() {
		return modellist.size();
	}

	@Override
	public Object getItem(int position) {
		return modellist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null){
	            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.skills_list_item, parent, false);
	    }
		
		TextView skills = (TextView) convertView.findViewById(R.id.skill_test);
		skills.setText(modellist.get(position).getSkillname());
		
		return convertView;
	}

	
}
