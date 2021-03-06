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
package fr.openbike.android.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import fr.openbike.android.R;
import fr.openbike.android.database.OpenBikeDBAdapter;

public class SettingsPreferencesActivity extends AbstractPreferencesActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityHelper.setupActionBar(getString(R.string.btn_settings));
		addPreferencesFromResource(R.xml.location_preferences);
		addPreferencesFromResource(R.xml.map_preferences);
		addPreferencesFromResource(R.xml.other_preferences);
		PreferenceScreen preferenceScreen = getPreferenceScreen();
		SharedPreferences preferences = preferenceScreen.getSharedPreferences();
		mNetworkPreference = preferenceScreen
				.findPreference(AbstractPreferencesActivity.NETWORK_PREFERENCE);
		mReportBugPreference = preferenceScreen
				.findPreference(AbstractPreferencesActivity.REPORT_BUG_PREFERENCE);
		mNetworkPreference.setSummary(preferences.getString(NETWORK_NAME, "")
				+ " : " + preferences.getString(NETWORK_CITY, ""));
		preferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == mNetworkPreference) {
			SharedPreferences prefs = preferenceScreen.getSharedPreferences();
			OpenBikeDBAdapter.getInstance(this).setUpdateVersion(
					prefs.getLong(STATIONS_VERSION, 0),
					prefs.getInt(NETWORK_PREFERENCE, 0));
			startActivity(new Intent(this, HomeActivity.class).setAction(
					HomeActivity.ACTION_CHOOSE_NETWORK).setFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP));
		} else if (preference == mReportBugPreference) {
			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "contact@openbike.fr" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Bug OpenBike");
			startActivity(Intent.createChooser(emailIntent, "Signaler un bug"));
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

}
