package pt.altran.script.db;

import org.apache.ibatis.jdbc.RuntimeSqlException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CompareResultListener implements ResultListener {
    protected final List<DataSet> dataSets = new ArrayList<DataSet>();
    protected int cols;
    protected int scriptIndex = 0;
    protected int statementIndex;
    protected int rowIndex;

    public void startScript() {
        scriptIndex++;
        statementIndex = 0;
        rowIndex = 0;
    }

    public void currentStatement(String statement) {
        rowIndex = 0;
        if (scriptIndex < 2) {
            dataSets.add(new DataSet());
        }
        statementIndex++;
    }

    public void setIsNotEmpty(boolean hasResult) {
    }

    protected abstract void compare(Map.Entry<String, Object> entry, Map<String, Object> line, ResultSet resultSet);

    public void currentResultSet(ResultSet resultSet) {
        try {
            Map<String, Object> line;
            if (scriptIndex == 2) {
                line = dataSets.get(statementIndex - 1).getLines().get(rowIndex);
                for (Map.Entry<String, Object> entry : line.entrySet()) {
                    compare(entry, line, resultSet);
                }
            } else {
                DataSet dataSet = dataSets.get(dataSets.size() - 1);
                line = new HashMap<String, Object>();
                for (int i = 0; i < cols; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i + 1);
                    Object value = resultSet.getObject(i + 1);
                    line.put(columnName, value);
                }
                dataSet.addLine(line);
            }
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
        rowIndex++;
    }

    public void setColumnsCount(int cols) {
        this.cols = cols;
    }
}
