/**
 * Copyright 2015 Michael Leahy / TyphonRT, Inc.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.typhonrt.android.java6.data.option.control;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.typhonrt.android.java6.data.option.model.IOptionModel;

import org.typhonrt.android.java6.gldemo.framework.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OptionModelAdapter extends ArrayAdapter<IOptionModel>
{
   private final List<IOptionModel> list;
   private final Activity           context;

   private AtomicBoolean            updateNotify;

   public OptionModelAdapter(Activity context, List<IOptionModel> list, AtomicBoolean updateNotify)
   {
      super(context, R.layout.drawer_objectmodel, list);

      this.context = context;
      this.list = list;
      this.updateNotify = updateNotify;
   }

   static class ViewHolder
   {
      protected TextView      text;
      protected Switch        switchView;
      protected SeekBar       seekbarView;
      protected IOptionModel  model;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
      View view;

      if (convertView == null)
      {
         LayoutInflater inflator = context.getLayoutInflater();
         view = inflator.inflate(R.layout.drawer_objectmodel, null);

         final ViewHolder viewHolder = new ViewHolder();
         viewHolder.text = (TextView) view.findViewById(R.id.label);
         viewHolder.model = list.get(position);

         viewHolder.seekbarView = (SeekBar) view.findViewById(R.id.seekbarView);
         viewHolder.switchView = (Switch) view.findViewById(R.id.switchView);

         viewHolder.seekbarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
               if (fromUser)
               {
                  viewHolder.model.setRangePercent(progress / (float)seekBar.getMax());
                  updateNotify.lazySet(true);
               }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
         });

         viewHolder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
         {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
               viewHolder.model.setRangePercent(buttonView.isChecked() ? 1f : 0f);
               updateNotify.lazySet(true);
            }
         });

         view.setTag(viewHolder);
      }
      else
      {
         view = convertView;
         ((ViewHolder) view.getTag()).model = list.get(position);
      }

      ViewHolder holder = (ViewHolder) view.getTag();

      holder.text.setText(holder.model.getTitle());

      switch (holder.model.getOptionType())
      {
         case BOOLEAN:
            holder.seekbarView.setVisibility(View.INVISIBLE);
            holder.switchView.setVisibility(View.VISIBLE);
            holder.switchView.setChecked(holder.model.getBooleanValue());
            break;

         case FLOAT:
            holder.seekbarView.setVisibility(View.VISIBLE);
            holder.switchView.setVisibility(View.INVISIBLE);
            holder.seekbarView.setProgress((int) (holder.seekbarView.getMax() * holder.model.getRangePercent()));
            break;
      }

      return view;
   }
}