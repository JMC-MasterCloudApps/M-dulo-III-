mvn package -DskipTests

export BUCKET_NAME=practica-1.cloud.jm.cabrerar.2021
export RDS_ENDPOINT=jm-cabrerar-2021-events-db.cp1qc6z599xl.us-east-1.rds.amazonaws.com:3306

export REGION=us-east-1
export RDS_DATABASE=events_db
export RDS_PASS=password
export RDS_USER=admin

java -jar -Dspring.profiles.active=production target/jm.cabrerar.2021-0.0.1-SNAPSHOT.jar