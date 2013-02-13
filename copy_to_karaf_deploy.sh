#!/bin/bash

# man: ./copy_to_karaf_deploy.sh cesta_k_deploy_adresáři [-f]
# -f force copy (jako u normálního cp)

# Zjisti zda byl zadán cílový adresář pro deploy
if [ -z $1 ] ; then 
    echo "Nebyl zadán cílový deploy adresář"
    exit
fi

# Vypadá to jako cesta ke Karaf deploy ? 
# Pokud ne, zeptej se zda je to opravdu úmysl ...
path=$(echo $1 | egrep -o ".*/deploy[/]?$")
if [ -z $path ] ; then
    echo -e "Zadaná cesta:\n$1\nnevypadá jako cesta k deploy adresáři Karaf - opravdu zkopírovat ? [^C pro zrušení; ENTER pokračovat]"
    read
fi

# Zjisti všechny target JAR soubory, které se budou přesouvat
# Zkopíruj do deploy adresáře
echo "Kopíruji:"
for target in $(find . -regex ".*target/.*\.jar" | egrep -v "^\./sandbox"); do
    echo -e "\t$target"
    cp $2 $target $1
done
