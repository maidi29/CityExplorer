package de.mareike.cityexplorer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BadgesListViewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public BadgesListViewAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String[] values = new String[] { "Erster Punkt - Erster Schritt", "Quizzer - 3 Punkte durch Quizzes verdient", "Kreativer Kopf - 3 Punkte durch Pinnwand-Uploads verdient",
                "Würzburg-Kenner - alle Marker in Würzburg abgehakt" };

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
        // change the icon for Windows and iPhone
        String s = values[position];
        if (s.startsWith("Erster Punkt - Erster Schritt")) {
            imageView.setImageResource(R.drawable.badge1);
        } else if (s.startsWith("Quizzer - 3 Punkte durch Quizzes verdient")){
            imageView.setImageResource(R.drawable.badge2);
        } else {
            imageView.setImageResource(R.drawable.badge3);
        }

        return rowView;
    }
}