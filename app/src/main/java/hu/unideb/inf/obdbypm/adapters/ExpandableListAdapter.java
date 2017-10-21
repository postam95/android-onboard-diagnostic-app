package hu.unideb.inf.obdbypm.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.activities.ServiceBookActivity;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.ServiceBookRecord;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private ServiceBookActivity activity;
    private List<Car> cars;

    public ExpandableListAdapter(ServiceBookActivity context, List<Car> rows) {
        this.activity = context;
        this.cars = rows;
    }

    @Override
    public ServiceBookRecord getChild(int groupPosition, int childPosititon) {
        return this.cars.get(groupPosition).getServiceBookRecords().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getGroup(groupPosition).getServiceBookRecords().size();
    }

    @Override
    public Car getGroup(int groupPosition) {
        return this.cars.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return cars.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_child_list, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        holder.textView.setText(getChild(groupPosition, childPosition).getServiceCompany());
        holder.btnEdit.setOnClickListener(onEditServiceBookRecordListener(getChild(groupPosition, childPosition),
                groupPosition, childPosition));
        holder.btnDelete.setOnClickListener(onDeleteServiceBookRecordListener(groupPosition, childPosition));

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_header_list, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.textView.setText(getGroup(groupPosition).getBrand());
        holder.btnEdit.setOnClickListener(onEditCarListener(getGroup(groupPosition), groupPosition));
        holder.btnDelete.setOnClickListener(onDeleteCarListener(groupPosition));

        return convertView;
    }

    private View.OnClickListener onDeleteServiceBookRecordListener(final int groupPosition, final int childPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("Delete?");
                alertDialog.setMessage("Are you sure to delete?");

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete in database
                        DatabaseManager.getInstance().deleteServiceBookRecord(getChild(groupPosition, childPosition).getId());

                        //update views
                        activity.getDataFromDB();
                    }
                });

                alertDialog.setNegativeButton("Cancel", null);
                alertDialog.show();
            }
        };
    }

    private View.OnClickListener onDeleteCarListener(final int groupPosition) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("Delete?");
                alertDialog.setMessage("Are you sure to delete?");

                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //delete in database
                        DatabaseManager.getInstance().deleteCar(getGroup(groupPosition).getId());

                        //update views
                        cars.remove(groupPosition);
                        notifyDataSetChanged();
                    }
                });

                alertDialog.setNegativeButton("Cancel", null);
                alertDialog.show();
            }
        };
    }

    private View.OnClickListener onEditCarListener(final Car car, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("EDIT Cat");
                alertDialog.setMessage("Please type a new cat name");

                final EditText input = new EditText(activity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(car.getBrand());

                alertDialog.setView(input);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //update database with new cat name
                        car.setBrand(input.getText().toString().trim());
                        DatabaseManager.getInstance().updateCar(car);

                        //update views
                        cars.get(position).setBrand(input.getText().toString().trim());
                        notifyDataSetChanged();
                    }
                });

                alertDialog.setNegativeButton("Cancel", null);
                alertDialog.show();
            }
        };
    }

    private View.OnClickListener onEditServiceBookRecordListener(final ServiceBookRecord serviceBookRecord, final int groupPos,
                                                      final int childPos) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("EDIT Kitten");
                alertDialog.setMessage("Please type a new kitten name");

                final EditText input = new EditText(activity);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(serviceBookRecord.getServiceCompany());

                alertDialog.setView(input);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //update database
                        serviceBookRecord.setServiceCompany(input.getText().toString().trim());
                        DatabaseManager.getInstance().updateServiceBookRecord(serviceBookRecord);

                        //update views
                        cars.get(groupPos).getServiceBookRecords().get(childPos).setServiceCompany(input.getText().toString());
                        notifyDataSetChanged();
                    }
                });

                alertDialog.setNegativeButton("Cancel", null);
                alertDialog.show();
            }
        };
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ChildViewHolder {
        private TextView textView;
        private View btnEdit;
        private View btnDelete;

        public ChildViewHolder(View v) {
            textView = (TextView) v.findViewById(R.id.service_book_record_name);
            btnEdit = v.findViewById(R.id.edit);
            btnDelete = v.findViewById(R.id.delete);
        }
    }

    private static class HeaderViewHolder {
        private TextView textView;
        private View btnEdit;
        private View btnDelete;

        public HeaderViewHolder(View v) {
            btnDelete = v.findViewById(R.id.delete);
            textView = (TextView) v.findViewById(R.id.car_name);
            btnEdit = v.findViewById(R.id.edit);
        }
    }
}