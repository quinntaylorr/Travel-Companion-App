package com.example.travelcompanionapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerCategory, spinnerFrom, spinnerTo;
    EditText etInput;
    Button btnConvert;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link this file to the XML layout
        setContentView(R.layout.activity_main);

        // Connect variables to UI elements
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        etInput = findViewById(R.id.etInput);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        // Set up the category spinner
        String[] categories = {"Currency", "Fuel & Distance", "Temperature"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // When the category changes, update the From/To unit spinners
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                String[] units;

                // This part of the code was helped through the use of AI. The general structure was important to get right
                switch (selectedCategory) {
                    case "Currency":
                        units = new String[]{"USD", "AUD", "EUR", "JPY", "GBP"};
                        break;
                    case "Fuel & Distance":
                        units = new String[]{"mpg", "km/L", "Gallon (US)", "Liters", "Nautical Mile", "Kilometers"};
                        break;
                    case "Temperature":
                        units = new String[]{"Celsius", "Fahrenheit", "Kelvin"};
                        break;
                    default:
                        units = new String[]{};
                }

                // Apply units to both From and To spinners
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_item, units);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrom.setAdapter(unitAdapter);
                spinnerTo.setAdapter(unitAdapter);

                // Default To spinner
                if (units.length > 1) spinnerTo.setSelection(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // When Convert is clicked, read inputs and show the result
        btnConvert.setOnClickListener(v -> {
            String input = etInput.getText().toString();
            String from = spinnerFrom.getSelectedItem().toString();
            String to = spinnerTo.getSelectedItem().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            double value = Double.parseDouble(input);
            double result = convert(category, from, to, value);

            tvResult.setText(input + " " + from + " = " + String.format("%.4f", result) + " " + to);
        });
    }

    private double convert(String category, String from, String to, double value) {

        // If same unit selected, no conversion needed
        if (from.equals(to)) return value;

        switch (category) {

            case "Currency":
                // Convert input to USD first, then to target currency
                double valueInUSD = 0;
                switch (from) {
                    case "USD": valueInUSD = value;          break;
                    case "AUD": valueInUSD = value / 1.55;   break;
                    case "EUR": valueInUSD = value / 0.92;   break;
                    case "JPY": valueInUSD = value / 148.50; break;
                    case "GBP": valueInUSD = value / 0.78;   break;
                }

                // Convert from USD to target currency
                switch (to) {
                    case "USD": return valueInUSD;
                    case "AUD": return valueInUSD * 1.55;
                    case "EUR": return valueInUSD * 0.92;
                    case "JPY": return valueInUSD * 148.50;
                    case "GBP": return valueInUSD * 0.78;
                }
                break;

            case "Fuel & Distance":
                // Use direct conversion formulas for each pair
                switch (from) {
                    case "mpg":
                        if (to.equals("km/L")) return value * 0.425;
                        break;
                    case "km/L":
                        if (to.equals("mpg")) return value / 0.425;
                        break;
                    case "Gallon (US)":
                        if (to.equals("Liters")) return value * 3.785;
                        break;
                    case "Liters":
                        if (to.equals("Gallon (US)")) return value / 3.785;
                        break;
                    case "Nautical Mile":
                        if (to.equals("Kilometers")) return value * 1.852;
                        break;
                    case "Kilometers":
                        if (to.equals("Nautical Mile")) return value / 1.852;
                        break;
                }
                break;

            case "Temperature":
                // Use direct conversion formulas
                switch (from) {
                    case "Celsius":
                        if (to.equals("Fahrenheit")) return (value * 1.8) + 32;
                        if (to.equals("Kelvin"))     return value + 273.15;
                        break;
                    case "Fahrenheit":
                        if (to.equals("Celsius")) return (value - 32) / 1.8;
                        if (to.equals("Kelvin"))  return ((value - 32) / 1.8) + 273.15;
                        break;
                    case "Kelvin":
                        if (to.equals("Celsius"))     return value - 273.15;
                        if (to.equals("Fahrenheit"))  return ((value - 273.15) * 1.8) + 32;
                        break;
                }
                break;
        }

        // Fallback if no conversion matched
        return value;
    }
}