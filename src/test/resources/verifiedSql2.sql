--This test will pass
SELECT * from PUBLIC.test
WHERE ID = 3;


--This test must be fails
SELECT * from PUBLIC.test
WHERE ID = 2;