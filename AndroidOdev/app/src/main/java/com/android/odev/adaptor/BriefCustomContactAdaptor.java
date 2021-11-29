package com.android.odev.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.odev.R;
import com.android.odev.model.CustomContact;

import java.util.List;

public class BriefCustomContactAdaptor extends BaseAdapter {

    private List<CustomContact> contacts;
    private Context context;
    private LayoutInflater layoutInflater;

    public BriefCustomContactAdaptor(Context context, List<CustomContact> contacts) {
        this.context = context;
        this.contacts = contacts;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = layoutInflater.inflate(R.layout.brief_contact_view, null);
        TextView name = (TextView) customView.findViewById(R.id.name);
        name.setText(contacts.get(position).getName());
        return customView;
    }

    public List<CustomContact> getContacts() {
        return contacts;
    }
}
