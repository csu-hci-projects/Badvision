package badvision.dataanalysis.util;

import badvision.dataanalysis.model.RecordedResponse;
import badvision.dataanalysis.model.Subject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * JSON Utilities for json data and file parsing
 * @author brobert
 */
public class JsonUtil {
    static Gson GSON;
    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        gsonBuilder.registerTypeAdapter(Calendar.class, new TypeAdapter<Calendar>() {
            @Override
            public void write(JsonWriter arg0, Calendar arg1) throws IOException {
                arg0.value(arg1.toInstant().toString());
            }

            @Override
            public Calendar read(JsonReader arg0) throws IOException {
                String str = arg0.nextString();
                Instant instant = Instant.parse(str);
                ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
                return GregorianCalendar.from(zdt);
            }
        });
        GSON = gsonBuilder.create();

    }

    public static <C> C parseStringAsClass(String str, Class<? extends C> clazz) {
        if (str.endsWith(",")) {
            str = str.substring(0, str.length()-1);
        }
        return GSON.fromJson(str, clazz);
    }

    public static <C> C parseReaderAsClass(Reader reader, Class<? extends C> clazz) {
        return GSON.fromJson(reader, clazz);
    }
    
    static int completeCount = 0;
    static int incompleteCount = 0;
    public static Stream<Subject> loadDataFile(File dataFile) {
        List<Subject> subjects = new ArrayList<>();
        try {
            Path filePath = dataFile.toPath();
            Subject s = new Subject();
            s.setFilename(dataFile.getName());
            for (String line : Files.readAllLines(filePath)) {
                Optional<RecordedResponse> r = RecordedResponse.fromString(line);
                if (r.isPresent()) {
                    s.getRecords().add(r.get());
                    if (r.get() instanceof RecordedResponse.EndRecord) {
                        subjects.add(s);
                        if (s.isComplete()) {
                            s.setNumber(++completeCount);
                        } else {
                            s.setNumber(++incompleteCount+1000);
                        }
                        s = new Subject();
                    }                    
                }
            }
            if (s.getRecordCount() > 0) {
                if (s.isComplete()) {
                    s.setNumber(++completeCount);
                } else {
                    s.setNumber(++incompleteCount+1000);
                }
                subjects.add(s);
            }
            subjects.forEach(subject -> {
                subject.getStartRecord().ifPresent(start-> {
                    subject.setIp(start.getDevice().getIp());
                    GeoUtil.geoGeoInfoForIP(subject.getIp()).ifPresent(subject::setLocation);
                });
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
        return subjects.stream();
    }
    
    
    private JsonUtil() {
        //Utility class has no constructor
    }

    
}
