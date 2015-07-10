package pt.altran.script.db;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.ibatis.jdbc.RuntimeSqlException;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.*;

public class CustomScriptRunner {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private static final String DEFAULT_DELIMITER = ";";
    private Connection connection;
    private boolean stopOnError;
    private boolean autoCommit;
    private boolean sendFullScript;
    private boolean removeCRs;
    private boolean escapeProcessing = true;
    private PrintWriter logWriter;
    private PrintWriter errorLogWriter;
    private String delimiter;
    private boolean fullLineDelimiter;
    protected ResultListener resultListener;

    public CustomScriptRunner(Connection connection, ResultListener resultListener) {
        this.resultListener = resultListener;
        this.logWriter = new PrintWriter(System.out);
        this.errorLogWriter = new PrintWriter(System.err);
        this.delimiter = ";";
        this.fullLineDelimiter = false;
        this.connection = connection;
    }

    public CustomScriptRunner(Connection connection) {
        this(connection, new DefaultResultListener());
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setSendFullScript(boolean sendFullScript) {
        this.sendFullScript = sendFullScript;
    }

    public void setRemoveCRs(boolean removeCRs) {
        this.removeCRs = removeCRs;
    }

    public void setEscapeProcessing(boolean escapeProcessing) {
        this.escapeProcessing = escapeProcessing;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setFullLineDelimiter(boolean fullLineDelimiter) {
        this.fullLineDelimiter = fullLineDelimiter;
    }

    public void runScript(Reader reader) {
        this.setAutoCommit();

        try {
            if (this.sendFullScript) {
                this.executeFullScript(reader);
            } else {
                this.executeLineByLine(reader);
            }
        } finally {
            this.rollbackConnection();
        }

    }

    private void executeFullScript(Reader reader) {
        StringBuilder script = new StringBuilder();

        String message;
        try {
            BufferedReader e = new BufferedReader(reader);

            while ((message = e.readLine()) != null) {
                script.append(message);
                script.append(LINE_SEPARATOR);
            }

            this.executeStatement(script.toString());
            this.commitConnection();
        } catch (Exception var5) {
            message = "Error executing: " + script + ".  Cause: " + var5;
            this.printlnError(message);
            throw new RuntimeSqlException(message, var5);
        }
    }

    private void executeLineByLine(Reader reader) {
        StringBuilder command = new StringBuilder();

        String message;
        try {
            for (BufferedReader e = new BufferedReader(reader); (message = e.readLine()) != null; command = this.handleLine(command, message)) {
                ;
            }

            this.commitConnection();
            this.checkForMissingLineTerminator(command);
        } catch (Exception var5) {
            message = "Error executing: " + command + ".  Cause: " + var5;
            this.printlnError(message);
            throw new RuntimeSqlException(message, var5);
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (Exception var2) {
            ;
        }

    }

    private void setAutoCommit() {
        try {
            if (this.autoCommit != this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(this.autoCommit);
            }

        } catch (Throwable var2) {
            throw new RuntimeSqlException("Could not set AutoCommit to " + this.autoCommit + ". Cause: " + var2, var2);
        }
    }

    private void commitConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.commit();
            }

        } catch (Throwable var2) {
            throw new RuntimeSqlException("Could not commit transaction. Cause: " + var2, var2);
        }
    }

    private void rollbackConnection() {
        try {
            if (!this.connection.getAutoCommit()) {
                this.connection.rollback();
            }
        } catch (Throwable var2) {
            ;
        }

    }

    private void checkForMissingLineTerminator(StringBuilder command) {
        if (command != null && command.toString().trim().length() > 0) {
            throw new RuntimeSqlException("Line missing end-of-line terminator (" + this.delimiter + ") => " + command);
        }
    }

    private StringBuilder handleLine(StringBuilder command, String line) throws SQLException, UnsupportedEncodingException {
        String trimmedLine = line.trim();
        if (this.lineIsComment(trimmedLine)) {
            this.println(trimmedLine);
        } else if (this.commandReadyToExecute(trimmedLine)) {
            command.append(line.substring(0, line.lastIndexOf(this.delimiter)));
            command.append(LINE_SEPARATOR);
            this.println(command);
            this.executeStatement(command.toString());
            command.setLength(0);
        } else if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }

        return command;
    }

    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        return !this.fullLineDelimiter && trimmedLine.contains(this.delimiter) || this.fullLineDelimiter && trimmedLine.equals(this.delimiter);
    }

    private void executeStatement(String command) throws SQLException, UnsupportedEncodingException {
        boolean hasResults = false;
        Statement statement = this.connection.createStatement();
        statement.setEscapeProcessing(this.escapeProcessing);
        String sql = command;
        if (this.removeCRs) {
            sql = command.replaceAll("\r\n", "\n");
        }

        resultListener.currentStatement(sql);

        if (this.stopOnError) {
            hasResults = statement.execute(sql);
        } else {
            try {
                hasResults = statement.execute(sql);
            } catch (SQLException var8) {
                String message = "Error executing: " + command + ".  Cause: " + var8;
                this.printlnError(message);
            }
        }

        this.printResults(statement, hasResults);

        try {
            statement.close();
        } catch (Exception var7) {
            ;
        }

    }

    private void printResults(Statement statement, boolean hasResults) {
        try {
            if (hasResults) {
                ResultSet e = statement.getResultSet();
                if (e != null) {
                    ResultSetMetaData md = e.getMetaData();
                    int cols = md.getColumnCount();
                    resultListener.setColumnsCount(cols);
                    int i;
                    String value;
                    for (i = 0; i < cols; ++i) {
                        value = md.getColumnLabel(i + 1);
                        this.print(value + "\t");
                    }

                    this.print("");
                    boolean isNotEmpty = false;
                    while (resultListener.delegateIteration() && e.next()) {
                        isNotEmpty |= true;
                        for (i = 0; i < cols; ++i) {
                            value = e.getString(i + 1);
                            this.print(value + "\t");
                        }
                        resultListener.currentResultSet(e);
                        this.print("");
                    }
                    resultListener.setIsNotEmpty(isNotEmpty);
                }
            }
        } catch (SQLException var8) {
            this.printlnError("Error printing results: " + var8.getMessage());
        }

    }

    private void print(Object o) {
        if (this.logWriter != null) {
            this.logWriter.print(o);
            this.logWriter.flush();
        }

    }

    private void println(Object o) {
        if (this.logWriter != null) {
            this.logWriter.println(o);
            this.logWriter.flush();
        }

    }

    private void printlnError(Object o) {
        if (this.errorLogWriter != null) {
            this.errorLogWriter.println(o);
            this.errorLogWriter.flush();
        }

    }
}
