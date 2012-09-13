package com.girillo;

import java.io.File;
import java.util.ArrayList;

import com.girillo.entities.Attachement;
import com.girillo.entities.Attachement.AttachementType;
import com.girillo.utils.GirilloUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

public class AttachmentAdapter extends ArrayAdapter<Attachement> implements OnClickListener, android.content.DialogInterface.OnClickListener {
	private Attachement attachment;
	private AlertDialog dialog;
	
	public AttachmentAdapter(Activity activity, int textViewResourceId, ArrayList<Attachement>   attachments) {
		super(activity, textViewResourceId, attachments);
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setIcon(R.drawable.delete);
		builder.setTitle(R.string.deleteAttachmentConfirm);
		builder.setPositiveButton(R.string.ok, 	   this); 
		builder.setNegativeButton(R.string.cancel, this);
		this.dialog = builder.create();
        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.attachment	 = this.getItem(position);
		Context  context = this.getContext();
		View	 rowUI 	 = convertView;
		
		if (rowUI == null) {
			LayoutInflater rowUIInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowUI = rowUIInflater.inflate(R.layout.attachments_row, null);
		}
		
		TextView    attachmentFileUI = (TextView)  		rowUI.findViewById(R.id.attachFile);
		ImageView   attachmentIconUI = (ImageView)		rowUI.findViewById(R.id.attachIcon);
		ImageButton previewCmd	 	 = (ImageButton)    rowUI.findViewById(R.id.attachPreview);
		ImageButton deleteCmd	 	 = (ImageButton)    rowUI.findViewById(R.id.attachDelete);
		
		
		attachmentFileUI.setText(attachment.getFileName());
		
		previewCmd.setOnClickListener(this);
		deleteCmd.setOnClickListener(this);
		
		previewCmd.setTag(attachment);
		deleteCmd.setTag(attachment);
		
		if (AttachementType.Image.equals(attachment.getAttachementType())){
			attachmentIconUI.setImageDrawable(context.getResources().getDrawable(R.drawable.image));
		} else if (AttachementType.Audio.equals(attachment.getAttachementType())){
			attachmentIconUI.setImageDrawable(context.getResources().getDrawable(R.drawable.audio));
		} else if (AttachementType.Video.equals(attachment.getAttachementType())){
			attachmentIconUI.setImageDrawable(context.getResources().getDrawable(R.drawable.video));
		}
			
		return rowUI;
	}

	@Override
	public void onClick(View v) {
		ImageButton button	= (ImageButton) v;
		this.attachment	    = (Attachement) button.getTag();
		
		if (button.getId() == R.id.attachPreview){
			this.preview(attachment);
		} else if (button.getId() == R.id.attachDelete){
			dialog.show();
		}
	}

	/**
	 * Previsualiza un archivo
	 */
	private void preview(Attachement attachment) {
		try {
			String mimetype = "*/*";
			if (AttachementType.Audio.equals(attachment.getAttachementType())){
				mimetype = "audio/*";
			} else if (AttachementType.Image.equals(attachment.getAttachementType())){
				mimetype = "image/*";
			} else if (AttachementType.Video.equals(attachment.getAttachementType())){
				mimetype = "video/*";
			}

			Intent intent   = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(attachment.getFullPath())), mimetype);
			this.getContext().startActivity(intent);
		} catch (Exception ex){
			// Pendiente
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == Dialog.BUTTON_POSITIVE){
			this.remove(attachment);
			GirilloUtils.deleteQuietly(attachment.getFullPath());
		}
	}
}