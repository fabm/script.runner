package pt.altran.script.db;

import java.sql.ResultSet;

public interface ResultListener {
    void currentStatement(String statement);

    void setIsNotEmpty(boolean hasResult);

    void currentResultSet(ResultSet resultSet);

    boolean delegateIteration();

    void setColumnsCount(int cols);
}
