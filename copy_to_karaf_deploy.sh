#!/bin/bash

# man: ./copy_to_karaf_deploy.sh cesta_k_deploy_adresáři [-f]
# -f force copy (jako u normálního cp)

# Zjisti zda byl zadán cílový adresář pro deploy
# TODO

# Vypadá to jako cesta ke Karaf deploy ? 
# Pokud ne, zeptej se zda je to opravdu úmysl ...
# TODO

# Zjisti všechny target JAR soubory, které se budou přesouvat
target_files=$(find . -wholename *target/*.jar)
echo $target_files

# Zkopíruj do deploy adresáře
# TODO
