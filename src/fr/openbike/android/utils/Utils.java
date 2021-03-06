/*
 * Copyright (C) 2011 Guillaume Delente
 *
 * This file is part of OpenBike.
 *
 * OpenBike is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * OpenBike is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenBike.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.openbike.android.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import fr.openbike.android.database.OpenBikeDBAdapter;
import fr.openbike.android.model.MinimalStation;
import fr.openbike.android.service.LocationService;
import fr.openbike.android.ui.AbstractPreferencesActivity;

public class Utils {
	
	public static final String FAVORITE_WHERE_CLAUSE = OpenBikeDBAdapter.KEY_FAVORITE + " = 1";

	static public void sortStationsByDistance(
			List<? extends MinimalStation> list) {
		Collections.sort(list, new Comparator<MinimalStation>() {
			@Override
			public int compare(MinimalStation s1, MinimalStation s2) {
				if (s1.getDistance() < s2.getDistance()) {
					return -1;
				} else if (s1.getDistance() > s2.getDistance()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}
/*
	static public void sortStationsByName(List<? extends Overlay> list) {
		Collections.sort(list, new Comparator<Overlay>() {
			@Override
			public int compare(Overlay o1, Overlay o2) {
				if (o1 instanceof StationOverlay
						&& o2 instanceof StationOverlay) {
					if (((StationOverlay) o1).isCurrent())
						return -1;
					if (((StationOverlay) o2).isCurrent())
						return 1;
					MinimalStation s1 = ((StationOverlay) o1).getStation();
					MinimalStation s2 = ((StationOverlay) o2).getStation();
					return s1.getName().compareToIgnoreCase(s2.getName());
				} else if (o1 instanceof MyLocationOverlay) {
					return -1;
				} else if (o2 instanceof MyLocationOverlay) {
					return 1;
				}
				return 0;
			}
		});
	}
	*/

	static public String whereClauseFromFilter(SharedPreferences preferences) {
		Vector<String> selection = new Vector<String>();
		if (preferences.getBoolean(AbstractPreferencesActivity.FAVORITE_FILTER, false))
			selection.add("(" + OpenBikeDBAdapter.KEY_FAVORITE + " = 1 )");
		if (preferences.getBoolean(AbstractPreferencesActivity.BIKES_FILTER, false))
			selection.add("(" + OpenBikeDBAdapter.KEY_BIKES + " != 0 )");
		if (preferences.getBoolean(AbstractPreferencesActivity.SLOTS_FILTER, false))
			selection.add("(" + OpenBikeDBAdapter.KEY_SLOTS + " != 0 )");
		int size = selection.size();
		if (size == 0)
			return null;
		String where = selection.firstElement();
		for (int i = 1; i < selection.size(); i++) {
			where += " AND " + selection.elementAt(i);
		}
		return where;
	}

	static public String formatDistance(int distance) {
		int km = distance / 1000;
		int m = distance % 1000;
		return km == 0 ? m + "m" : km + "," + m + "km ";
	}

	static public boolean isIntentAvailable(Intent intent, Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static int computeDistance(int latitude, int longitude, Location from) {
		if (from == null) {
			return LocationService.DISTANCE_UNAVAILABLE;
		}
		Location l = new Location("");
		l.setLatitude((double) latitude * 1E-6);
		l.setLongitude((double) longitude * 1E-6);
		return (int) from.distanceTo(l);
	}

	public static Intent getNavigationIntent(int latitude, int longitude) {
		return new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="
				+ latitude * 1E-6 + "," + longitude * 1E-6 + "&mode=w"));
	}

	public static Intent getMapsIntent(int latitude, int longitude, String label) {
		return new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + latitude
				* 1E-6 + "," + longitude * 1E-6 + " (" + label + ")"));
	}
}
