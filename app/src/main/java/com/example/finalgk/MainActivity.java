package com.example.finalgk;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

    public class MainActivity extends AppCompatActivity {

        private EditText usernameInput, weightInput, heightInput;
        private TextView resultText, adviceText;
        private ImageView adviceImage;
        private Button calculateButton, updateButton;
        private RadioGroup genderGroup;
        private RadioButton selectedGender;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // View elements
            usernameInput = findViewById(R.id.username_input);
            weightInput = findViewById(R.id.weight_input);
            heightInput = findViewById(R.id.height_input);
            resultText = findViewById(R.id.result_text);
            adviceText = findViewById(R.id.advice_text);
            adviceImage = findViewById(R.id.advice_image);
            calculateButton = findViewById(R.id.calculate_button);
            updateButton = findViewById(R.id.update_button);
            genderGroup = findViewById(R.id.gender_group);

  // Calculate button click event
            calculateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = usernameInput.getText().toString().trim();
                    String weightStr = weightInput.getText().toString().trim();
                    String heightStr = heightInput.getText().toString().trim();

//Kullanıcıdan alınan değerler (username, weightStr, heightStr) kontrol edilir.
// Eğer eksik bilgi varsa bir Toast mesajı ile uyarı gösterilir.
                    if (username.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get selected gender
                    int selectedId = genderGroup.getCheckedRadioButtonId();
                    selectedGender = findViewById(selectedId);
                    boolean isMale = selectedGender.getId() == R.id.radio_male;

                    try {
                        float weight = Float.parseFloat(weightStr);
                        float height = Float.parseFloat(heightStr) / 100; // Convert cm to meters

                        if (weight <= 0 || height <= 0) {
                            Toast.makeText(MainActivity.this, "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Calculate BMI
                        float bmi = weight / (height * height);
                        resultText.setText(String.format("BMI: %.2f", bmi));

                        // Determine advice and image
                        if (bmi < 18.5) {
                            adviceText.setText("Zayıfsınız. Kilo almaya çalışın." +
                                     "\nDaha fazla kalori almak için fındık, avokado ve tam tahıllı gıdalar gibi besleyici yiyecekler tüketmeye çalışın");
                            adviceImage.setImageResource(R.drawable.underweight_image);
                        } else if (bmi >= 18.5 && bmi < 24.9) {
                            adviceText.setText("Normal kilonuz var. Böyle devam edin!" +
                                    "\nDengeli bir diyetle meyveler, sebzeler, yağsız proteinler ve tam tahıllı gıdalar tüketmeye devam edin.");
                            adviceImage.setImageResource(R.drawable.normal_weight_image);
                        } else if (bmi >= 25) {
                            adviceText.setText("Fazla kilonuz var. Düzenli olarak egzersiz yapmayı deneyin." +
                                    "\nİşlenmiş gıdalar ve şeker tüketimini azaltmayı düşünün, sebzeler, yağsız proteinler ve sağlıklı yağlar tüketmeye odaklanın.");
                            adviceImage.setImageResource(R.drawable.overweight_image);
                        }
                        adviceImage.setVisibility(View.VISIBLE);

  // Save user data using SharedPreferences
 //putFloat(): Kullanıcının kilo ve boy bilgileri, benzersiz bir anahtar
 // (ör. username_weight) ile saklanır.

                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat(username + "_weight", weight);
                        editor.putFloat(username + "_height", height);
                        editor.apply();

                        // Calculate and display ideal weight
                        float idealWeight = calculateIdealWeight(height, isMale);
                        adviceText.append("\nİdeal kilonuz yaklaşık " + idealWeight + " kg.");

                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Update button click event
            //putFloat(): Kullanıcının kilo ve boy bilgileri, benzersiz bir anahtar
            // (ör. username_weight) ile saklanır.
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = usernameInput.getText().toString().trim();
                    if (username.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter a username to update data.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                    float weight = sharedPreferences.getFloat(username + "_weight", 0);
                    float height = sharedPreferences.getFloat(username + "_height", 0);

                    if (weight == 0 || height == 0) {
                        Toast.makeText(MainActivity.this, "No data found for this user.", Toast.LENGTH_SHORT).show();
                    } else {
                        weightInput.setText(String.valueOf(weight));
                        heightInput.setText(String.valueOf(height * 100)); // Convert meters to cm
                    }
                }
            });
        }
//Kullanıcının cinsiyetine göre ideal BMI aralığı belirlenir.
//Ortalama ideal kilo hesaplanır ve sonuç olarak döndürülür.
        private float calculateIdealWeight(float height, boolean isMale) {
            if (isMale) {
                return height * height * 22; // Use fixed BMI for males
            } else {
                return height * height * 21; // Use fixed BMI for females
            }
        }
    }