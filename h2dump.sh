#!/bin/sh

java -cp ~/.m2/repository/com/h2database/h2/1.3.168/h2-1.3.168.jar org.h2.tools.Script -url jdbc:h2:tcp://localhost/grass3H2DB.h2.db -user sa
name=$(date +backup_%d_%m_%Y.sql)
mv backup.sql $name
echo "Záloha uložena jako $name"
