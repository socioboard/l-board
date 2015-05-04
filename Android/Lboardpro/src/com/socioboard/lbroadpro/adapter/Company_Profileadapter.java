package com.socioboard.lbroadpro.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.socioboard.lbroadpro.MainActivity;
import com.socioboard.lbroadpro.R;
import com.socioboard.lbroadpro.fragments.Company_Detail;
import com.socioboard.lbroadpro.fragments.Company_Profile;
import com.socioboard.lbroadpro.fragments.Company_Update;
import com.socioboard.lbroadpro.fragments.Company_Updates;
import com.socioboard.lbroadpro.models.CompanyDetails;

public class Company_Profileadapter extends BaseAdapter{

	private Context context;
    private ArrayList<CompanyDetails> companyItems;
	
    public Company_Profileadapter(Context context, ArrayList<CompanyDetails> navDrawerItems) 
    {
        this.context = context;
        this.companyItems = navDrawerItems;
    }
    
	@Override
	public int getCount() {
		return companyItems.size();
	}

	@Override
	public Object getItem(int position) {
		return companyItems.get(position);
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
	            convertView = mInflater.inflate(R.layout.companyprofilelistitem, parent, false);
	        }
		 
		 TextView companyname = (TextView) convertView.findViewById(R.id.companyname);
		 
		 companyname.setText(companyItems.get(position).getCompanyname());
		 
		 companyname.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(Company_Profile.iscompanyprofile)
				{
					Company_Profile.companyid = companyItems.get(position).getCompanyid();
					 Fragment fragment = new Company_Detail();
					    MainActivity.swipeFragment(fragment);
				}else
				{
					Company_Updates.company_id =  companyItems.get(position).getCompanyid();
					 Fragment fragment = new Company_Update();
					    MainActivity.swipeFragment(fragment);
				}			
			}
		});
		return convertView;
	}

}
