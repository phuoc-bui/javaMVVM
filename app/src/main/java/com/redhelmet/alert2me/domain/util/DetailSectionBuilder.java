package com.redhelmet.alert2me.domain.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.redhelmet.alert2me.data.model.Entry;
import com.redhelmet.alert2me.data.model.Section;

public class DetailSectionBuilder {
    private Context _context;

    public DetailSectionBuilder(Context context) {
        _context = context;
    }

    public View BuildSection(Section section) {

        List<Entry> entries = section.getEntries();
        if (entries == null || entries.isEmpty()) return new LinearLayout(_context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        LinearLayout container = new LinearLayout(_context);
        container.setLayoutParams(layoutParams);
        container.setOrientation(LinearLayout.VERTICAL);

        View headerContainer = CreateHeader(section);
        container.addView(headerContainer);


        for (Entry entry : section.getEntries()) {
            View entryContainer = BuildEntry(entry);
            container.addView(entryContainer);
        }

        addLine(container);
        return container;
    }

    private void addLine(LinearLayout container) {
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lineParams.setMargins(0, 20, 0, 20);
        View finalLine = new View(_context);
        finalLine.setLayoutParams(lineParams);
        finalLine.setBackgroundColor(Color.parseColor("#c0c0c0"));
        container.addView(finalLine);
    }

    private View CreateHeader(Section section) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginDp = pixelToDp(16);
        layoutParams.setMargins(marginDp, 0, marginDp, marginDp);
        LinearLayout headerContainer = new LinearLayout(_context);
        headerContainer.setLayoutParams(layoutParams);
        headerContainer.setOrientation(LinearLayout.HORIZONTAL);

        TextView sectionTitle = new TextView(_context);
        sectionTitle.setText(section.getName());
        int blackColor =Color.parseColor("#000000");
        sectionTitle.setTextColor(blackColor);

        headerContainer.addView(sectionTitle);
        return headerContainer;

        /*String icon = section.getIcon();
        ImageView sectionIcon = new ImageView(_context);
        sectionIcon.setImageBitmap(BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_info_black_24dp));
        headerContainer.addView(sectionIcon);*/
    }

    private View BuildEntry(final Entry entry) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginDp = pixelToDp(16);
        layoutParams.setMargins(marginDp, 0, marginDp, marginDp);
        LinearLayout entryContainer = new LinearLayout(_context);
        entryContainer.setLayoutParams(layoutParams);
        entryContainer.setOrientation(LinearLayout.VERTICAL);
        TextView entryTitle = new TextView(_context);
        entryTitle.setTypeface(entryTitle.getTypeface(), Typeface.BOLD);
        entryTitle.setText(entry.getTitle());
        entryTitle.setTextColor(Color.parseColor("#383838"));
        TextView entryValue = new TextView(_context);
        entryValue.setTextColor(Color.parseColor("#383838"));
        if (entry.getLink() == null ) {
            if(!entry.getTitle().toString().equals("attachment")) {
                if (entry.getValue() != null)
                    entryValue.setText(entry.getValue().toString());
            }
        } else {
            Spanned html = Html.fromHtml(String.format("<a href='%s' >%s</a>", entry.getLink(), entry.getValue().toString()));
            entryValue.setText(html);
            entryValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_context != null) {
                        try {
                            String url = entry.getLink();
                            if (!url.contains("http://")){
                                url = String.format("http://%s", url);
                            }
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            _context.startActivity(browserIntent);
                        } catch (Throwable t) {
                            Toast.makeText(_context, "Invalid url", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }
        entryContainer.addView(entryTitle);
        entryContainer.addView(entryValue);

        return entryContainer;
    }

    private int pixelToDp(int pixel) {
        final float scale = _context.getResources().getDisplayMetrics().density;
        return (int) ((pixel * scale) + 0.5f);
    }
}
