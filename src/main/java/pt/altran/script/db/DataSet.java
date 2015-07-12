package pt.altran.script.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSet {
    private List<Map<String, Object>> lines = new ArrayList<Map<String, Object>>();

    public void addLine(Map<String, Object> line) {
        lines.add(line);
    }

    public List<Map<String, Object>> getLines() {
        return lines;
    }
}
