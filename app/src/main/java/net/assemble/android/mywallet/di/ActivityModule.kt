package net.assemble.android.mywallet.di

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.provider

fun activityModule(activity: AppCompatActivity) = Kodein.Module {
    bind<LayoutInflater>() with provider { activity.layoutInflater }
}
