cd fill_tables; rm *.class; ./code.sh; cd -
rm *.class
javac -cp ".:postgresql-9.4.1211.jar" ConcurrentTransactions.java
java -cp ".:postgresql-9.4.1211.jar" ConcurrentTransactions $1 $2
