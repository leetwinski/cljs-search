#!/bin/bash

i=0
FILE=$1

escape_quotes() {
    echo $1 | sed 's|\"|\\\"|g'
}

remove_blank_lines() {
    sed '/^\s*$/d' < $1 | cat
}

echo -e "$(remove_blank_lines "$FILE")" | if [[ -f "$FILE" ]]; then 
    while ((i++)); IFS='' read -r line || [[ -n "$line" ]]; do
        printf '{"index": {"_index":"test", "_type": "_doc", "_id": "%s"}}\n' $i
        printf '{"text": "%s"}\n' "$(escape_quotes "$line")"
    done 
else
    echo "file $FILE doesn't exist" >&2
    exit 1
fi
