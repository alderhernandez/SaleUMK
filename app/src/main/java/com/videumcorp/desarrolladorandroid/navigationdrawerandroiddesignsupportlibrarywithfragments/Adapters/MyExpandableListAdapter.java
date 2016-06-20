package com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.Lib.ChildRow;
import com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.Lib.ParentRow;
import com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.R;

import java.util.ArrayList;

/**
 * Created by marangelo.php on 13/06/2016.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;


    public MyExpandableListAdapter(Context context, ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList= new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }



    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parentRowList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentRow parentRow = (ParentRow) getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row,null);
        }
        TextView heading = (TextView) convertView.findViewById(R.id.parent_row);

        heading.setText(parentRow.getName().trim());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildRow childRow = (ChildRow) getChild(groupPosition,childPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row,null);
        }
        final TextView childText = (TextView) convertView.findViewById(R.id.chil_text);
        childText.setText(childRow.getText().trim());

        final View finalConvertView = convertView;




        childText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Toast.makeText(finalConvertView.getContext(),childText.getText(),Toast.LENGTH_SHORT).show();

                String Nombre = String.valueOf(finalConvertView.getContext());
                int Posicion = Nombre.indexOf("mtcl");
                if (Posicion != -1){
                    Log.d("Vista de","CLIENTES");
                    Toast.makeText(finalConvertView.getContext(),String.valueOf(finalConvertView.getContext()),Toast.LENGTH_SHORT).show();
                    //Intent MenuIntent = new Intent(context,Sale_Activity.class);
                    //MenuIntent.putExtra("Vendedor",User.toUpperCase());
                    //context.startActivity(MenuIntent);

                }


            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public void filterData(String query){

        query = query.toLowerCase();
        parentRowList.clear();
        if (query.isEmpty()){
            parentRowList.addAll(originalList);
        }else{

            for (ParentRow parentRow : originalList){
                if (parentRow.getName().toLowerCase().contains(query)){
                    ArrayList<ChildRow> childList = parentRow.getChildList();
                    //newList.add(childRow);
                    ArrayList<ChildRow> newList = new ArrayList<ChildRow>();
                    for (ChildRow childRow: childList){
                        newList.add(childRow);
                    }
                    if (newList.size() > 0){
                        ParentRow nParentRow = new ParentRow(parentRow.getName(),newList);
                        parentRowList.add(nParentRow);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }
}