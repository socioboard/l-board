package com.socioboard.lbroadpro.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.WebActivity;
import com.socioboard.lbroadpro.models.CompanyUpdatemodel;

public class Company_Updateadapter extends BaseAdapter{

	private Context context;
	private ArrayList<CompanyUpdatemodel> companylist = new ArrayList<CompanyUpdatemodel>();
	
	public Company_Updateadapter(Context context, ArrayList<CompanyUpdatemodel> navDrawerItems) 
    {
        this.context = context;
        this.companylist = navDrawerItems;
    }
	
	@Override
	public int getCount() {
		return companylist.size();
	}

	@Override
	public Object getItem(int position) {
		return companylist.get(position);
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
            convertView = mInflater.inflate(R.layout.company_updatelistitem, parent, false);
        }
        
        TextView companyname = (TextView) convertView.findViewById(R.id.cp_name);
        TextView jobdescription = (TextView) convertView.findViewById(R.id.cp_jobdescription);
        TextView jobtitle = (TextView) convertView.findViewById(R.id.cp_jobtitle);
        TextView joblocation = (TextView) convertView.findViewById(R.id.cp_joblocation);
        ImageView applynowbutton = (ImageView) convertView.findViewById(R.id.apply_nowbtn);
        
        companyname.setText(companylist.get(position).getCompanyname());
        jobdescription.setText(companylist.get(position).getJobdescription());
        jobtitle.setText(companylist.get(position).getJobtitle());
        joblocation.setText(companylist.get(position).getJoblocation());
        
        applynowbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent webactivity = new Intent(context, WebActivity.class);
				webactivity.putExtra("WEB_URL", companylist.get(position).getJoburl());
				context.startActivity(webactivity);
				
			}
		});
        return convertView;
    }

	
	
}
