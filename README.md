# script.runner
Project to run scripts and assert select statements

The script runner goal is to run sql scripts, however is possibole to pass a listener in constructor to listen events of running statments

Ex:

        CustomScriptRunner customScriptRunner = new CustomScriptRunner(connection,new StatementsVerifier());
        customScriptRunner.runScript(new FileReader(sqlScript));
