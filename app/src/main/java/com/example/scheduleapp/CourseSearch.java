package com.example.scheduleapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Adapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CourseSearch extends AppCompatActivity {

    private List<XMLParser.SpecificClassData> internalClassList;
    private String xmlAsString;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CourseInfo> listItems;

    private Map<String, String> courseSubjToSubjCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate","onCreate started");

        courseSubjToSubjCode = createMap();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);
        findViewById(R.id.courseSearchCancel).setOnClickListener(v -> cancel());
        findViewById(R.id.courseSearchSearchButton).setOnClickListener(v -> parseXml());
        recyclerView = findViewById(R.id.courseSearchRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();

        //Assigning stuff for autocomplete for subjects
        AutoCompleteTextView courseSubject = (AutoCompleteTextView) findViewById(R.id.courseSearchSubject);
        String[] subjects = getResources().getStringArray(R.array.courseSubjectAutoComplete);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subjects);
        courseSubject.setAdapter(adapter4);

        Log.d("onCreate","onCreate ended");
    }

    private Map<String, String> createMap() {
        Map<String, String> myMap = new HashMap<>();
        myMap.put("Asian American Studies", "AAS");
        myMap.put("Agricultural and Biological Engineering", "ABE");
        myMap.put("Accountancy", "ACCY");
        myMap.put("Agricultural and Consumer Economics", "ACE");
        myMap.put("Agricultural, Consumer and Environmental Sciences", "ACES");
        myMap.put("Advertising", "ADV");
        myMap.put("Aerospace Engineering", "AE");
        myMap.put("Air Force Aerospace Studies", "AFAS");
        myMap.put("African American Studies", "AFRO");
        myMap.put("African Studies", "AFST");
        myMap.put("Agricultural Communications", "AGCM");
        myMap.put("Agricultural Education", "AGED");
        myMap.put("Applied Health Sciences Courses", "AGS");
        myMap.put("American Indian Studies", "AIS");
        myMap.put("Animal Sciences", "ANSC");
        myMap.put("Anthropology", "ANTH");
        myMap.put("Arabic", "ARAB");
        myMap.put("Architecture", "ARCH");
        myMap.put("Art", "ART");
        myMap.put("Art--Design", "ARTD");
        myMap.put("Art--Education", "ARTE");
        myMap.put("Art--Foundation", "ARTF");
        myMap.put("Art--History", "ARTH");
        myMap.put("Art-Studio", "ARTS");
        myMap.put("Actuarial Science and Risk Management", "ASRM");
        myMap.put("Asian Studies", "ASST");
        myMap.put("Astronomy", "ASTR");
        myMap.put("Atmospheric Sciences", "ATMS");
        myMap.put("Business Administration", "BADM");
        myMap.put("Basque", "BASQ");
        myMap.put("Brain and Cognitive Science", "BCOG");
        myMap.put("Bosnian-Croatian-Serbian", "BCS");
        myMap.put("Biochemistry", "BIOC");
        myMap.put("Bioengineering", "BIOE");
        myMap.put("Biology", "BIOL");
        myMap.put("Biophysics", "BIOP");
        myMap.put("Biomedical Sciences and Engineering", "BSE");
        myMap.put("Business and Technical Writing", "BTW");
        myMap.put("Business", "BUS");
        myMap.put("Comparative Biosciences", "CB");
        myMap.put("Cell and Developmental Biology", "CDB");
        myMap.put("Civil and Environmental Engineering", "CEE");
        myMap.put("Chemical and Biomolecular Engineering", "CHBE");
        myMap.put("Chemistry", "CHEM");
        myMap.put("Chinese", "CHIN");
        myMap.put("Community Health", "CHLH");
        myMap.put("Campus Honors Program Courses", "CHP");
        myMap.put("Curriculum and Instruction", "CI");
        myMap.put("Committee on Inst Cooperation", "CIC");
        myMap.put("Classical Civilization", "CLCV");
        myMap.put("Clinical Sciences and Engineering", "CLE");
        myMap.put("Communication", "CMN");
        myMap.put("Crop Sciences", "CPSC");
        myMap.put("Computer Science", "CS");
        myMap.put("Computational Science and Engineering", "CSE");
        myMap.put("Creative Writing", "CW");
        myMap.put("Comparative and World Literature", "CWL");
        myMap.put("Czech", "CZCH");
        myMap.put("Dance", "DANC");
        myMap.put("East Asian Language and Culture", "EALC");
        myMap.put("Electrical and Computer Engineering", "ECE");
        myMap.put("Economics", "ECON");
        myMap.put("Educational Practice", "EDPR");
        myMap.put("Education", "EDUC");
        myMap.put("English as an International Language", "EIL");
        myMap.put("Engineering", "ENG");
        myMap.put("Engineering Honors", "ENGH");
        myMap.put("English", "ENGL");
        myMap.put("Environmental Sustainability", "ENSU");
        myMap.put("Entomology", "ENT");
        myMap.put("Environmental Studies", "ENVS");
        myMap.put("Educational Organization and Leadership", "EOL");
        myMap.put("Education Policy, Organization and Leadership", "EPOL");
        myMap.put("Educational Policy Studies", "EPS");
        myMap.put("Educational Psychology", "EPSY");
        myMap.put("Earth, Society, and Environment", "ESE");
        myMap.put("English as a Second Language", "ESL");
        myMap.put("European Union Studies", "EURO");
        myMap.put("Fine and Applied Arts", "FAA");
        myMap.put("Finance", "FIN");
        myMap.put("Foreign Language Teacher Education", "FLTE");
        myMap.put("French", "FR");
        myMap.put("Food Science and Human Nutrition", "FSHN");
        myMap.put("Graduate College", "GC");
        myMap.put("Grand Challenge Learning", "GCL");
        myMap.put("Geography", "GEOG");
        myMap.put("Geology", "GEOL");
        myMap.put("German", "GER");
        myMap.put("Global Studies", "GLBL");
        myMap.put("Germanic", "GMC");
        myMap.put("Greek", "GRK");
        myMap.put("Modern Greek", "GRKM");
        myMap.put("General Studies", "GS");
        myMap.put("Gender and Women's Studies", "GWS");
        myMap.put("Human Development and Family Studies", "HDFS");
        myMap.put("Hebrew, Modern and Classical", "HEBR");
        myMap.put("History", "HIST");
        myMap.put("Hindi", "HNDI");
        myMap.put("Horticulture", "HORT");
        myMap.put("Human Resource Development", "HRD");
        myMap.put("Health Technology", "HT");
        myMap.put("Humanities Courses", "HUM");
        myMap.put("Integrative Biology", "IB");
        myMap.put("Industrial Engineering", "IE");
        myMap.put("i-Health Program", "IHLT");
        myMap.put("Informatics", "INFO");
        myMap.put("Information Sciences", "IS");
        myMap.put("Italian", "ITAL");
        myMap.put("Japanese", "JAPN");
        myMap.put("Journalism", "JOUR");
        myMap.put("Jewish Studies", "JS");
        myMap.put("Kinesiology", "KIN");
        myMap.put("Korean", "KOR");
        myMap.put("Landscape Architecture", "LA");
        myMap.put("Liberal Arts and Sciences", "LAS");
        myMap.put("Latin American and Caribbean Studies", "LAST");
        myMap.put("Latin", "LAT");
        myMap.put("Law", "LAW");
        myMap.put("Labor and Employment Relations", "LER");
        myMap.put("Linguistics", "LING");
        myMap.put("Latina/Latino Studies", "LLS");
        myMap.put("Media and Cinema Studies", "MACS");
        myMap.put("Mathematics", "MATH");
        myMap.put("Regular MBA Program Administration", "MBA");
        myMap.put("Molecular and Cell Biology", "MCB");
        myMap.put("Media", "MDIA");
        myMap.put("Medieval Studies", "MDVL");
        myMap.put("Mechanical Engineering", "ME");
        myMap.put("Microbiology", "MICR");
        myMap.put("Military Science", "MILS");
        myMap.put("Molecular and Integrative Physiology", "MIP");
        myMap.put("Materials Science and Engineering", "MSE");
        myMap.put("Music", "MUS");
        myMap.put("Museum Studies", "MUSE");
        myMap.put("Neuroscience", "NEUR");
        myMap.put("Nuclear, Plasma, and Radiological Engineering", "NPRE");
        myMap.put("Natural Resources and Environmental Sciences", "NRES");
        myMap.put("Naval Science", "NS");
        myMap.put("Nutritional Sciences", "NUTR");
        myMap.put("Pathobiology", "PATH");
        myMap.put("Plant Biology", "PBIO");
        myMap.put("Persian", "PERS");
        myMap.put("Philosophy", "PHIL");
        myMap.put("Physics", "PHYS");
        myMap.put("Plant Pathology", "PLPA");
        myMap.put("Polish", "POL");
        myMap.put("Portuguese", "PORT");
        myMap.put("Political Science", "PS");
        myMap.put("Profressional Science Master's Program", "PSM");
        myMap.put("Psychology", "PSYS");
        myMap.put("Russian, East European and Eurasian Studies", "REES");
        myMap.put("Rehabilitation Counseling", "REHB");
        myMap.put("Religion", "REL");
        myMap.put("Rhetoric and Composition", "RHET");
        myMap.put("Romance Linguistics", "RMLG");
        myMap.put("Rural Sociology", "RSOC");
        myMap.put("Recreation, Sport, and Tourism", "RST");
        myMap.put("Russian", "RUSS");
        myMap.put("South Asian and Middle Eastern Studies", "SAME");
        myMap.put("Strategic Brand Communication", "SBC");
        myMap.put("Scandinavian", "SCAN");
        myMap.put("Systems Engineering and Design", "SE");
        myMap.put("Speech and Hearing Science", "SHS");
        myMap.put("Slavic", "SLAV");
        myMap.put("Second Language Studies", "SLS");
        myMap.put("Sociology", "SOC");
        myMap.put("Social Work", "SOCW");
        myMap.put("Spanish", "SPAN");
        myMap.put("Special Education", "SPED");
        myMap.put("Statistics", "STAT");
        myMap.put("Swahili", "SWAH");
        myMap.put("Theoretical and Applied Mechanics", "TAM");
        myMap.put("Technology Entrepreneurship", "TE");
        myMap.put("Theatre", "THEA");
        myMap.put("Technology and Management Courses", "TMGT");
        myMap.put("Translation Studies", "TRST");
        myMap.put("Technical Systems Management", "TSM");
        myMap.put("Turkish", "TURK");
        myMap.put("Ukrainian", "UKR");
        myMap.put("Urban and Regional Planning", "UP");
        myMap.put("Veterinary Clinical Medicine", "VCM");
        myMap.put("Veterinary Medicine Courses", "VM");
        myMap.put("Wolof", "WLOF");
        myMap.put("Writing Studies", "WRIT");
        myMap.put("Yiddish", "YDSH");
        myMap.put("Zulu", "ZULU");
        return myMap;
    }

    private void loadRecyclerViewData() {
        for (XMLParser.SpecificClassData current : internalClassList) {
            CourseInfo course = new CourseInfo(current.getLabel(),
                    current.getType(),
                    current.getSectionNumber(),
                    current.getStart() + " - " + current.getEnd(),
                    current.getDays(),
                    current.getBuildingName(),
                    current.getCreditHours(),
                    current.getCrn());
            listItems.add(course);
        }
        adapter = new CourseSearchAdapter(listItems, CourseSearch.this);
        recyclerView.setAdapter(adapter);
    }

    private String getURL() {
        String courseSubject = courseSubjToSubjCode.get(((TextView) findViewById(R.id.courseSearchSubject)).getText().toString());
        String courseNumber = ((TextView) findViewById(R.id.courseSearchNumber)).getText().toString();
        Log.d("URL", "Returning: " + "https://courses.illinois.edu/cisapp/explorer/schedule/2019/fall/"
                + courseSubject + "/" + courseNumber
                + ".xml?mode=cascade");
        return "https://courses.illinois.edu/cisapp/explorer/schedule/2019/fall/"
                + courseSubject + "/" + courseNumber
                + ".xml?mode=cascade";
    }

    private void getXmlAsString() {
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            xmlAsString = getRequest.execute(getURL()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void parseXml() {
        listItems.clear();
        Log.d("parseXML", "xmlAsStringBefore: " + xmlAsString);
        getXmlAsString();
        Log.d("parseXML", "xmlAsStringAfter: " + xmlAsString);
        InputStream stream = new ByteArrayInputStream(xmlAsString.getBytes(Charset.forName("UTF-8")));
        XMLParser parser = new XMLParser();

        try {
            internalClassList = parser.parseSpecificClass(stream);
            loadRecyclerViewData();
        } catch (Exception e) {
            listItems.clear();
            adapter = new CourseSearchAdapter(listItems, CourseSearch.this);
            recyclerView.setAdapter(adapter);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sorry this class is either invalid or does not have any sections this semester!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        /**
        catch (XmlPullParserException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

    void cancel() {
        Intent intent = new Intent(CourseSearch.this, createSchedule.class);
        startActivity(intent);
        finish();
    }

    void addCourse() {
        Intent intent = new Intent(CourseSearch.this, createSchedule.class);
        startActivity(intent);
        finish();
    }
}
