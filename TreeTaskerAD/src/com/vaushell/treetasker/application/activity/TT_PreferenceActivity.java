package com.vaushell.treetasker.application.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.vaushell.treetasker.R;

public class TT_PreferenceActivity
	extends PreferenceActivity
{

	@Override
	protected void onCreate(
		Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.preferences );
	}
}
