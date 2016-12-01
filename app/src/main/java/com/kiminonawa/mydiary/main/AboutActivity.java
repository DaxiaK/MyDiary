package com.kiminonawa.mydiary.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kiminonawa.mydiary.R;

/**
 * Created by daxia on 2016/12/1.
 */

public class AboutActivity extends AppCompatActivity {

    private StringBuilder lincense;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        lincense = new StringBuilder();
        lincense.append("This open source project is coding by Daxia , see more information:\n\n" +
                "https://github.com/erttyy8821/MyDiary\n\n");
        lincense.append("This project release by MIT License:\n");
        lincense.append(
                new LicenseObj("MyDiary", "Daxia", "2016", LicenseObj.MIT)
                        .getLincense());
        lincense.append("\nI use some lib from:\n");
        lincense.append(
                new LicenseObj("android-segmented-control", "Le Van Hoang", "2014", LicenseObj.MIT)
                        .getLincense());
        lincense.append(
                new LicenseObj("HoloColorPicker", "Lars Werkman", "2012", LicenseObj.APACHE)
                        .getLincense());
        ((TextView) findViewById(R.id.TV_about_text)).setText(lincense.toString());
    }

    public class LicenseObj {

        public final static int MIT = 0;
        public final static int APACHE = 1;

        private String softwareName;
        private String author;
        private String year;
        private int license;

        public LicenseObj(String softwareName, String author, String year, int license) {
            this.softwareName = softwareName;
            this.author = author;
            this.year = year;
            this.license = license;
        }

        public String getLincense() {
            switch (license) {
                case MIT:
                    return "\n==" + softwareName + "==\n\n" +
                            "The MIT License (MIT)\n\n" +
                            "Copyright (c) " + year + " " + author + "\n\n" +
                            "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                            "of this software and associated documentation files (the \"Software\"), to deal\n" +
                            "in the Software without restriction, including without limitation the rights\n" +
                            "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                            "copies of the Software, and to permit persons to whom the Software is\n" +
                            "furnished to do so, subject to the following conditions:\n" +
                            "\n" +
                            "The above copyright notice and this permission notice shall be included in all\n" +
                            "copies or substantial portions of the Software.\n" +
                            "\n" +
                            "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                            "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                            "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                            "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                            "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                            "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                            "SOFTWARE." +
                            "\n\n=====\n";
                case APACHE:
                    return "\n==" + softwareName + "==\n\n" +
                            "copyright " + year + ", " + author + "\n\n" +
                            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                            "you may not use this file except in compliance with the License.\n" +
                            "You may obtain a copy of the License at\n" +
                            "\n" +
                            "    http://www.apache.org/licenses/LICENSE-2.0\n" +
                            "\n" +
                            "Unless required by applicable law or agreed to in writing, software\n" +
                            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                            "See the License for the specific language governing permissions and\n" +
                            "limitations under the License." +
                            "\n\n=====\n";
                default:
                    return "";
            }
        }
    }

}
