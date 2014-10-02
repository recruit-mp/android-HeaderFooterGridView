/*
 * Copyright (C) 2014  Recruit Marketing Partners Co.,Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.recruit_mp.android.headerfootergridviewdemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.co.recruit_mp.android.widget.HeaderFooterGridView;

public class HeaderFooterGridViewDemoActivity extends ActionBarActivity {

    private HeaderFooterGridViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_footer_grid_view_demo);

        final HeaderFooterGridView headerFooterGridView = (HeaderFooterGridView) findViewById(R.id.HeaderFooterGridView);

        {
            String title = "Header #1";
            View header = LayoutInflater.from(this).inflate(R.layout.grid_header, null, false);
            ((TextView) header.findViewById(R.id.text)).setText(title);
            headerFooterGridView.addHeaderView(header, title, true); // Call this before calling setAdapter
        }
        {
            String title = "Header #2 (unclickable)";
            View header = LayoutInflater.from(this).inflate(R.layout.grid_header, null, false);
            ((TextView) header.findViewById(R.id.text)).setText(title);
            headerFooterGridView.addHeaderView(header, title, false); // Call this before calling setAdapter
        }
        {
            String title = "Footer #1";
            View footer = LayoutInflater.from(this).inflate(R.layout.grid_header, null, false);
            ((TextView) footer.findViewById(R.id.text)).setText(title);
            headerFooterGridView.addFooterView(footer, title, true); // Call this before calling setAdapter
        }
        {
            String title = "Footer #2 (unclickable)";
            View footer = LayoutInflater.from(this).inflate(R.layout.grid_header, null, false);
            ((TextView) footer.findViewById(R.id.text)).setText(title);
            headerFooterGridView.addFooterView(footer, title, false); // Call this before calling setAdapter
        }

        mAdapter = new HeaderFooterGridViewAdapter(this);
        headerFooterGridView.setAdapter(mAdapter);

        headerFooterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = (String)headerFooterGridView.getAdapter().getItem(position);
                Toast.makeText(HeaderFooterGridViewDemoActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
