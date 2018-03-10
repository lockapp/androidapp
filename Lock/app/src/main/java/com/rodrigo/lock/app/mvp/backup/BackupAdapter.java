package com.rodrigo.lock.app.mvp.backup;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.drive.DriveId;
import com.rodrigo.lock.app.R;
import com.rodrigo.lock.app.data.Clases.GlucosioBackup;
import com.rodrigo.lock.app.utils.FormatDateTime;

import java.util.List;

/**
 * Created by Rodrigo on 27/12/2017.
 */


public class BackupAdapter extends ArrayAdapter<GlucosioBackup> {

    private Context context;
    private FormatDateTime formatDateTime;

    public BackupAdapter(Context context, int resource, List<GlucosioBackup> items) {
        super(context, resource, items);
        this.context = context;
        formatDateTime = new FormatDateTime(context);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_backup_drive_restore_item, parent, false);
        }

        final GlucosioBackup p = getItem(position);
        if (p != null) {
            final DriveId driveId = p.getDriveId();
            final String modified = formatDateTime.formatDate(p.getModifiedDate());
            final String size = Formatter.formatFileSize(getContext(), p.getBackupSize());
            String name = p.getTitle();
            if (name.endsWith(".lock")){
                name= name.substring(0, name.length() - 5);
            }


            TextView modifiedTextView = (TextView) v.findViewById(R.id.item_history_time);
            TextView firstTextView = (TextView) v.findViewById(R.id.first_text_view);
            TextView secondTextView = (TextView) v.findViewById(R.id.second_text_view);
            modifiedTextView.setText(modified);
            firstTextView.setText(name);
            secondTextView.setText(size);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_backup_restore);
                    TextView nameTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_name);
                    TextView createdTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_created);
                    TextView sizeTextView = (TextView) dialog.findViewById(R.id.dialog_backup_restore_size);
                    Button restoreButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_restore);
                    Button cancelButton = (Button) dialog.findViewById(R.id.dialog_backup_restore_button_cancel);

                    nameTextView.setText(p.getTitle());
                    createdTextView.setText(modified);
                    sizeTextView.setText(size);

                    restoreButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((BackupActivity) context).downloadFromDrive(p.getTitle(),driveId.asDriveFile());
                        }
                    });

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
        }

        return v;
    }
}