package pt.altran.script.db;

import java.sql.ResultSet;

public interface ResultListener {
    void startScript();

    void currentStatement(String statement);

    void setIsNotEmpty(boolean hasResult);

    void currentResultSet(ResultSet resultSet);

    void setColumnsCount(int cols);
}
