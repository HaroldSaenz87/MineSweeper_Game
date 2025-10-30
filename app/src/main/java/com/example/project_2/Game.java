package com.example.project_2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Game#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Game extends Fragment {

    // this is to modify our linear layout
    LinearLayout vert;

    // board for the
    Button[][] board;
    boolean[][] mines;
    boolean[][] revealed;

    boolean[][] suspected;

    boolean gameOver = false;

    // pass the sharedpref into these variables
    int rows;
    int cols;
    int mineCount;

    String covered_Color;
    String uncovered_Color;
    String sus_Color;
    String mine_color;

    public Game() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Game.
     */

    public static Game newInstance(String param1, String param2) {
        Game fragment = new Game();
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
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        SharedPreferences sp = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);

        // default as first option if user skips settings this would be the first build or if possibly a null
        rows = sp.getInt("rows", 5);
        cols = sp.getInt("cols", 5);
        int mine_percent = sp.getInt("mines", 15);
        covered_Color = sp.getString("covered", "Gray");
        uncovered_Color = sp.getString("uncovered", "White");
        sus_Color = sp.getString("sus", "Red");
        mine_color = sp.getString("mineColor", "Black");

        // linear layout vertical id being stored
        vert = view.findViewById(R.id.board_game_vert);


        // make a game board for minesweeper using above values from shared prefs
        createBoard(rows, cols, mine_percent);

        return view;
    }

    private void createBoard(int rows, int cols, int minePercent)
    {
        gameOver = false;

        suspected = new boolean[rows][cols];

        vert.removeAllViews();
        board = new Button[rows][cols];
        mines = new boolean[rows][cols];
        revealed = new boolean[rows][cols];

        mineCount = (rows * cols * minePercent) / 100;
        placeMines(mineCount);


        for(int i = 0; i < rows; i++)
        {
            LinearLayout rll = new LinearLayout(getContext());
            rll.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < cols; j++)
            {
                Button cell = new Button(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
                params.setMargins(2,2,2,2);
                cell.setLayoutParams(params);

                try
                {
                    cell.setBackgroundColor(getColorName(covered_Color));
                }
                catch (IllegalArgumentException e)
                {
                    cell.setBackgroundColor(Color.GRAY);
                }

                cell.setText("?");
                int finalI = i;
                int finalJ = j;

                cell.setOnClickListener(v -> revealCell(finalI, finalJ));

                cell.setOnLongClickListener(v->{
                    toggleSuspect(cell, finalI, finalJ);
                    return true;
                });

                board[i][j] = cell;
                rll.addView(cell);
            }
            vert.addView(rll);
        }
        // this is to debug and show where the mines are and to fix some minor issues
        //debugShowMines();
    }

// just a debug function
    private void debugShowMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (mines[i][j]) {
                    board[i][j].setText("💣"); // shows bomb emoji
                    board[i][j].setBackgroundColor(Color.MAGENTA); // or use getColorName(mineColor)
                }
            }
        }
    }



    private void placeMines(int count) {
        Random rand = new Random();
        int placed = 0;
        while (placed < count)
        {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            if (!mines[r][c])
            {
                mines[r][c] = true;
                placed++;
            }
        }
    }

    private void revealCell(int r, int c) {
        if (suspected[r][c] || revealed[r][c] || gameOver) return;
        revealed[r][c] = true;

        if (mines[r][c])
        {
            board[r][c].setBackgroundColor(getColorName(mine_color));
            Toast.makeText(getContext(), "You lose!", Toast.LENGTH_SHORT).show();
            revealAllMines();
            gameOver = true;
            return;
        }

        board[r][c].setBackgroundColor(getColorName(uncovered_Color));

        int mineCount = countAdjacentMines(r, c);
        if (mineCount > 0)
        {
            board[r][c].setText(String.valueOf(mineCount));
        }
        else
        {
            board[r][c].setText("0");
            // Empty cell: auto reveal nearby
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nr = r + i, nc = c + j;
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        revealCell(nr, nc);
                    }
                }
            }
        }
        // to stop recursion of you win toast
        if(!gameOver && checkWin())
        {
            revealAllMines();
            Toast.makeText(getContext(), "You Win!", Toast.LENGTH_SHORT).show();
            gameOver = true;
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (!mines[i][j] && !revealed[i][j])
                {
                    return false; // still some non-mine cells hidden
                }
            }
        }
        return true; // all non-mine cells revealed
    }

    private int countAdjacentMines(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++)
        {
            for (int j = -1; j <= 1; j++)
            {
                int nr = r + i, nc = c + j;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && mines[nr][nc])
                {
                    count++;
                }
            }
        }
        return count;
    }

    private void toggleSuspect(Button cell, int r, int c) {
        if (revealed[r][c]) return; // don’t mark opened cells

        suspected[r][c] = !suspected[r][c]; // toggle state

        if (suspected[r][c]) {
            try
            {
                // would crash if i use .parseColor on orange and any other color that android studio doesnt see.
                cell.setBackgroundColor(getColorName(sus_Color)); // used a function to get the color
            }
            catch (IllegalArgumentException e)
            {
                cell.setBackgroundColor(Color.RED);     // fall back to this if the try fails
            }
        }
        else
        {
            try
            {
                cell.setBackgroundColor(getColorName(covered_Color));
            }
            catch (IllegalArgumentException e)
            {
                cell.setBackgroundColor(Color.GRAY);        // fall back to this if the try fails
            }
        }
    }

    // found out that android studio can see some colors by strings for example "Orange" so this function below fixes that
    // uses hex to get the actual color
    private int getColorName(String name)
    {
        switch(name.toLowerCase())
        {
            case "red" :
                return Color.RED;
            case "gray" :
                return  Color.GRAY;
            case "purple" :
                return Color.parseColor("#9C27B0");
            case "blue" :
                return  Color.BLUE;
            case "white" :
                return Color.WHITE;
            case "green" :
                return Color.GREEN;
            case "orange" :
                return Color.parseColor("#FFA500");
            case "yellow" :
                return Color.YELLOW;
            default:
                return Color.CYAN;

        }
    }

    private void revealAllMines()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (!revealed[i][j])
                {
                    revealed[i][j] = true;

                    if (mines[i][j])
                    {
                        board[i][j].setBackgroundColor(getColorName(mine_color));

                    }
                    else
                    {
                        board[i][j].setBackgroundColor(getColorName(uncovered_Color));
                        int count = countAdjacentMines(i, j);
                        if (count > 0)
                        {
                            board[i][j].setText(String.valueOf(count));
                        }
                        else
                        {
                            board[i][j].setText("");
                        }
                    }
                }
            }
        }
    }

    // when the user switches back to the screen or if theres still nothing chosen or if possibly a null so it falls back to the default values
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = requireActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);

         // default set to first option
        rows = sp.getInt("rows", 5);
        cols = sp.getInt("cols", 5);
        int mine_percent = sp.getInt("mines", 15);
        covered_Color = sp.getString("covered", "Gray");
        uncovered_Color = sp.getString("uncovered", "White");
        sus_Color = sp.getString("sus", "Red");
        mine_color = sp.getString("mineColor", "Black");

        createBoard(rows, cols, mine_percent);
    }
}