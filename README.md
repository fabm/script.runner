# script.runner
Project to run scripts and assert select statements

The script runner goal is to run sql scripts, however is possibole to pass a listener in constructor to listen events of running statments

Ex:

        CustomScriptRunner customScriptRunner = new CustomScriptRunner(connection,new StatementsVerifier());
        customScriptRunner.runScript(new FileReader(sqlScript));


To use in maven:

    <repositories>
        <repository>
            <id>script.runner</id>
            <url>https://raw.github.com/fabm/script.runner/mvn-repo/</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>


    <dependencies>
        <dependency>
            <groupId>pt.altran.script.db</groupId>
            <artifactId>script.runner</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
