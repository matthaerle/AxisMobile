package com.acusportrtg.axismobile.Methods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.acusportrtg.axismobile.AppSettingsActivity;
import com.acusportrtg.axismobile.InventoryFirearmsActivity;
import com.acusportrtg.axismobile.InventoryProductsActivity;
import com.acusportrtg.axismobile.Inventory_Task;
import com.acusportrtg.axismobile.R;
import com.acusportrtg.axismobile.SearchFirearmsActivity;
import com.acusportrtg.axismobile.SearchProductsActivity;
import com.acusportrtg.axismobile.UpdateMinMaxActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by mhaerle on 10/31/2017.
 */

public class CustomDrawerBuilder {
    public Drawer CustomDrawer(Context context, Activity activity, AccountHeader accountHeader,Toolbar toolbar,Drawer result,Bundle savedInstanceState) {
        final Context context1 = context;
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withTranslucentStatusBar(false)
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
                            else if (drawerItem.getIdentifier() == 5 )
                                intent = new Intent(context1,AppSettingsActivity.class);
                            if (intent != null)
                                context1.startActivity(intent);
                        }
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.menu_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(5)
                )
                .withSavedInstance(savedInstanceState)
                .build();
        return result;
    }
}
