package com.hfad.wloskieconieco;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

public class MainActivity extends Activity {
    //Kod który należy wykonać po kliknięciu elementu listy.
    //Kiedy użytkownik kliknie któryś z elementów w szufladzie nawigacyjnej,
    //zostanie wywołana metoda onItemclick()
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    private ShareActionProvider shareActionProvider;
    private String[] titles;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titles = getResources().getStringArray(R.array.titles);
        drawerList = (ListView)findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Określamy zawartość widoku ListView.
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1, titles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        // Wyświetlamy odpowiedni fragment.
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("position");
            setActionBarTitle(currentPosition);
        } else {
            selectItem(0);
        }
        // Tworzymy obiekt ActionBarDrawerToggle.
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                                                 R.string.open_drawer, R.string.close_drawer) {
            // wywoływana kiedy stan szuflady odpowiada jej całkowitemu zamknięciu
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
            // wywoływana kiedy stan szuflady odpowiada jej całkowitemu otworzeniu
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        //Ustawiamy ActionBarDrawerToggle jako nasłuchujacy układu DrawerLayout
        drawerLayout.setDrawerListener(drawerToggle);
        //Włączamy przycisk W góre dzieki czemu bedzie  mógł być on używany przez ActionBarDrawerToggle
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        FragmentManager fragMan = getFragmentManager();
                        Fragment fragment = fragMan.findFragmentByTag("visible_fragment");
                        if (fragment instanceof TopFragment) {
                            currentPosition = 0;
                        }
                        if (fragment instanceof PizzaMaterialFragment) {
                            currentPosition = 1;
                        }
                        if (fragment instanceof PastaFragment) {
                            currentPosition = 2;
                        }
                        if (fragment instanceof StoresFragment) {
                            currentPosition = 3;
                        }
                        setActionBarTitle(currentPosition);
                        drawerList.setItemChecked(currentPosition, true);
                    }
                }
        );
    }
    //Sprawdzamy pozycję klikniętego elementu na liście
    private void selectItem(int position) {
        // Aktualizujemy główną zawartość aplikacji podmieniając prezentowany fragment.
        currentPosition = position;
        Fragment fragment;
        switch(position) {
            case 1:
                fragment = new PizzaMaterialFragment();
                break;
            case 2:
                fragment = new PastaFragment();
                break;
            case 3:
                fragment = new StoresFragment();
                break;
            default:
                fragment = new TopFragment();
        }
        //Wyświetlamy fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment, "visible_fragment");
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        // Ustawiamy tytuł paska akcji.
        setActionBarTitle(position);
        // Zamykamy szufladę nawigacyjną.
        drawerLayout.closeDrawer(drawerList);
    }
    //Metoda wywoływana po każdym wywołaniu metody invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Jeśli szuflada jest otworzona, ukrywamy elementy akcji związane
        // z prezentowaną zawartością.
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.action_share).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    //Metoda synchronizacji stanu przycisku przełącznika po wywołaniu metody onRestoreInstanceState
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Synchronizujemy stan przycisku przełącznika po wywołaniu
        // metody onRestoreInstanceState.
        drawerToggle.syncState();
    }
    //Metoda tą musisz dodać do aktywności, tak by wszystkie zmiany konfiguracji były
    //przekazywane do klasy ActionBarDrawerToggle
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", currentPosition);
    }

    private void setActionBarTitle(int position) {
        String title;
        if (position == 0) {
            title = getResources().getString(R.string.app_name);
        } else {
            title = titles[position];
        }
        //Wyswietla łańcuch znaków w tytule akcji
        getActionBar().setTitle(title);
    }
    //Metoda spowoduje dodanie do paska akcji wszystkich elementów z podanego pliku zasobów menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Przygotowujemy menu; to wywołanie dodaje elementy do paska akcji jeśli jest używany
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
        setIntent("To jest przykładowy tekst.");
        return super.onCreateOptionsMenu(menu);
    }
    //Metoda tworzy intencje na potrzeby akcji Udostepnij
    private void setIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }
    //Obiekt MenuItem reprezentuje kliknięty element paska akcji
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Dzieki temu wywołaniu przycik ActionBarDrawerToggle bedzie mógł obsługiwać kliknięcie
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //Sprawdzamy który element został kliknięty
        switch (item.getItemId()) {
            case R.id.action_create_order:
                // Kod wykonywany po kliknięciu przycisku Złóż zamówienie
                Intent intent = new Intent(this, OrderActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                // Kod wykonywany po kliknięciu przycisku Ustawienia
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
