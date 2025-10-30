package com.example.project_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */

    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // all the settings spinners

        Spinner row = view.findViewById(R.id.row_spinner);
        Spinner col = view.findViewById(R.id.column_spinner);
        Spinner mines = view.findViewById(R.id.mines_spinner);
        Spinner covered = view.findViewById(R.id.covered_spinner);
        Spinner uncovered = view.findViewById(R.id.Uncovered_spinner);
        Spinner suspicious_marks = view.findViewById(R.id.Sus_spinner);
        Spinner mine_color = view.findViewById(R.id.MineColor_spinner);

        SharedPreferences sp = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        // since there is a lot of spinners and to really save some time use a common listener
        AdapterView.OnItemSelectedListener listen = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences.Editor e = sp.edit();

                // for the row
                if(parent.getId() == R.id.row_spinner)
                {
                    // string to int
                    int selectedRows = Integer.parseInt(parent.getItemAtPosition(position).toString());

                    e.putInt("rows", selectedRows);
                }
                // for the columns
                else if(parent.getId() == R.id.column_spinner)
                {
                    // string to int
                    int selectedCol = Integer.parseInt(parent.getItemAtPosition(position).toString());

                    e.putInt("cols", selectedCol);
                }
                // for the percentage of mines
                else if(parent.getId() == R.id.mines_spinner)
                {
                    // get the string then remove the percent symbol
                    String selected_percent = parent.getItemAtPosition(position).toString();

                    int percent = Integer.parseInt(selected_percent.replace("%", ""));

                    e.putInt("mines" , percent);
                }
                // for the cells being covered
                else if(parent.getId() == R.id.covered_spinner)
                {
                    String color_covered = parent.getItemAtPosition(position).toString();

                    e.putString("covered", color_covered);
                }
                // for the cells being uncovered
                else if(parent.getId() == R.id.Uncovered_spinner)
                {
                    String color_uncovered = parent.getItemAtPosition(position).toString();

                    e.putString("uncovered", color_uncovered);
                }
                // for the suspicious cells
                else if(parent.getId() == R.id.Sus_spinner)
                {
                    String color_sus = parent.getItemAtPosition(position).toString();

                    e.putString("sus", color_sus);
                }
                // for the planted mines
                else if(parent.getId() == R.id.MineColor_spinner)
                {
                    String color_mines = parent.getItemAtPosition(position).toString();

                    e.putString("mineColor", color_mines);
                }

                // save change
                e.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        };

        // for all spinners use the common listener above (really saves so much time)
        row.setOnItemSelectedListener(listen);
        col.setOnItemSelectedListener(listen);
        mines.setOnItemSelectedListener(listen);
        covered.setOnItemSelectedListener(listen);
        uncovered.setOnItemSelectedListener(listen);
        suspicious_marks.setOnItemSelectedListener(listen);
        mine_color.setOnItemSelectedListener(listen);


        return view;

    }
}