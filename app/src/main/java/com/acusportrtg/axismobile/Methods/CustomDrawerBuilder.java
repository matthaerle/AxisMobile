package com.acusportrtg.axismobile.Methods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.acusportrtg.axismobile.AppSettingsActivity;
import com.acusportrtg.axismobile.Globals;
import com.acusportrtg.axismobile.InventoryFirearmsActivity;
import com.acusportrtg.axismobile.InventoryProductsActivity;
import com.acusportrtg.axismobile.Inventory_Task;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.LoginActivity;
import com.acusportrtg.axismobile.R;
import com.acusportrtg.axismobile.SearchFirearmsActivity;
import com.acusportrtg.axismobile.SearchProductsActivity;
import com.acusportrtg.axismobile.UpdateMinMaxActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by mhaerle on 10/31/2017.
 */

public class CustomDrawerBuilder {
    private IProfile profile;
    private AccountHeader headerResult = null;
    public Drawer CustomDrawer(Context context, Activity activity, Toolbar toolbar, Drawer result, Bundle savedInstanceState, GetEmployees employee) {
        final Context context1 = context;
        final Globals globals = new Globals();

        profile = new ProfileDrawerItem().withName(employee.getFirstName() + ' ' + employee.getLastName());
        buildHeader(false, savedInstanceState, activity);
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.btn_product_search).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.btn_firearm_search).withIdentifier(2).withSelectable(false),
                        new ExpandableDrawerItem().withName(R.string.btn_Inventory).withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.firearm_inventory_title).withLevel(2).withIdentifier(2000),
                                new SecondaryDrawerItem().withName(R.string.inv_stocktaking).withLevel(2).withIdentifier(2001)
                        ),
                        new PrimaryDrawerItem().withName(R.string.btn_Update_MinMax).withIdentifier(4).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1)
                                intent = new Intent(context1,SearchProductsActivity.class);
                            else if (drawerItem.getIdentifier() == 2 )
                                intent = new Intent(context1,SearchFirearmsActivity.class);
                            else if (drawerItem.getIdentifier() == 2000)
                                intent = new Intent(context1,InventoryFirearmsActivity.class);
                            else if (drawerItem.getIdentifier() == 2001)
                                intent = new Intent(context1,Inventory_Task.class);
                            else if (drawerItem.getIdentifier() == 4)
                                intent = new Intent(context1,UpdateMinMaxActivity.class);
                            else if (drawerItem.getIdentifier() == 5 ) {
                                intent = new Intent(context1, LoginActivity.class);
                                globals.setEmployee(null);
                            }
                            if (intent != null)
                                context1.startActivity(intent);
                        }
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(5)
                )
                .withSavedInstance(savedInstanceState)
                .build();
        return result;
    }

    private void buildHeader(boolean b, Bundle savedInstanceState,Activity activity) {
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .addProfiles(profile)
                .build();
    }
}
