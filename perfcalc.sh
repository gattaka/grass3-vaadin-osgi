#!/bin/bash

echo "První parametr je cesta k perfstats.log (včetně názvu souboru),"
echo "druhý parametr je pak identifikační část tagu dle kterého se má počítat průměr - například 'getRandom'"

sum=0
count=0

for i in $(cat $1 | egrep -o "time.*[0-9]+.*$2" | egrep -o "time\[[0-9]+" | egrep -o "[0-9]+")
do 

count=$(($count + 1)) 
sum=$(($sum + $i))

done

echo "sum = $sum"
echo "count = $count"
echo "div = " $(($sum / $count))
