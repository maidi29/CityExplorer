package de.mareike.cityexplorer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import de.mareike.cityexplorer.R;

public class UserInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    //Das Info Fenster der Marker personalisieren und mit dem Layout user_info_windows verbinden
    LayoutInflater inflater = null;
    UserInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoWindows = inflater.inflate(R.layout.user_info_windows, null);
        TextView title = (TextView)infoWindows.findViewById(R.id.title);
        TextView description = (TextView)infoWindows.findViewById(R.id.description);

        title.setText(marker.getTitle());
        description.setText(marker.getSnippet());

        return (infoWindows);
    }

}
