package pt.altran.script.db;

import java.sql.ResultSet;

public interface ResultListener {
    void currentStatement(String statement);

    void setIsNotEmpty(boolean hasResult);

    void currentResultSet(ResultSet resultSet);

    boolean delegatePrint();

    void setColumnsCount(int cols);
}
