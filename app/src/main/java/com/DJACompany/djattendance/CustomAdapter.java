package com.DJACompany.djattendance;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
class CustomAdapter extends BaseAdapter {
    private Context context;
    private static ArrayList<Model> modelArrayList;
    CustomAdapter(Context context, ArrayList<Model> modelArrayList) {
        this.context = context;
        CustomAdapter.modelArrayList = modelArrayList;
    }
    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return modelArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    public ArrayList<String> getSelectedNames(){
        ArrayList<String> selected = new ArrayList<>();
        for (Model element: modelArrayList) {
            if(element.getSelected()){
                selected.add(element.getPlayer());
            }
        }
        return selected;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.list_item, null, true);
            holder.checkBox = convertView.findViewById(R.id.checkBox);
            holder.tvPlayer = convertView.findViewById(R.id.playerNameList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.checkBox.setText(" " + (position+1));
        holder.tvPlayer.setText(modelArrayList.get(position).getPlayer());
        holder.checkBox.setChecked(modelArrayList.get(position).getSelected());
        holder.checkBox.setTag(R.integer.btnPlusView, convertView);
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.checkBox.getTag();
                if (modelArrayList.get(pos).getSelected()) {
                    modelArrayList.get(pos).setSelected(false);
                } else {
                    modelArrayList.get(pos).setSelected(true);
                }
            }
        });
        return convertView;
    }
    private class ViewHolder {
        CheckBox checkBox;
        private TextView tvPlayer;
    }
}