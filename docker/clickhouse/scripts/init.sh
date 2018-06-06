#!/usr/bin/env bash

FILE_PATH="/usr/src/init.sql"
echo "$FILE_PATH"

STR=""

cat "$FILE_PATH" | awk -v str="$STR" -F  ";" '{
    where = match($0, /;/)
    if (where != 0) {
        print str$1
        str = ""
    } else str = str$1
}' > r.sql

while IFS='' read -r line || [[ -n "$line" ]]; do
    echo "Execute query: $line"
    clickhouse-client -q "$line"
done < "./r.sql"

